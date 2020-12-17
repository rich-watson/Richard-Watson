package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQ_CODE = 123;
    private static final int OTHER_CODE = 345;
    private static final int ACT_CODE = 678;
    private final ArrayList<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readJSONData();
        recyclerView = findViewById(R.id.noteRecycler);

        noteAdapter nAdapter = new noteAdapter(noteList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setActionBar();


    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (!noteList.isEmpty()) {
                int size = noteList.size();
                String title = "Notes Application " + "(" + size + ")";
                actionBar.setTitle(title);

            } else {
                actionBar.setTitle("Notes Application");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent intent2 = new Intent(this, AboutActivity.class);
                intent2.putExtra("note_list", noteList);
                startActivityForResult(intent2, ACT_CODE);
                break;
            case R.id.menu_add:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("note_list", noteList);
                startActivityForResult(intent, REQ_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void writeJSONData() {

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("notes.json", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note n : noteList) {
                writer.beginObject();
                writer.name("title").value(n.getTitle());
                writer.name("text").value(n.getBody());
                writer.name("lastDate").value(n.getDateTime());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: " + e.getMessage());
        }
    }

    private void readJSONData() {

        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("notes.json");

            // Read string content from file
            byte[] data = new byte[(int) fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);

                // Access note data fields
                String title = nObj.getString("title");
                String text = nObj.getString("text");
                long recordedDate = nObj.getLong("lastDate");


                // Create Note and add to ArrayList
                Note n = new Note(title, text);
                n.setLastDate(recordedDate);
                noteList.add(n);
            }
            Log.d(TAG, "readJSONData: " + noteList);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "readJSONData: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("position", pos);
        intent.putExtra("note", n);
        intent.putExtra("note_list", noteList);
        startActivityForResult(intent, OTHER_CODE);

    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Note n = noteList.get(pos);
        final noteAdapter nAdapter = new noteAdapter(noteList, this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Do you want to delete this note?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(n);
                writeJSONData();
                recyclerView.setAdapter(nAdapter);
                setActionBar();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent newData) {
        super.onActivityResult(requestCode, resultCode, newData);
        noteList.clear();
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if (newData != null) {
                    readJSONData();
                    noteAdapter nAdapter = new noteAdapter(noteList, this);
                    recyclerView.setAdapter(nAdapter);
                    setActionBar();
                }
            }
        }
        if (requestCode == OTHER_CODE) {
            if (resultCode == RESULT_OK) {
                if (newData != null) {
                    readJSONData();
                    noteAdapter nAdapter = new noteAdapter(noteList, this);
                    recyclerView.setAdapter(nAdapter);
                    setActionBar();

                } else {
                    readJSONData();
                    noteAdapter nAdapter = new noteAdapter(noteList, this);
                    recyclerView.setAdapter(nAdapter);
                    setActionBar();
                }
            }
        }
        if (requestCode == ACT_CODE) {
            if (resultCode == RESULT_OK) {
                readJSONData();
                noteAdapter nAdapter = new noteAdapter(noteList, this);
                recyclerView.setAdapter(nAdapter);
                setActionBar();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("note_list", noteList);


        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Note> newNoteList;
        newNoteList = (ArrayList<Note>) savedInstanceState.getSerializable("note_list");
        readJSONData();
        noteAdapter nAdapter = new noteAdapter(newNoteList, this);
        recyclerView.setAdapter(nAdapter);
        setActionBar();


    }
}