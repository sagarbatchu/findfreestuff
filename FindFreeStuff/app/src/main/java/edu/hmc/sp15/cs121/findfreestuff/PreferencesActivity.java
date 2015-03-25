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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by reidcallan on 2/24/15.
 */
public class PreferencesActivity extends Activity {

    private ParseUser user;
    private Button updateDistanceButton;
    private ListView myItems;
    private EditText newDistance;
    private TextView usernameText;
    private TextView distanceText;
    private final String maxDistance = "maxDistance";
    private Context context;
    private ListQueryAdapter adapter;
    private Location location;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Intent intent = getIntent();

        location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);

        context = getApplicationContext();
        myItems = (ListView) findViewById(R.id.preferences_ListOfItems);
        newDistance = (EditText) findViewById(R.id.preferences_ItemDistanceField);
        usernameText = (TextView) findViewById(R.id.preferences_Username);
        distanceText = (TextView) findViewById(R.id.preferences_ItemDistance);

        //get current user
        user = ParseUser.getCurrentUser();
        if (user.get(maxDistance) == null) {
            user.put(maxDistance, 5.0);
        }

        //fill list view with user created items
        adapter = new ListQueryAdapter(context);
        adapter.setTextKey("title");
        myItems.setAdapter(adapter);
        adapter.loadObjects();

        myItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToItem(position);
                adapter.loadObjects();
            }
        });

        CharSequence maxDistanceText = "" +  user.get(maxDistance);
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
    }

    private void updateDistance () {
        String distance = newDistance.getText().toString();
        Double distanceNum;
        try {
           distanceNum = Double.parseDouble(distance);
           distanceText.setText(distance);
           user.put(maxDistance, distanceNum);
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
        if (adapter.getItem(position) != null) {
            FreeItem item = (FreeItem) adapter.getItem(position);
            Intent intent = new Intent(PreferencesActivity.this, ItemActivity.class);
            intent.putExtra(Application.INTENT_EXTRA_LOCATION, location);
            intent.putExtra(Application.INTENT_EXTRA_ID, item.getObjectId());
            startActivityForResult(intent, 0);
        }
        else {
            adapter.loadObjects();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            adapter.loadObjects();
        }
    }





}
