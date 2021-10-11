package com.example.pharmago.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


import com.example.pharmago.db.DbHandler;
import com.example.pharmago.db.SQLiteDatabaseHelper;
import com.example.pharmago.model.Customer;
import com.example.pharmago.model.CustomerCategory;
import com.example.pharmago.util.SharedPref;
import com.example.pharmago.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


import static com.example.pharmago.controller.BaseController.READ_LOCK;
import static com.example.pharmago.controller.BaseController.WRITE_LOCK;


public class CustomerController {

    public static void saveDownloadedCustomers(Context context, ArrayList<Customer> customers) throws Exception {

        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            database.execSQL("delete from tbl_customer");

            String chemistInsertQuery = "replace into tbl_customer( " +
                    "customerId ," +
                    "customerName ," +
                    "ownerName," +
                    "customerAddress," +
                    "customePhone," +
                    "customeMobile," +
                    "customeEmail," +
                    "customerCode," +
                    "routeId," +
                    "lat," +
                    "lon," +
                    "photo_path," +
                    "" +
                    "customerCategory  , date_of_birth,sequence_no " +
                    "  " +
                    "    ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement statement = database.compileStatement(chemistInsertQuery);

            for (Customer customer : customers) {
                DbHandler.performExecuteInsert(statement, new Object[]{
                        customer.getCustomerId(),
                        customer.getCustomerName(),
                        customer.getOwner(),
                        customer.getCustomerAddress(),
                        customer.getPhoneNumber(),
                        customer.getCustomerCode(),
                        customer.getRouteId(),
                        customer.getLat(),
                        customer.getLon(),
                        customer.getImagePath(),
                        customer.getCustomerCategory(), customer.getDateOfBirth(), customer.getSequenceId()});

            }


            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }

    public static void saveDownloadedCustomerCategories(Context context, ArrayList<CustomerCategory> customerCategories) throws Exception {

        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            database.execSQL("delete from tbl_customer_category");

            String chemistInsertQuery = "replace into tbl_customer_category( customerCategoryId ,customerCategory ) values(?,?)";

            SQLiteStatement statement = database.compileStatement(chemistInsertQuery);

            for (CustomerCategory customer : customerCategories) {
                DbHandler.performExecuteInsert(statement, new Object[]{
                        customer.getCustomerCategoryId(),
                        customer.getCustomerCategory()});

            }


            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }


    public static void clearChemistTable(Context context) throws Exception {

        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            database.execSQL("delete from tbl_customer");

            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }


    public static Customer getCustomerById(Context context, int id) {
        READ_LOCK.lock();
        Customer customer = null;

        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT customerId ,customerName ,ownerName,customerAddress," +
                "customePhone, customeMobile, customeEmail,customerCode,routeId,lat ,lon ," +
                "photo_path , date_of_birth FROM tbl_customer WHERE customerId = ?";

        try {

            Cursor cursor = database.rawQuery(query, new String[]{id + ""});

            if (cursor.moveToFirst()) {
                do {
                    customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setOwner(cursor.getString(2));
                    customer.setCustomerAddress(cursor.getString(3));
                    customer.setPhoneNumber(cursor.getString(4));
                    customer.setCustomerCode(cursor.getString(7));
                    customer.setRouteId(cursor.getInt(8));
                    customer.setLat(cursor.getDouble(9));
                    customer.setLon(cursor.getDouble(10));
                    customer.setImagePath(cursor.getString(11));

                    customer.setDateOfBirth(cursor.getString(12));

                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        } finally {
            READ_LOCK.unlock();
        }


        return customer;
    }

    public static ArrayList<Customer> getAllCustomers(Context context, int routeId) {
        ArrayList<Customer> customers = new ArrayList<>();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT customerId ," +
                "customerName ," +
                "ownerName," +
                "customerAddress," +
                "customePhone," +
                "customeMobile," +
                "customeEmail," +
                "customerCode," +
                "routeId," +
                "lat," +
                "lon ," +
                "photo_path," +
                "" +
                "customerCategory , date_of_birth , sequence_no FROM tbl_customer  WHERE routeId = " + routeId + " order by sequence_no ";

        try {

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Customer customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setCustomerAddress(cursor.getString(3));
                    customer.setOwner(cursor.getString(2));
                    customer.setPhoneNumber(cursor.getString(4));
                    customer.setCustomerCode(cursor.getString(7));
                    customer.setRouteId(cursor.getInt(8));
                    customer.setLat(cursor.getDouble(9));
                    customer.setLon(cursor.getDouble(10));
                    customer.setImagePath(cursor.getString(11));

                    customer.setCustomerCategory(cursor.getInt(12));

                    customer.setDateOfBirth(cursor.getString(13));
                    customer.setSequenceId(cursor.getInt(14));
                    customers.add(customer);


                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        }


        return customers;
    }

    public static ArrayList<Customer> getAllCustomers(Context context) {
        ArrayList<Customer> customers = new ArrayList<>();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT customerId ," +
                "customerName ," +
                "ownerName," +
                "customerAddress," +
                "customePhone," +
                "customeMobile," +
                "customeEmail," +
                "customerCode," +
                "routeId," +
                "lat," +
                "lon ," +
                "photo_path," +
                "" +
                "customerCategory , date_of_birth , sequence_no FROM tbl_customer   order by sequence_no ";

        try {

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Customer customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setCustomerAddress(cursor.getString(3));
                    customer.setOwner(cursor.getString(2));
                    customer.setPhoneNumber(cursor.getString(4));
                    customer.setCustomerCode(cursor.getString(7));
                    customer.setRouteId(cursor.getInt(8));
                    customer.setLat(cursor.getDouble(9));
                    customer.setLon(cursor.getDouble(10));
                    customer.setImagePath(cursor.getString(11));

                    customer.setCustomerCategory(cursor.getInt(12));

                    customer.setDateOfBirth(cursor.getString(13));
                    customer.setSequenceId(cursor.getInt(14));
                    customers.add(customer);


                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        }


        return customers;
    }

    public static ArrayList<Customer> getAllCustomersWithoutRoute(Context context) {
        ArrayList<Customer> customers = new ArrayList<>();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT customerId ," +
                "customerName ," +
                "ownerName," +
                "customerAddress," +
                "customePhone," +
                "customeMobile," +
                "customeEmail," +
                "customerCode," +
                "routeId," +
                "lat," +
                "lon ," +
                "photo_path," +
                "" +
                "customerCategory , date_of_birth , sequence_no FROM tbl_customer order by sequence_no ";

        try {

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Customer customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setCustomerAddress(cursor.getString(3));
                    customer.setOwner(cursor.getString(2));
                    customer.setPhoneNumber(cursor.getString(4));
                    customer.setCustomerCode(cursor.getString(7));
                    customer.setRouteId(cursor.getInt(8));
                    customer.setLat(cursor.getDouble(9));
                    customer.setLon(cursor.getDouble(10));
                    customer.setImagePath(cursor.getString(11));

                    customer.setCustomerCategory(cursor.getInt(12));
                    customer.setDateOfBirth(cursor.getString(13));
                    customer.setSequenceId(cursor.getInt(14));
                    customers.add(customer);


                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        }


        return customers;
    }

    public static ArrayList<Customer> getAllDirectCustomers(Context context) {
        ArrayList<Customer> customers = new ArrayList<>();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT customerId ," +
                "customerName ," +
                "ownerName," +
                "customerAddress," +
                "customePhone," +
                "customeMobile," +
                "customeEmail," +
                "customerCode," +
                "routeId," +
                "lat," +
                "lon ," +
                "photo_path," +
                "" +
                "customerCategory , date_of_birth , sequence_no FROM tbl_customer  WHERE is_direct_dealer = 1 order by sequence_no ";

        try {

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Customer customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setCustomerAddress(cursor.getString(3));
                    customer.setOwner(cursor.getString(2));
                    customer.setPhoneNumber(cursor.getString(4));
                    customer.setCustomerCode(cursor.getString(7));
                    customer.setRouteId(cursor.getInt(8));
                    customer.setLat(cursor.getDouble(9));
                    customer.setLon(cursor.getDouble(10));
                    customer.setImagePath(cursor.getString(11));
                    customer.setCustomerCategory(cursor.getInt(12));
                    customer.setDateOfBirth(cursor.getString(13));
                    customer.setSequenceId(cursor.getInt(14));
                    customers.add(customer);


                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        }

        Customer dis = new Customer();
        dis.setCustomerId(-1);
        dis.setCustomerName("Distributor");
        customers.add(dis);


        return customers;
    }



    public static void saveUnplannedCustomer(Context context, Customer unplanned_customer) throws Exception {

        WRITE_LOCK.lock();
        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();


            String chemistInsertQuery = "replace into tbl_unplanned_customer(    customerId , customerName ,customerAddress ,  " +
                    "customePhone,  customerCode ) values(?,?,?,?,?)";

            SQLiteStatement statement = database.compileStatement(chemistInsertQuery);


            DbHandler.performExecuteInsert(statement, new Object[]{unplanned_customer.getCustomerId(), unplanned_customer.getCustomerName(),
                    unplanned_customer.getCustomerAddress(), unplanned_customer.getPhoneNumber(), unplanned_customer.getCustomerCode()});


            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            databaseHelper.close();
            WRITE_LOCK.unlock();
        }
    }


    public static ArrayList<Customer> getUnplannedChemists(Context context) {
        ArrayList<Customer> customers = new ArrayList<>();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT    customerId INTEGER ," +
                "         customerName ," +
                "         customerAddress ," +
                "         customePhone," +
                "         customerCode " +
                "          FROM tbl_unplanned_customer ";

        try {

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Customer customer = new Customer();
                    customer.setCustomerId(cursor.getInt(0));
                    customer.setCustomerName(cursor.getString(1));
                    customer.setCustomerAddress(cursor.getString(2));
                    customer.setPhoneNumber(cursor.getString(3));
                    customer.setCustomerCode(cursor.getString(4));


                    customers.add(customer);


                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        }


        return customers;
    }


    public static boolean checkIfProductiveOutlet(Context context, int customerId) {
        boolean status = false;

        READ_LOCK.lock();


        SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper
                .getDatabaseInstance(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String query = "SELECT  *  " +
                "FROM tbl_order WHERE outletId = ?";

        try {

            Cursor cursor = database.rawQuery(query, new String[]{customerId + ""});

            if (cursor.moveToFirst()) {
                status = true;
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        } finally {
            READ_LOCK.unlock();
        }

        return status;
    }






}
