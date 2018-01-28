package com.technion.nssl.oneday;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
/*This class handle the client thread which responsible to get the volunteer event from the server and to parse the response. The response is written in JSON format  */
class AsyncTaskWorker extends AsyncTask<String,Void,String> {
    private ArrayList<Events> eventsList;
    private LinkedList<Events> preferedEventList;
    private LinkedList<Events> unPreferedEventList;
    private EventsAdapter adapter;
    private String ArrayName;
    private int saveToLocalType;
    private Activity A;
    private Map regevent, waitevents;
    private JSONArray RegUserEvent;
    String fbid;

    public AsyncTaskWorker(ArrayList<Events> lv, EventsAdapter ea, Map _regevent, Map _waitingList, Activity _A) {
        saveToLocalType = 0;
        eventsList = lv;
        adapter = ea;
        regevent = _regevent;
        waitevents = _waitingList;
        A = _A;
        preferedEventList = new LinkedList<Events>();
        unPreferedEventList = new LinkedList<Events>();
    }

    public AsyncTaskWorker(Map _eventToUpdate, int type) {
        if (type==1) regevent = _eventToUpdate;
        else if (type==2) waitevents = _eventToUpdate;
        saveToLocalType = type;
    }

    @Override
    protected String doInBackground(String... params) {
        String result;

        ArrayName = params[2];
        Handle_http http_handle;
        if (params[3].equals("GET")) {
            http_handle = new Handle_get_http();
        }
        else {
            http_handle = new Handle_post_http();
        }
        result =  http_handle.handle(params[0],params[1]);
        try {
            JSONObject jsono = new JSONObject(result);
            JSONArray jarray = jsono.getJSONArray(ArrayName);
            JSONObject regUser;
            JSONArray jRegUsers;
            if (saveToLocalType == 0){
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    Events event = new Events();
                    event.setActivityID(object.getInt("activityID"));
                    event.setName(object.getString("name"));
                    event.setLocation(object.getInt("location"));
                    event.setStartTime(object.getString("startTime").substring(0, 16).replace("T", ""));
                    event.setEndTime(object.getString("endTime").substring(0, 16).replace("T"," "));
                    event.setDescription(object.getString("description"));
                    event.setCategory(object.getString("category"));
                    event.setCapacity(object.getString("capacity"));
                    regUser = new JSONObject(http_handle.handle("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventGetRegisteredUsers", "eventID=" + event.getActivityID()));
                    jRegUsers = regUser.getJSONArray("RegisteredUsers");
                    event.setCurrentRegistered(jRegUsers);
                    String user;
                    StringBuilder users_carpool = new StringBuilder();
                    for (int q = 0; q < jRegUsers.length(); q++) {
                        user = http_handle.handle("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/user", "fb_userID=" + jRegUsers.getJSONObject(q).get("userID"));
                        JSONObject jobj = new JSONObject(user);
                        if (jobj.getInt("isCarPool") == 1) {
                            users_carpool.append("Name: " + jobj.get("firstName") + "\n" + "Email: " + jobj.get("email") + "\n");
                        }
                    }
                    Log.i("Carpool0", users_carpool.toString());
                    event.setRegCarPoolUsers(users_carpool.toString());
                    event.setIsActive(object.getString("isActive"));
                    event.setImage(object.getString("image"));
                    event.setOrganizationID(object.getString("organizationID"));
                    if ((regevent.get(event.getActivityID()) == null) || !(boolean) (regevent.get(event.getActivityID()))) {
                        event.setRegistered(false);
                        Log.d("adapter123", "the event " + event.getActivityID() + " is not registered yet");
                        if (waitevents.get(event.getActivityID()) == null || !(boolean) (waitevents.get(event.getActivityID()))) {
                            event.setWaitingList(false);
                            Log.d("adapter123", "the event " + event.getActivityID() + " is not in the waiting list");
                        } else {
                            event.setWaitingList(true);
                            Log.d("adapter123", "the event " + event.getActivityID() + " is in the waiting list already");
                        }
                    } else {
                        Log.d("adapter123", "the event " + event.getActivityID() + " IS registered yet");
                        event.setRegistered(true);
                    }
                    boolean isPrefered = false;
                    int location = ((OneDay)A.getApplication()).my_user.getCity();
                    JSONArray Preferred_arr = ((OneDay)A.getApplication()).my_user.getPreferences();
                    try {
                        if (Preferred_arr.length() > 0) {
                            for (int j = 0; j < Preferred_arr.length(); j++) {
                                switch (Preferred_arr.getInt(j)) {
                                    //Hospitals
                                    case 1:
                                        isPrefered = event.getCategory().equals("Hospitals");
                                        break;
                                    //Needy families
                                    case 2:
                                        isPrefered = event.getCategory().equals("Needy families");
                                        break;
                                    //Animals
                                    case 3:
                                        isPrefered = event.getCategory().equals("Animals");
                                        break;
                                    //Teenagers
                                    case 4:
                                        isPrefered = event.getCategory().equals("Teenagers");
                                        break;
                                    //Elderly
                                    case 5:
                                        isPrefered = event.getCategory().equals("Elderly");
                                        break;
                                    //Environment
                                    case 6:
                                        isPrefered = event.getCategory().equals("Environment");
                                        break;
                                    default:
                                        Log.d("mydebug", "donothing: ");
                                        break;
                                }
                                if (isPrefered) break;
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    if (isPrefered) {
                        if (event.getLocation() == location)
                        {
                            preferedEventList.addFirst(event);
                        }
                        else
                        {
                            preferedEventList.addLast(event);
                        }
                    }
                    else
                    {
                        if (event.getLocation() == location)
                        {
                            unPreferedEventList.addFirst(event);
                        }
                        else
                        {
                            unPreferedEventList.addLast(event);
                        }
                    }
                }
                preferedEventList.addAll(unPreferedEventList);
                Iterator Iterator = preferedEventList.iterator();
                while (Iterator.hasNext()) {
                    eventsList.add((Events)Iterator.next());
                }
            }
            else if (saveToLocalType==1){
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    Log.d("adapter123","REG: the event "+object.getInt("activityID")+ " IS registered yet");
                    regevent.put(object.getInt("activityID"),true);
                }
            }
            else if (saveToLocalType==2){
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    Log.d("adapter123","WAITING: the event "+object.getInt("activityID")+ " IS in the waiting list");
                    waitevents.put(object.getInt("activityID"),true);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //    super.onPostExecute(result);
        if (saveToLocalType == 0) adapter.notifyDataSetChanged();
    }
}

