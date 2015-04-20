package edu.hmc.sp15.cs121.findfreestuff;

import android.content.Context;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reidcallan on 3/6/15.
 */
public class ListQueryAdapter extends ParseQueryAdapter {

    public ListQueryAdapter(Context context, final String type, final ParseGeoPoint currentLocation, final Double maxDistance, final String[] tags) {
        // Use the QueryFactory to construct a PQA that will only show
        // Items posted by the user or items near the user depending on what is desired
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery(Application.FREE_ITEM_CLASS);

                //case where we want user created items
                if (type.equals(Application.STRING_USER)) {
                    query.whereEqualTo(Application.STRING_USER, ParseUser.getCurrentUser());
                    return query;
                }
                //case where we want nearby items
                else if (type.equals(Application.STRING_MAXDISTANCE)) {
                    //location may be null, if so just show any items
                    if (currentLocation != null) {
                        query.whereWithinMiles(Application.STRING_LOCATION, currentLocation, maxDistance);
                    }
                    return query;
                }
                else if (type.equals(Application.STRING_WISHLIST)) {
                    List<ParseQuery<ParseObject>> queriesList = new ArrayList<ParseQuery<ParseObject>>();

                    if (tags[0].equals("")) {return query.whereExists("GIBBERISH");}


                    for (String tag : tags) {
                        ParseQuery<ParseObject> tagsQueryLower = ParseQuery.getQuery(Application.FREE_ITEM_CLASS);
                        tagsQueryLower.whereContains(Application.STRING_TAGS, tag);
                        tagsQueryLower.whereWithinMiles(Application.STRING_MAXDISTANCE, currentLocation, maxDistance);
                        queriesList.add(tagsQueryLower);
                    }
                    query = ParseQuery.or(queriesList);
                }
                return query;
            }
        });
    }

}
