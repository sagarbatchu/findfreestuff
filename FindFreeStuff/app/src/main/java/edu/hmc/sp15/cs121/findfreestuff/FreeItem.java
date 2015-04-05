package edu.hmc.sp15.cs121.findfreestuff;

/**
 * Created by reidcallan on 2/17/15.
 */
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Data model for a free thing.
 */
@ParseClassName("FreeItem")
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
    // Note: we store "tags" as a comma-separated string of tags, so we can easily query
    // based on tags and also not have to do JSON serialization
    public void setTags(String[] tags) {
        for (String tag : tags) {
            add("tags", tag);
        }
        saveInBackground();
    }
    public void addTags(String tags) {
        String oldTags = getString("tags");
        oldTags = (oldTags == null) ? "" : oldTags;

        String newTags = new StringBuilder().append(oldTags).append(tags).toString();

        put("tags", newTags);
    }
    public List getTags() {
        return getList("tags");
    }
    public static ParseQuery<FreeItem> getQuery() {
        return ParseQuery.getQuery(FreeItem.class);
    }
    public String getPartialClaim() { return getString("partialClaim"); }
    public void setPartialClaim(String value) { put("partialClaim", value); }
}

