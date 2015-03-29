package edu.hmc.sp15.cs121.findfreestuff;

import android.content.Context;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by reidcallan on 3/6/15.
 */
public class ListQueryAdapter extends ParseQueryAdapter {

    public ListQueryAdapter(Context context, final String type, final ParseGeoPoint currentLocation, final Double maxDistance) {
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
                return query;
            }
        });
    }

}
