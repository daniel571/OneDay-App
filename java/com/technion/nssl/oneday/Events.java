/**
 * Created by Daniel on 09/08/2017.
 */
package com.technion.nssl.oneday;

import org.json.JSONArray;

import java.util.List;
/* This class represent the volunteer event*/
public class Events {
    private int activityID;
    private String name;
    private int Location;
    private String startTime;
    private String endTime;
    private String Description;
    private String capacity;
    private String currentRegistered;
   // private String registeredUsers;
    private String isActive;
    private String organizationID;
    private String image;
    private boolean isRegistered;
    private boolean isWaitingList;
    private String RegCarPoolUsers;
    private String category;



    public String getRegCarPoolUsers() {
        return RegCarPoolUsers;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public void setRegCarPoolUsers(String regCarPoolUsers) {
        RegCarPoolUsers = regCarPoolUsers;
    }

    public boolean isWaitingList() {
        return isWaitingList;
    }

    public void setWaitingList(boolean waitingList) {
        isWaitingList = waitingList;
    }

    public  String getImage(){
        return image;
    }

    public void setImage(String _image){
        image = _image;
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocation() {
        return Location;
    }

    public void setLocation(int location) {
        Location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCurrentRegistered() {
        return currentRegistered;
    }

    public void setCurrentRegistered(JSONArray currentRegistered) {
        this.currentRegistered = String.valueOf(currentRegistered.length());
    }

/*    public String getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(String registeredUsers) {
        this.registeredUsers = registeredUsers;
    }*/

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }


}
