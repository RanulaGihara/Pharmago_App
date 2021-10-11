package com.example.pharmago;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.model.LocationSer;
import com.example.pharmago.model.User;
import com.example.pharmago.util.SingleShotLocationProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class UserRegistrationActivity extends AppCompatActivity {
    private LocationSer locationx;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference("pharmagoDB");

    private TextInputEditText textName,
            textUserName, textPassword,
            textRepass, textAddress, textRate;

    private FloatingActionButton floating_action_button;
    private RadioGroup radioGroupOption;

    private TextView textTitle;
    private MaterialSpinner categorySpinner;
    private ArrayAdapter<HashMap<String, String>> adapterCategory;
    private int category = -1;
    private int rate;
    private TextInputLayout rateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        textTitle = findViewById(R.id.textTitle);
        textTitle.setText("User Registration");

        textName = findViewById(R.id.textName);
        textUserName = findViewById(R.id.textUserName);
        textPassword = findViewById(R.id.textPassword);
        textRepass = findViewById(R.id.textRepass);
        textAddress = findViewById(R.id.textAddress);
        textRate = findViewById(R.id.textRate);
        radioGroupOption = findViewById(R.id.radioGroupOption);

        categorySpinner = findViewById(R.id.categorySpinner);

        floating_action_button = findViewById(R.id.floating_action_button);

        rateLayout = findViewById(R.id.rateLayout);
        ArrayList<HashMap<String, String>> hashMaps = getUserTypes();

        adapterCategory = new ArrayAdapter<HashMap<String, String>>(UserRegistrationActivity.this, android.R.layout.simple_spinner_item, hashMaps);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapterCategory);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == -1) return;

                try {
                    HashMap<String, String> hashMap = (HashMap<String, String>) parent.getItemAtPosition(position);

                    Integer integer = Integer.parseInt(hashMap.get("categoryId"));

                    category = integer;

                    if (category == User.PHARMACY) {
                        radioGroupOption.setVisibility(View.VISIBLE);
                        rateLayout.setVisibility(View.VISIBLE);
                    } else {
                        radioGroupOption.setVisibility(View.GONE);
                        rateLayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        floating_action_button.setImageResource(R.drawable.ic_baseline_add_24);

        floating_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    rate = Integer.parseInt(textRate.getText().toString().trim());
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
                captureLocation(UserRegistrationActivity.this);
                if (textName.getText().toString().trim().equals("") ||
                        textUserName.getText().toString().trim().equals("") || textPassword.getText().toString().trim().equals("") ||
                        textRepass.getText().toString().trim().equals("") || textAddress.getText().toString().trim().equals("") || (category == -1)) {

                    new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.ERROR_TYPE).
                            setTitleText("ERROR").setContentText("All Fields Are Mandatory").show();

                } else {
                    if (textPassword.getText().toString().trim().equals(textRepass.getText().toString())) {

                        try {

                            if (locationx != null) {

                                int pharmacyType = 1;

                                if (radioGroupOption.getCheckedRadioButtonId() == R.id.radioSpc) {
                                    pharmacyType = 1;
                                } else {
                                    pharmacyType = 2;
                                }

                                User user = new User(System.currentTimeMillis(), textName.getText().toString().trim(), textPassword.getText().toString().trim(),
                                        textUserName.getText().toString().trim(), textAddress.getText().toString().trim(), category, rate, locationx.getLatitude(), locationx.getLongitude(), pharmacyType);

                                addUser(user);


                            } else {
                                SweetAlertDialog dialog = new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").
                                        setContentText("Try again, location coordinates not found");
                                dialog.show();

                            }
                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
                        }
                    } else {
                        new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.ERROR_TYPE).
                                setTitleText("ERROR").setContentText("Passwords are not matching").show();


                        //textRepass.set
                    }
                }
            }
        });
        captureLocation(UserRegistrationActivity.this);

    }

    Handler handler = new Handler();

    private SweetAlertDialog dialog;
    private boolean status = false;

    private void addUser(final User user) {
        //    mDatabase.child("users").child(user.getUserId() + "").setValue(user);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        dialog.setTitleText("UPLOADING DATA...");
                        dialog.show();
                    }
                });

                try {


                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("jsonString", User.getUserAsJson(user).toString()));
                    String response = BaseController.postToServerGzipWithToken("", BaseController.baseURL + "create_user", valuePairs);

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("result")) {

                        Log.e("ERROR", jsonObject.toString());
                        status = true;
                    }


                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissWithAnimation();

                        if (status) {
                            SweetAlertDialog dialog = new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("SUCCESS").setContentText("Registration is completed");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Intent intent = new Intent(UserRegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                        } else {
                            SweetAlertDialog dialog = new SweetAlertDialog(UserRegistrationActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("ERROR").setContentText("Something went wrong!");
                            dialog.show();
                        }
                    }
                });

            }
        });

        t.start();


    }


    private ArrayList<HashMap<String, String>> getUserTypes() {
        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();




        HashMap<String, String> hashMap1 = new HashMap<String, String>() {
            @NonNull
            @Override
            public String toString() {
                return super.get("category");
            }
        };

        hashMap1.put("categoryId", "1");
        hashMap1.put("category", "PHARMACY");
        hashMaps.add(hashMap1);


        HashMap<String, String> hashMap2 = new HashMap<String, String>() {
            @NonNull
            @Override
            public String toString() {
                return super.get("category");
            }
        };

        hashMap2.put("categoryId", "2");
        hashMap2.put("category", "USER");
        hashMaps.add(hashMap2);
        return hashMaps;

    }

    public void captureLocation(Context context) {


        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates locationxy) {
                        //      Log.d("Location", "my location is " + location.latitude+" "+location.longitude);

                        Location location = locationxy.location;

                        if (location != null) {
                            locationx = new LocationSer();
                            locationx.setLatitude(location.getLatitude());
                            locationx.setLongitude(location.getLongitude());
                            locationx.setAccuracy(location.getAccuracy());
                            locationx.setBearing(location.getBearing());
                            locationx.setSpeed(location.getSpeed());
                            locationx.setProvider(location.getProvider());
                        }

                    }
                });
    }
}