package com.example.contacts.model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

public class Contact implements Serializable {
    private Integer id;

    private String name;

    private String address;

    private String photo="";

    private List<Phone> phones;

    private List<Email> emails;

    public Contact() {
    }

    public Contact(Integer id, String name, String address,
                   String photo, List<Phone> phones, List<Email> emails) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.phones = phones;
        this.emails = emails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                ", phones=" + phones +
                ", emails=" + emails +
                '}';
    }
}
