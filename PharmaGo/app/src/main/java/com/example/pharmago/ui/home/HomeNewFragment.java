package com.example.pharmago.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pharmago.ChatActivity;
import com.example.pharmago.MainActivity;
import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.util.BitmapUtility;
import com.example.pharmago.util.SharedPref;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeNewFragment extends Fragment {

    private Button chatWithBotButton, uploadPresButton;
    private static final int GALLERY_IMAGE_REQ_CODE = 102;
    private Handler handler;
    private SweetAlertDialog dialog;
    private SharedPref pref;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_home, container, false);
        chatWithBotButton = root.findViewById(R.id.chatWithBotButton);
        uploadPresButton = root.findViewById(R.id.uploadPresButton);

        chatWithBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        uploadPresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickGalleryImage(v);
            }
        });

        pref = new SharedPref(getActivity());

        return root;
    }

    public static HomeNewFragment getInstance() {
        return new HomeNewFragment();
    }

    public void pickGalleryImage(View view) {
        ImagePicker.with(this)
                // Crop Image(User can choose Aspect Ratio)
                .crop()
                // User can only select image from Gallery
                .galleryOnly()

                .galleryMimeTypes(new String[]{"image/png",
                        "image/jpg",
                        "image/jpeg"
                })
                // Image resolution will be less than 1080 x 1920
                .maxResultSize(1080, 1920)
                // .saveDir(getExternalFilesDir(null))
                .start(GALLERY_IMAGE_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            Uri uri = data.getData();

            switch (requestCode) {

                case GALLERY_IMAGE_REQ_CODE:
                    bitmap = BitmapUtility.uriToBitmap(getActivity(), uri);
                    Log.i("text", "text");
                    uploadImage(bitmap);
                    break;

            }
        }
    }

    private void uploadImage(final Bitmap bitmap) {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Processing Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ArrayList<CustomNameValuePair> valuePairs = new ArrayList<>();
                    long userId = pref.getStoredUser().getUserId();
                    valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                    valuePairs.add(new CustomNameValuePair("imageString", BitmapUtility.convert(bitmap)));
                    valuePairs.add(new CustomNameValuePair("timestamp", System.currentTimeMillis() + ""));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "imageUpload", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    Log.e("RESP", response);

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    try {

                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra("navPosition", "2");
                                        intent.putExtra("content",object.getJSONArray("data").toString());
                                        intent.putExtra("originalContent",object.getString("originalContent"));
                                        startActivity(intent);
                                        getActivity().finish();

                                    } catch (Exception e) {
                                        Log.e("ERROR x", e.toString());
                                    }
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