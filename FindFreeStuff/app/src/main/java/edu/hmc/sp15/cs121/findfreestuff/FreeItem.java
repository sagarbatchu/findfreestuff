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
@ParseClassName(Application.FREE_ITEM_CLASS)
public class FreeItem extends ParseObject {
    public String getPostTitle() {
        return getString(Application.STRING_TITLE);
    }
    public void setPostTitle(String value) {
        put(Application.STRING_TITLE, value);
    }
    public String getPostDetails() {
        return getString(Application.STRING_DETAILS);
    }
    public void setPostDetails(String value) {
        put(Application.STRING_DETAILS, value);
    }
    public ParseUser getUser() {
        return getParseUser(Application.STRING_USER);
    }
    public void setUser(ParseUser value) {
        put(Application.STRING_USER, value);
    }
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(Application.STRING_LOCATION);
    }
    public void setLocation(ParseGeoPoint value) {
        put(Application.STRING_LOCATION, value);
    }
    // Note: we store "tags" as a comma-separated string of tags, so we can easily query
    // based on tags and also not have to do JSON serialization
    public void setTags(String tags) {
        put(Application.STRING_TAGS, tags);
        saveInBackground();
    }
    public void addTags(String tags) {
        String oldTags = getString(Application.STRING_TAGS);
        oldTags = (oldTags == null) ? "" : oldTags;

        String newTags = new StringBuilder().append(oldTags).append(tags).toString();

        put(Application.STRING_TAGS, newTags);
    }
    public String getTags() {
        return getString(Application.STRING_TAGS);
    }
    public static ParseQuery<FreeItem> getQuery() {
        return ParseQuery.getQuery(FreeItem.class);
    }
}

