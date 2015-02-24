package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by reidcallan on 2/20/15.
 */
public class ItemActivity extends Activity {

    //UI references
    private Button claimButton;
    private TextView titleText;
    private TextView detailsText;
    private ParseGeoPoint geopoint;
    private FreeItem item;
    Context context;
    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        context = getApplicationContext();
        Intent intent = getIntent();

        Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
        geopoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        //get the item id that the MapActivity hopefully sent through
        Bundle b = intent.getExtras();
        if(b != null) {
            id = b.getString("itemID");
        }
        //find the requested item
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FreeItem");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    item = (FreeItem) object;
                } else {
                    //cry
                }
            }
        });

        //when we have a ParseUser logged in and can access its data
        //if(item.getUser() == current user)
        //then set up the page so that the user can edit the details/delete

        titleText = (TextView) findViewById(R.id.item_Title);
        detailsText = (TextView) findViewById(R.id.item_Details);

        //set up button
        claimButton = (Button) findViewById(R.id.item_ClaimButton);
        claimButton.setEnabled(true);
        claimButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                claim();
            }
        });

        //set textfield text with item title/details
        if (item != null) {
            String title = item.getPostTitle();
            titleText.setText(title.toCharArray(), 0, title.length());
            String details = item.getPostDetails();
            detailsText.setText(details.toCharArray(), 0, details.length());
        }
        //in case we can't find the item
        else {
            CharSequence text = "Could Not Find Requested Item";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            claimButton.setEnabled(false);
        }
    }

    private void claim () {
        //if we are close enough delete the item from parse
        if (geopoint.distanceInKilometersTo(item.getLocation()) < .25) {
            //delete item from parse
            item.deleteInBackground();
            CharSequence congrats = "You have successfully claimed this free item!.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, congrats, duration);
            toast.show();
        }
        //otherwise you have to get closer
        else {
            //error message
            CharSequence error = "You are not close enough to this item to claim it.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
    }


}
