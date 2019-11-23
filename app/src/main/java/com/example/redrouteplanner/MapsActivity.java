package com.example.redrouteplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int DEFAULT_COLOR = 0;
    private static final int DEFAULT_VALUE_OF_POINTS = 0;
    private static final int ID_OF_START_POINT = 0;
    private static final double ERROR_LATITUDE = -79.272051;
    private static final double ERROR_LONGTITUDE = 49.547948;
    private static final boolean DEFAULT_MOVEMENT_IS_CAR = false;

    private ImageButton settingsButton;
    private ImageButton clearScreenButton;
    private Button createNewRouteButton;

    private GoogleMap mMap;
    private Marker[] markers;
    List<com.google.maps.model.LatLng> correctPoints;
    List<LatLng> correctMarkerPoints;

    private Integer routeColor;
    private Integer markerColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        clearScreenButton = (ImageButton) findViewById(R.id.clearScreenButton);
        createNewRouteButton = (Button) findViewById(R.id.createNewRouteButton);
        routeColor = DEFAULT_COLOR;
        markerColor = DEFAULT_COLOR;

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
                intent.putExtra("routeColor", routeColor);
                intent.putExtra("markerColor", markerColor);
                startActivityForResult(intent, 2);
            }
        });

        createNewRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = mMap.getMyLocation();
                Intent intent = new Intent(MapsActivity.this, RouteActivity.class);
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("long", location.getLongitude());
                startActivityForResult(intent, 1);
            }
        });

        clearScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            int countOfPoints = data.getIntExtra("countOfPoints", DEFAULT_VALUE_OF_POINTS);
            double[] pointsLatitudes = data.getDoubleArrayExtra("pointsLatitudes");
            double[] pointsLongtitudes = data.getDoubleArrayExtra("pointsLongtitudes");
            boolean typeOfMovementIsCar = data.getBooleanExtra("typeOfMovementIsCar",
                    DEFAULT_MOVEMENT_IS_CAR);
            if (countOfPoints > 0) {
                getOnlyCorrectPoints(countOfPoints, pointsLatitudes, pointsLongtitudes);
                clearMap();
                setNewMarkers(correctMarkerPoints);
                createNewRoute(typeOfMovementIsCar);
            }
        } else if (resultCode == 2) {
            routeColor = data.getIntExtra("newColorRoute", DEFAULT_COLOR);
            markerColor = data.getIntExtra("newColorMarker", DEFAULT_COLOR);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation();
            }
        }
    }

    private void clearMap() {
        mMap.clear();
    }

    private void setNewMarkers(List<LatLng> list) {
        markers = new Marker[list.size()];
        for (int i = 0; i < markers.length; i++) {
            markers[i] = mMap.addMarker(new MarkerOptions()
                    .position(list.get(i))
                    .title(i + 1 + "")
                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor())));
        }
    }

    private void getOnlyCorrectPoints(int size, double[] lat, double[] lng) {
        correctMarkerPoints = new ArrayList<>();
        correctPoints = new ArrayList<>();
        Location location = mMap.getMyLocation();
        correctPoints.add(new com.google.maps.model.LatLng(location.getLatitude(),
                location.getLongitude()));
        for (int i = 0; i < size; i++) {
            if (lat[i] != -ERROR_LATITUDE && lng[i] != ERROR_LONGTITUDE) {
                correctPoints.add(new com.google.maps.model.LatLng(lat[i], lng[i]));
                correctMarkerPoints.add(new LatLng(lat[i], lng[i]));
            }
        }
    }

    private void createNewRoute(boolean typeOfMovementIsCar) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyCr706wj6wwi9EfO24u6gcQZ-s5Eweg5DI")
                .build();
        DirectionsResult result = null;
        try {
            com.google.maps.model.LatLng[] wayPoints =
                    new com.google.maps.model.LatLng[correctPoints.size() - 2];
            for (int i = 0; i < wayPoints.length; i++) {
                wayPoints[i] = correctPoints.get(i + 1);
            }
            if (typeOfMovementIsCar) {
                result = DirectionsApi.newRequest(geoApiContext)
                        .alternatives(false)
                        .mode(TravelMode.DRIVING)
                        .origin(correctPoints.get(ID_OF_START_POINT))
                        .destination(correctPoints.get(correctPoints.size() - 1))
                        .waypoints(wayPoints).await();
            } else {
                result = DirectionsApi.newRequest(geoApiContext)
                        .alternatives(false)
                        .mode(TravelMode.WALKING)
                        .origin(correctPoints.get(ID_OF_START_POINT))
                        .destination(correctPoints.get(correctPoints.size() - 1))
                        .waypoints(wayPoints).await();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();
        PolylineOptions line = new PolylineOptions();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < path.size(); i++) {
            line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat,
                    path.get(i).lng));
            latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat,
                    path.get(i).lng));
        }
        line.width(10f);
        line.color(getRouteColor());
        mMap.addPolyline(line);
    }

    private int getRouteColor() {
        switch (routeColor) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
        }
        return Color.BLACK;
    }

    private float getMarkerColor() {
        switch (markerColor) {
            case 0:
                return BitmapDescriptorFactory.HUE_RED;
            case 1:
                return BitmapDescriptorFactory.HUE_GREEN;
            case 2:
                return BitmapDescriptorFactory.HUE_BLUE;
        }
        return BitmapDescriptorFactory.HUE_RED;
    }
}
