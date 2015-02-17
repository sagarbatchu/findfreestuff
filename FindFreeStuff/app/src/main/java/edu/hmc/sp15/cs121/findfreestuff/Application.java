package edu.hmc.sp15.cs121.findfreestuff;

// Modeled after the example given in the Parse Anywall tutorial

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Application extends android.app.Application {

    ParseObject freeStuffDataStore;

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeStuffDataStore");
        query.getInBackground("uDpR2VOyA2", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    freeStuffDataStore = object;

                    ParseObject freeThing = new ParseObject("FreeStuff");
                    //System.out.println("THIS IS BEFORE THE FIRST PUT");
                    freeThing.put("title", "free1");

                    List<ParseObject> freeStuff = new ArrayList<ParseObject>();
                    freeStuff.add(freeThing);

                    //System.out.println("THIS IS BEFORE THE OTHER PRINT");
                    System.out.println(freeStuff);


                    freeStuffDataStore.put("freeStuff", freeStuff);

                    freeStuffDataStore.saveInBackground();

                    System.out.println("FOUND OUR OBJECT");
                } else {
                    // something went wrong
                }
            }
        });


        ParseObject freeThing = new ParseObject("FreeStuff");
        System.out.println("THIS IS BEFORE THE FIRST PUT");
        freeThing.put("title", "free1");

        List<ParseObject> freeStuff = new ArrayList<ParseObject>();
        freeStuff.add(freeThing);

        System.out.println("THIS IS BEFORE THE OTHER PRINT");
        System.out.println(freeStuff);


        //freeStuffDataStore.put("freeStuff", freeStuff);

        //freeStuffDataStore.saveInBackground();


//        ParseObject freeStuffDataStore = new ParseObject("FreeStuffDataStore");
//        freeStuffDataStore.saveInBackground();
        //ParseGeoPoint claremontGeoPoint = new ParseGeoPoint(34.1067409,-117.7072027);
        //claremontLocation.put("location", claremontGeoPoint);
        // Note: eventually, we should always use ParseObject.saveEventually() so we will handle no network connection
        // gracefully.
        //claremontLocation.saveInBackground();

    }




}
