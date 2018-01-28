package com.technion.nssl.oneday;



import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import org.json.JSONArray;
import org.json.JSONException;

/*The role of this activity is to make the user choose his preferred volunteering categories*/
public class MainActivity extends AppCompatActivity {
    Button Accept;
    Intent intentIn;
    StringBuilder preferences = new StringBuilder();
    boolean is_exist;
    int len = ((OneDay)this.getApplication()).category_number;
    String fbid;
    // 0 - Hospital to 5 - Environment
    private CheckBox[] ChboxArr = new CheckBox[len];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTitle(R.string.help_today_label);
        setContentView(R.layout.activity_main);
        fbid = getIntent().getStringExtra(facebook_login.EXTRA_ID);
        Resources res = getResources();
        ((OneDay)this.getApplication()).setfbID(fbid);
        for(int i=0; i<len; i++) {
            String idName = "checkBox" + i;
            ChboxArr[i] = (CheckBox)findViewById(res.getIdentifier(idName,"id",getPackageName()));
        }
        intentIn = getIntent();
        InternetTracker IT = new InternetTracker();
        is_exist = getIntent().getBooleanExtra("IS_EXIST",false);
        if (!is_exist) {
            ((OneDay)this.getApplication()).my_user.setFb_userID(getIntent().getStringExtra(facebook_login.EXTRA_ID));
            if (getIntent().getStringExtra(facebook_login.EXTRA_GENDER)!=null) ((OneDay)this.getApplication()).my_user.setGender(getIntent().getStringExtra(facebook_login.EXTRA_GENDER));
            if (getIntent().getStringExtra(facebook_login.EXTRA_EMAIL) != null)((OneDay)this.getApplication()).my_user.setEmail(getIntent().getStringExtra(facebook_login.EXTRA_EMAIL));
        }
        if(!IT.isConnected(MainActivity.this)) IT.buildDialog(MainActivity.this).show();
      //  ActionBarTitleGravity();
        Accept = (Button)findViewById(R.id.button3);
        InitialCheckBox();
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateCategories();
                new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangePreferences");

            }
        });

    }
    private void UpdateCategories(){
        preferences.setLength(0);
        preferences.append("[");
        StringBuilder jPreferedArray = new StringBuilder("[");
        for (int i = 0; i < len ; i++){
            ((OneDay)this.getApplication()).Categories_flags[i] = ChboxArr[i].isChecked();
            if (ChboxArr[i].isChecked()) {
                preferences.append(String.valueOf(i+1)+",");
                jPreferedArray.append(String.valueOf(i+1)+",");
            }
        }
        if (preferences.length()>1){
            preferences.setLength(preferences.length()-1);
            jPreferedArray.deleteCharAt(jPreferedArray.length()-1);
        }
        preferences.append("]");
        jPreferedArray.append("]");
        JSONArray jsonObj = null;
        try {
            jsonObj = new JSONArray(jPreferedArray.toString());
            ((OneDay)this.getApplication()).my_user.setPreferences(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void InitialCheckBox() {
        for (int i = 0; i < len ; i++) {
            if (((OneDay)this.getApplication()).Categories_flags[i]) {
                ChboxArr[i].setChecked(true);
            }
            else {
                ChboxArr[i].setChecked(false);
            }
        }
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
                break;
            case R.id.Settings:
                Intent intent1 = new Intent(getApplicationContext(),user_profile.class);
                startActivity(intent1);
                break;
            case R.id.Donate:
                Intent intent2 = new Intent(getApplicationContext(),donate_activity.class);
                startActivity(intent2);
                break;
            case R.id.Contact:
                Intent intent3 = new Intent(getApplicationContext(),contact_activity.class);
                startActivity(intent3);
                break;
            case R.id.Upcoming_vol:
                Intent intent4 = new Intent(getApplicationContext(),upcoming_vol.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*This class implement the async task that communicate with server.*/
    public class UserModifyAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            Handle_post_http http_handle = new Handle_post_http();
            result =  http_handle.handle(params[0],"fb_userID="+fbid+"&preferences="+preferences.toString());
            Log.d("preferences", "send to POST req "+ preferences.toString() );
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast toast = Toast.makeText( getApplicationContext(), result.toString(), Toast.LENGTH_SHORT);
            toast.show();
            if (result.equals("Message: User's details were successfully updated.")){
                intentIn.setClass(getBaseContext(), user_profile.class);
                startActivity(intentIn);
                finish();
            }

        }
    }

}
