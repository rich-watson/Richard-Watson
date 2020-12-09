package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PhotoDetail extends AppCompatActivity {

    TextView loc;
    TextView oTitle;
    TextView oName;
    ImageView oPic;
    ImageView oLogo;
    Official o;
    String location;
    ConstraintLayout cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        loc = findViewById(R.id.location);
        oTitle = findViewById(R.id.officialTitle);
        oName = findViewById(R.id.name);
        oPic = findViewById(R.id.officialImg);
        oLogo = findViewById(R.id.officialLogo);
        cs = findViewById(R.id.detailConstraint);

        Intent intent  = getIntent();
        if (intent.hasExtra("official") && intent.hasExtra("location")) {
            o = (Official) intent.getSerializableExtra("official");
            location = intent.getStringExtra("location");
        }


        loc.setText(location);

        if (doNetCheck())
            loadRemoteImage(o.getPhotoURL());
        else
            oPic.setImageResource(R.drawable.brokenimage);

        oName.setText(o.getName());
        oTitle.setText(o.getOffice());

        if (o.getParty().equals("Democratic Party") || o.getParty().equals("Democrat")) {
            oLogo.setImageResource(R.drawable.dem_logo);
            cs.setBackgroundColor(Color.parseColor("#0046FA"));
        } else if (o.getParty().equals("Republican Party") || o.getParty().equals("Republican")) {
            oLogo.setImageResource(R.drawable.rep_logo);
            cs.setBackgroundColor(Color.parseColor("#BC0D00"));
        } else
            cs.setBackgroundColor(Color.parseColor("#090808"));


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

    private void loadRemoteImage(final String imageURL) {

        Picasso.get().load(imageURL)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(oPic);

    }
}