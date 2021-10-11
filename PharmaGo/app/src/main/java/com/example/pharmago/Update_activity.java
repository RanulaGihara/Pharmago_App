package com.example.pharmago;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.pharmago.dialog.ProgressDialog;
import com.example.pharmago.util.SharedPref;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class Update_activity extends AppCompatActivity {

    private ProgressDialog dlDialog;
    String path = Environment.getExternalStorageDirectory() + "/"; // Path to where you want to save the file
    String inet = ""; // Internet path to the file
    String cachedir = "";
    String filename = "TMC.apk";
    final Context context = this;
    String abs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_activity);


        Intent data = getIntent();
        inet = data.getStringExtra("url");


        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(Update_activity.this);


        alertDialog.setTitle("Update").setMessage("Do you want to update this app?  (Unsynchronized data may be lost)").setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface sDialog, int i) {
                sDialog.dismiss();
                downloader();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface sDialog, int i) {
                sDialog.dismiss();
                finish();
            }
        }).show();


    }

    public void downloader() {
        File getcache = this.getCacheDir();
        cachedir = getcache.getAbsolutePath();

        dlDialog = new ProgressDialog(Update_activity.this);
        dlDialog.setCancelable(false);
        dlDialog.setTitle("Downloading................");
        dlDialog.setCanceledOnTouchOutside(false);
        dlDialog.show();


        new Thread(new Runnable() {

            public void run() {

                String filePath = path;

                InputStream is = null;
                OutputStream os = null;
                URLConnection URLConn = null;

                try {
                    URL fileUrl;
                    byte[] buf;
                    int ByteRead = 0;
                    int ByteWritten = 0;
                    fileUrl = new URL(inet);

                    URLConn = fileUrl.openConnection();

                    is = URLConn.getInputStream();

                    String fileName = inet.substring(inet.lastIndexOf("/") + 1);

                    File f = new File(filePath);
                    f.mkdirs();
                    abs = filePath + fileName;
                    f = new File(abs);


                    os = new BufferedOutputStream(new FileOutputStream(abs));

                    buf = new byte[1024];

                    /*
                     * This loop reads the bytes and updates a progressdialog
                     */


                    while ((ByteRead = is.read(buf)) != -1) {

                        os.write(buf, 0, ByteRead);
                        ByteWritten += ByteRead;

                        final int tmpWritten = ByteWritten / 1024;
                        runOnUiThread(new Runnable() {

                            public void run() {
                                dlDialog.setTitle("" + tmpWritten + " KB " + "downloaded");
                            }

                        });
                    }

                    runOnUiThread(new Runnable() {

                        public void run() {
                            dlDialog.setTitle("Download Completed");
                        }

                    });
                    is.close();
                    os.flush();
                    os.close();


                    Thread.sleep(200);

                    runOnUiThread(new Runnable() {

                        public void run() {

                            dlDialog.dismiss();


                            //     Uri uri = FileProvider.getUriForFile(Update_activity.this, Update_activity.this.getPackageName() + ".com.ceylonlinux.sunshinemr.util.GenericFileProvider", new File(abs));

                            Uri uri = FileProvider.getUriForFile(Update_activity.this, Update_activity.this.getApplicationContext().getPackageName() + ".com.ceylonlinux.cwmackie.util.GenericFileProvider", new File(abs));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                Uri apkUri = Uri.fromFile(new File(abs));
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        //    new SharedPref(Update_activity.this).setAppUpdateStatus();

                       /*     try {


                                Thread.sleep(5000);
                            } catch (Exception e) {
                                Log.e("ERROR", e.toString());
                            }*/
                            //   FileUtil.clearAppData(Update_activity.this);


                            finish();
                        }

                    });


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }).start();
    }


}
