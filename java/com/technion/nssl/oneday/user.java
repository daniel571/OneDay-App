package com.technion.nssl.oneday;

import org.json.JSONArray;

public class user {
    private String fb_userID;
    private String firstName;
    private String lastName;
    private String phone;
    private int city;
    private int isStudent;
    private int isCarPool;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender.equals("M"))
            this.gender = "M";
        else this.gender = "F";
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    private String occupation; //fixme maybe not needed - make sure with Noam.
    private String gender; //fixme check with Ido why it's missing

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    private String Email; // fixme check with Ido why it's missing - we will need it to sync with calendar
    private String birthYear; //fixme spelling mistake to Ido. need fb review
    private JSONArray preferences;
    private int eventCounter;
    private int hourCounter;
    private boolean isStaff;
    private boolean isAdmin;

    public int isCarPool() {
        return isCarPool;
    }

    public void setCarPool(int carPool) {
        isCarPool = carPool;
    }

    //private String tbl_userscol; //fixme ask Ido what is it ?

    public user() {

    }

    public user(String fb_userID, String firstName, String lastName, String phone, int city, int isStudent, String birthYear, JSONArray preferences, int eventCounter, int hourCounter, boolean isStaff, boolean isAdmin) {
        this.fb_userID = fb_userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
        this.isStudent = isStudent;
        this.birthYear = birthYear;
        this.preferences = preferences;
        this.eventCounter = eventCounter;
        this.hourCounter = hourCounter;
        this.isStaff = isStaff;
        this.isAdmin = isAdmin;
    }

    public String getFb_userID() {
        return fb_userID;
    }

    public void setFb_userID(String fb_userID) {
        this.fb_userID = fb_userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int isStudent() {
        return isStudent;
    }

    public void setStudent(int student) {
        isStudent = student;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public JSONArray getPreferences() {
        return preferences;
    }

    public void setPreferences(JSONArray preferences) {
        this.preferences = preferences;
    }

    public int getEventCounter() {
        return eventCounter;
    }

    public void setEventCounter(int eventCounter) {
        this.eventCounter = eventCounter;
    }

    public int getHourCounter() {
        return hourCounter;
    }

    public void setHourCounter(int hourCounter) {
        this.hourCounter = hourCounter;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void deep_copy(user from_user){
        this.setFb_userID(from_user.getFb_userID());
        if (from_user.getBirthYear() != null) this.setBirthYear(from_user.getBirthYear());
        if (from_user.getOccupation() != null) this.setOccupation(from_user.getOccupation());
        if (from_user.getCity()!=0) this.setCity(from_user.getCity());
        if (from_user.getEmail()!=null) this.setEmail(from_user.getEmail());
        this.setEventCounter(from_user.getEventCounter());
        if (from_user.getFirstName()!=null) this.setFirstName(from_user.getFirstName());
        if (from_user.getGender()!=null) this.setGender(from_user.getGender());
        this.setHourCounter(from_user.getHourCounter());
        if (from_user.getLastName()!=null) this.setLastName(from_user.getLastName());
        this.setStudent(from_user.isStudent());
        if (from_user.getPhone()!=null) this.setPhone(from_user.getPhone());
        this.setStaff(from_user.isStaff());
        if (from_user.getPreferences() != null) this.setPreferences(from_user.getPreferences());
        this.setAdmin(from_user.isAdmin());
        this.setCarPool(from_user.isCarPool());
        if (from_user.getFirstName() != null) this.setFirstName(from_user.getFirstName());
        if (from_user.getLastName() != null ) this.setLastName(from_user.getLastName());
    }

}
