package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by reidcallan on 2/20/15.
 */
public class ItemActivity extends Activity {

    //UI references
    private Button claimButton;
    private Button editUpdateButton;
    private Button deleteButton;
    private EditText titleText;
    private EditText detailsText;
    private ParseGeoPoint geopoint;
    private FreeItem item;
    Context context;
    private String id;
    private final String edit = "Edit";
    private final String update = "Update";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        context = getApplicationContext();
        Intent intent = getIntent();

        //set current location in a geopoint
        Location currentLocation = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
        geopoint = currentLocation != null ? new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()) : new ParseGeoPoint();

        //get the item id that the Map/ListActivity hopefully sent through
        Bundle b = intent.getExtras();
        if(b != null) {
            id = b.getString(Application.INTENT_EXTRA_ID);
        }

        //find the requested item
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeItem");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    item = (FreeItem) object;

                    //get item location
                    ParseGeoPoint location = item.getLocation();

                    //set up the title and details views
                    titleText = (EditText) findViewById(R.id.item_TitleText);
                    detailsText = (EditText) findViewById(R.id.item_DetailsText);
                    titleText.setEnabled(false);
                    detailsText.setEnabled(false);

                    //set up button
                    claimButton = (Button) findViewById(R.id.item_ClaimButton);
                    claimButton.setEnabled(true);


                    //set textfield text with item title/details
                    if (item != null) {

                        titleText.setHint(item.getPostTitle());
                        detailsText.setHint(item.getPostDetails());

                        //get the item's user
                        ParseUser itemUser = null;
                        try {
                            itemUser = item.getUser().fetchIfNeeded();
                        }
                        catch (ParseException f) {
                            Log.d("Error:", f.toString());
                        }

                        //save the username in a string
                        String itemUserName = "";
                        if (itemUser != null) {
                            itemUserName = itemUser.getUsername();
                        }

                        //if the item was posted by the current user
                        if (itemUserName.equals(ParseUser.getCurrentUser().getUsername())) {

                            //set the claim button to become the edit/update button
                            editUpdateButton = claimButton;
                            editUpdateButton.setText(edit);
                            editUpdateButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    editUpdate();
                                }
                            });

                            //make the delete button visible
                            deleteButton = (Button) findViewById(R.id.item_DeleteButton);
                            deleteButton.setVisibility(View.VISIBLE);
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    delete();
                                }
                            });


                        }
                        else {
                            //otherwise this is another user's item, and we just
                            //want the normal claim screen
                            claimButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    claim();
                                }
                            });
                        }
                    }
                    //in case we can't find the item
                    else {
                        CharSequence text = "Could Not Find Requested Item";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        claimButton.setEnabled(false);
                    }

                } else {
                    Log.d("Error:", e.getMessage());
                }
            }
        });

    }

    private void claim () {
        //find the item we want to claim
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeItem");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    item = (FreeItem) object;


                    //if we are close enough delete the item from parse
                    if (geopoint.distanceInKilometersTo(item.getLocation()) < .25) {
                        //delete item from parse
                        item.deleteInBackground();
                        CharSequence congrats = "You have successfully claimed the free item: " + item.getPostTitle();
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, congrats, duration);
                        toast.show();
                        setResult(0);
                        finish();
                    }
                    //otherwise you have to get closer
                    else {
                        //error message
                        CharSequence error = "You are not close enough to this item to claim it.";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, error, duration);
                        toast.show();
                    }

                } else {
                    Log.d("Logging Message", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void editUpdate () {
        if (editUpdateButton.getText().toString().equals(update)) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeItem");
            query.getInBackground(id, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        item = (FreeItem) object;
                        item.setPostTitle(titleText.getText().toString());
                        item.setPostDetails(detailsText.getText().toString());
                        titleText.setEnabled(false);
                        detailsText.setEnabled(false);
                        editUpdateButton.setText(edit);

                        //let user know the post updated
                        CharSequence msg = "Post Successfully Updated!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, msg, duration);
                        toast.show();
                    }
                    else {
                        Log.d("Error:", e.getMessage());
                    }
                }
            });
        }
        else {
            titleText.setEnabled(true);
            detailsText.setEnabled(true);
            claimButton.setText(update);
            CharSequence msg = "You may now edit this post";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, msg, duration);
            toast.show();
        }
    }

    private void delete () {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeItem");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    item = (FreeItem) object;
                    item.deleteInBackground();
                    CharSequence text = "Item Successfully Deleted!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    editUpdateButton.setEnabled(false);
                    setResult(-1);
                    finish();
                }
                else {
                    Log.d("Error:", e.getMessage());
                }
            }
        });
    }


}
