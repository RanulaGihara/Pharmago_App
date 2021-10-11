package com.example.pharmago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.pharmago.controller.BaseController;
import com.example.pharmago.controller.UserController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.model.User;
import com.example.pharmago.util.NetworkUtil;
import com.example.pharmago.util.SharedPref;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Button loginButton,buttonSignUp;
    private MaterialAlertDialogBuilder alertDialog;
    private Handler handler;
    private SweetAlertDialog dialog;
    private TextInputEditText text_password, text_username;
    private SharedPref sharedPref;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text_password = findViewById(R.id.text_password);
        text_username = findViewById(R.id.text_username);

        loginButton = findViewById(R.id.loginButton);
        buttonSignUp= findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(LoginActivity.this)) {

                    alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                    alertDialog.setTitle("No Connection");
                    alertDialog.setMessage("Please enable mobile data or WIFI");

                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();


                } else {

                    Intent i = new Intent(LoginActivity.this,UserRegistrationActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtil.isNetworkAvailable(LoginActivity.this)) {

                    alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                    alertDialog.setTitle("No Connection");
                    alertDialog.setMessage("Please enable mobile data or WIFI");

                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();


                } else {

                    syncData();
                }
            }
        });
    }


    private void syncData() {
        handler = new Handler();
        dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Authenticating...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String response = UserController.authenticate(text_username.getText().toString().trim(), text_password.getText().toString().trim());

                //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {

                            User user = User.getUserByJson(object.getJSONObject("user"));
                            user.setPassword(text_password.getText().toString().trim());
                            sharedPref = new SharedPref(LoginActivity.this);
                            sharedPref.storeUser(user);


                            synchronizeData();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                                    alertDialog.setTitle("ERROR");

                                    try {

                                        alertDialog.setMessage(object.getString("message"));

                                    } catch (Exception e) {
                                        alertDialog.setMessage("Invalid response from the server!");
                                    }

                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                                alertDialog.setTitle("ERROR");
                                alertDialog.setMessage("Invalid response from the server!");

                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertDialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                            alertDialog.setTitle("ERROR");
                            alertDialog.setMessage("Something went wrong");

                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }

    private void synchronizeData() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.setTitle("Itinerary Details are downloading.....");
            }
        });


        try {
            List<CustomNameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new CustomNameValuePair("userId", sharedPref.getStoredUser().getUserId() + ""));
            String response = BaseController.postToServerGzip(BaseController.baseURL + "route_plan", valuePairs);
            try {
                JSONObject jsonObject = new JSONObject(response);


                //   Toast.makeText(LoginActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();

                if (jsonObject.getBoolean("result")) {

                }

            } catch (Exception e) {

                Log.e("ERROR-E1", e.toString());

            }
        } catch (Exception e) {
            Log.e("ERROR-E1", e.toString());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    private void loginFunctionx(final String username, final String password) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Query myTopPostsQuery = mDatabase.child("pharmagoDB").child("users").orderByChild("username").equalTo(username);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    user = postSnapshot.getValue(User.class);

                    Log.d("II", "onChildAdded:" + dataSnapshot.getKey());
                }


                if (user != null) {
                    if (user.getPassword().equals(password)) {


                        sharedPref = new SharedPref(LoginActivity.this);
                        sharedPref.storeUser(user);

                        if (user.getType() == User.USER) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                        }

                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE).
                                setTitleText("ERROR").setContentText("Password is wrong").show();
                    }

                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE).
                            setTitleText("ERROR").setContentText("User not found").show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }




}