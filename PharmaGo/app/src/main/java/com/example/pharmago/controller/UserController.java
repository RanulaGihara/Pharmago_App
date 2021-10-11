package com.example.pharmago.controller;
import android.util.Log;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.util.SharedPref;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserController extends BaseController {

    private static SharedPref pref;
    private static final String LOG_TAG = UserController.class.getSimpleName();

    public static String authenticate(String username, String password) throws IOException {

        List<CustomNameValuePair> params = new ArrayList<>();
        params.add(new CustomNameValuePair("username", username));
        params.add(new CustomNameValuePair("password", password));

        Log.d(LOG_TAG, "Authenticating");

        return postToServerGzip(baseURL + "user", params);
    }




}
