package edu.hmc.sp15.cs121.findfreestuff;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>(); // Maps Parse Object IDs to Markers
    private final Map<String, String> markerIDs = new HashMap<String, String>(); // Maps Marker IDs to Parse Object IDs

    private LocationRequest locationRequest;
    private Location currentLocation;

    private GoogleApiClient mGoogleApiClient;

    private String freeItemParseName = "FreeItem";

    ///////////////////////////// Activity Lifetime Functions /////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set and setup the MapView
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Set up the handler for the post button click
        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Only allow posts if we have a location
                Location myLoc = (currentLocation == null) ? null : currentLocation;
                if (myLoc == null) {
                    Toast.makeText(MapsActivity.this,
                            "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MapsActivity.this, PostActivity.class);
                intent.putExtra(Application.INTENT_EXTRA_LOCATION, myLoc);
                startActivity(intent);
            }
        });



        //Set up the handler for the Preferences button click
        Button preferencesButton = (Button) findViewById(R.id.preferences_button);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start an intent for the preferences activity
                Intent intent = new Intent(MapsActivity.this, PreferencesActivity.class);
                intent.putExtra(Application.INTENT_EXTRA_LOCATION, currentLocation);
                startActivity(intent);
            }
        });

        // Set up the handler for the list button click
        Button listButton = (Button) findViewById(R.id.listview_button);
        listButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(MapsActivity.this, ListActivity.class);
                intent2.putExtra(Application.INTENT_EXTRA_LOCATION, currentLocation);
                startActivity(intent2);
                }
            });
        displayFreeStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        displayFreeStuff();
    }

    /////////////////////////// Setup, UI, and Data Manipulation Functions /////////////////////////

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

            mMap.setOnMarkerClickListener(this);
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

        // Move initial view to Claremont, as emulator doesn't like initializing with GPS location
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(claremont, 13));

        // Set zoom controls enabled, really helps with emulator
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Sync down and display the Free Items from the Parse Core backend
        displayFreeStuff();
    }

    /**
     * Called when a marker on the map is selected. Currently, just displays information
     * associated with the marker, but will eventually open a new ItemActivity.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        final String freeItemID = markerIDs.get(marker.getId());
        // Create and make a Parse Query for a free item with
        // the ID associated with this marker
        ParseQuery<ParseObject> query = ParseQuery.getQuery(freeItemParseName);
        query.getInBackground(freeItemID, new GetCallback<ParseObject>() {
            public void done(ParseObject freeItem, ParseException e) {
                // If the free item actually exists, display its details
                if (e == null) {
                    marker.showInfoWindow();

                    Intent intent = new Intent(MapsActivity.this, ItemActivity.class);
                    intent.putExtra(Application.INTENT_EXTRA_LOCATION, currentLocation);
                    intent.putExtra(Application.INTENT_EXTRA_ID, freeItem.getObjectId());
                    startActivity(intent);

                    return;
                // If the free item does not actually exist, remove it from our hash maps
                // and remove its marker from the map
                } else {
                    mapMarkers.remove(freeItemID);
                    markerIDs.remove(marker.getId());
                    marker.remove();
                }
            }
        });

        return true;
    }

    /**
     * Retrieves Free Items from the Parse Core backend and displays them on the map
     * as Markers.
     */
    private void displayFreeStuff() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(freeItemParseName);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> freeStuffList, ParseException e) {
                if (e == null) {
                    // Create HashSet of markers to keep on the map
                    Set<String> toKeep = new HashSet<String>();

                    // For each Free Item from Parse, if it has a "location" data member
                    // display it on the Map with a marker
                    for (int i = 0; i < freeStuffList.size(); ++i ) {
                        // Grab the free item from the list returned by the Parse Query
                        // and try to extract its location/ID
                        ParseObject freeItem = freeStuffList.get(i);
                        String freeItemID = freeItem.getObjectId();
                        ParseGeoPoint freeItemLocation = freeItem.getParseGeoPoint("location");
                        // Add the marker to the to list of markers to keep on the map
                        toKeep.add(freeItemID);
                        // Try to grab existing marker for this free item
                        Marker freeItemMarker = mapMarkers.get(freeItemID);
                        // If the marker does not already exist
                        if (freeItemMarker == null) {
                            // If the location value of the Parse Object exists
                            if (freeItemLocation != null) {
                                // Put a marker on the map with the proper information
                                LatLng freeItemLatLong = new LatLng(freeItemLocation.getLatitude(), freeItemLocation.getLongitude());
                                Marker newMarker = mMap.addMarker(new MarkerOptions().position(freeItemLatLong).title(freeItem.getString("title")));
                                // Put the marker in the mapMarkers hash map, keyed to Parse Object ID
                                mapMarkers.put(freeItemID, newMarker);
                                // Put the Parse Object ID in the markerIDs hash map, keyed to the marker ID
                                markerIDs.put(newMarker.getId(), freeItemID);
                            }
                        }
                    }
                    // Clean up all of the markers that are not in toKeep
                    cleanUpMarkers(toKeep);
                } else {
                    Log.d("Logging Message", "Error: " + e.getMessage());
                }
            }
        });
    }

    /*
     * Helper method to clean up old markers
     */
        private void cleanUpMarkers(Set<String> markersToKeep) {
            for (String objId : new HashSet<String>(mapMarkers.keySet())) {
                if (!markersToKeep.contains(objId)) {
                    Marker marker = mapMarkers.get(objId);
                    markerIDs.remove(marker.getId());
                    marker.remove();
                    mapMarkers.get(objId).remove();
                    mapMarkers.remove(objId);
                }
            }
        }


    ///////////////////// Google Play Location Services Functions //////////////////////////////////

    /**
     * Report location updates to the UI.
     */
    public void onLocationChanged(Location location) {
        currentLocation = location;

        displayFreeStuff();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Called by Location Services when the request to connect the client finishes successfully. At
     * this point, you can request the current location or start periodic updates
     */
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Update location every second

        // Start location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
    }

    /**
     * Called by Location Services if the connection to the location client drops because of an error.
     */
    public void onDisconnected() {

    }

    @Override
    public void onConnectionSuspended(int i) {
        /// Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "GoogleApiClient connection has failed");
    }

}
