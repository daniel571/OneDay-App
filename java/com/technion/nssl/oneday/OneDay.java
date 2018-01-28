package com.technion.nssl.oneday;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;


public class OneDay extends Application{
    public static final int category_number = 6;
    private String fbID;
    public Activity User_a;
    public user my_user = new user();
    final String Contact_mail = "daniel5712@gmail.com";
    // final String Contact_mail = "noams@onedayvolunteering.org";
    public boolean[] Categories_flags = new boolean[category_number];
    Map registeredEvents = new HashMap();//The key of the hash map is ActivityId that the user is registered to and the content is True.
    Map waitingListEvents = new HashMap();
    Map citiesMap = new HashMap();
    public String getfbID() {
        return fbID;
    }
    public void setfbID(String id) {
        fbID = id;
    }
    public void ResetGparams() {
        registeredEvents = new HashMap();
        Categories_flags = new boolean[category_number];
        my_user = new user();
        citiesMap = new HashMap();
    }

}