package com.technion.nssl.oneday;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.facebook.login.LoginManager;
/*This activity is create when the user navigates to Contact us activity*/
public class contact_activity extends AppCompatActivity {
    Button send_btn;
    EditText editTextSubject;
    EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_activity);
        send_btn = (Button) findViewById(R.id.button5);
        editTextSubject = (EditText) findViewById(R.id.editText4);
        editTextMessage = (EditText) findViewById(R.id.editText5);
        InternetTracker IT = new InternetTracker();
        if(!IT.isConnected(contact_activity.this)) IT.buildDialog(contact_activity.this).show();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
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
    private void sendEmail() {
        //Getting content for email
        String email = ((OneDay)this.getApplication()).Contact_mail;
        String subject = editTextSubject.getText().toString().trim();
        user my_user = ((OneDay)this.getApplication()).my_user;
        Log.d("debug",my_user.getEmail());
        try {
            String message = "Hi Admin, this message is from: " + my_user.getFirstName() + " " + my_user.getLastName() + ".\n" + "Email: " + my_user.getEmail() + "\n" + editTextMessage.getText().toString().trim();
            SendMail sm = new SendMail(this, email, subject, message);
            //Executing sendmail to send email
            sm.execute();
        }
        catch (NullPointerException ex){
            Log.e("UserError","User is not fully recognize in the server");
        }
        //Creating SendMail object

    }

}
