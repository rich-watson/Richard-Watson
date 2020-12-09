package com.example.knowyourgovernment;

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


public class GoogleRunnable implements Runnable {

    private static final String API_KEY = "AIzaSyASDTKTDeAS-Bl-WvmUj_ZaXYP-IzDu3Ls";
    private static final String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=" + API_KEY + "&address=";
    private MainActivity mainActivity;
    private String loc;
    private static final String TAG = "GoogleRunnable";
    private String city;
    private String state;
    private String zip;


    GoogleRunnable(MainActivity mainActivity, String loc) {
        this.mainActivity = mainActivity;
        this.loc = loc;
    }

    @Override
    public void run() {

        Uri dataUri = Uri.parse(DATA_URL + loc);
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

            return;
        }

        final ArrayList<Official> officials = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String loc = city + " " + state + " " + zip;
                mainActivity.updateData(officials, loc);

            }
        });
    }

    private ArrayList<Official> parseJSON(String s) {

        ArrayList<Official> officials = new ArrayList<>();
        try {

            JSONObject completeForm = new JSONObject(s);
            JSONObject normalizedInput = completeForm.getJSONObject("normalizedInput");
            city = normalizedInput.getString("city");
            state = normalizedInput.getString("state");
            if (!normalizedInput.getString("zip").equals("")){
                zip = normalizedInput.getString("zip");
            } else
                zip = "";

            JSONArray offices = completeForm.getJSONArray("offices");

            for (int i = 0; i < offices.length(); i++) {
                JSONObject currentOffice = (JSONObject) offices.get(i);
                String office = currentOffice.getString("name");
                JSONArray officialIndices = currentOffice.getJSONArray("officialIndices");
                JSONArray officialInfo = completeForm.getJSONArray("officials");

                for (int j = 0; j < officialIndices.length(); j++) {
                    JSONObject currentOfficial = (JSONObject) officialInfo.get((Integer) officialIndices.get(j));
                    String party = "Unknown";
                    if (currentOfficial.has("party")) {
                        if (currentOfficial.getString("party").equals("Republican Party") ||
                                currentOfficial.getString("party").equals("Democratic Party") ||
                                currentOfficial.getString("party").equals("Democrat"))
                            party = currentOfficial.getString("party");
                    }
                    Official o = new Official(currentOfficial.getString("name"),
                            office, party);



                    o.setAddressTitle("");
                    o.setStreet("");
                    o.setState("");
                    o.setCity("");
                    o.setZip("");
                    if (currentOfficial.has("address")) {
                        JSONArray addressArray = currentOfficial.getJSONArray("address");
                        JSONObject address = (JSONObject) addressArray.get(0);
                        if (address.has("line2")) {
                            o.setAddressTitle(address.getString("line1"));
                            o.setStreet(address.getString("line2"));
                        } else {

                            o.setStreet(address.getString("line1"));
                        }
                        o.setCity(address.getString("city"));
                        o.setState(address.getString("state"));
                        o.setZip(address.getString("zip"));
                    }

                    if (currentOfficial.has("phones")) {
                        o.setPhoneNumber((String) currentOfficial.getJSONArray("phones").get(0));
                    } else
                        o.setPhoneNumber("");
                    if (currentOfficial.has("urls")) {
                        o.setWebsite((String) currentOfficial.getJSONArray("urls").get(0));
                    } else
                        o.setWebsite("");
                    if (currentOfficial.has("emails")) {
                        o.setEmail((String) currentOfficial.getJSONArray("emails").get(0));
                    } else
                        o.setEmail("");
                    if (currentOfficial.has("photoUrl")) {
                        o.setPhotoURL(currentOfficial.getString("photoUrl"));
                    } else
                        o.setPhotoURL("");  //needs placeholder photo Url

                    o.setFacebook("");
                    o.setTwitter("");
                    o.setYoutube("");
                    if (currentOfficial.has("channels")) {
                        JSONArray channels = currentOfficial.getJSONArray("channels");

                        for (int k = 0; k < channels.length(); k++) {
                            JSONObject curr = (JSONObject) channels.get(k);
                            if (curr.getString("type").equals("Facebook"))
                                o.setFacebook((String) curr.get("id"));

                            else if (curr.getString("type").equals("Twitter"))
                                o.setTwitter((String) curr.get("id"));

                            else if (curr.getString("type").equals("YouTube"))
                                o.setYoutube((String) curr.get("id"));

                        }
                    }

                    officials.add(o);
                }
            }


        } catch (JSONException e) {
            handleResults(null);
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return officials;
    }
}
