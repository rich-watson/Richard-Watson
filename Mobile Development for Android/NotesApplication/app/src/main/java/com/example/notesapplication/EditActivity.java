package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class EditActivity extends AppCompatActivity {

    EditText title;
    EditText body;
    private ArrayList<Note> noteList;
    private static final String TAG = "EditActivity";
    Note incomingNote;
    boolean triedToEdit = false;
    private static int pos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.noteTitle);
        body = findViewById(R.id.noteBody);
        String titleText = title.getText().toString();
        String bodyText = body.getText().toString();
        body.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if (intent.hasExtra("note_list")) {
            noteList = (ArrayList<Note>) intent.getSerializableExtra("note_list");
        }
        if (intent.hasExtra("note") && intent.hasExtra("note_list") &&
                intent.hasExtra("position")) {
            incomingNote = (Note) intent.getSerializableExtra("note");
            noteList = (ArrayList<Note>) intent.getSerializableExtra("note_list");
            pos = intent.getIntExtra("position", -1);
            title.setText(incomingNote.getTitle());
            body.setText(incomingNote.getBody());
            triedToEdit = true;
        }


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

    private void save() {
        if (!title.getText().toString().isEmpty()) {
            if (triedToEdit) {
                Note noteToUpdate = noteList.get(pos);
                String oldTitle = noteToUpdate.getTitle();
                String oldBody = noteToUpdate.getBody();
                if (oldTitle.equals(title.getText().toString()) && oldBody.equals(body.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra("note_list", noteList);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    noteToUpdate.setTitle(title.getText().toString());
                    noteToUpdate.setBody(body.getText().toString());
                    noteToUpdate.setLastDate(System.currentTimeMillis());
                    noteList.remove(pos);
                    noteList.add(0, noteToUpdate);
                    writeJSONData();
                    Intent intent = new Intent();
                    intent.putExtra("note_list", noteList);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            } else {
                Note n = new Note(title.getText().toString(), body.getText().toString());
                n.setLastDate(System.currentTimeMillis());
                noteList.add(0, n);
                writeJSONData();
                Intent intent = new Intent();
                intent.putExtra("note_list", noteList);
                setResult(RESULT_OK, intent);
                finish();
            }

        } else {
            Toast.makeText(this,
                    "Your untitled message was not saved", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("note_list", noteList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            save();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Data");
        builder.setMessage("Do you want to save this data?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                save();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.putExtra("note_list", noteList);
                setResult(RESULT_OK);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("note_list", noteList);
        outState.putString("title", title.getText().toString());
        outState.putString("body", body.getText().toString());


        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        title.setText(savedInstanceState.getString("title"));
        body.setText(savedInstanceState.getString("body"));


    }
}