package com.example.pharmago.model;

/**
 * Created by TaZ on 4/23/15.
 * Model class to hold a name value pair.
 *
 */
public class CustomNameValuePair {

    private String name, value;

    public CustomNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " : " + value;
    }
}
