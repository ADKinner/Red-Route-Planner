package com.example.redrouteplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private Button addPointTextViewButton;
    private Button deleteLastPointTextViewButton;
    private Button createRouteButton;
    private Spinner typeMovementSpinner;

    private TextView firstPointTextView;
    private TextView secondPointTextView;
    private TextView thirdPointTextView;
    private TextView fouthPointTextView;

    private static final String EMPTY_LINE = "";
    private static final int SEARCH_POINTS_RADIUS = 5000;
    private static final double ERROR_LATITUDE = -79.272051;
    private static final double ERROR_LONGTITUDE = 49.547948;

    private int idOfLastInvisibleTextView;
    private double userLatitude;
    private double userLongtitude;
    private double[] pointsLatitudes;
    private double[] pointsLongtitudes;

    private List<LatLng> latLngNearestPointList;
    private List<LatLng> latLngAllPointsList;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Create new route");

        initializeSpinner();

        Bundle arguments = getIntent().getExtras();
        userLatitude = arguments.getDouble("lat");
        userLongtitude = arguments.getDouble("long");

        latLngNearestPointList = new ArrayList<>();

        mQueue = Volley.newRequestQueue(this);

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
                int i = 1;
                pointsLongtitudes = new double[namesOfPoints.size()];
                pointsLatitudes = new double[namesOfPoints.size()];
                for (String nameOfPoint : namesOfPoints) {
                    getAllPointsFromName(nameOfPoint, new LatLng(userLatitude, userLongtitude), i);
                    try {
                        Thread.sleep(300);
                        i++;
                        setReturnButtonNotClickable();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        firstPointTextView = (TextView) findViewById(R.id.firstPointTextView);
        secondPointTextView = (TextView) findViewById(R.id.secondPointTextView);
        thirdPointTextView = (TextView) findViewById(R.id.thirdPointTextView);
        fouthPointTextView = (TextView) findViewById(R.id.fouthPointTextView);


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
        List<String> namesOfPointsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    if (firstPointTextView.isEnabled()) {
                        getPointsNamesFromTextView(firstPointTextView, namesOfPointsList);
                    }
                    break;
                case 1:
                    if (secondPointTextView.isEnabled()) {
                        getPointsNamesFromTextView(secondPointTextView, namesOfPointsList);
                    }
                    break;
                case 2:
                    if (thirdPointTextView.isEnabled()) {
                        getPointsNamesFromTextView(thirdPointTextView, namesOfPointsList);
                    }
                    break;
                case 3:
                    if (fouthPointTextView.isEnabled()) {
                        getPointsNamesFromTextView(fouthPointTextView, namesOfPointsList);
                    }
                    break;
            }
        }
        return namesOfPointsList;
    }

    private void getAllPointsFromName(String name, LatLng current, final int id) {
        String url = getURL(name, current).toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("results")) {
                    try {
                        latLngAllPointsList = new ArrayList<>();
                        String jsonStatus = response.getString("status");
                        if (jsonStatus.equals("ZERO_RESULTS")) {
                            latLngNearestPointList.add(new LatLng(ERROR_LATITUDE, ERROR_LONGTITUDE));
                            pointsLatitudes[id - 1] = getLatitudeFromListById(latLngNearestPointList, id);
                            pointsLongtitudes[id - 1] = getLongtitudeFromListById(latLngNearestPointList, id);
                        } else {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                                JSONObject jsonGeometryObject = jsonArrayObject.getJSONObject("geometry");
                                JSONObject jsonLocationObject = jsonGeometryObject.getJSONObject("location");
                                double lat = jsonLocationObject.getDouble("lat");
                                double lng = jsonLocationObject.getDouble("lng");
                                latLngAllPointsList.add(new LatLng(lat, lng));
                            }
                            latLngNearestPointList.add(latLngAllPointsList.get(getIdOfNearestPoint()));
                            pointsLatitudes[id - 1] = getLatitudeFromListById(latLngNearestPointList, id);
                            pointsLongtitudes[id - 1] = getLongtitudeFromListById(latLngNearestPointList, id);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }  finally {
                        if (getNamesOfPoints().size() == id) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            boolean typeOfMovementIsCar = true;
                            if (typeMovementSpinner.getSelectedItemPosition() == 1) {
                                typeOfMovementIsCar = true;
                            } else {
                                typeOfMovementIsCar = false;
                            }
                            Intent intent = new Intent();
                            intent.putExtra("countOfPoints", id);
                            intent.putExtra("pointsLatitudes", pointsLatitudes);
                            intent.putExtra("pointsLongtitudes", pointsLongtitudes);
                            intent.putExtra("typeOfMovementIsCar", typeOfMovementIsCar);
                            setResult(1, intent);
                            finish();
                        }
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private int getIdOfNearestPoint() {
        List<Double> listOfDistances = new ArrayList<>();
        for (LatLng latLng : latLngAllPointsList) {
            listOfDistances.add(Math.sqrt(Math.pow(userLatitude - latLng.latitude, 2) +
                    Math.pow(userLongtitude - latLng.longitude, 2)));
        }
        return listOfDistances.indexOf(Collections.min(listOfDistances));
    }

    private double getLatitudeFromListById(List<LatLng> list, int id) {
        System.out.println(id);
        return list.get(id - 1).latitude;
    }

    private double getLongtitudeFromListById(List<LatLng> list, int id) {
        return list.get(id - 1).longitude;
    }

    private StringBuffer getURL(String name, LatLng current) {
        StringBuffer urlStringBuffer = new StringBuffer("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=");
        urlStringBuffer.append(current.latitude);
        urlStringBuffer.append(",");
        urlStringBuffer.append(current.longitude);
        urlStringBuffer.append("&radius=");
        urlStringBuffer.append(SEARCH_POINTS_RADIUS);
        urlStringBuffer.append("&name=");
        urlStringBuffer.append(name);
        urlStringBuffer.append("&sensor=true&key=AIzaSyCr706wj6wwi9EfO24u6gcQZ-s5Eweg5DI");
        return urlStringBuffer;
    }

    private void getPointsNamesFromTextView(TextView textView, List<String> namesOfPointsList) {
        String text = textView.getText().toString();
        if (!text.equals(EMPTY_LINE)) {
            namesOfPointsList.add(text);
        }

    }

    private void initializeSpinner() {
        List<String> spinnerParametrs = new ArrayList<>();
        spinnerParametrs.add("Пешком");
        spinnerParametrs.add("На машине");

        typeMovementSpinner = findViewById(R.id.typeMovementSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerParametrs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMovementSpinner.setAdapter(adapter);
    }

    private void setReturnButtonNotClickable() {
        returnButton.setClickable(false);
    }
}
