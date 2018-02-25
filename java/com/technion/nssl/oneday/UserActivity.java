package com.technion.nssl.oneday;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserActivity extends AppCompatActivity {
    TextView greeting;
    Button upcoming_but, waiting_but,signed_but;
    TextView preferencesView, totalHoursView, percentView;
    user my_user;
    ProgressBar progressBar;
    String fbid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(UserActivity.this)) IT.buildDialog(UserActivity.this).show();
        Intent intent = getIntent();
        fbid =  intent.getStringExtra(facebook_login.EXTRA_ID);
        String my_name = intent.getStringExtra(facebook_login.EXTRA_NAME);
        ((OneDay)getApplication()).User_a = this;
        greeting = (TextView)findViewById(R.id.textView7);
        greeting.setText("Hello "+my_name+"!");
        upcoming_but = (Button)findViewById(R.id.button2);
        waiting_but= (Button)findViewById(R.id.button7);
        signed_but = (Button)findViewById(R.id.button8);
        percentView = (TextView) findViewById(R.id.percent);
        percentView.bringToFront();
        preferencesView = (TextView) findViewById (R.id.prefer);
        totalHoursView =  (TextView) findViewById (R.id.hours);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        /*
        In Order to do progressBar in a shape of pie or circle add this to the progress Bar xml android:progressDrawable="@drawable/circular"
        In order to make in like a ring we need to play with the innnerRadiusRadio and the size of the shape
        * */

        //new UserAsynTask(this).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/user");
        upcoming_but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), upcoming_vol.class);
                startActivity(intent);
            }
        });
        waiting_but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), waiting_vol.class);
                startActivity(intent);
            }
        });
        signed_but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), signed_vol.class);
                startActivity(intent);
            }
        });
    }

    private void NotifyServerTHirt() {
        String email = ((OneDay)this.getApplication()).Contact_mail;
        String subject = "User " + fbid +" TShirt awarded";
        user my_user = ((OneDay)this.getApplication()).my_user;
        try {
            String message = "Hi Admin, The user " + my_user.getFirstName() + " " + my_user.getLastName() + " complete 100 hours of volunteering.\nAnd shall awarded with a TShirt." + "Email: " + my_user.getEmail() + "\n";
            SendMail sm = new SendMail(email, subject, message);
            //Executing sendmail to send email
            sm.execute();
        }
        catch (NullPointerException ex){
            Log.e("UserError","User is not fully recognize in the server");
        }
        //Creating SendMail object
    }

    @Override
    protected void onResume(){
        super.onResume();
        new UserAsynTask(this).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/user",fbid);
    }

    public class UserAsynTask extends AsyncTask<String,Void,String> {

        private Activity A;

        public UserAsynTask(Activity _A) {
            this.A = _A;
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            Handle_get_http http_handle = new Handle_get_http();
            result =  http_handle.handle(params[0],"fb_userID="+params[1]);  // FIXME: 23/09/2017 waiting to Ido to start the server
            Log.d("User", "Get user detail for userID= "+params[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //    super.onPostExecute(result);
            if (result.equals("ERROR")){
                AlertDialog adb = new AlertDialog.Builder(A).create();
                adb.setTitle("Error");
                adb.setMessage("The server is not responding");
                adb.setIcon(R.drawable.error);

                adb.setButton(AlertDialog.BUTTON_NEUTRAL , "Quit",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        A.finish();
                    }
                });
                adb.show();
            }
            try {
                preferencesView.setText("Preferred categories:");
                totalHoursView.setText("Total volunteer hours: ");
                JSONObject jsono = new JSONObject(result);
                my_user = new user();
                my_user.setFb_userID(fbid);
                my_user.setHourCounter(jsono.getInt("hourCounter"));
                my_user.setPhone(jsono.getString("phone"));
                my_user.setCity(jsono.getInt("city"));
                my_user.setStudent(jsono.getInt("isStudent"));
                my_user.setGender(jsono.getString("gender"));
                my_user.setFirstName(jsono.getString("firstName"));
                my_user.setLastName(jsono.getString("lastName"));
                my_user.setEmail(jsono.getString("email"));
                my_user.setPreferences(jsono.getJSONArray("preferences"));
                my_user.setCarPool(jsono.getInt("isCarPool"));
                my_user.setOccupation(jsono.getString("occupation"));
                my_user.setBirthYear(jsono.getString("birthYear"));
                ((OneDay)A.getApplication()).my_user.deep_copy(my_user);
                totalHoursView.append(String.valueOf(my_user.getHourCounter()));
                percentView.setText(my_user.getHourCounter() +"%");
                progressBar.setProgress(my_user.getHourCounter());
                JSONArray j_preferences_array = my_user.getPreferences();
                // String Preferred  = my_user.getPreferences();
                if (j_preferences_array.length()>0) {
                    // String[] Preferred_arr = Preferred.split(",");
                    StringBuilder Preferred_str = new StringBuilder();
                    //    int[] IntPreferred_arr = {-1, -1, -1, -1, -1, -1};
                    int i;
//                    for (i = 0; i < jpreferences_array.length(); i++) {
//                        IntPreferred_arr[i] = jpreferences_array.getInt(i);
//                    }
                    Preferred_str.append("\n");
                    for (i = 0; i < j_preferences_array.length(); i++) {
                        switch (j_preferences_array.getInt(i)) {
                            //Hospitals
                            case 1:
                                ((OneDay) A.getApplication()).Categories_flags[0] = true;
                                Preferred_str.append(", Hospitals");
                                break;
                            //Needy families
                            case 2:
                                ((OneDay) A.getApplication()).Categories_flags[1] = true;
                                Preferred_str.append(", Needy families");
                                break;
                            //Animals
                            case 3:
                                ((OneDay) A.getApplication()).Categories_flags[2] = true;
                                Preferred_str.append(", Animals");
                                break;
                            //Teenagers
                            case 4:
                                ((OneDay) A.getApplication()).Categories_flags[3] = true;
                                Preferred_str.append(", Teenagers");
                                break;
                            //Elderly
                            case 5:
                                ((OneDay) A.getApplication()).Categories_flags[4] = true;
                                Preferred_str.append(", Elderly");
                                break;
                            //Environment
                            case 6:
                                ((OneDay) A.getApplication()).Categories_flags[5] = true;
                                Preferred_str.append(", Environment");
                                break;
                            default:
                                Log.d("mydebug", "donothing: ");
                                break;

                        }
                    }
                    Preferred_str.deleteCharAt(1);
                    Preferred_str.deleteCharAt(1);
                    Preferred_str.append(".");
                    preferencesView.append(Preferred_str.toString());
                }
                if ((my_user.getHourCounter() >= 100) && !((OneDay)A.getApplication()).ServerTShirtNotification)
                {
                    ((OneDay)A.getApplication()).ServerTShirtNotification = true;
                    NotifyServerTHirt();
                }
            } catch (JSONException e) {
                e.printStackTrace();
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


}
