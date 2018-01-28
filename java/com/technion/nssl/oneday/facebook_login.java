package com.technion.nssl.oneday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.media.tv.TvInputService;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class facebook_login extends AppCompatActivity {

    public static final String EXTRA_ID = "1";
    public static final String EXTRA_NAME= "2";
    public static final String EXTRA_GENDER= "3";
    public static final String EXTRA_EMAIL= "4";
    public static final String IS_EXIST= "5";

    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;
    String id,name, email,gender;
    int is_exist;
    Activity A;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        A = this;
        if (((OneDay)getApplication()).User_a != null) {
            ((OneDay)getApplication()).User_a.finish();
            ((OneDay)getApplication()).ResetGparams();
        }
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(facebook_login.this)) IT.buildDialog(facebook_login.this).show();
        boolean LoggedIn = AccessToken.getCurrentAccessToken() != null; //fixme:Here is the Access TOKEN!
        if (LoggedIn) {
            id = Profile.getCurrentProfile().getId();
            ((OneDay)this.getApplication()).setfbID(id);
            name = Profile.getCurrentProfile().getName();
            Log.d("Facebook", "LoggedIn"+id + " " + name );

            // new UserAsynTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventRegister"); //// FIXME: 21/09/2017 need to update the URL - waiting for Ido <--- why this is eventRegister!
            new UserAsynTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/login");
        }
        loginButton = (LoginButton)findViewById(R.id.fb_login_bn);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        //textView = (TextView)findViewById(R.id.textView2);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("Facebook","OnSuccess"+ response.toString());
                                try {
                                    id = object.getString("id");
                                    name = object.getString("name");
                                    gender = object.getString("gender");
                                    email = object.getString("email");
                                    Log.d("mydebug",id + " " + name );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //if the user exist - next activity is profile settings -"שלום דניאל"
                                //if the user not exist - next activity is - main activity - choose catagory
                                new UserAsynTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/login");

                            }  // OnComplete()
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,gender,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
              //  textView.setText("Login Success \n" + loginResult.getAccessToken().getUserId()+"\n"+loginResult.getAccessToken().getToken());
                // After succesfuly login the user moved to the main screen.
                //if the user exist - next activity is profile settings -"שלום דניאל"
                //if the user not exist - next activity is - main activity - choose catagory

            }

            @Override
            public void onCancel() {
                textView.setText("Login failed \n");
            }

            @Override
            public void onError(FacebookException error) {
                textView.setText("Unexpected failed \n");
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class UserAsynTask extends AsyncTask<String,Void,String> {
        private JSONObject access_user;
        boolean need_to_conf;

        @Override
        protected String doInBackground(String... params) {
            String result;
            Handle_post_http http_handle = new Handle_post_http();
            Log.v("LoginActivity", "send access token and user id "+AccessToken.getCurrentAccessToken().getToken().toString()+" &userId= "+id);
            String inputUrl = params[0]+"?accessToken="+AccessToken.getCurrentAccessToken().getToken().toString()+"&userId="+id;
            result =  http_handle.handle(inputUrl,"");
            String my_user;
            Handle_get_http http_get_handle = new Handle_get_http();
            my_user = http_get_handle.handle("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/user","fb_userID="+id);
            try {
                access_user = new JSONObject(my_user);
                user cached_user = new user();
                cached_user.setFb_userID(id);
                if (!access_user.isNull("phone")) cached_user.setPhone(access_user.getString("phone"));
                if (!access_user.isNull("city")) cached_user.setCity(access_user.getInt("city"));
                if (!access_user.isNull("city")) cached_user.setStudent(access_user.getInt("isStudent"));
                if (!access_user.isNull("gender")) cached_user.setGender(access_user.getString("gender"));
                if (!access_user.isNull("email")) cached_user.setEmail(access_user.getString("email"));
                if (!access_user.isNull("isCarPool")) cached_user.setCarPool(access_user.getInt("isCarPool"));
                if (!access_user.isNull("occupation")) cached_user.setOccupation(access_user.getString("occupation"));
                if (!access_user.isNull("birthYear")) cached_user.setBirthYear(access_user.getString("birthYear"));
                ((OneDay)A.getApplication()).my_user.deep_copy(cached_user);
            } catch (JSONException e) {
                e.printStackTrace();
                need_to_conf = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("LoginActivity", "The result from the server is "+result.toString());

            try {
                need_to_conf = (need_to_conf)? true : check_valid();
                if (result.equals("ERROR")){
                    AlertDialog adb = new AlertDialog.Builder(facebook_login.this).create();
                    adb.setTitle("Error");
                    adb.setMessage("The server is not responding");
                    adb.setIcon(R.drawable.error);

                    adb.setButton(AlertDialog.BUTTON_NEUTRAL , "Quit",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            facebook_login.this.finish();
                        }
                    });
                    adb.show();
                }
                else if(result.equals("Error: Error while trying to get User's details from Facebook.")) {
                    AlertDialog adb = new AlertDialog.Builder(facebook_login.this).create();
                    adb.setTitle("Error");
                    adb.setMessage("Error while trying to get User's details from Facebook");
                    adb.setIcon(R.drawable.error);

                    adb.setButton(AlertDialog.BUTTON_NEUTRAL , "Quit",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            facebook_login.this.finish();
                        }
                    });
                    adb.show();
                }
                else if(result.equals("Error: Facebook access token or userId are invalid.")) {
                    AlertDialog adb = new AlertDialog.Builder(facebook_login.this).create();
                    adb.setTitle("Error");
                    adb.setMessage("Facebook access token or userId are invalid");
                    adb.setIcon(R.drawable.error);

                    adb.setButton(AlertDialog.BUTTON_NEUTRAL , "Quit",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            facebook_login.this.finish();
                        }
                    });
                    adb.show();
                }
                else if(result.equals("Message: User is connected.") && !need_to_conf) {
                    // Already exists
                    Intent intent1 = new Intent(getApplicationContext(),UserActivity.class);
                    intent1.putExtra(EXTRA_ID, id);
                    intent1.putExtra(EXTRA_NAME, name);
                    intent1.putExtra(IS_EXIST,true);
                    startActivity(intent1);
                    finish();
                }
                else if(result.equals("Message: User was added to DB.") || need_to_conf) {
                    // New user
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    intent2.putExtra(EXTRA_ID, id);
                    intent2.putExtra(EXTRA_NAME, name);
                    intent2.putExtra(EXTRA_GENDER, gender);
                    intent2.putExtra(EXTRA_EMAIL, email);
                    intent2.putExtra(IS_EXIST, false);
                    startActivity(intent2);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private boolean check_valid() throws JSONException {
             return access_user.isNull("phone")||access_user.isNull("email")||access_user.isNull("phone")||access_user.isNull("gender") || access_user.isNull("preferences") ||access_user.isNull("city")||access_user.isNull("isStudent");
        }
    }
}
