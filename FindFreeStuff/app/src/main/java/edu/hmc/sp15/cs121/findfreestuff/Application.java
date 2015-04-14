package edu.hmc.sp15.cs121.findfreestuff;

// Modeled after the example given in the Parse Anywall tutorial

import com.parse.Parse;
import com.parse.ParseObject;

public class Application extends android.app.Application {

    public static final String INTENT_EXTRA_LOCATION = "location";
    public static final String INTENT_EXTRA_ID = "id";
    public static final String FREE_ITEM_CLASS = "FreeItem";
    public static final String STRING_LOCATION = "location";
    public static final String STRING_TITLE = "title";
    public static final String STRING_MAXDISTANCE = "maxDistance";
    public static final String STRING_TAGS = "tags";
    public static final String STRING_DETAILS = "details";
    public static final String STRING_USER = "user";
    public static final String STRING_WISHLIST = "wishlist";



    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse Setup
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(FreeItem.class);
        Parse.initialize(this, "9EGWtEu72mUi9x09655QqEUwrMAXuAXF3WvXU3EN", "rnhQwFeiDnbV6GtCjfeZGTP4auFI1pLTy5Kraj0s");
    }

}
