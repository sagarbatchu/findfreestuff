package edu.hmc.sp15.cs121.findfreestuff;

/**
 * Created by reidcallan on 2/20/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class PostActivity extends Activity {
    // UI references.
    private EditText postEditTextTitle;
    private EditText postEditTextDetails;
    private Button postButton;
    private ParseGeoPoint geoPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
        geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        postEditTextTitle = (EditText) findViewById(R.id.post_editTextTitle);
        postEditTextDetails = (EditText) findViewById(R.id.post_editTextDetails);
        postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                post();
            }
        });
        updatePostButtonState();
    }
    private void post () {
        String text = postEditTextTitle.getText().toString().trim();
        String title = postEditTextTitle.getText().toString().trim();
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
// Give public read access
        acl.setPublicReadAccess(true);
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
    private String getPostEditTextTitleText () {
        return postEditTextTitle.getText().toString().trim();
    }
    private String getPostEditTextDetailsText () {
        return postEditTextDetails.getText().toString().trim();
    }
    private void updatePostButtonState () {
        int length1 = getPostEditTextTitleText().length();
        int length2 = getPostEditTextDetailsText().length();
        boolean enabled = length1 > 0 && length2 > 0;
        postButton.setEnabled(enabled);
    }

}
