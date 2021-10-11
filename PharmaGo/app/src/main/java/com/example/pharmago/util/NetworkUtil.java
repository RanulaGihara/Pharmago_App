package com.example.pharmago.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by TaZ on 2/5/15.
 * Handles network based common functions.
 */
public class NetworkUtil {

    public static final int SOCKET_TIMEOUT_MS=30000;

    /**
     * Function to check if network connectivity is available.
     * @param context The context of the application.
     * @return TRUE if network connectivity is available and FALSE if not.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}
