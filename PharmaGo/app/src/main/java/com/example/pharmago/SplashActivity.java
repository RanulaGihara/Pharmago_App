
package com.example.pharmago;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pharmago.model.User;
import com.example.pharmago.util.SharedPref;


/**
 * SplashActivity - Entry point of the app
 *
 * @author Ceylon Linux (pvt) Ltd
 */

public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor( ContextCompat.getColor(SplashActivity.this,
                   R.color.colorPrimary1));
        }

        if (isPermissionGranted())
            initialize();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    // <editor-fold defaultstate="collapsed" desc="Initialize">
    private void initialize() {


        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SharedPref pref = new SharedPref(SplashActivity.this);

                User user = pref.getStoredUser();

                if (user == null) {
                    Intent homeActivity = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(homeActivity);
                    finish();
                } else {
                    Intent homeActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(homeActivity);
                    finish();
                }


            }
        }.start();
    }
    // </editor-fold>


    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (
                    (checkSelfPermission(Manifest.permission.VIBRATE)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.BLUETOOTH)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.WAKE_LOCK)
                            == PackageManager.PERMISSION_GRANTED)) {
                Log.i("v", "Permission is granted");
                return true;
            } else {

                Log.i("v", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        //    Manifest.permission.READ_PHONE_STATE,
                        //    Manifest.permission.READ_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        //     Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.RECORD_AUDIO}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.i("v", "Permission is granted");
            return true;
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allow_go = true;

        for (int i = 0; i < permissions.length; i++) {
            Log.e("xx", permissions[i] + " " + grantResults[i]);

            if (grantResults[i] == -1) {
                allow_go = false;
            }
        }


        if (allow_go) {

            initialize();
        } else {
            Toast.makeText(SplashActivity.this, "Provide All Permissions", Toast.LENGTH_SHORT).show();
            isPermissionGranted();
            finish();
        }
    }
}
