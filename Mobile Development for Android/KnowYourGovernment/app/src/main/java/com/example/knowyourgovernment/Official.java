package com.example.knowyourgovernment;


import java.io.Serializable;

public class Official implements Serializable {

    private String name;
    private String office;
    private String party;
    private String addressTitle;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;
    private String website;
    private String email;
    private String photoURL;
    private String facebook;
    private String twitter;
    private String youtube;


    Official(String name, String office, String party) {
        this.name = name;
        this.office = office;
        this.party = party;
    }

    public String getName() { return this.name = name;  }

    public void setName(String s) {  this.name = s;  }

    public String getOffice() {  return this.office = office;  }

    public void setOffice(String s) { this.office = s; }

    public String getParty() {  return this.party = party;  }

    public void setParty(String s) {  this.party = s;  }

    public String getAddressTitle() { return this.addressTitle = addressTitle;  }

    public void setAddressTitle(String s) {  this.addressTitle = s;  }

    public String getStreet() {  return this.street = street;  }

    public void setStreet(String s) {  this.street = s;  }

    public String getCity() {  return this.city = city;  }

    public void setCity(String s) {  this.city = s;  }

    public String getState() {  return this.state = state;  }

    public void setState(String s) {  this.state = s;  }

    public String getZip() {  return this.zip = zip;  }

    public void setZip(String s) {  this.zip = s;  }

    public String getPhoneNumber() { return this.phoneNumber = phoneNumber;  }

    public void setPhoneNumber(String s) {  this.phoneNumber = s;  }

    public String getWebsite() {  return this.website = website;  }

    public void setWebsite(String s) {  this.website = s;  }

    public String getEmail() {  return this.email = email;  }

    public void setEmail(String s) {  this.email = s;  }

    public String getFacebook() {  return this.facebook = facebook;  }

    public void setFacebook(String s) {  this.facebook = s;  }

    public String getPhotoURL() {  return this.photoURL = photoURL;  }

    public void setPhotoURL(String s) {  this.photoURL = s;  }

    public String getTwitter() {  return this.twitter = twitter;  }

    public void setTwitter(String s) {  this.twitter = s;  }

    public String getYoutube() {  return this.youtube = youtube;  }

    public void setYoutube(String s) {  this.youtube = s;  }





}
