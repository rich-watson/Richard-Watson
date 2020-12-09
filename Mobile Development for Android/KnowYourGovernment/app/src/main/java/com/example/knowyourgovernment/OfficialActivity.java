package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class OfficialActivity extends AppCompatActivity {

    Official o;
    String location;
    TextView loc;
    TextView oTitle;
    TextView oParty;
    TextView oName;
    TextView addressLine;
    TextView address;
    TextView phoneLine;
    TextView phoneNumber;
    TextView websiteLine;
    TextView website;
    TextView emailLine;
    TextView email;
    TextView building;
    ImageView oImage;
    ImageView oPartyPic;
    ImageView twitter;
    ImageView facebook;
    ImageView youtube;
    ConstraintLayout cs;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        loc = findViewById(R.id.location);
        oTitle = findViewById(R.id.officialTitle);
        oParty = findViewById(R.id.officialParty);
        oName = findViewById(R.id.officialName);
        building = findViewById(R.id.buildingTitle);
        addressLine = findViewById(R.id.addressLine);
        address = findViewById(R.id.address);
        phoneLine = findViewById(R.id.phoneLine);
        phoneNumber = findViewById(R.id.phoneNumber);
        websiteLine = findViewById(R.id.websiteLine);
        website = findViewById(R.id.website);
        emailLine = findViewById(R.id.emailLine);
        email = findViewById(R.id.email);
        oImage = findViewById(R.id.officialPic);
        oPartyPic = findViewById(R.id.partyLogo);
        twitter = findViewById(R.id.twitterLogo);
        youtube = findViewById(R.id.youtubeLogo);
        facebook = findViewById(R.id.facebookLogo);
        cs = findViewById(R.id.officialConstraintLayout);



        Intent intent = getIntent();
        if (intent.hasExtra("official") && intent.hasExtra("location")) {
            o = (Official) intent.getSerializableExtra("official");
            location = intent.getStringExtra("location");
        }

        loc.setText(location);

        oTitle.setText(o.getOffice());

        oName.setText(o.getName());


        oParty.setText(String.format("(%s)", o.getParty()));

        if (!o.getAddressTitle().equals(""))
            building.setText(o.getAddressTitle());


        if (!o.getStreet().equals("")){
            String completeAddr = String.format("%s \n %s, %s %s", o.getStreet(), o.getCity(), o.getState(), o.getZip());
            address.setText(completeAddr);
            Linkify.addLinks(address, Linkify.ALL);
        } else {
            addressLine.setText("");
        }


        if (!o.getWebsite().equals("")) {
            website.setText(o.getWebsite());
            Linkify.addLinks(website, Linkify.ALL);
        } else
            websiteLine.setText("");

        if (!o.getPhoneNumber().equals("")){
            phoneNumber.setText(o.getPhoneNumber());
            Linkify.addLinks(phoneNumber, Linkify.ALL);
            }
        else
            phoneLine.setText("");

        if (!o.getEmail().equals("")) {
            emailLine.setText("Email:");
            email.setText(o.getEmail());
            Linkify.addLinks(email, Linkify.ALL);
        }


        if (doNetCheck()){

            if (!o.getPhotoURL().equals(""))
                loadRemoteImage(o.getPhotoURL());
            else
                oImage.setImageResource(R.drawable.missing);
        } else
            oImage.setImageResource(R.drawable.brokenimage);


        if (o.getParty().equals("Democratic Party") || o.getParty().equals("Democrat")) {
            oPartyPic.setImageResource(R.drawable.dem_logo);
            cs.setBackgroundColor(Color.parseColor("#0046FA"));
        } else if (o.getParty().equals("Republican Party") || o.getParty().equals("Republican")) {
            oPartyPic.setImageResource(R.drawable.rep_logo);
            cs.setBackgroundColor(Color.parseColor("#BC0D00"));
        } else
            cs.setBackgroundColor(Color.parseColor("#090808"));


        if (!o.getFacebook().equals(""))
            facebook.setImageResource(R.drawable.facebook);

        if (!o.getTwitter().equals(""))
            twitter.setImageResource(R.drawable.twitter);

        if (!o.getYoutube().equals(""))
            youtube.setImageResource(R.drawable.youtube);


    }

    public void imageClick(View v) {
        if (!o.getPhotoURL().equals("")) {
            Intent intent = new Intent(this, PhotoDetail.class);
            intent.putExtra("official", o);
            intent.putExtra("location", location);
            startActivity(intent);
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
        } else
            return false;
    }

    public void clickTwitter(View v) {
        String twitterAppUrl = "twitter://user?screen_name=" + o.getTwitter();
        String twitterWebUrl = "https://twitter.com/" + o.getTwitter();

        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        startActivity(intent);
    }

    public void clickYoutube(View v) {
        String youtubeAppUrl = "youtube://user?screen_name=" + o.getYoutube();
        String youtubeWebUrl = "https://youtube.com/" + o.getYoutube();

        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.youtube.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeWebUrl));
        }
        startActivity(intent);
    }

    public void clickFacebook(View v) {


        String FACEBOOK_URL = "https://www.facebook.com/" + o.getFacebook();

        Intent intent;
        String urlToUse;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);

            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + o.getFacebook();
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        startActivity(intent);
    }




    private void loadRemoteImage(final String imageURL) {

        Picasso.get().load(imageURL)
                .error(R.drawable.missing)
                .placeholder(R.drawable.placeholder)
                .into(oImage);

    }
}