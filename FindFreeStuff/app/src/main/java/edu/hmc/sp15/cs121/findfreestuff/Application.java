package edu.hmc.sp15.cs121.findfreestuff;

// Modeled after the example given in the Parse Anywall tutorial

import com.parse.Parse;

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse Setup
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "9EGWtEu72mUi9x09655QqEUwrMAXuAXF3WvXU3EN", "rnhQwFeiDnbV6GtCjfeZGTP4auFI1pLTy5Kraj0s");
    }




}
