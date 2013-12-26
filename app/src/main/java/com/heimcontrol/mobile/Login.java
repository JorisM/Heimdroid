package com.heimcontrol.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class Login extends Activity
{

    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = getPreferences(MODE_PRIVATE);

        setContentView(R.layout.fragment_login);

        loggedIn();
    }

    public void authenticate(View view)
    {
        String editText = ((EditText) findViewById(R.id.email)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.password)).getText().toString();

        JSONObject params = new JSONObject();
        try {
            params.put("email", editText);
            params.put("password", passwordText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RestClient.postJSON(
                (Heimcontrol) getApplicationContext(),
                "api/login",
                entity,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(JSONObject responseData)
                    {
                        try
                        {
                            String applicationKey = responseData.getString("token");
                            ((Heimcontrol) getApplicationContext()).user.setKey(applicationKey);
                            Login.this.loggedIn();
                        }
                        catch (JSONException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void loggedIn()
    {
        Log.v("Bejoo", "LoggedIn");
        String key = ((Heimcontrol)getApplicationContext()).user.getKey();
        Log.v("Bejoo", "Key: "+key);

        if (key != "")
        {
            Intent intent = new Intent(this, Switches.class);
            startActivity(intent);
            // finish the Authenticate Activity, so that we have no entry in the history
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.switches, menu);
        return true;
    }
}
