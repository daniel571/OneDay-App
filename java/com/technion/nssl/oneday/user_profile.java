package com.technion.nssl.oneday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*This activity is create when the user navigates to Settings activity in the options menu
* This activity is updating the server for any new change in the user personal settings*/
public class user_profile extends AppCompatActivity {
    Button Change_preferred_vol, Change_phone_num, Change_email_num, Change_occupation, Done_btn, Change_birth_year;
    boolean phone_waiting_to_approve, email_waiting_to_approve, occupation_waiting_to_approve, birth_year_waiting_to_approve;
    TextView Phone_edit;
    TextView Phone_view;
    TextView Occ_view;
    EditText Email_edit;
    EditText Occu_edit;
    TextView Email_view;
    TextView Preferences_view;
    TextView BirthYear_edit;
    TextView BirthYear_view;
    Spinner Gender_edit_spn;
    Spinner Location_edit_spn;
    CheckBox IsStudent, IsCarpool;
    ArrayAdapter<CharSequence> adapter;
    ArrayList<String> city_list;
    ArrayAdapter<String> city_adapter;
    user my_user;
    Context context;
    int duration;
    boolean mIsSpinnerFirstCall = true;
    boolean CitySpinnerReady = false;
    boolean valid_gender, valid_city;
    int City_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        InternetTracker IT = new InternetTracker();
        duration  = Toast.LENGTH_SHORT;

        context = getApplicationContext();
        if (!IT.isConnected(user_profile.this)) IT.buildDialog(user_profile.this).show();

        Phone_edit = (EditText) findViewById(R.id.EditPhone);
        Phone_view = (TextView) findViewById(R.id.MyPhone);
        Email_edit = (EditText) findViewById(R.id.EditEmail);
        Email_view = (TextView) findViewById(R.id.MyEmail);
        Occ_view = (TextView) findViewById(R.id.MyOccupation);
        Occu_edit = (EditText) findViewById(R.id.EditOccupation);
        BirthYear_edit = (EditText) findViewById(R.id.EditBirthYear);
        BirthYear_view =(TextView) findViewById(R.id.MyBirthYear);
        Preferences_view = (TextView) findViewById(R.id.VolKinds);
        /*Downloading the data from the server to Phone View and EMail View and preferred volunteering */
        Change_preferred_vol = (Button) findViewById(R.id.chg_kinds_butn);
        Change_phone_num = (Button) findViewById(R.id.chg_phone_butn);
        Change_email_num = (Button) findViewById(R.id.chg_email_butn);
        Change_occupation = (Button) findViewById(R.id.chg_occupation_butn);
        Change_birth_year = (Button)findViewById(R.id.chg_birthyear_butn);
        Location_edit_spn = (Spinner)findViewById(R.id.Change_city_sp);
        city_list = new ArrayList<>();
        Done_btn = (Button) findViewById(R.id.button4);
        IsStudent = (CheckBox) findViewById(R.id.ChangeStudent);
        IsCarpool = (CheckBox) findViewById(R.id.IsCarPool);
        Gender_edit_spn = (Spinner)findViewById(R.id.ChangeGender);
        adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender_edit_spn.setAdapter(adapter);
        //  InitialViews();
        Gender_edit_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!mIsSpinnerFirstCall) {
                    Log.d("Spinner", "selected " + parent.getItemIdAtPosition(position));
                    if (position == 0) {
                        new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeGender", "fb_userID="+my_user.getFb_userID()+"&gender=M");
                    } else {
                        new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeGender", "fb_userID="+my_user.getFb_userID()+"&gender=F");
                    }
                    valid_gender = true;
                }
                mIsSpinnerFirstCall = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        // TODO - consider taking the initial values from the server or from the global my_user variable

        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt("phone_view")) {
                case View.VISIBLE: {
                    Phone_view.setVisibility(View.INVISIBLE);
                    Phone_edit.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    Phone_view.setVisibility(View.VISIBLE);
                    Phone_edit.setVisibility(View.INVISIBLE);
                }
            }
            switch (savedInstanceState.getInt("email_view")) {
                case View.VISIBLE: {
                    Email_view.setVisibility(View.INVISIBLE);
                    Email_edit.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    Email_view.setVisibility(View.VISIBLE);
                    Email_edit.setVisibility(View.INVISIBLE);
                }
            }
            switch (savedInstanceState.getInt("occupation_view")) {
                case View.VISIBLE: {
                    Occ_view.setVisibility(View.INVISIBLE);
                    Occu_edit.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    Occ_view.setVisibility(View.VISIBLE);
                    Occu_edit.setVisibility(View.INVISIBLE);
                }
            }
            switch (savedInstanceState.getInt("birth_year_view")) {
                case View.VISIBLE: {
                    BirthYear_view.setVisibility(View.INVISIBLE);
                    BirthYear_edit.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    BirthYear_view.setVisibility(View.VISIBLE);
                    BirthYear_edit.setVisibility(View.INVISIBLE);
                }
            }

        }
        Change_preferred_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra(facebook_login.EXTRA_ID, my_user.getFb_userID());
                intent.putExtra("IS_EXIST", true);
                startActivity(intent);
            }
        });
        Change_phone_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone_waiting_to_approve) {
                    Phone_view.setVisibility(View.INVISIBLE);
                    Phone_edit.setText(Phone_view.getText());
                    Phone_edit.setVisibility(View.VISIBLE);
                    Change_phone_num.setText("Done");
                    phone_waiting_to_approve = true;
                } else {
                    if (!Phone_edit.getText().toString().matches("[0-9]{3,}-[0-9]{7,}")) {
                        Toast toast = Toast.makeText(context, "Phone number is invalid. Please type again", duration);
                        toast.show();
                        return;
                    }
                    Change_phone_num.setText("Edit");
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangePhone","fb_userID="+my_user.getFb_userID()+"&phone="+Phone_edit.getText().toString());
                    /*save the data to the server*/
                    Phone_view.setText(Phone_edit.getText());
                    Phone_edit.setVisibility(View.INVISIBLE);
                    Phone_view.setVisibility(View.VISIBLE);
                    phone_waiting_to_approve = false;
                }
            }
        });
        Change_email_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email_waiting_to_approve) {
                    Email_view.setVisibility(View.INVISIBLE);
                    Email_edit.setText(Email_view.getText());
                    Email_edit.setVisibility(View.VISIBLE);
                    Change_email_num.setText("Done");
                    email_waiting_to_approve = true;
                } else {
                    if (!Email_edit.getText().toString().matches(".+@.+\\..+")) {
                        Toast toast = Toast.makeText(context, "Email is invalid. Please type again", duration);
                        toast.show();
                        return;
                    }
                    Change_email_num.setText("Edit");
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeEmail","fb_userID="+my_user.getFb_userID()+"&email="+Email_edit.getText().toString());
                    Email_view.setText(Email_edit.getText());
                    Email_edit.setVisibility(View.INVISIBLE);
                    Email_view.setVisibility(View.VISIBLE);
                    email_waiting_to_approve = false;
                }
            }
        });
        Change_occupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!occupation_waiting_to_approve) {
                    Occ_view.setVisibility(View.INVISIBLE);
                    Occu_edit.setText(Occ_view.getText());
                    Occu_edit.setVisibility(View.VISIBLE);
                    Change_occupation.setText("Done");
                    occupation_waiting_to_approve = true;
                } else {
                    if (!Occu_edit.getText().toString().matches("([a-zA-Z])+")) {
                        Toast toast = Toast.makeText(context, "Occupation is invalid. Please type again", duration);
                        toast.show();
                        return;
                    }
                    Change_occupation.setText("Edit");
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeOccupation","fb_userID="+my_user.getFb_userID()+"&occupation="+Occu_edit.getText().toString());
                    /*save the data to the server*/
                    Occ_view.setText(Occu_edit.getText());
                    Occu_edit.setVisibility(View.INVISIBLE);
                    Occ_view.setVisibility(View.VISIBLE);
                    occupation_waiting_to_approve = false;
                }
            }
        });
        Change_birth_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!birth_year_waiting_to_approve) {
                    BirthYear_view.setVisibility(View.INVISIBLE);
                    BirthYear_edit.setText(BirthYear_view.getText());
                    BirthYear_edit.setVisibility(View.VISIBLE);
                    Change_birth_year.setText("Done");
                    birth_year_waiting_to_approve = true;
                } else {
                    if (!BirthYear_edit.getText().toString().matches("[0-9][0-9][0-9][0-9]")) {
                        Toast toast = Toast.makeText(context, "Birth Year is invalid. Please type again in four digits format", duration);
                        toast.show();
                        return;
                    }
                    Change_birth_year.setText("Edit");
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeBirthYear","fb_userID="+my_user.getFb_userID()+"&birthYear="+BirthYear_edit.getText().toString());
                    /*save the data to the server*/
                    BirthYear_view.setText(BirthYear_edit.getText());
                    BirthYear_edit.setVisibility(View.INVISIBLE);
                    BirthYear_view.setVisibility(View.VISIBLE);
                    birth_year_waiting_to_approve = false;
                }
            }
        });



        Done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (!intent.getBooleanExtra(facebook_login.IS_EXIST, true)) {

                    // User first time log in-> check correctness
                    if (Phone_view.getText().length()==0){
                        Toast toast = Toast.makeText(context, "Phone number is empty. Please fill", duration);
                        toast.show();
                        return;
                    }
                    if (Email_view.getText().length()==0){
                        Toast toast = Toast.makeText(context, "Email is empty. Please fill", duration);
                        toast.show();
                        return;
                    }
                    if (Occ_view.getText().length()==0){
                        Toast toast = Toast.makeText(context, "Please fill your occupation", duration);
                        toast.show();
                        return;
                    }
                    if (BirthYear_view.getText().length() == 0) {
                        Toast toast = Toast.makeText(context, "Please fill your birth year", duration);
                        toast.show();
                        return;
                    }
                    if (!valid_city) {
                        Toast toast = Toast.makeText(context, "Please choose an initial city", duration);
                        toast.show();
                        return;
                    }
                    if (!valid_gender) {
                        Toast toast = Toast.makeText(context, "Please choose an initial gender", duration);
                        toast.show();
                        return;
                    }
                }
                intent.setClass(getBaseContext(), UserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitialViews();
    }

    public void InitialViews() {
        new GetLocationAsyncTask(this).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/cities");
        my_user = ((OneDay) this.getApplication()).my_user;
        if (my_user.getPhone()!=null) Phone_view.setText(my_user.getPhone());
        if (my_user.getCity()!=0) {
            City_id = my_user.getCity();
            valid_city = true;
        }
        if (my_user.getEmail()!=null) Email_view.setText(my_user.getEmail());
        IsStudent.setChecked(my_user.isStudent() == 1);
        IsCarpool.setChecked(my_user.isCarPool() == 1);
        if (my_user.getBirthYear()!=null) BirthYear_view.setText(my_user.getBirthYear());
        Occ_view.setText(my_user.getOccupation());
        if (my_user.getGender() == null) {
            Gender_edit_spn.setSelection(0);
        }
        else if (my_user.getGender().equals("F")) {
            Gender_edit_spn.setSelection(1);
            valid_gender = true;
        } else if (my_user.getGender()!=null) {
            Gender_edit_spn.setSelection(0);
            valid_gender = true;
        }
        StringBuilder StringToAppend = new StringBuilder();
        int i;
        JSONArray Preferred_arr = my_user.getPreferences();
        try {
            if (Preferred_arr.length() > 0) {
                for (i = 0; i < Preferred_arr.length(); i++) {
                    switch (Preferred_arr.getInt(i)) {
                        //Hospitals
                case 1:
                    StringToAppend.append(", Hospitals");
                    break;
                //Needy families
                case 2:
                    StringToAppend.append(", Needy families");
                    break;
                //Animals
                case 3:
                    StringToAppend.append(", Animals");
                    break;
                //Teenagers
                case 4:
                    StringToAppend.append(", Teenagers");
                    break;
                //Elderly
                case 5:
                    StringToAppend.append(", Elderly");
                    break;
                //Environment
                case 6:
                    StringToAppend.append(", Environment");
                    break;
                default:
                    Log.d("mydebug", "donothing: ");
                    break;
            }
        }
                StringToAppend.deleteCharAt(0);
                StringToAppend.deleteCharAt(0);
                StringToAppend.append(".");
                Preferences_view.setText(StringToAppend.toString());
            }
            else {
                Preferences_view.setText("No preferences selected.");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        IsStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(IsStudent.isChecked()) {
                    // The user is student
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeIsStudent","fb_userID="+my_user.getFb_userID()+"&isStudent=1");
                }
                else {
                    // The user is not a user
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeIsStudent","fb_userID="+my_user.getFb_userID()+"&isStudent=0");
                }
            }
        });


        IsCarpool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(IsCarpool.isChecked()) {
                    // The user is student
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeIsCarpool","fb_userID="+my_user.getFb_userID()+"&isCarPool=1");
                }
                else {
                    // The user is not a user
                    new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeIsCarpool","fb_userID="+my_user.getFb_userID()+"&isCarPool=0");
                }
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
        switch (item.getItemId()) {
            case R.id.Logout:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(), facebook_login.class);
                startActivity(intent);
                finish();
                break;
            case R.id.Settings:
                Intent intent1 = new Intent(getApplicationContext(), user_profile.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.Donate:
                Intent intent2 = new Intent(getApplicationContext(), donate_activity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.Contact:
                Intent intent3 = new Intent(getApplicationContext(), contact_activity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.Upcoming_vol:
                Intent intent4 = new Intent(getApplicationContext(), upcoming_vol.class);
                startActivity(intent4);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("mydebug", "Saving the activity state");
        final int visibility_edit_email = Email_edit.getVisibility();
        final int visibility_edit_phone = Phone_edit.getVisibility();

        final int visibility_edit_occupation = Occu_edit.getVisibility();
        final int visibility_edit_birth_year = BirthYear_edit.getVisibility();

        outState.putInt("phone_view", visibility_edit_phone);
        outState.putInt("email_view", visibility_edit_email);
        outState.putInt("occupation_view", visibility_edit_occupation);
        outState.putInt("birth_year_view", visibility_edit_birth_year);
    }

    public class UserModifyAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            Handle_post_http http_handle = new Handle_post_http();
            result =  http_handle.handle(params[0],params[1]);
            Log.d("UserEditing", "Changing user to  "+ params[1] );
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //    super.onPostExecute(result);
            Toast toast = Toast.makeText(context, result.toString(), duration);
            toast.show();
        }
    }


    public class GetLocationAsyncTask extends AsyncTask<String,Void,String> {
        Activity A;
        boolean flag;
        int init_pos;
        public GetLocationAsyncTask(Activity _A) {
            A = _A;
            flag = false;
        }
        @Override
        protected String doInBackground(String... params) {

            String result;
            Handle_get_http http_handle = new Handle_get_http();
            result =  http_handle.handle(params[0],"");
//            Log.d("UserEditing", "Changing user to  "+ params[1] );
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsono = new JSONObject(result);
                JSONArray jarray = jsono.getJSONArray("Cities"); // FIXME wait for Ido's mail - need to add it.
               // init_pos = 0;
                city_list.clear();
                //city_list.add("Choose city...");
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    ((OneDay)A.getApplication()).citiesMap.put(object.getString("cityName"),object.getInt("cityID"));
                    city_list.add(object.getString("cityName"));
                    if (object.getInt("cityID") == City_id){
                        init_pos = city_list.size()-1;
                    }
                }
               // ((OneDay)A.getApplication()).citiesMap.put("Choose city...",0);
                Location_edit_spn.setAdapter(new ArrayAdapter<String>(A, android.R.layout.simple_spinner_dropdown_item, city_list));
                CitySpinnerReady = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Location_edit_spn.setSelection(init_pos);
            Location_edit_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (flag) {
                        String City = parent.getItemAtPosition(position).toString();
                        String cityID = ((OneDay) A.getApplication()).citiesMap.get(City).toString();
                        Log.d("CitySpinner", "selected " + parent.getItemIdAtPosition(position) + " city id is =" + City);
                        new UserModifyAsyncTask().execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/userChangeCity", "fb_userID=" + my_user.getFb_userID() + "&cityID=" + cityID);
                        valid_city = true;
                    }
                    flag = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });
        }
    }
}

