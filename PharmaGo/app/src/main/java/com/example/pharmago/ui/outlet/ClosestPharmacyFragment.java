package com.example.pharmago.ui.outlet;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.pharmago.LoginActivity;
import com.example.pharmago.MainActivity;
import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.controller.UserController;
import com.example.pharmago.dialog.OptionSelectingDialog;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.model.LocationSer;
import com.example.pharmago.model.User;
import com.example.pharmago.ui.home.HomeFragment;
import com.example.pharmago.ui.home.HomeNewFragment;
import com.example.pharmago.util.BitmapUtility;
import com.example.pharmago.util.SharedPref;
import com.example.pharmago.util.SingleShotLocationProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lapra.toast.custoast.CusToast;

public class ClosestPharmacyFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private MaterialButton button, buttonPlaceOrder;
    private DatabaseReference mDatabase;
    private LocationSer locationx;
    private String productData = "";
    private SharedPref pref;
    private long pharmaId = 0;
    private TextView textSelectedPharmacy;
    private String originalContent;

    public ClosestPharmacyFragment(String productData, String originalContent) {
        this.productData = productData;
        this.originalContent = originalContent;
    }

    public static ClosestPharmacyFragment getInstance(String productData, String originalContent) {
        return new ClosestPharmacyFragment(productData, originalContent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_outlet_detail, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        button = rootView.findViewById(R.id.button);
        buttonPlaceOrder = rootView.findViewById(R.id.buttonPlaceOrder);
        textSelectedPharmacy = rootView.findViewById(R.id.textSelectedPharmacy);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        pref = new SharedPref(getActivity());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    findClosestPharmecy();
                OptionSelectingDialog dialog = new OptionSelectingDialog(getActivity());
                dialog.setOnOkButtonClick(new OptionSelectingDialog.onReasonDialogButtonClick() {
                    @Override
                    public void onClick(OptionSelectingDialog dialog, int option) {
                        // Toast.makeText(getActivity(), "xxx " + option, Toast.LENGTH_SHORT).show();

                        if (option == 2) {
                            if (productData.length() > 0) {
                                findMostRatedPharmacy();
                            } else {
                                CusToast.makeText(getActivity(), "Products not found", Toast.LENGTH_SHORT, 16f).show();
                            }


                        } else if (option == 1) {
                            if (productData.length() > 0) {
                                findLowestCostPharmacy();
                            } else {
                                CusToast.makeText(getActivity(), "Products not found", Toast.LENGTH_SHORT, 16f).show();
                            }
                        }


                    }
                });
                dialog.setOnCancelButtonClick(new OptionSelectingDialog.onReasonDialogButtonClick() {
                    @Override
                    public void onClick(OptionSelectingDialog dialog, int option) {

                    }
                });

                dialog.show();

            }
        });

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
        captureLocation(getActivity());
        return rootView;
    }


    @Override
    public void onResume() {
        if (mapView != null)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(false);
        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(false);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        handler = new Handler();

        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        while (locationx == null) {
//
//                            captureLocation(getActivity());
//                        }
                    }
                });


            }
        }).start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismissWithAnimation();
                            if (locationx != null) {


                                getPharmacies(locationx.getLatitude(), locationx.getLongitude());
                            } else {


                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Location not found");
                                dialog.show();
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
                        }
                    }
                });

            }
        }, 5000);


    }

    User min = null;
    ArrayList<User> users;

    public void findClosestPharmacy() {
        captureLocation(getActivity());
        HashMap<User, Double> hashMap = new HashMap<>();


        if (locationx != null) {
            for (User user : users) {


                hashMap.put(user, distance(locationx.getLatitude(), user.getLat(), locationx.getLongitude(), user.getLon(), 0, 0));

            }

        }


        double disMin = 0;

        for (Map.Entry<User, Double> entry : hashMap.entrySet()) {
            //  System.out.println(entry.getKey() + "/" + entry.getValue());

            if (disMin == 0) {
                disMin = entry.getValue();
                min = entry.getKey();
            }

            if (entry.getValue() < disMin) {
                disMin = entry.getValue();
                min = entry.getKey();
            }
        }


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                if (min != null) {
                    pharmaId = min.getUserId();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(min.getLat(), min.getLon()), 13));
                    if (productData.length() > 0)
                        buttonPlaceOrder.setEnabled(true);
                }
            }
        });

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


    private Handler handler;
    private SweetAlertDialog dialog;

    private void getPharmacies(final double lat, final double lon) {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Downloading Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("lat", lat + ""));
                    valuePairs.add(new CustomNameValuePair("lon", lon + ""));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "pharmacies", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {

                            users = User.getUsersByJson(object.getJSONArray("users"));


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();


                                    mapView.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap map) {
                                            double latM = 0;
                                            double lonM = 0;
                                            for (User user : users) {
                                                map.addMarker(new MarkerOptions()
                                                        .position(new LatLng(user.getLat(), user.getLon()))
                                                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtility.getScaledBitmap(BitmapUtility.getMarkerBitmapFromView(user.getName().substring(0, 1), getActivity()), 96, 96))).title(user.getName()));
                                                latM = latM + user.getLat();
                                                lonM = lonM + user.getLon();
                                            }


                                            if (users.size() > 0) {
                                                latM = latM / users.size();
                                                lonM = lonM / users.size();
                                            }


                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latM, lonM), 10));
                                        }
                                    });
                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                    dialog.setTitleText("ERROR");

                                    try {

                                        dialog.setContentText(object.getString("message"));

                                    } catch (Exception e) {
                                        dialog.setContentText("Invalid response from the server!");
                                    }

                                    dialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Something went wrong");
                                dialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("ERROR");
                            dialog.setContentText("Something went wrong");
                            dialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }


    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));// get tan value
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private void findLowestCostPharmacy() {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Uploading Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("jsonArray", productData + ""));
                    long userId = pref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("lat", locationx.getLatitude() + ""));
                    valuePairs.add(new CustomNameValuePair("lon", locationx.getLongitude() + ""));

                    String response = BaseController.postToServerGzip(BaseController.baseURL + "findLowestCostPharmacy", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    textSelectedPharmacy.setText("");
                                    try {
                                        textSelectedPharmacy.setText("Selected Pharmacy :" + object.getString("pharmacyName"));
                                    } catch (Exception e) {
                                        Log.e("ERROR", e.toString());
                                    }

                                    mapView.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap map) {
                                            try {

                                                pharmaId = object.getInt("pharmacyId");

                                                for (User user : users) {
                                                    if (user.getUserId() == pharmaId) {
                                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.getLat(), user.getLon()), 13));
                                                        buttonPlaceOrder.setEnabled(true);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                Log.e("ERROR", e.toString());
                                            }
                                        }
                                    });
                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                    dialog.setTitleText("ERROR");

                                    try {

                                        dialog.setContentText(object.getString("message"));

                                    } catch (Exception e) {
                                        dialog.setContentText("Invalid response from the server!");
                                    }

                                    dialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Something went wrong");
                                dialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("ERROR");
                            dialog.setContentText("Something went wrong");
                            dialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }

    private void findMostRatedPharmacy() {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Uploading Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("jsonArray", productData + ""));
                    long userId = pref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("lat", locationx.getLatitude() + ""));
                    valuePairs.add(new CustomNameValuePair("lon", locationx.getLongitude() + ""));

                    String response = BaseController.postToServerGzip(BaseController.baseURL + "findMostRatedPharmacy", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textSelectedPharmacy.setText("");
                                    dialog.dismiss();
                                    try {
                                        textSelectedPharmacy.setText("Selected Pharmacy :" + object.getString("pharmacyName"));
                                    } catch (Exception e) {
                                        Log.e("ERROR", e.toString());
                                    }

                                    mapView.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap map) {
                                            try {

                                                pharmaId = object.getInt("pharmacyId");

                                                for (User user : users) {
                                                    if (user.getUserId() == pharmaId) {
                                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.getLat(), user.getLon()), 13));
                                                        buttonPlaceOrder.setEnabled(true);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                Log.e("ERROR", e.toString());
                                            }
                                        }
                                    });
                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                    dialog.setTitleText("ERROR");

                                    try {

                                        dialog.setContentText(object.getString("message"));

                                    } catch (Exception e) {
                                        dialog.setContentText("Invalid response from the server!");
                                    }

                                    dialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Something went wrong");
                                dialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("ERROR");
                            dialog.setContentText("Something went wrong");
                            dialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }

    private void placeOrder() {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Uploading Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("jsonArray", productData + ""));
                    long userId = pref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("pharmacyId", pharmaId + ""));
                    valuePairs.add(new CustomNameValuePair("originalContent", originalContent));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "placeOrder", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                                    dialog.setTitleText("SUCCESS");
                                    dialog.setContentText("ORDER SUCCESSFULLY PLACED");
                                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            changeFragment(HomeNewFragment.getInstance());
                                        }
                                    });
                                    dialog.show();

                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                    dialog.setTitleText("ERROR");

                                    try {

                                        dialog.setContentText(object.getString("message"));

                                    } catch (Exception e) {
                                        dialog.setContentText("Invalid response from the server!");
                                    }

                                    dialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Something went wrong");
                                dialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("ERROR");
                            dialog.setContentText("Something went wrong");
                            dialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }


    private void changeFragment(Fragment fragment) {


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
