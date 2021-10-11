package com.example.pharmago.ui.order;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.ui.IOnBackPressed;
import com.example.pharmago.ui.home.HomeFragment;
import com.example.pharmago.ui.notification.NotificationFragment;
import com.example.pharmago.ui.notification.NotificationViewerFragment;
import com.example.pharmago.util.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderAcceptingFragment extends Fragment implements IOnBackPressed {

    private TextView textOrderDetail, textPrescription;
    private String additional;
    private Handler handler;
    private SweetAlertDialog dialog;
    private SharedPref sharedPref;

    public static OrderAcceptingFragment getInstance() {

        return new OrderAcceptingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        textOrderDetail = rootView.findViewById(R.id.textOrderDetail);
        textPrescription = rootView.findViewById(R.id.textPrescription);

        additional = getArguments().getString("additional");
        sharedPref = new SharedPref(getActivity());

        placeOrder(1);


        return rootView;
    }

    @Override
    public boolean onBackPressed() {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Back")
                .setContentText("Do You Really Want To Go Back?")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        changeFragment(NotificationFragment.getInstance(), "Notifications");


                    }
                }).setCancelText("CANCEL").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        })
                .show();


        return true;
    }

    private void placeOrder(int status) {
        /// i- approve 2- reject
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
                    long userId = sharedPref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("orderId", additional + ""));
                    valuePairs.add(new CustomNameValuePair("status", status + ""));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "approveOrder", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();

                                    try {
                                        String prescription = object.getString("prescription");
                                        String orderDetail = object.getString("orderDetail");

                                        textOrderDetail.setText(orderDetail);
                                        textPrescription.setText(prescription);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                                    dialog.setTitleText("SUCCESS");
                                    dialog.setContentText("");
                                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            // changeFragment(HomeFragment.getInstance(), "Home");
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

    private void changeFragment(Fragment fragment, String title) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
