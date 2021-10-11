package com.example.pharmago.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Serializable {


    public static final int PHARMACY = 1;
    public static final int USER = 2;

    public static final int SPC = 1;
    public static final int RATED = 2;

    private long userId;
    private String name, password, username, address;
    private int type, rate;
    private double lat, lon;
    private int pharmacyType;

    public User() {
    }

    public User(long userId, String name, String password, String username, String address, int type, int rate, double lat, double lon, int pharmacyType) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.username = username;
        this.address = address;
        this.type = type;
        this.rate = rate;
        this.lat = lat;
        this.lon = lon;
        this.pharmacyType = pharmacyType;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public static User getUserByJson(JSONObject jsonObject) {
        User user = null;
        try {
            user = new User(jsonObject.getLong("userId"), jsonObject.getString("name"),
                    jsonObject.getString("password"), jsonObject.getString("username"), jsonObject.getString("address"), jsonObject.getInt("type")
                    , jsonObject.getInt("rate"), Double.parseDouble(jsonObject.getString("lat").trim()), Double.parseDouble(jsonObject.getString("lon")), jsonObject.getInt("pharmacyType"));


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            user = null;
        }
        return user;
    }

    public static JSONObject getUserAsJson(User user) {
        JSONObject object = new JSONObject();

        try{
            object.put("userId",user.getUserId());
            object.put("name",user.name);
            object.put("password",user.getPassword());
            object.put("username",user.getUsername());
            object.put("address",user.getAddress());
            object.put("type",user.getType());
            object.put("rate",user.getRate());
            object.put("lat",user.getLat());
            object.put("lon",user.getLon());
            object.put("pharmacyType",user.getPharmacyType());
        }catch (Exception e)
        {
            Log.e("ERROR",e.toString());
        }

        return object;
    }


    public static ArrayList<User> getUsersByJson(JSONArray jsonArray) throws JSONException {
        ArrayList<User> users = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject object = jsonArray.getJSONObject(i);

            User user = User.getUserByJson(object);

            users.add(user);

        }

        return users;
    }

    public int getPharmacyType() {
        return pharmacyType;
    }

    public void setPharmacyType(int pharmacyType) {
        this.pharmacyType = pharmacyType;
    }
}
