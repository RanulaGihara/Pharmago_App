package com.example.pharmago.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.pharmago.db.DbHandler;
import com.example.pharmago.db.SQLiteDatabaseHelper;
import com.example.pharmago.model.NotificationM;

import org.json.JSONArray;

import java.util.ArrayList;

import static com.example.pharmago.controller.BaseController.WRITE_LOCK;


public class NotificationController {

    public static void saveNotification(ArrayList<NotificationM> mArrayList, Context context) {


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            String chemistInsertQuery = "replace into tbl_notification" +
                    "( tbl_id ,title ,content_text,seen_status,n_time, type_id ," +
                    "additional ) values(?,?,?,?,?,?,?)";

            SQLiteStatement statement = database.compileStatement(chemistInsertQuery);

            for (NotificationM notificationM : mArrayList) {
                DbHandler.performExecuteInsert(statement, new Object[]{notificationM.getNotificationId(),
                        notificationM.getNotificationTitle(), notificationM.getNotificationContent(),
                        notificationM.getIsSeen() ? 1 : 0, notificationM.getTimeStamp(), notificationM.getTypeId(), notificationM.getAdditionalData()});

            }


            database.setTransactionSuccessful();
            database.endTransaction();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {


        }

    }


    public static void setSeenStatusUp(int tableId, Context context) {
        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            String chemistInsertQuery = "UPDATE  tbl_notification " +
                    " SET seen_status=?  WHERE tbl_id = ?";

            SQLiteStatement statement = database.compileStatement(chemistInsertQuery);


            DbHandler.performExecuteInsert(statement, new Object[]{1, tableId});


            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }

    public static JSONArray getReadNotificationIds(Context context) {

        JSONArray array = new JSONArray();
        try {

            SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                    .getDatabaseInstance(context);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();

            String query = "SELECT tbl_id FROM tbl_notification WHERE seen_status =1 ";


            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    array.put(cursor.getInt(0));

                } while (cursor.moveToNext());

            }


            cursor.close();


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            return array;
        }


    }

    public static void clearNotificationTable(Context context) {
        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            database.execSQL("delete from tbl_notification");


            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }


    public static ArrayList<NotificationM> getAllNotifications(Context context) {
        ArrayList<NotificationM> notificationMS = new ArrayList<>();


        try {

            SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                    .getDatabaseInstance(context);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();

            String query = "SELECT  tbl_id,title,content_text,seen_status, n_time, type_id," +
                    "additional  FROM tbl_notification  ORDER BY tbl_id DESC";


            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    NotificationM notificationM = new NotificationM();
                    notificationM.setNotificationId(cursor.getInt(0));
                    notificationM.setNotificationTitle(cursor.getString(1));
                    notificationM.setNotificationContent(cursor.getString(2));

                    boolean isSeen = false;
                    if (cursor.getInt(3) == 1) {
                        isSeen = true;
                    }
                    notificationM.setIsSeen(isSeen);
                    notificationM.setTimeStamp(cursor.getLong(4));
                    notificationM.setTypeId(cursor.getInt(5));
                    notificationM.setAdditionalData(cursor.getString(6));
                    notificationMS.add(notificationM);

                } while (cursor.moveToNext());

            }


            cursor.close();


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {

        }


        return notificationMS;
    }
}
