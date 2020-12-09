package com.example.stockticker;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class StockDownloaderRunnable implements Runnable {

    private static final String urlBeginning = "https://cloud.iexapis.com/stable/stock/";
    private static final String urlEnd = "/quote?token=";
    private static final String apiKey = "pk_521c9e92a7854f9c937da48e4b2df989";
    private MainActivity mainActivity;
    private String ticker;
    private static final String TAG = "StockDownloaderRunnable";


    StockDownloaderRunnable(MainActivity mainActivity, String ticker) {
        this.mainActivity = mainActivity;
        this.ticker = ticker;
    }


    @Override
    public void run() {

        Uri dataUri = Uri.parse(urlBeginning + ticker + urlEnd + apiKey);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            Log.d(TAG, "run: " + sb.toString());


        } catch (Exception e) {
            Log.d(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            Toast.makeText(mainActivity, "Download Failed, try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Stock stockToAdd = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainActivity.updateData(stockToAdd);

            }
        });
    }

    private Stock parseJSON(String s) {
        Stock stockToAdd = new Stock("", "", 0.0, 0.0, 0.0);
        try {

            JSONObject jStock = new JSONObject(s);
            String name = jStock.getString("companyName");
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

            stockToAdd.setName(name);
            stockToAdd.setSymbol(symbol);
            stockToAdd.setPrice(stockPrice);
            stockToAdd.setPriceChange(change);
            stockToAdd.setPercentChange(percentChange);

        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return stockToAdd;
    }
}
