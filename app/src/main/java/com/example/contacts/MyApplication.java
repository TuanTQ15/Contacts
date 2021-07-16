package com.example.contacts;

import android.app.Application;

import androidx.room.Room;

import com.example.contacts.dao.AppDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "myDatabase").allowMainThreadQueries().build();
    }

    private static AppDatabase db;
    public static AppDatabase getDb(){
        return db;
    }
}
