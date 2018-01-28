package com.technion.nssl.oneday;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Map;

/*This activity is create when the user navigates to Event on waiting list */
public class waiting_vol extends AppCompatActivity {
    ListView listview;
    ArrayList<Events> eventsList;
    EventsAdapter adapter;
    String fbid;
    Map registerd_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_vol);
        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(waiting_vol.this)) IT.buildDialog(waiting_vol.this).show();
        fbid = ((OneDay) this.getApplication()).getfbID();
        listview = (ListView)findViewById(R.id.list_wait);
        eventsList = new ArrayList<Events>();
        registerd_events = ((OneDay)this.getApplication()).registeredEvents;
        adapter = new EventsAdapter(getApplicationContext(), R.layout.row, eventsList, this);
        listview.setAdapter(adapter);
        new AsyncTaskWorker(((OneDay)this.getApplication()).waitingListEvents, 2).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetWaitedbyUser","fb_userID="+fbid,"WaitedEvents","GET");
        new AsyncTaskWorker(eventsList, adapter, ((OneDay)this.getApplication()).registeredEvents, ((OneDay)this.getApplication()).waitingListEvents , this).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventsGetWaitedbyUser","fb_userID="+fbid,"WaitedEvents","GET");

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

