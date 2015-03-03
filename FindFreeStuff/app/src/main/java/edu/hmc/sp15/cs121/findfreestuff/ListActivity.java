package edu.hmc.sp15.cs121.findfreestuff;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by Woky on 3/1/15.
 */
public class ListActivity extends Activity {

    private ParseQueryAdapter<ParseObject> titleAdapter;
    private ListView listView;


    // Inside an Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //Intent intent2 = getIntent();

        titleAdapter = new ParseQueryAdapter<ParseObject>(this, "FreeItem");
        titleAdapter.setTextKey("title");

        // Initialize ListView and set initial view to titleAdapter
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(titleAdapter);


        titleAdapter.loadObjects();

    }

}