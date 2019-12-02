package com.example.redrouteplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private Button saveSettingsButton;
    private Spinner colorMarkerSpinner;
    private Spinner colorRouteSpinner;

    private Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        arguments = getIntent().getExtras();
        initializeRouteSpinner();
        initializeMarkerSpinner();

        returnButton = (ImageButton) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.finish();
            }
        });

        saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("newColorRoute",
                        colorRouteSpinner.getSelectedItemPosition());
                intent.putExtra("newColorMarker",
                        colorMarkerSpinner.getSelectedItemPosition());
                setResult(2, intent);
                finish();
            }
        });
    }

    private void initializeMarkerSpinner() {
        List<String> spinnerParametrs = new ArrayList<>();
        spinnerParametrs.add("Красный");
        spinnerParametrs.add("Зелёный");
        spinnerParametrs.add("Синий");

        colorMarkerSpinner = findViewById(R.id.colorMarkerSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerParametrs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorMarkerSpinner.setAdapter(adapter);
        colorMarkerSpinner.setSelection(arguments.getInt("markerColor"));
    }

    private void initializeRouteSpinner() {
        List<String> spinnerParametrs = new ArrayList<>();
        spinnerParametrs.add("Чёрный");
        spinnerParametrs.add("Зелёный");
        spinnerParametrs.add("Синий");

        colorRouteSpinner = findViewById(R.id.colorRouteSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerParametrs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorRouteSpinner.setAdapter(adapter);
        colorRouteSpinner.setSelection(arguments.getInt("routeColor"));
    }
}
