package com.example.pharmago.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {

    private int customerId;
    private String customerName, dateOfBirth;
    private String customerAddress;
    private String customerCode;
    private String phoneNumber;
    private String imagePath;
    private int customerCategory;
    private int customerSpecification;
    private double lat;
    private double lon;
    private String owner;
    private boolean isEdited;
    private int routeId;
    private int sequenceId = 1;
    private boolean dealerBoardStatus;
    private boolean vatStatus;
    private boolean syncStatus;

    public boolean isSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    public boolean isDealerBoardStatus() {
        return dealerBoardStatus;
    }

    public void setDealerBoardStatus(boolean dealerBoardStatus) {
        this.dealerBoardStatus = dealerBoardStatus;
    }

    public boolean isVatStatus() {
        return vatStatus;
    }

    public void setVatStatus(boolean vatStatus) {
        this.vatStatus = vatStatus;
    }

    public int getCustomerSpecification() {
        return customerSpecification;
    }

    public void setCustomerSpecification(int customerSpecification) {
        this.customerSpecification = customerSpecification;
    }


    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Customer(int customerId, String customerName, String customerAddress, String customerCode, String phoneNumber, String emailAddress,
                    String imagePath, int customerCategory, double lat, double lon,
                    String mobileNumber, String owner, boolean isEdited, boolean isDirectDealer, int routeId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerCode = customerCode;
        this.phoneNumber = phoneNumber;
        this.imagePath = imagePath;
        this.customerCategory = customerCategory;
        this.lat = lat;
        this.lon = lon;
        this.owner = owner;
        this.isEdited = isEdited;
        this.routeId = routeId;

    }

    public Customer() {

    }


    public static ArrayList<Customer> getCustomersByJsonArray(JSONArray jsonArray) {
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject objec = jsonArray.getJSONObject(i);

                Customer customer = new Customer();
                customer.customerId = objec.getInt("customerId");
                customer.customerName = objec.getString("customerName");
                customer.customerAddress = objec.getString("customerAddress");
                customer.customerCode = objec.getString("customerCode");
                customer.phoneNumber = objec.getString("phoneNumber");
                customer.imagePath = objec.getString("imagePath");
                customer.customerCategory = objec.getInt("customerCategory");
                customer.customerSpecification = objec.getInt("customerSpecification");
                customer.vatStatus = objec.getBoolean("vatStatus");
                customer.dealerBoardStatus = objec.getBoolean("dealerBoardStatus");
                customer.lat = objec.getDouble("lat");
                customer.lon = objec.getDouble("lon");
                customer.owner = objec.getString("owner");
                customer.setRouteId(objec.getInt("routeId"));

                try {


                    String dateOfBirth = objec.getString("dateOfBirth");

                    if (dateOfBirth.equals("null")) {
                        dateOfBirth = "";
                    }


                    customer.setDateOfBirth(dateOfBirth);


                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }

                try {
                    String seqsId = objec.getString("sequenceId");
                    int sequenceId = Integer.parseInt(seqsId);
                    customer.setSequenceId(sequenceId);
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }


                customers.add(customer);
            }


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        return customers;


    }


    public static Customer getCustomerByJson(JSONObject objec) {

        Customer customer = null;
        try {


            customer = new Customer();
            customer.customerId = objec.getInt("customerId");
            customer.customerName = objec.getString("customerName");
            customer.customerAddress = objec.getString("customerAddress");
            customer.customerCode = objec.getString("customerCode");
            customer.phoneNumber = objec.getString("phoneNumber");
            customer.imagePath = objec.getString("imagePath");
            customer.customerCategory = objec.getInt("customerCategory");
            customer.lat = objec.getDouble("lat");
            customer.lon = objec.getDouble("lon");
            customer.owner = objec.getString("owner");
            customer.setRouteId(objec.getInt("routeId"));
            try {

                customer.setDateOfBirth(objec.getString("dateOfBirth"));


            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        return customer;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(int customerCategory) {
        this.customerCategory = customerCategory;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public int getRouteId() {
        return routeId;
    }


    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }


    @Override
    public String toString() {
        return customerName;
    }
}
