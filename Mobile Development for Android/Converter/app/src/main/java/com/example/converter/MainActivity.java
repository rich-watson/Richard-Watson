package com.example.converter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private TextView outputField;
    private EditText kiloView;
    private TextView topBox;
    private TextView bottomBox;
    private boolean convertSwitch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = findViewById(R.id.inputText);
        outputField = findViewById(R.id.outputField);
        kiloView = findViewById(R.id.inputText2);
        topBox = findViewById(R.id.textView2);
        bottomBox = findViewById(R.id.textView4);

        outputField.setMovementMethod(new ScrollingMovementMethod());

    }

    public void onPress(View v) {
        String valIn = userInput.getText().toString();
        String historyText = outputField.getText().toString();
        if (!valIn.isEmpty()) {
            Double num = Double.parseDouble(valIn);
            if (!convertSwitch) {
                num = num * 1.60934;
            } else if (convertSwitch) {
                num = num * 0.621371;
            }

            String s2 = String.format("%.1f", num);
            if (!convertSwitch) {
                String s3 = String.format("%s Mi ==> %s Km", valIn, s2);
                outputField.setText(String.format("%s\n%s", s3, historyText));
            } else {
                String s4 = String.format("%s Km ==> %s Mi", valIn, s2);
                outputField.setText(String.format("%s\n%s", s4, historyText));
            }
            kiloView.setText(s2);
            userInput.setText("");
        } else return;
    }

    public void switchKiloView(View v) {
        topBox.setText("Kilometers value: ");
        bottomBox.setText("Miles Value: ");
        convertSwitch = true;
        kiloView.setText("");

    }

    public void switchMilesView(View v) {
        topBox.setText("Miles Value: ");
        bottomBox.setText("Kilometers Value: ");
        convertSwitch = false;
        kiloView.setText("");

    }

    public void clearClick(View v) {
        outputField.setText("");
        kiloView.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("HISTORY", outputField.getText().toString());
        outState.putBoolean("ConvertSwitch", convertSwitch);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        outputField.setText(savedInstanceState.getString("HISTORY"));
        convertSwitch = savedInstanceState.getBoolean("ConvertSwitch");
        if (convertSwitch) {
            topBox.setText("Kilometers value: ");
            bottomBox.setText("Miles Value: ");
        }

    }


}