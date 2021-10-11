package com.example.pharmago.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerCategory {
    private Integer customerCategoryId;
    private String customerCategory;

    public CustomerCategory(Integer customerCategoryId, String customerCategory) {
        this.customerCategoryId = customerCategoryId;
        this.customerCategory = customerCategory;
    }

    public Integer getCustomerCategoryId() {
        return customerCategoryId;
    }

    public void setCustomerCategoryId(Integer customerCategoryId) {
        this.customerCategoryId = customerCategoryId;
    }

    public String getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    public static ArrayList<CustomerCategory> getCustomerCategories(JSONArray jsonArray) {

        ArrayList<CustomerCategory> customerCategories = new ArrayList<>();

        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                CustomerCategory category = new CustomerCategory(jsonObject.getInt("customer_category_id"), jsonObject.getString("customer_category_name"));

                customerCategories.add(category);

            }

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        return customerCategories;
    }

    @Override
    public String toString() {
        return  customerCategory ;
    }
}
