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
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final ArrayList<FreeItem> freeStuff = new ArrayList<>(); // Private Free Stuff data member

    private LocationRequest locationRequest;
    private Location lastLocation;
    private Location currentLocation;

    private GoogleApiClient mGoogleApiClient;

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
                Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
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

        displayFreeStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

        // Sync down and display the Free Items from the Parse Core backend
        displayFreeStuff();

        mMap.addMarker(new MarkerOptions().position(claremont).title("Marker"));
        //VERY IMPORTANT!!!
        //include this when we add clickable free item markers to the map screen
        //so that the ItemActivity can have a reference to the free item
        //in the itemIsClicked (or whatever name of your choosing) method,
        //include:
        //Intent i = new Intent(this, TheNextActivity.class);
        //i.putExtra("itemID", this item.getObjectId());
        //startItemActivity(i);

    }

    /**
     * Retrieves Free Items from the Parse Core backend and displays them on the map
     * as Markers.
     */
    private void displayFreeStuff() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeStuff");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> freeStuffList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < freeStuffList.size(); ++i ) {
                        // For each Free Item from Parse, if it has a "location" data member
                        // display it on the Map
                        ParseObject freeThing = freeStuffList.get(i);
                        ParseGeoPoint freeThingLocation = freeThing.getParseGeoPoint("location");

                        if (freeThingLocation != null) {
                            LatLng freeThingLatLong = new LatLng(freeThingLocation.getLatitude(), freeThingLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(freeThingLatLong).title(freeThing.getString("title")));

                            FreeItem newFreeItem = new FreeItem();
                            newFreeItem.setLocation(freeThingLocation);
                            freeStuff.add(newFreeItem);
                        }
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    ///////////////////// Google Play Location Services Functions //////////////////////////////////

    /**
     * Report location updates to the UI.
     */
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());


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

        System.out.println("WE ARE IN ON CONNECTED AND ARE GETTING THE LOCATION");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Update location every second

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
