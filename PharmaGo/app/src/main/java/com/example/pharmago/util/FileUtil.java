package com.example.pharmago.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    public static String encodeFileToBase64Binary(String fileName) throws IOException {
        byte[] encoded = Base64.encode(convertFileToByteArray(fileName), Base64.DEFAULT);
        return new String(encoded);
    }


    private static byte[] convertFileToByteArray(String path) throws IOException {

        File file = new File(path);
        //init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        return bytesArray;


    }

    public static void clearAppData(Context context) {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = context.getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
