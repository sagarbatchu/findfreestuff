package edu.hmc.sp15.cs121.findfreestuff;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final ArrayList<ParseObject> freeStuff = new ArrayList<>(); // Private Free Stuff data member

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
//        if (freeStuff == null) {
//            freeStuff = new ArrayList<ParseObject>();
//        }

//        // For now, hardcode some Free Stuff near Claremont
//        ParseObject stuff1 = new ParseObject("FreeStuff");
//        ParseGeoPoint stuff1Location = new ParseGeoPoint(34.1067409,-117.7072027);
//        stuff1.put("location", stuff1Location);
//        stuff1.put("title", "stuff1");
//
//        ParseObject stuff2 = new ParseObject("FreeStuff");
//        ParseGeoPoint stuff2Location = new ParseGeoPoint(34.1057409,-117.7072027);
//        stuff2.put("location", stuff2Location);
//        stuff2.put("title", "stuff2");
//
//        ParseObject stuff3 = new ParseObject("FreeStuff");
//        ParseGeoPoint stuff3Location = new ParseGeoPoint(34.1047409,-117.7072027);
//        stuff3.put("location", stuff3Location);
//        stuff3.put("title", "stuff3");
//
//        ParseObject stuff4 = new ParseObject("FreeStuff");
//        ParseGeoPoint stuff4Location = new ParseGeoPoint(34.1137409,-117.7072027);
//        stuff4.put("location", stuff4Location);
//        stuff4.put("title", "stuff4");
//
//        // Add our Free Stuff to our private data member
//        freeStuff.add(stuff1);
//        freeStuff.add(stuff2);
//        freeStuff.add(stuff3);
//        freeStuff.add(stuff4);



        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeStuff");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> freeStuffList, ParseException e) {
                if (e == null) {

                    System.out.println(freeStuffList.toArray().toString());
                    System.out.println("stuff list is length: " + freeStuffList.size());
                    for (int i = 0; i < freeStuffList.size(); ++i ) {

//                        ParseObject freeThingOriginal = freeStuffList.get(i);
//                        final ParseObject freeThingCopy = new ParseObject("FreeStuff");
//                        ParseGeoPoint freeThingLocation = freeThingOriginal.getParseGeoPoint("location");
//                        if (freeThingLocation != null) {
//                            freeThingCopy.put("location", freeThingOriginal.getParseGeoPoint("location"));
//                            freeThingCopy.put("title", freeThingOriginal.getString("title"));
//                            freeStuff.add(freeThingCopy);
//                        } else {
//                            System.out.println("free stuff had no location");
//                        }

                        ParseObject freeThing = freeStuffList.get(i);
                        ParseGeoPoint freeThingLocation = freeThing.getParseGeoPoint("location");
                        if (freeThingLocation != null) {
                            LatLng stuff = new LatLng(freeThingLocation.getLatitude(), freeThingLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(stuff).title(freeThing.getString("title")));
                            freeStuff.add(freeThing);
                        }
                    }

                    System.out.println("free stuff is of size :" + freeStuff.size());
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        System.out.println(freeStuff.size());

//        for (int i = 0; i < freeStuff.size(); ++i ) {
//            ParseObject freeThing = freeStuff.get(i);
//            freeThing.saveInBackground();
//        }
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

        // Set zoom controls enabled, really helps with emulator
        mMap.getUiSettings().setZoomControlsEnabled(true);

        System.out.println("free stuff is of sizeE :" + freeStuff.size());

        for (int i = 0; i < freeStuff.size(); ++i ) {
            //ParseObject freeThing = freeStuff.get(i);
            //System.out.println("free thing is: " + freeThing.toString());
            //ParseGeoPoint freeThingLocation = freeThing.getParseGeoPoint("location");
            //LatLng stuff = new LatLng(freeThingLocation.getLatitude(), freeThingLocation.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(stuff).title(freeThing.getString("title")));
        }

    }
}
