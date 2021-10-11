package com.example.pharmago.util;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.pm.PackageInfoCompat;

import com.example.pharmago.Update_activity;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.CustomNameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Update_manger extends Service {

    private Timer timer;
    private SharedPref sharedPref;
    private Handler handler;


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        sharedPref = new SharedPref(Update_manger.this);
        timer = new Timer("Updater");
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (NetworkUtil.isNetworkAvailable(Update_manger.this)) {

                    downloadApp();

                }

            }

        }, 0, 300000);// within one hour 3600000

    }


    private void downloadApp() {


        try {

            List<CustomNameValuePair> valuePairs = new ArrayList<>();
            try {
                PackageInfo pInfo = Update_manger.this.getPackageManager().getPackageInfo(Update_manger.this.getPackageName(), 0);
                long longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo);
                int versionCode = (int) longVersionCode;

                String version = versionCode + "";


                valuePairs.add(new CustomNameValuePair("apk_type", "1"));
                valuePairs.add(new CustomNameValuePair("apk_version", version));


            } catch (PackageManager.NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String response = BaseController.postToServerGzip(BaseController.baseURL + "getLatestApp", valuePairs);
            //     String response = BaseController.postToServerGzipWithToken(sharedPref.getStoredUser().getToken(), "https://run.mocky.io/v3/23a95651-cc4d-4774-99a6-5d68c01efca8", valuePairs);
            //https://my-json-server.typicode.com/lahiru04/json/update
            try {
                final JSONObject jsonObject = new JSONObject(response);


                //   Toast.makeText(LoginActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();

                if (jsonObject.getBoolean("result")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String url = jsonObject.getString("url");

                                Intent intent = new Intent(Update_manger.this,
                                        Update_activity.class);
                                intent.putExtra("url", String.valueOf(url));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e("ERROR", e.toString());
                            }
                        }
                    });

                }

            } catch (Exception e) {

                Log.e("ERROR-E1", e.toString());

            }
        } catch (Exception e) {
            Log.e("ERROR-E1", e.toString());
        }


    }


}
