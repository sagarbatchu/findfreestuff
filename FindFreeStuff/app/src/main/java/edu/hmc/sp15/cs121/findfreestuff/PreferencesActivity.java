package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String maxDistance = "maxDistance";
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        context = getApplicationContext();
        myItems = (ListView) findViewById(R.id.preferences_ListOfItems);
        newDistance = (EditText) findViewById(R.id.preferences_ItemDistanceField);
        usernameText = (TextView) findViewById(R.id.preferences_Username);
        distanceText = (TextView) findViewById(R.id.preferences_ItemDistance);

        //get current user
        user = ParseUser.getCurrentUser();
        if (user.get(maxDistance) == null) {
            user.put(maxDistance, "5");
        }

        CharSequence maxDistanceText = "" +  user.get(maxDistance);
        distanceText.setText(maxDistanceText);
        usernameText.setText(user.getUsername());

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
        Double distanceNum;
        try {
           distanceNum = Double.parseDouble(distance);
           distanceText.setText(distance);
           user.put(maxDistance, distanceNum);

        }
        catch (NumberFormatException e){
            CharSequence error = "Please enter a decimal number";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
    }


}
