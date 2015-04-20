package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Created by reidcallan on 4/13/15.
 */
public class WishlistActivity extends Activity {

    private TextView title;
    private ListView wishlist;
    private EditText tagsList;
    private Button updateButton;
    private ListQueryAdapter wishAdapter;
    private Location location;
    private ParseUser user;
    private Context context;
    private String tags;
    private double maxDistance;
    private String[] words;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Intent intent = getIntent();
        location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);

        user = ParseUser.getCurrentUser();
        if (user.get(Application.STRING_MAXDISTANCE) == null) {
            user.put(Application.STRING_MAXDISTANCE, 5.0);
            user.saveInBackground();
        }
        maxDistance = user.getDouble(Application.STRING_MAXDISTANCE);

        if (user.get(Application.STRING_WISHLIST) == null) {
            user.put(Application.STRING_WISHLIST, "");
            user.saveInBackground();
        }
        tags = user.getString(Application.STRING_WISHLIST);
        tags = tags.trim().toLowerCase();

        words = tags.replaceAll("\\s","").split(",");
        String help = "";

        for (String tag : words) {
            if(tag.equals("")) {break;}
            help += tag + ", ";
        }

        context = getApplicationContext();

        //set up UI
        title = (TextView) findViewById(R.id.wishlist_title);
        wishlist = (ListView) findViewById(R.id.wishlist_listview);
        if (location != null) {
            wishAdapter = new ListQueryAdapter(context, Application.STRING_WISHLIST, new ParseGeoPoint(location.getLatitude(), location.getLongitude()), maxDistance, words);
            wishlist.setAdapter(wishAdapter);
            wishAdapter.loadObjects();
            wishAdapter.setTextKey(Application.STRING_TAGS);
        }
        updateButton = (Button) findViewById(R.id.wishlist_updateButton);
        tagsList = (EditText) findViewById(R.id.wishlist_tags);
        tagsList.setText(help);




        wishlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToItem(position);
                wishAdapter.loadObjects();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tags = tagsList.getText().toString().trim();
                words = tags.replaceAll("\\s","").split(",");
                user.put(Application.STRING_WISHLIST, tags);
                user.saveInBackground();
                if (location != null) {
                    wishAdapter = new ListQueryAdapter(context, Application.STRING_WISHLIST, new ParseGeoPoint(location.getLatitude(), location.getLongitude()), maxDistance, words);
                    wishAdapter.loadObjects();
                    wishlist.setAdapter(wishAdapter);
                }
            }
        });


    }

    private void goToItem (int position) {
        if (wishAdapter.getItem(position) != null) {
            FreeItem item = (FreeItem) wishAdapter.getItem(position);
            Intent intent = new Intent(WishlistActivity.this, ItemActivity.class);
            intent.putExtra(Application.INTENT_EXTRA_LOCATION, location);
            intent.putExtra(Application.INTENT_EXTRA_ID, item.getObjectId());
            startActivityForResult(intent, 0);
        }
        else {
            wishAdapter.loadObjects();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            wishAdapter.loadObjects();
        }
    }

}
