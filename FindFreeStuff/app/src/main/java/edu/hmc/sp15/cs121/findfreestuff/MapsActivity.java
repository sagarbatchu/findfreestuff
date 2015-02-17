package edu.hmc.sp15.cs121.findfreestuff;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.ParseGeoPoint;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ArrayList<ParseGeoPoint> freeStuff; // Private Free Stuff data member

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize private Free Stuff data member
        initFreeStuff();

        // Set and setup the MapView
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the private data member containing the Free Stuff to be
     * displayed on the MapView. Creates the private data member if it does
     * not already exist and adds the available Free Stuff to it.
     *
     * Currently, values near Claremont are hardcoded and added to a private
     * ArrayList "freeStuff"
     */
    private void initFreeStuff() {
        // Create the freeStuff data member to hold our freeStuff.
        // Parse GeoPoints right now, should be data/model class eventually that works
        // with Stuff objects
        if (freeStuff == null) {
            freeStuff = new ArrayList<ParseGeoPoint>();
        }

        // For now, hardcode some Free Stuff near Claremont
        ParseGeoPoint stuff1 = new ParseGeoPoint(34.1067409,-117.7072027);
        ParseGeoPoint stuff2 = new ParseGeoPoint(34.1057409,-117.7072027);
        ParseGeoPoint stuff3 = new ParseGeoPoint(34.1047409,-117.7072027);
        ParseGeoPoint stuff4 = new ParseGeoPoint(34.1137409,-117.7072027);

        // Add our Free Stuff to our private data member
        freeStuff.add(stuff1);
        freeStuff.add(stuff2);
        freeStuff.add(stuff3);
        freeStuff.add(stuff4);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Claremont, and move the camera to that marker.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng claremont = new LatLng(34.1067409,-117.7072027);

        // Move initial view to Claremont, as the hardcoded free stuff is here
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(claremont, 13));

        for (int i = 0; i < freeStuff.size(); ++i ) {
            ParseGeoPoint freeThing = freeStuff.get(i);
            LatLng stuff = new LatLng(freeThing.getLatitude(), freeThing.getLongitude());
            mMap.addMarker(new MarkerOptions().position(stuff).title(freeThing.toString()));
        }

    }
}
