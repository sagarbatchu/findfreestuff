package edu.hmc.sp15.cs121.findfreestuff;

/**
 * Created by reidcallan on 2/17/15.
 */
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
/**
 * Data model for a free thing.
 */
@ParseClassName("Posts")
public class FreeItem extends ParseObject {
    public String getPostTitle() {
        return getString("title");
    }
    public void setPostTitle(String value) {
        put("title", value);
    }
    public String getPostDetails() {
        return getString("details");
    }
    public void setPostDetails(String value) {
        put("details", value);
    }
    public ParseUser getUser() {
        return getParseUser("user");
    }
    public void setUser(ParseUser value) {
        put("user", value);
    }
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }
    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }
    public static ParseQuery<FreeItem> getQuery() {
        return ParseQuery.getQuery(FreeItem.class);
    }
}

