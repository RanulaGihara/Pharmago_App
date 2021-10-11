package com.example.pharmago;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmago.adapter.ChatMessageAdapter;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.ChatMessage;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.util.BitmapUtility;
import com.example.pharmago.util.SharedPref;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChatActivity extends AppCompatActivity {

    private static final int GALLERY_IMAGE_REQ_CODE = 102;
    private static final int CAMERA_IMAGE_REQ_CODE = 103;

    public TextToSpeech t1, t2;


    private TextView txtSpeechInput;
    private FloatingActionButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;

    private ChatMessageAdapter mAdapter;
    private Handler handler;
    private SharedPref pref;
    private SweetAlertDialog dialog;

    private FloatingActionButton fabCamera, fabGallery;


    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        handler = new Handler();
        pref = new SharedPref(ChatActivity.this);

        File dir1 = new File(Environment.getExternalStorageDirectory() + "bald");
        if (dir1.isDirectory()) {
            String[] children = dir1.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir1, children[i]).delete();
            }
        }


        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = findViewById(R.id.btn_send);
        mButtonSend.setImageResource(R.drawable.ic_menu_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        txtSpeechInput = findViewById(R.id.txtSpeechInput);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setImageResource(R.drawable.ic_baseline_mic_24);

        fabCamera = findViewById(R.id.fabCamera);
        fabGallery = findViewById(R.id.fabGallery);


        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCameraImage(v);
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickGalleryImage(v);
            }
        });


        //hide the action bar
        //getActionBar().hide();   Our Nigga

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                promptSpeechInput();
            }
        });


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                //bot

                //    String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<CustomNameValuePair> valuePairs = new ArrayList<>();
                            valuePairs.add(new CustomNameValuePair("chatInput", message + ""));
                            long userId = pref.getStoredUser().getUserId();
                            valuePairs.add(new CustomNameValuePair("userId", userId + ""));
                            String response = BaseController.postToServerGzip(BaseController.baseURL + "chat", valuePairs);
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                String val = jsonObject.getString("chatBotReply");

                                JSONObject object = new JSONObject(val);

                                String responseChatbot = object.getString("content");

                                String originalContent = object.getString("originalContent");


                                int type = object.getInt("type");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        sendMessage(message);
                                        mimicOtherMessage(responseChatbot);
                                        mEditTextMessage.setText("");
                                        mListView.setSelection(mAdapter.getCount() - 1);

//my message
                                        //Toast.makeText(getApplicationContext(),"Res : "+response,Toast.LENGTH_LONG).show();
                                        t1.speak(responseChatbot, TextToSpeech.QUEUE_FLUSH, null);

                                        if (type == 2) {

                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                                                                intent.putExtra("navPosition", "2");
                                                                intent.putExtra("content", object.getJSONArray("data").toString());
                                                                intent.putExtra("originalContent",originalContent);
                                                                startActivity(intent);
                                                                finish();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });

                                                }
                                            }, 2000);


                                        } else if (type == 3) {

                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                pickGalleryImage(v);
                                                            } catch (Exception e) {
                                                                Log.e("ERROR", e.toString());
                                                            }
                                                        }
                                                    });

                                                }
                                            }, 2000);


                                        }


                                    }
                                });


                            } catch (Exception e) {

                                Log.e("ERROR-E1", e.toString());

                            }
                        } catch (Exception e) {
                            Log.e("ERROR-E1", e.toString());
                        }
                    }
                }).start();


                //Toast.makeText(getApplicationContext(),"list : "+mListView.getCount(),Toast.LENGTH_LONG).show();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

    //voice output
    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();


    }


    //speech start
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            Uri uri = data.getData();

            switch (requestCode) {

                case GALLERY_IMAGE_REQ_CODE:
                    bitmap = BitmapUtility.uriToBitmap(ChatActivity.this, uri);
                    Log.i("text", "text");
                    uploadImage(bitmap);
                    break;
                case CAMERA_IMAGE_REQ_CODE:
                    bitmap = BitmapUtility.uriToBitmap(ChatActivity.this, uri);
                    Log.i("text", "text");
                    uploadImage(bitmap);
                    //  ImageViewExtensionKt.setLocalImage(imgCamera, uri, false);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
//speech end


    public void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    public void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    //check SD card availability
    public static boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }


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


    public void pickCameraImage(View view) {
        ImagePicker.with(this)
                // User can only capture image from Camera
                .cameraOnly()
                // Image size will be less than 1024 KB
                // .compress(1024)
                //  Path: /storage/sdcard0/Android/data/package/files
                .saveDir(getExternalFilesDir(null))
                //  Path: /storage/sdcard0/Android/data/package/files/DCIM
                .saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM))
                //  Path: /storage/sdcard0/Android/data/package/files/Download
                .saveDir(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
                //  Path: /storage/sdcard0/Android/data/package/files/Pictures
                .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
                .saveDir(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker"))
                //  Path: /storage/sdcard0/Android/data/package/files/ImagePicker
                .saveDir(getExternalFilesDir("ImagePicker"))
                //  Path: /storage/sdcard0/Android/data/package/cache/ImagePicker
                .saveDir(new File(getExternalCacheDir(), "ImagePicker"))
                //  Path: /data/data/package/cache/ImagePicker
                .saveDir(new File(getCacheDir(), "ImagePicker"))
                //  Path: /data/data/package/files/ImagePicker
                .saveDir(new File(getFilesDir(), "ImagePicker"))

                // Below saveDir path will not work, So do not use it
                //  Path: /storage/sdcard0/DCIM
                //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
                //  Path: /storage/sdcard0/Pictures
                //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
                //  Path: /storage/sdcard0/ImagePicker
                //  .saveDir(File(Environment.getExternalStorageDirectory(), "ImagePicker"))

                .start(CAMERA_IMAGE_REQ_CODE);
    }


    private void uploadImage(final Bitmap bitmap) {
        handler = new Handler();
        dialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

                                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                                        intent.putExtra("navPosition", "2");
                                        intent.putExtra("content", object.getJSONArray("data").toString());
                                        intent.putExtra("originalContent",object.getString("originalContent"));
                                        startActivity(intent);
                                        finish();

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

                                    dialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE);
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

                                dialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE);
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

                            dialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.putExtra("navPosition", "1");
        startActivity(intent);
        finish();
    }
}
