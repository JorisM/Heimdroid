package com.heimcontrol.mobile;

import android.content.SharedPreferences;

/**
 * Created by root on 12/23/13.
 */
public class User
{
    private SharedPreferences preferences;
    private static String key;

    User(SharedPreferences preferences)
    {
        this.preferences = preferences;
        key = this.preferences.getString("application-key", "");
    }

    public void setKey(String keyA)
    {
        key = keyA;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("application-key", key);
        editor.commit();
    }

    public static String getKey()
    {
        return key;
    }

}
