package com.technion.nssl.oneday;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/*This activity is create when the user navigates to Upcoming Events activity */
public class upcoming_vol extends AppCompatActivity {
    ListView listview;
    ArrayList<Events> eventsList;
    EventsAdapter adapter;
    String fbid;
    Map registerd_events, waitinglist_events;
    Activity A;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_vol);
        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(upcoming_vol.this)) IT.buildDialog(upcoming_vol.this).show();
        listview = (ListView)findViewById(R.id.list);
        eventsList = new ArrayList<Events>();
        fbid = ((OneDay) this.getApplication()).getfbID();
        A = this;
        registerd_events = ((OneDay)this.getApplication()).registeredEvents;
        waitinglist_events = ((OneDay)this.getApplication()).waitingListEvents;
        adapter = new EventsAdapter(getApplicationContext(), R.layout.row, eventsList, this);
        listview.setAdapter(adapter);
        new AsyncTaskWorker(((OneDay)this.getApplication()).registeredEvents, 1).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetRegisteredbyUser","fb_userID="+fbid,"RegisteredEvents","GET");
        new AsyncTaskWorker(((OneDay)this.getApplication()).waitingListEvents, 2).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetWaitedbyUser","fb_userID="+fbid,"WaitedEvents","GET");
        new AsyncTaskWorker(eventsList, adapter,registerd_events,waitinglist_events,this).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/events","","Events","GET");
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.SwipeLayoutUCV);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,R.color.refresh,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                listview.setEnabled(false);
                eventsList.clear();
//                new AsyncTaskWorker(((OneDay)upcoming_vol.this.getApplication()).registeredEvents, 1).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetRegisteredbyUser","fb_userID="+fbid,"RegisteredEvents","GET");
//                new AsyncTaskWorker(((OneDay)upcoming_vol.this.getApplication()).waitingListEvents, 2).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetWaitedbyUser","fb_userID="+fbid,"WaitedEvents","GET");

                new AsyncTaskWorker(eventsList, adapter,registerd_events, waitinglist_events,A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/events","","Events","GET");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Logout:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(),facebook_login.class);
                startActivity(intent);
                finish();
                break;
            case R.id.Settings:
                Intent intent1 = new Intent(getApplicationContext(),user_profile.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.Donate:
                Intent intent2 = new Intent(getApplicationContext(),donate_activity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.Contact:
                Intent intent3 = new Intent(getApplicationContext(),contact_activity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.Upcoming_vol:
                Intent intent4 = new Intent(getApplicationContext(),upcoming_vol.class);
                startActivity(intent4);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
