package edu.hmc.sp15.cs121.findfreestuff;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by Woky on 3/1/15.
 */
public class ListActivity extends Activity {
    private ParseQueryAdapter<ParseObject> titleAdapter;
    private ListView listView;
    private Location location;

    // Inside an Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
        ParseGeoPoint locationPoint = null;
        if (location != null) {
            locationPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        }

        titleAdapter = new ListQueryAdapter(getApplicationContext(), Application.STRING_MAXDISTANCE, locationPoint, ParseUser.getCurrentUser().getDouble(Application.STRING_MAXDISTANCE));
        titleAdapter.setTextKey(Application.STRING_TITLE);

        // Initialize ListView and set initial view to titleAdapter
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(titleAdapter);
        titleAdapter.loadObjects();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToItem(position);
                titleAdapter.loadObjects();
            }
        });


    }

    private void goToItem (int position) {
        if (titleAdapter.getItem(position) != null) {
            FreeItem item = (FreeItem) titleAdapter.getItem(position);
            Intent intent = new Intent(ListActivity.this, ItemActivity.class);
            intent.putExtra(Application.INTENT_EXTRA_LOCATION, location);
            intent.putExtra(Application.INTENT_EXTRA_ID, item.getObjectId());
            startActivityForResult(intent, 0);
        }
        else {
            titleAdapter.loadObjects();
        }
    }
}

