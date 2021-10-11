package com.example.pharmago.ui.notification;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.controller.NotificationController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.ui.IOnBackPressed;
import com.example.pharmago.ui.home.HomeFragment;
import com.example.pharmago.ui.order.OrderAcceptingFragment;
import com.example.pharmago.util.SharedPref;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotificationViewerFragment extends Fragment implements IOnBackPressed {

    private WebView webView;
    private String title, content;
    private Integer id;
    private Integer typeId;
    private String additional;
    private SharedPref sharedPref;
    private SweetAlertDialog pDialog;
    private MaterialButton buttonAccept, buttonReject;
    private Handler handler;
    private SweetAlertDialog dialog;


    public NotificationViewerFragment() {

    }

    public static NotificationViewerFragment getInstance() {

        return new NotificationViewerFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview_notification, container, false);
        webView = (WebView) rootView.findViewById(R.id.webView1);

        sharedPref = new SharedPref(getActivity());

        title = getArguments().getString("title");
        content = getArguments().getString("content");
        id = getArguments().getInt("id");
        typeId = getArguments().getInt("type");
        additional = getArguments().getString("additional");
        buttonAccept = rootView.findViewById(R.id.buttonAccept);
        buttonReject = rootView.findViewById(R.id.buttonReject);

        if (typeId == 1) {
            buttonAccept.setVisibility(View.VISIBLE);
            buttonReject.setVisibility(View.VISIBLE);
        }

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  placeOrder(1);
                Fragment fragment = OrderAcceptingFragment.getInstance();

                Bundle args = new Bundle();

                args.putString("additional", additional);
                fragment.setArguments(args);

                changeFragment(fragment, "");
            }
        });
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder(2);
            }
        });

        NotificationController.setSeenStatusUp(id, getActivity());


        loadWebview();


        return rootView;
    }


    private String getMonthlyItineraryHtml(String title, String content) {
        String html = "<html lang=\"en\" class=\"user_font_size_normal user_font_system\" style=\"padding: 0px; margin: 0px;\"><head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "\n" +
                "    <style type=\"text/css\">.MsgHeaderTable .Object{cursor:pointer;color:#005A95;text-decoration:none;cursor:pointer;white-space:nowrap;}\n" +
                ".MsgHeaderTable .Object-hover{cursor:pointer;color:#005A95;text-decoration:underline;white-space:nowrap;}\n" +
                ".MsgBody{background-color:#fff;-moz-user-select:element;-ms-user-select:element;}\n" +
                ".MsgBody-text{color:#333;font-family:monospace;word-wrap:break-word;}\n" +
                ".MsgBody-text,.MsgBody-html{padding:10px;}\n" +
                "div.MsgBody,div.MsgBody *{font-size:1.18rem;}\n" +
                "body.MsgBody{font-size:1.18rem;}\n" +
                ".MsgBody .SignatureText{color:gray;}\n" +
                ".MsgBody .QuotedText0{color:purple;}\n" +
                ".MsgBody .QuotedText1{color:green;}\n" +
                ".MsgBody .QuotedText2{color:red;}\n" +
                ".user_font_modern{font-family:\"Helvetica Neue\",Helvetica,Arial,\"Liberation Sans\",sans-serif;}\n" +
                ".user_font_classic{font-family:Tahoma,Arial,sans-serif;}\n" +
                ".user_font_wide{font-family:Verdana,sans-serif;}\n" +
                ".user_font_system{font-family:\"Segoe UI\",\"Lucida Sans\",sans-serif;}\n" +
                ".user_font_size_small{font-size:11px;}\n" +
                ".user_font_size_normal{font-size:12px;}\n" +
                ".user_font_size_large{font-size:14px;}\n" +
                ".user_font_size_larger{font-size:16px;}\n" +
                ".MsgBody .Object{color:#005A95;text-decoration:none;cursor:pointer;}\n" +
                ".MsgBody .Object-hover{color:#005A95;text-decoration:underline;}\n" +
                ".MsgBody .Object-active{color:darkgreen;text-decoration:underline;}\n" +
                ".MsgBody .FakeAnchor,.MsgBody a:link,.MsgBody a:visited{color:#005A95;text-decoration:none;cursor:pointer;}\n" +
                ".MsgBody a:hover{color:#005A95;text-decoration:underline;}\n" +
                ".MsgBody a:active{color:darkgreen;text-decoration:underline;}\n" +
                ".MsgBody .POObject{color:blue;}\n" +
                ".MsgBody .POObjectApproved{color:green;}\n" +
                ".MsgBody .POObjectRejected{color:red;}\n" +
                ".MsgBody .zimbraHide{display:none;}\n" +
                ".MsgBody-html pre,.MsgBody-html pre *{white-space:pre-wrap;word-wrap:break-word!important;text-wrap:suppress!important;}\n" +
                ".MsgBody-html tt,.MsgBody-html tt *{font-family:monospace;white-space:pre-wrap;word-wrap:break-word!important;text-wrap:suppress!important;}\n" +
                ".MsgBody .ZmSearchResult{background-color:#FFFEC4;}</style></head>" +
                "<body class=\"MsgBody MsgBody-html\" style=\"margin: 0px; height: auto;\">" +
                "<div style=\"\"><div><div style=\"font-family: roboto, sans-serif; border: 1px solid rgb(224, 224, 224); background-color: white; max-width: 600px; margin: 0px auto;\">" +
                "<div style=\"background-color: white; padding: 24px 0px;\"></div><table style=\"width: 100%; background-color: rgb(3, 155, 229);\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td style=\"padding: 24px;\"><div style=\"font-size: 20px; line-height: 24px; color: white;\"><div style=\"padding-top: 4px;\">" + title + "</div></div></td><td style=\"padding: 24px; text-align: right;\"></td></tr><tr></tr></tbody></table><div style=\"background: rgb(236, 239, 241); padding-top: 40px;\"><table style=\"border-spacing: 0px; border-collapse: separate; width: 85%; min-width: 300px; max-width: 400px; margin: 0px auto; padding: 0px; border: 0px; border-radius: 8px; overflow: hidden;\"><tbody><tr style=\"text-align: center; margin: 0px; padding: 0px; border: 0px;\" align=\"center\"><td style=\"border-top-left-radius: 8px; border-top-right-radius: 8px; font-family: &quot;helvetica neue&quot;, helvetica, arial, sans-serif; color: rgb(255, 255, 255); background: rgb(255, 179, 0); padding: 1.5em 0.75em; border-width: 1px 1px 0px; border-top-style: solid; border-right-style: solid; border-left-style: solid; border-top-color: rgb(215, 226, 233); border-right-color: rgb(215, 226, 233); border-left-color: rgb(215, 226, 233); border-image: initial; border-bottom-style: initial; border-bottom-color: initial;\"><div style=\"text-align: center; margin-bottom: 0.75em;\" align=\"center\"></div><div style=\"font-size: 22px; font-weight: 400;\">" + content + "</div><span class=\"Object\" role=\"link\" id=\"OBJ_PREFIX_DWT80_com_zimbra_url\"></span></td></tr><tr style=\"text-align: center; margin: 0px; padding: 0px; border: 0px;\" align=\"center\"><td style=\"border-bottom-left-radius: 8px;border-bottom-right-radius: 8px;font-family: &quot;helvetica neue&quot;, helvetica, arial, sans-serif;text-align: left;background: white;padding: 20px 15px;border-left: 1px solid rgb(236, 240, 241);border-right: 1px solid rgb(215, 226, 233);border-bottom: 20px solid rgb(236, 240, 241);\" align=\"left\"></td></tr></tbody></table></div><div style=\"\"><div><div style=\"font-family: arial, helvetica, sans-serif; font-size: 12pt; color: rgb(0, 0, 0);\"><br><br><div></div></div></div></div><div style=\"background-color: rgb(120, 144, 156); padding: 24px;\"></div></div></div></div></body></html>";
        return html;
    }


    private void loadWebview() {

        String data = getMonthlyItineraryHtml(title, content);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");


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

    private void changeFragment(Fragment fragment, String title) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
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

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                                    dialog.setTitleText("SUCCESS");
                                    dialog.setContentText("");
                                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            changeFragment(HomeFragment.getInstance(), "Home");
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

}
