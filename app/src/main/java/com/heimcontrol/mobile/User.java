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
        key = this.preferences.getString("authorization", "");
    }

    public void setKey(String keyA)
    {
        key = keyA;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("authorization", key);
        editor.commit();
    }

    public static String getKey()
    {
        return key;
    }

}
