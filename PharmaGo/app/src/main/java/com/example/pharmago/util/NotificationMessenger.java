package com.example.pharmago.util;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.pharmago.MainActivity;
import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.controller.NotificationController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.model.NotificationM;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotificationMessenger extends Service {
    private Timer timer;
    private SharedPref pref;
    private int mNotificationId = 0;
    private SharedPref sharedPref;
    private String CHANNEL_ID = "xxx";
    private Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer("Notification");
        pref = new SharedPref(NotificationMessenger.this);


        sharedPref = new SharedPref(NotificationMessenger.this);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    //     notificationGenerator("test", "content");
                    syncNotification();


                } catch (Exception e) {

                    Log.e("ERROR", e.toString());
                }
            }
        }, 0, 60000);


    }


    public void notificationGenerator(String title, String description) {


        Intent intent = new Intent(NotificationMessenger.this, MainActivity.class);

        Bundle mBundle = new Bundle();
        mBundle.putString("navPosition", "3");
        intent.putExtras(mBundle);


        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        String CHANNEL_ID = "my_channel_01";


        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationMessenger.this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

      //  Uri soundUri = Uri.parse("android.resource://" + NotificationMessenger.this.getPackageName() + "/" + R.raw.gesture);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "01")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setContentText(description)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationMessenger.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // For Oreo and greater than it, we required Notification Channel.
            CharSequence name = "My New Channel";                   // The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance); //Create Notification Channel
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(5 /* ID of notification */, notificationBuilder.build());


    }

    private void syncNotification() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    long userId = pref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("seenIds", NotificationController.getReadNotificationIds(NotificationMessenger.this).toString()));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "getNotifications", valuePairs);
                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.getBoolean("result")) {
                            ArrayList<NotificationM> notificationMS = NotificationM.getArrayListFromJsonArray(jsonObject.getJSONArray("notifications"));

                            NotificationController.saveNotification(notificationMS, NotificationMessenger.this);




                            boolean status = false;

                            for (NotificationM notificationM : notificationMS) {
                                if (!notificationM.getIsSeen() ) {
                                    status = true;
                                }
                            }


                            if (status) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notificationGenerator("ALERT", "YOU HAVE UNREAD NOTIFICATIONS");
                                    }
                                });


                            }
                        }


                    } catch (Exception e) {

                        Log.e("ERROR-E2", e.toString());

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }


            }
        }).start();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerTask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
