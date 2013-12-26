package com.heimcontrol.mobile;
import android.app.Application;

public class Heimcontrol extends Application
{

    User user;

    @Override
    public void onCreate()
    {
        super.onCreate();
        user = new User(this.getSharedPreferences("Heimcontrol", MODE_PRIVATE));
    }

}
