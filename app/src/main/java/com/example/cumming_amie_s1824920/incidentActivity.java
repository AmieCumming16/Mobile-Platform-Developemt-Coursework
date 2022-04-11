
package com.example.cumming_amie_s1824920;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;



public class incidentActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
// Amie Cumming S1824920
    // acummi205@caledonian.ac.uk

    private GoogleMap mMap;
    private itemData incident_selected;
    private String[] incident_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        incident_selected = getIntent().getParcelableExtra("selected_incident");
        TextView incident_title = (TextView) findViewById(R.id.incident);
        incident_title.setText(incident_selected.getTitle());


        incident_points = incident_selected.getxPosition().split(" ");
        String[] incident_description = incident_selected.getDescription().split("<br /");
        String start_date = incident_description[0].split(": ")[1];
        String end_date = incident_description[1].split(": ")[1];


        TextView start = (TextView) findViewById(R.id.start);
        TextView end = (TextView) findViewById(R.id.end);

        start.setText(start_date);
        end.setText(end_date);



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMinZoomPreference(15);
        LatLng incident = new LatLng(Double.parseDouble(incident_selected.getxPosition()), Double.parseDouble(incident_selected.getyPosition()));
        mMap.addMarker(new MarkerOptions()
                .position(incident)
                .title(incident_selected.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(incident));


    }
    @Override
    public void onClick(View view) {


    }
}