package edu.hmc.sp15.cs121.findfreestuff;

/**
 * Created by reidcallan on 2/20/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PostActivity extends Activity {
    // UI references.
    private EditText postEditTextTitle;
    private EditText postEditTextDetails;
    private Button postButton;
    private ParseGeoPoint geoPoint;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);

        geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        postEditTextTitle = (EditText) findViewById(R.id.post_editTextTitle);
        postEditTextDetails = (EditText) findViewById(R.id.post_editTextDetails);
        postButton = (Button) findViewById(R.id.post_PostButton);
        postButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                post();
            }
        });
        postButton.setEnabled(true);

        context = getApplicationContext();
    }

    private void post () {
        String text = postEditTextDetails.getText().toString().trim();
        String title = postEditTextTitle.getText().toString().trim();

        if(text.length() == 0 || title.length() == 0) {
            CharSequence alert = "Please enter a title and post details";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
        else {
            // Set up a progress dialog
            final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
            dialog.setMessage(getString(R.string.progress_post));
            dialog.show();

            // Create a post.
            FreeItem post = new FreeItem();
            // Set the location to the current user's location
            post.setLocation(geoPoint);
            post.setPostDetails(text);
            post.setPostTitle(title);
            post.setUser(ParseUser.getCurrentUser());
            ParseACL acl = new ParseACL();

            // Give public read and write access (so they can create and claim items)
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            post.setACL(acl);

            // Save the post
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
    }

}
