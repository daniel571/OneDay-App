package com.technion.nssl.oneday;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
/*This activity is create when the user navigates to Donate activity*/
public class donate_activity extends AppCompatActivity {
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_activity);
        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(donate_activity.this)) IT.buildDialog(donate_activity.this).show();
        img = (ImageView)findViewById(R.id.imageView5);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.odsv.org/he/donate/"));
                startActivity(intent);
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
