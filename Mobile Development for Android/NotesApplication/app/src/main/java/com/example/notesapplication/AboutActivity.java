package com.example.notesapplication;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    private static ArrayList<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Intent intent = getIntent();
        if (intent.hasExtra("note_list")) {
            noteList = (ArrayList<Note>) intent.getSerializableExtra("note_list");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("note_list", noteList);
        setResult(RESULT_OK);
        finish();

    }
}