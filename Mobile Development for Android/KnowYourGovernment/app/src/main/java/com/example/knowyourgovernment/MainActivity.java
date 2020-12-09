package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private OfficialAdapter oAdapter;
    private List<Official> officialList = new ArrayList<>();
    private LocationManager locationManager;
    private Criteria criteria;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    TextView currentLoc;
    String zip;
    String state;
    String city;
    String userInput;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (doNetCheck()) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();

            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            currentLoc = findViewById(R.id.currentLocation);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE_ID);
            } else {
                    setLocation();
            }
            GoogleRunnable googleRunnable = new GoogleRunnable(this, zip);
            new Thread(googleRunnable).start();

            recyclerView = findViewById(R.id.offRecycler);
            oAdapter = new OfficialAdapter(officialList, this);
            recyclerView.setAdapter(oAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else
            networkDialog();

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        currentLoc.setText("Wrong permission was asked for");

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
        } else
            return false;
    }

    private void networkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    @SuppressLint("MissingPermission")
    private void setLocation() {

        Geocoder geocoder = new Geocoder(this);
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            Double lat = currentLocation.getLatitude();
            Double lon = currentLocation.getLongitude();
            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(lat, lon, 10);
                displayAddress(addresses);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "Current Location not found", Toast.LENGTH_SHORT).show();

        }


    }

    private void setNewLocation(String loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        userInput = loc.trim();
        try {
            List<Address> addresses;
            loc = loc.trim();
            currentLoc.setText(loc);
            addresses = geocoder.getFromLocationName(loc, 10);
            displayAddress(addresses);
        } catch (IOException e) {

            Log.d(TAG, "setNewLocation: " + e);
            e.printStackTrace();

        }

    }

    private void displayAddress(List<Address> addresses) {

        if (addresses.size() == 0) {
            currentLoc.setText(userInput);
            return;
        }
        Address ad = addresses.get(0);

        if (ad.getPostalCode() == null)
            zip = "";
        else
            zip = ad.getPostalCode();

        if (ad.getLocality() == null)
            city = "";
        else
            city = ad.getLocality() + ",";

        if (ad.getAdminArea() == null)
            state = "";
        else
            state = ad.getAdminArea();

        String a = String.format("%s %s %s ", city,
                state, zip);

        currentLoc.setText(a);
    }

    void updateData(ArrayList<Official> offs, String location) {
        setNewLocation(location.trim());
        officialList.clear();
        officialList.addAll(offs);
        oAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official o = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("official", o);
        intent.putExtra("location", currentLoc.getText().toString());
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.location:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (doNetCheck()) {
                            String location = et.getText().toString();
                            GoogleRunnable googleRunnable = new GoogleRunnable(MainActivity.this, location);
                            new Thread(googleRunnable).start();
                        } else
                            networkDialog();


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setTitle("Enter a City, State, or Zip Code:");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


}
