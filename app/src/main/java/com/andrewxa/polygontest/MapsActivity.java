package com.andrewxa.polygontest;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView marker;
    TextView markerDis;
    KmlLayer layer;
    Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        marker = findViewById(R.id.marker);
        markerDis = findViewById(R.id.markerDistance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Creates the polygon layer and add it to the map
        initPolygonLayer();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polygon.getPolygonPoints().get(0), 14));
        mMap.addPolygon(polygon.getOptions());

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LatLng newMarker = new LatLng(latLng.latitude, latLng.longitude);

                if (polygon.isPointInsidePolygon(latLng)) {
                    // The clicked point inside polygon

                    mMap.addMarker(new MarkerOptions().position(newMarker).title("Inside")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    marker.setText("Last added point is Inside!");
                    marker.setTextColor(Color.GREEN);
                    markerDis.setText("");

                } else {
                    // The clicked point outside

                    // Finds the nearest point on polygon from clicked point(latLng)
                    LatLng nearestPointOnPolygon = polygon.findNearestPoint(latLng);

                    // Calculates distance between clicked point(latLng) and
                    // the nearest point on polygon from clicked point(nearestPointOnPolygon)
                    String closestDistance = Util.getDistance(latLng, nearestPointOnPolygon);

                    mMap.addMarker(new MarkerOptions().position(newMarker).title("Shortest distance: " + closestDistance));

                    marker.setText("Last added point is Outside!");
                    marker.setTextColor(Color.RED);
                    markerDis.setText("Shortest distance to polygon is: " + closestDistance);
                }
            }
        });

    }

    private void initPolygonLayer() {
        try {
            layer = new KmlLayer(mMap, R.raw.allowed_area, getApplicationContext());
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create Polygon class and add All point from kml-layer file
        polygon = new Polygon(getPolygonPoints());

        // Removes layer to make possible to click inside Polygon area
        layer.removeLayerFromMap();
    }

    // Gets all point from kml-layer
    public List<LatLng> getPolygonPoints() {
        List<LatLng> points = new ArrayList<>();
        for (KmlContainer container : layer.getContainers()) {
            Iterable<KmlPlacemark> placemarks = container.getPlacemarks();
            if (placemarks != null) {
                for (KmlPlacemark placemark : placemarks) {
                    points = ((KmlPolygon) placemark.getGeometry()).getOuterBoundaryCoordinates();
                }
            }
        }
        return points;
    }
}
