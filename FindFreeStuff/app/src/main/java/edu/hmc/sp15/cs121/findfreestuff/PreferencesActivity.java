package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.ParseUser;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        myItems = (ListView) findViewById(R.id.preferences_ListOfItems);
        newDistance = (EditText) findViewById(R.id.preferences_ItemDistanceField);
        usernameText = (TextView) findViewById(R.id.preferences_Username);
        distanceText = (TextView) findViewById(R.id.preferences_ItemDistance);

        //get ParseUser object somehow
        //user.get("maxDistance");
        //also user.getUsername();
        //until then use hard-coded values
        //Intent intent = getIntent(); possibly through an Intent?
        CharSequence username = "User";
        double maxDistanceNum = 5;
        CharSequence maxDistance = "" +  maxDistanceNum;
        distanceText.setText(maxDistance);
        usernameText.setText(username);

        //do a query to get all FreeItems that have this ParseUser
        //put them into the ListView
        //take functionality from ListActivity, modify query to
        //depend on user, not distance

        updateDistanceButton = (Button) findViewById(R.id.preferences_setDistanceButton);
        updateDistanceButton.setEnabled(true);
        updateDistanceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateDistance();
            }
        });
    }

    private void updateDistance () {
        String distance = newDistance.getText().toString();
        //convert to double
        //user.setMaxDistance(distance)
        distanceText.setText(distance);
    }


}
