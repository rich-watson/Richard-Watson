package com.example.stockticker;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NameDownloaderRunnable implements Runnable {

    private static final String TAG = "StockLoaderRunnable";
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private MainActivity mainActivity;


    NameDownloaderRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
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

        final HashMap stockMap = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainActivity.updateBackgroundData(stockMap);

            }
        });
    }


    private HashMap<String, String> parseJSON(String s) {
        HashMap<String, String> stockMap = new HashMap<>();
        try {
            JSONArray jObjMain = new JSONArray(s);
            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String name = jStock.getString("name");
                String symbol = jStock.getString("symbol");

                stockMap.put(symbol, name);

            }
        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return stockMap;
    }


}


