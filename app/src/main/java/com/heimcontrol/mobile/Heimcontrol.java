package com.heimcontrol.mobile;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Heimcontrol extends Application
{

    User user;

    @Override
    public void onCreate()
    {
        super.onCreate();
        user = new User(this.getSharedPreferences("Heimcontrol", MODE_PRIVATE));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

}
