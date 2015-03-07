package edu.hmc.sp15.cs121.findfreestuff;

import android.content.Context;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by reidcallan on 3/6/15.
 */
public class ListQueryAdapter extends ParseQueryAdapter {

    public ListQueryAdapter(Context context) {
        // Use the QueryFactory to construct a PQA that will only show
        // Items posted by the user
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("FreeItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                return query;
            }
        });
    }

}
