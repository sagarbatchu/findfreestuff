package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MapsActivity) or logged out
 * (SignUpActivity or LoginActivity) activity.
 */
public class DispatchActivity extends Activity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
      startActivity(new Intent(this, MapsActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, WelcomeActivity.class));
    }
  }

}
