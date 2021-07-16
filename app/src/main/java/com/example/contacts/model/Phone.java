package com.example.contacts.model;


import java.io.Serializable;

public class Phone implements Serializable {

    private String phoneNumber;

    private String type;

    public Phone() {
    }

    public Phone(String phoneNumber, String type) {
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
