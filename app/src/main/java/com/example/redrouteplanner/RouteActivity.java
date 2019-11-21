package com.example.redrouteplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private Button addPointTextViewButton;
    private Button deleteLastPointTextViewButton;
    private Button createRouteButton;

    private TextView firstPointTextView;
    private TextView secondPointTextView;
    private TextView thirdPointTextView;
    private TextView fouthPointTextView;
    private TextView fifthPointTextView;

    private static final String EMPTY_LINE = "";

    private int idOfLastInvisibleTextView;

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

        addPointTextViewButton = (Button) findViewById(R.id.addPointTextViewButton);
        addPointTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibleNextPointTextView(getNextInvisiblePointTextView());
            }
        });

        deleteLastPointTextViewButton = (Button) findViewById(R.id.deleteLastPointTextViewButton);
        deleteLastPointTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInvisibleLastPointTextView(getLastVisiblePointTextView());
            }
        });

        createRouteButton = (Button) findViewById(R.id.createRouteButton);
        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> namesOfPoints = getNamesOfPoints();

                for (String text : namesOfPoints) {
                    System.out.println(text);
                }
            }
        });

        firstPointTextView = (TextView) findViewById(R.id.firstPointTextView);
        secondPointTextView = (TextView) findViewById(R.id.secondPointTextView);
        thirdPointTextView = (TextView) findViewById(R.id.thirdPointTextView);
        fouthPointTextView = (TextView) findViewById(R.id.fouthPointTextView);
        fifthPointTextView = (TextView) findViewById(R.id.fifthPointTextView);

        setStartTextViewVisibility();
    }

    private void setStartTextViewVisibility() {
        idOfLastInvisibleTextView = 1;
        secondPointTextView.setEnabled(false);
        secondPointTextView.setVisibility(View.INVISIBLE);
        thirdPointTextView.setEnabled(false);
        thirdPointTextView.setVisibility(View.INVISIBLE);
        fouthPointTextView.setEnabled(false);
        fouthPointTextView.setVisibility(View.INVISIBLE);
        fifthPointTextView.setEnabled(false);
        fifthPointTextView.setVisibility(View.INVISIBLE);
    }

    private TextView getNextInvisiblePointTextView() {
        TextView nextInvisibleTextPointView = null;
        switch (idOfLastInvisibleTextView) {
            case 1:
                nextInvisibleTextPointView = secondPointTextView;
                break;
            case 2:
                nextInvisibleTextPointView = thirdPointTextView;
                break;
            case 3:
                nextInvisibleTextPointView = fouthPointTextView;
                break;
            case 4:
                nextInvisibleTextPointView = fifthPointTextView;
                break;
        }
        return nextInvisibleTextPointView;
    }

    private void setVisibleNextPointTextView(TextView textView) {
        if (textView == null) {
            return;
        } else {
            textView.setEnabled(true);
            textView.setVisibility(View.VISIBLE);
            idOfLastInvisibleTextView++;
        }
    }

    private TextView getLastVisiblePointTextView() {
        TextView lastVisiblePointTextView = null;
        switch (idOfLastInvisibleTextView) {
            case 2:
                lastVisiblePointTextView = secondPointTextView;
                break;
            case 3:
                lastVisiblePointTextView = thirdPointTextView;
                break;
            case 4:
                lastVisiblePointTextView = fouthPointTextView;
                break;
            case 5:
                lastVisiblePointTextView = fifthPointTextView;
                break;
        }
        return lastVisiblePointTextView;
    }

    private void setInvisibleLastPointTextView(TextView textView) {
        if (textView == null) {
            return;
        } else {
            textView.setEnabled(false);
            textView.setVisibility(View.INVISIBLE);
            idOfLastInvisibleTextView--;
        }
    }

    private List getNamesOfPoints() {
        List<String> namesOfPoints = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    if (firstPointTextView.isEnabled()) {
                        String text = firstPointTextView.getText().toString();
                        if (!text.equals(EMPTY_LINE)) {
                            namesOfPoints.add(text);
                        }
                    }
                    break;
                case 1:
                    if (secondPointTextView.isEnabled()) {
                        String text = secondPointTextView.getText().toString();
                        if (!text.equals(EMPTY_LINE)) {
                            namesOfPoints.add(text);
                        }
                    }
                    break;
                case 2:
                    if (thirdPointTextView.isEnabled()) {
                        String text = thirdPointTextView.getText().toString();
                        if (!text.equals(EMPTY_LINE)) {
                            namesOfPoints.add(text);
                        }
                    }
                    break;
                case 3:
                    if (fouthPointTextView.isEnabled()) {
                        String text = fouthPointTextView.getText().toString();
                        if (!text.equals(EMPTY_LINE)) {
                            namesOfPoints.add(text);
                        }
                    }
                    break;
                case 4:
                    if (fifthPointTextView.isEnabled()) {
                        String text = fifthPointTextView.getText().toString();
                        if (!text.equals(EMPTY_LINE)) {
                            namesOfPoints.add(text);
                        }
                    }
                    break;
            }
        }
        return namesOfPoints;
    }
}
