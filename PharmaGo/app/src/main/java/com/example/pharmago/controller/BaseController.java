package com.example.pharmago.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.pharmago.db.SQLiteDatabaseHelper;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.util.LockProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;

public class BaseController {

    protected static final ReentrantReadWriteLock.ReadLock READ_LOCK;
    protected static final ReentrantReadWriteLock.WriteLock WRITE_LOCK;

    static {
        READ_LOCK = LockProvider.getReadLock();
        WRITE_LOCK = LockProvider.getWriteLock();
    }

    private static final String LOG_TAG = BaseController.class.getSimpleName();
    //    public static String domain = "http://192.168.0.48/falcon";

     public static String domain = "http://192.168.1.102:5000/";
 //   public static String domain = "http://falcon.salespad.lk/falcon-test";


    public static String baseURL = domain + "/api/v1/resources/";

    public static String baseURL2 = "https://raw.githubusercontent.com/lahiru04/json/master/";

// function for post services 
    
    public static String postToServerGzip(String url, List<CustomNameValuePair> params) throws IOException {
        Log.d("<> URL <>", url);
        String response = "";

        URL postURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setConnectTimeout(100 * 1000);
        con.setReadTimeout(600 * 1000);
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(generatePOSTParams(params));
        writer.flush();
        writer.close();
        os.close();

        con.connect();

        int status = con.getResponseCode();
        switch (status) {
            case 200:
            case 201:

                Reader reader = null;
                if ("gzip".equals(con.getContentEncoding())) {
                    reader = new InputStreamReader(new GZIPInputStream(con.getInputStream()));

                } else {
                    reader = new InputStreamReader(con.getInputStream());
                }

                Log.e("type", con.getContentEncoding() + "");

                BufferedReader br = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();

                response = sb.toString();
                Log.e(LOG_TAG, "Server Response : \n" + response);
        }
        Log.e("<><><>", response);

        return response;
    }

    private static String generatePOSTParams(List<CustomNameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (CustomNameValuePair pair : params) {
            if (pair != null) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
        }

        Log.d(LOG_TAG, "Server REQUEST : " + result.toString());
        Log.d(LOG_TAG, "Upload size : " + result.toString().getBytes().length + " bytes");

        return result.toString();
    }


    public static void clearTables(Context context) {
        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            database.execSQL("delete from tbl_unproductive_visit");
            database.execSQL("delete from tbl_productive_visit");
            database.execSQL("delete from tbl_productive_visit_sampling_detail");
            database.execSQL("delete from tbl_day_visit_detail");
            database.execSQL("delete from tbl_promotion_visit");
            database.execSQL("delete from tbl_promotion_visit_product_detail");


            database.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }


    public static String postToServerGzipWithToken(String token, String url, List<CustomNameValuePair> params) throws IOException {
        Log.d("<> URL <>", url);
        String response = "";

        URL postURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("ContentType", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setConnectTimeout(20 * 1000);
        con.setReadTimeout(30 * 1000);
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(generatePOSTParams(params));
        writer.flush();
        writer.close();
        os.close();

        con.connect();

        int status = con.getResponseCode();
        switch (status) {
            case 200:
            case 201:

                Reader reader = null;
                if ("gzip".equals(con.getContentEncoding())) {
                    reader = new InputStreamReader(new GZIPInputStream(con.getInputStream()));

                } else {
                    reader = new InputStreamReader(con.getInputStream());
                }

                Log.e("type", con.getContentEncoding() + "");

                BufferedReader br = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();

                response = sb.toString();
                Log.e(LOG_TAG, "Server Response : \n" + response);
        }
        Log.e("<><><>", response);

        return response;
    }


    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);
        urlConnection.disconnect();

        return new JSONObject(jsonString);
    }

}
