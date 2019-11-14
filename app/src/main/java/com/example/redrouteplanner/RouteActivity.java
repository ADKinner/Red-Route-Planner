package com.example.redrouteplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class RouteActivity extends AppCompatActivity {
    private ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Create new route");

        returnButton = (ImageButton) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteActivity.super.finish();
            }
        });
    }
}
