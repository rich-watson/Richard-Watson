package com.example.notesapplication;


import androidx.annotation.NonNull;

import java.io.Serializable;


public class Note implements Serializable {

    private String title;
    private String body;
    private long lastDate;


    Note(String nTitle, String nBody) {
        this.title = nTitle;
        this.body = nBody;
        lastDate = 0;

    }

    public String getTitle() {
        return title;
    }


    public String getBody() {
        return body;
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public void setBody(String s) {
        this.body = s;
    }

    public long getDateTime() {
        return this.lastDate;
    }

    public void setLastDate(long lastTimeMS) {
        lastDate = lastTimeMS;
    }


    @NonNull
    @Override
    public String toString() {
        return title + ": " + body + ".";
    }

//    @Override
//    public int compareTo(Note o) {
//        if (lastDate.before(o.lastDate))
//            return -1;
//        else if (lastDate.after(o.lastDate))
//            return 1;
//        else return 0;
//    }
}
