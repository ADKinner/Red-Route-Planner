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

    private GoogleMap mMap;

    private GeoApiContext mGeoApiContext = null;
    private Marker[] markers;
    List<com.google.maps.model.LatLng> correctPoints;
    List<LatLng> correctMarkerPoints;

    private ImageButton settingsButton;
    private Button createNewRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        createNewRouteButton = (Button) findViewById(R.id.createNewRouteButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(intent);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            int countOfPoints = data.getIntExtra("countOfPoints", -1);
            double[] pointsLatitudes = data.getDoubleArrayExtra("pointsLatitudes");
            double[] pointsLongtitudes = data.getDoubleArrayExtra("pointsLongtitudes");
            boolean typeOfMovementIsCar = data.getBooleanExtra("typeOfMovementIsCar",
                    false);
            getOnlyCorrectPoints(countOfPoints, pointsLatitudes, pointsLongtitudes);
            clearMap();
            setNewMarkers(correctMarkerPoints);
            createNewRoute(0, false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            markers[i] = mMap.addMarker(new MarkerOptions().position(list.get(i)));
        }
    }

    private void getOnlyCorrectPoints(int size, double[] lat, double[] lng) {
        correctMarkerPoints = new ArrayList<>();
        correctPoints = new ArrayList<>();
        Location location = mMap.getMyLocation();
        correctPoints.add(new com.google.maps.model.LatLng(location.getLatitude(),
                location.getLongitude()));
        for (int i = 0; i < size; i++) {
            if (lat[i] != -79.272051) {
                correctPoints.add(new com.google.maps.model.LatLng(lat[i], lng[i]));
                correctMarkerPoints.add(new LatLng(lat[i], lng[i]));
            }
        }
    }

    private void createNewRoute(int color, boolean typeOfMovementIsCar) {
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
                        .origin(correctPoints.get(0))//Место старта
                        .destination(correctPoints.get(correctPoints.size() - 1))//Пункт назначения
                        .waypoints(wayPoints).await();//Промежуточные точки. Да, не очень красиво, можно через цикл, но зато наглядно
            } else {
                result = DirectionsApi.newRequest(geoApiContext)
                        .alternatives(false)
                        .mode(TravelMode.WALKING)
                        .origin(correctPoints.get(0))//Место старта
                        .destination(correctPoints.get(correctPoints.size() - 1))//Пункт назначения
                        .waypoints(wayPoints).await();//Промежуточные точки. Да, не очень красиво, можно через цикл, но зато наглядно
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
            line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
            latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
        }
        line.width(10f);
        line.color(Color.DKGRAY);
        mMap.addPolyline(line);
    }
}
