package com.heimcontrol.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class Login extends Activity
{

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        context = getApplicationContext();

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RestClient.setBaseUrl(prefs.getString("heimcontrol_url", ""));

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
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if(statusCode == 401)
                        {
                            CharSequence text = statusCode + ": Wrong email or password";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else
                        {
                            CharSequence text = "Error " + statusCode + " while trying to log in";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }
        );
    }

    public void loggedIn()
    {
        String key = ((Heimcontrol)getApplicationContext()).user.getKey();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
