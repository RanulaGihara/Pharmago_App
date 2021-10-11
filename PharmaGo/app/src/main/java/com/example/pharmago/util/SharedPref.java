package com.example.pharmago.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pharmago.model.User;
import com.google.gson.Gson;


public class SharedPref {
    private static final String LOG_TAG = SharedPref.class.getSimpleName();

    private Context context;

    private static SharedPreferences sharedPref;


    public SharedPref(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);


    }

    public void storeUser(User user) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user); // myObject - instance of TargetAchievement
        editor.putString("user", json);
        editor.commit();
    }


    public User getStoredUser() {
        User user = null;
        Gson gson = new Gson();

        try {

            if (sharedPref.contains("user")) {

                String json = sharedPref.getString("user", "{}");
                user = gson.fromJson(json, User.class);
            }

        } catch (Exception e) {
            Log.e(SharedPref.class.getSimpleName(), e.toString());
        }

        return user;

    }

    public void clearPref() {
        sharedPref.edit().clear().commit();
    }
}
