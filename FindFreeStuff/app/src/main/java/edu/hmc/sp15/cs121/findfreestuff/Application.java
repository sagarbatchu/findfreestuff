package edu.hmc.sp15.cs121.findfreestuff;

// Modeled after the example given in the Parse Anywall tutorial

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class Application extends android.app.Application {

    public static final String INTENT_EXTRA_LOCATION = "location";

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse Setup
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "9EGWtEu72mUi9x09655QqEUwrMAXuAXF3WvXU3EN", "rnhQwFeiDnbV6GtCjfeZGTP4auFI1pLTy5Kraj0s");

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

        ParseObject claremontLocation = new ParseObject("LocationPoint");
        ParseGeoPoint claremontGeoPoint = new ParseGeoPoint(34.1067409,-117.7072027);
        claremontLocation.put("location", claremontGeoPoint);
        // Note: eventually, we should always use ParseObject.saveEventually() so we will handle no network connection
        // gracefully.
        claremontLocation.saveInBackground();

    }




}
