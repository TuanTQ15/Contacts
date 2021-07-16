package com.example.contacts.model;


import java.io.Serializable;

public class Email implements Serializable {

    private String emailAddress;

    private String type;

    public Email() {
    }

    public Email(String emailAddress, String type) {
        this.emailAddress = emailAddress;
        this.type = type;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
