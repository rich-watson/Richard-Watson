package com.example.stockticker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter mAdapter;
    private static HashMap<String, String> stockMap = new HashMap<>();
    private List<Stock> userStockList = new ArrayList<>();
    private final String MARKET_URL = "https://www.marketwatch.com/investing/stock/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userStockList = readJSONData();

        if (doNetCheck()) {

            recyclerView = findViewById(R.id.stockRecycler);
            swiper = findViewById(R.id.swiper);
            mAdapter = new StockAdapter(userStockList, this);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            NameDownloaderRunnable nameDownloader = new NameDownloaderRunnable(this);
            new Thread(nameDownloader).start();
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doRefresh();
                }
            });

        } else {
            for (Stock s : userStockList) {
                s.setPrice(0.0);
                s.setPriceChange(0.0);
                s.setPercentChange(0.0);
            }
            Collections.sort(userStockList);
            swiper = findViewById(R.id.swiper);
            recyclerView = findViewById(R.id.stockRecycler);
            mAdapter = new StockAdapter(userStockList, this);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doRefresh();
                }
            });

        }

    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Added Without a Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addButton) {
            if (doNetCheck()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                builder.setView(et);
                //            builder.setIcon();
                NameDownloaderRunnable nameDownloader = new NameDownloaderRunnable(MainActivity.this);
                new Thread(nameDownloader).start();

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userInput = et.getText().toString();
                        final ArrayList<String> choices = searchUserSelection(userInput);
                        CharSequence[] arr = new CharSequence[choices.size()];
                        for (int i = 0; i < choices.size(); i++) {
                            arr[i] = choices.get(i);
                        }
                        for (int i = 0; i < userStockList.size(); i++) {
                            if (userStockList.get(i).getSymbol().equals(userInput)) {
                                duplicateStock(userInput);
                                return;
                            }
                        }
                        if (choices.isEmpty())
                            stockNotFound(userInput);
                        else {
                            if (choices.size() == 1 && stockMap.containsKey(userInput)) {
                                addStock(userInput);
                            } else if (choices.size() > 1)
                                listDialog(arr);
                        }
                    }


                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setMessage("Please enter a stock symbol: ");
                builder.setTitle("Stock Selection");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> searchUserSelection(String s) {
        ArrayList<String> choices = new ArrayList<>();
        for (Map.Entry e : stockMap.entrySet()) {
            String key = (String) e.getKey();
            String val = (String) e.getValue();
            if (key.contains(s)) {
                choices.add(key + " - " + val);
            }
        }
        return choices;
    }

    private void stockNotFound(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Symbol Not Found: " + s);
        builder.setMessage("Please type a valid stock ticker");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void duplicateStock(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock symbol " + s + " is already displayed");
        builder.setIcon(R.drawable.baseline_warning_black_18);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void listDialog(final CharSequence[] arr) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Make a selection");
        //builder1.setIcon();
        builder1.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CharSequence choice = arr[which];
                String s = (String) choice;
                String toAdd = s.substring(0, s.indexOf("-") - 1);
                addStock(toAdd);
            }
        });

        builder1.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog1 = builder1.create();
        dialog1.show();

    }

    private void addStock(String s) {
        StockDownloaderRunnable stockDownloader = new StockDownloaderRunnable(this, s);
        new Thread(stockDownloader).start();


    }

    private void doRefresh()  {

        userStockList.clear();
        ArrayList<Stock> newList = readJSONData();

        if (doNetCheck()) {
            NameDownloaderRunnable nameDownloader = new NameDownloaderRunnable(this);
            new Thread(nameDownloader).start();

            for (int i = 0; i < newList.size(); i++) {
                StockDownloaderRunnable stockDownloader = new StockDownloaderRunnable(this, newList.get(i).getSymbol());
                new Thread(stockDownloader).start();
            }
            Collections.sort(userStockList);
            mAdapter.notifyDataSetChanged();

        }
        swiper.setRefreshing(false);
    }

    public void updateBackgroundData(HashMap<String, String> sMap) {
        stockMap.putAll(sMap);
    }

    public void updateData(Stock s) {

        userStockList.add(s);
        Collections.sort(userStockList);
        writeJSONData();
        mAdapter.notifyDataSetChanged();

    }

    private ArrayList<Stock> readJSONData() {
        ArrayList<Stock> tempList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("stocks.json");

            BufferedReader reader = new BufferedReader((new InputStreamReader(fis)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Stock> retList = parseJSON(sb.toString());
        tempList.addAll(retList);
        return tempList;


    }

    private ArrayList<Stock> parseJSON(String s) {
        ArrayList<Stock> tempList = new ArrayList<>();

        try {
            JSONArray jObjMain = new JSONArray(s);
            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String name = jStock.getString("name");
                String symbol = jStock.getString("symbol");
                String price = jStock.getString("latestPrice");
                Double stockPrice = 0.0;
                if (!price.trim().isEmpty() && !price.trim().equals("null"))
                    stockPrice = Double.parseDouble(price);
                String priceChange = jStock.getString("change");
                Double change = 0.0;
                if (!priceChange.trim().isEmpty() && !priceChange.trim().equals("null"))
                    change = Double.parseDouble(priceChange);
                String percentDiff = jStock.getString("changePercent");
                Double percentChange = 0.0;
                if (!percentDiff.trim().isEmpty() && !percentDiff.trim().equals("null"))
                    percentChange = Double.parseDouble(percentDiff);

                tempList.add(new Stock(name, symbol, stockPrice, change, percentChange));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempList;

    }

    private void writeJSONData() {

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("stocks.json", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Stock s : userStockList) {
                writer.beginObject();
                writer.name("name").value(s.getName());
                writer.name("symbol").value(s.getSymbol());
                writer.name("latestPrice").value(s.getPrice());
                writer.name("change").value(s.getPriceChange());
                writer.name("changePercent").value(s.getPercentChange());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = userStockList.get(pos);
        String symbol = s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(MARKET_URL + symbol));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Stock s = userStockList.get(pos);
        mAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete stock " + s.getSymbol() + "?");
        builder.setIcon(R.drawable.baseline_delete_black_18);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userStockList.remove(s);
                writeJSONData();
                recyclerView.setAdapter(mAdapter);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }
}