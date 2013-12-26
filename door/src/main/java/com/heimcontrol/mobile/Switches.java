package com.heimcontrol.mobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.*;

import java.net.MalformedURLException;

import io.socket.*;

public class Switches extends Activity {

    private SocketIO socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switches);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        connectToSocket();

        // This line is cached until the connection is establisched.

    }


    public void openDoor(View v)
    {
        if(socket.isConnected())
        {
            JSONObject params = new JSONObject();
            try {
                params.put("id", "52bc1202836586f90d89c83e");
                params.put("value", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("gpio-toggle", params);
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            socket.emit("gpio-toggle", params);
        }else
        {
            connectToSocket();
        }
    }

    public void logout(View v)
    {
        ((Heimcontrol) getApplicationContext()).user.setKey("");
    }

    public void connectToSocket()
    {

        try {
            String authKey = User.getKey();
            socket = new SocketIO("http://192.168.1.4:8080/");
            socket.addHeader("authorization", authKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
                try {
                    System.out.println("Server said:" + json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                System.out.println("Server said: " + data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                System.out.println("an Error occured");
                socketIOException.printStackTrace();
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminated.");
            }

            @Override
            public void onConnect() {
                System.out.println("Connection established");
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... args) {
                System.out.println("Server triggered event '" + event + "'");
                Context context = getApplicationContext();
                CharSequence text = "Some triggered the door";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_switches, container, false);
            return rootView;
        }
    }

}
