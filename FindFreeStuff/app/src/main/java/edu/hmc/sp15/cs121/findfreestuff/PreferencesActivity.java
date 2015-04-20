package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by reidcallan on 2/24/15.
 */
public class PreferencesActivity extends Activity {

    //user reference
    private ParseUser user;

    //UI references
    private Button updateDistanceButton;
    private ListView myItems;
    private EditText newDistance;
    private TextView usernameText;
    private TextView distanceText;
    private Button wishButton;

    //Data references
    private Context context;
    private ListQueryAdapter userAdapter; // for user created items
    private Location location;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //Get the Intent and the current location
        Intent intent = getIntent();
        location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);

        //get the context
        context = getApplicationContext();

        //Find UI components
        myItems = (ListView) findViewById(R.id.preferences_ListOfItems);
        newDistance = (EditText) findViewById(R.id.preferences_ItemDistanceField);
        usernameText = (TextView) findViewById(R.id.preferences_Username);
        distanceText = (TextView) findViewById(R.id.preferences_ItemDistance);

        //get current user
        user = ParseUser.getCurrentUser();
        if (user.get(Application.STRING_MAXDISTANCE) == null) {
            user.put(Application.STRING_MAXDISTANCE, 5.0);
            user.saveInBackground();
        }

        //fill myItems with user created items
        userAdapter = new ListQueryAdapter(context, Application.STRING_USER, null, null, null);
        userAdapter.setTextKey(Application.STRING_TITLE);
        myItems.setAdapter(userAdapter);
        userAdapter.loadObjects();

        myItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToItem(position);
                userAdapter.loadObjects();
            }
        });



        //set up distance UI
        CharSequence maxDistanceText = "" +  user.get(Application.STRING_MAXDISTANCE);
        distanceText.setText(maxDistanceText);
        usernameText.setText(user.getUsername());
        updateDistanceButton = (Button) findViewById(R.id.preferences_setDistanceButton);
        updateDistanceButton.setEnabled(true);
        updateDistanceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateDistance();
            }
        });

        // Set up the handler for the logout button click
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Call the Parse log out method
                ParseUser.logOut();
                // Start an intent for the dispatch activity
                Intent intent = new Intent(PreferencesActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        wishButton = (Button) findViewById(R.id.preferences_wishlistButton);
        wishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PreferencesActivity.this, WishlistActivity.class);
                intent.putExtra(Application.INTENT_EXTRA_LOCATION, location);
                startActivity(intent);
            }
        });
    }

    private void updateDistance () {
        String distance = newDistance.getText().toString();
        Double distanceNum;
        try {
           distanceNum = Double.parseDouble(distance);
           distanceText.setText(distance);
           user.put(Application.STRING_MAXDISTANCE, distanceNum);
           user.saveInBackground();
        }
        catch (NumberFormatException e){
            CharSequence error = "Please enter a decimal number";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
    }

    private void goToItem (int position) {
        if (userAdapter.getItem(position) != null) {
            FreeItem item = (FreeItem) userAdapter.getItem(position);
            Intent intent = new Intent(PreferencesActivity.this, ItemActivity.class);
            intent.putExtra(Application.INTENT_EXTRA_LOCATION, location);
            intent.putExtra(Application.INTENT_EXTRA_ID, item.getObjectId());
            startActivityForResult(intent, 0);
        }
        else {
            userAdapter.loadObjects();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            userAdapter.loadObjects();
        }
    }

}
