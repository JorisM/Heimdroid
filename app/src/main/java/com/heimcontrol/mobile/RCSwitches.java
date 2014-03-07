package com.heimcontrol.mobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import io.socket.*;

import static android.widget.Toast.makeText;


public class RCSwitches extends Fragment implements RefreshInterface {

    private SocketIO socket;
    private String url;
    private Context context;
    private RCSwitchArrayAdapter listAdapter;
    private ArrayList<RCSwitch> switchesList;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        if(switchesList == null)
            switchesList = new ArrayList<RCSwitch>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.url = prefs.getString("heimcontrol_url", "");

        if (getKey().equals("") || this.url.equals(""))
        {
            CharSequence text = "No key available, please check heimcontrol url in settings and log in.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = makeText(context, text, duration);
            toast.show();
            this.logout();
        }

        RestClient.setBaseUrl(this.url, context);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }
        this.connectToSocket();

        this.setSwitches();

    }


    public void attachView(View myView)
    {
        final ListView listview = (ListView) myView.findViewById(R.id.switchesList);
        listAdapter = new RCSwitchArrayAdapter(context, R.layout.fragment_switches, getSwitchesList());
        listview.setAdapter(listAdapter);

    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.inflater = inflater;
        View myView = inflater.inflate(R.layout.activity_switches, container, false);
        this.attachView(myView);
        return myView;
    }


    public ArrayList<RCSwitch> getSwitchesList()
    {
        if(switchesList == null)
            switchesList = new ArrayList<RCSwitch>();
        return switchesList;
    }

    public void setSwitches()
    {
        final RCSwitches that = this;
        JSONObject data = new JSONObject();
        try {
            data.put("method", "rcswitch");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RestClient.postJSON(
                context,
                "api/arduino/post",
                entity,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONArray responseData) {
                        try {
                            ArrayList<RCSwitch> list = new ArrayList<RCSwitch>();
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject obj = responseData.getJSONObject(i);
                                String id = obj.getString("_id");
                                String value = obj.getString("value");
                                String description = obj.getString("description");
                                String pin = obj.getString("pin");
                                String code = obj.getString("code");
                                list.add(new RCSwitch(id, description, isBoolean(value), pin, code));
                            }
                            setSwitches(list);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 401) {
                            that.logout();
                        }
                        CharSequence text = "Error " + statusCode + " while fetching toggles";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = makeText(context, text, duration);
                        toast.show();
                    }
                }
        );
    }

    private synchronized void setSwitches(ArrayList<RCSwitch> list)
    {
        getSwitchesList().clear();
        getSwitchesList().addAll(list);
        listAdapter.notifyDataSetChanged();
    }


    private synchronized void refreshList()
    {
        this.setSwitches();
    }




    public void notifyHeimcontrol(RCSwitch obj, boolean on)
    {
        if(!socket.isConnected())
            connectToSocket();

        String value = "0";
        if(!obj.getValue())
        {
            value = "1";
        }else
        {
            value = "0";
        }
        obj.setValue(!obj.getValue());
        JSONObject params = new JSONObject();
        try {
            params.put("value", value);
            params.put("id", obj.get_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("arduino-rcswitch", params);
    }

    public void logout()
    {
        ((MainActivity)context).logout();
    }

    public void connectToSocket()
    {
        if(socket != null && socket.isConnected())
            return;

        try {
            String authKey = User.getKey();
            socket = new SocketIO(this.url);
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
                System.out.println(data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                /*CharSequence text = "Socketio error occurred";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = makeText(context, text, duration);
                toast.show();
                socketIOException.printStackTrace();*/
            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {
                System.out.println("Connection established");
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... args)
            {
                JSONObject incoming = (JSONObject) args[0];
                String id = null;
                String value = null;
                try {
                    id = incoming.getString("id");
                    value = incoming.getString("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < switchesList.size(); i++)
                {
                    if(switchesList.get(i).get_id().equals(id))
                    {
                        switchesList.get(i).setValue(isBoolean(value));
                        //todo: whatever this blah here is
                        switchesList.get(i).setDescription("blah");
                    }
                }
            }
        });
    }
    public void refresh()
    {
        this.refreshList();
        this.connectToSocket();
    }

    //This is an ugly hack
    /*private synchronized void copySwitchesList(ArrayList<RCSwitch> switches)
    {
        ArrayList<RCSwitch> s = new ArrayList<RCSwitch>();
        s.addAll(switches);
        this.setSwitches(s);
    }*/

    private synchronized boolean isBoolean(String value)
    {
        boolean val;
        if(value.equals("0"))
        {
            val = false;
        }else
        {
            val = true;
        }
        return val;
    }

    private synchronized void toastit(String ttext)
    {
        CharSequence text = ttext;
        int duration = Toast.LENGTH_LONG;
        Toast toast = makeText(context, text, duration);
        toast.show();
    }

    public String getKey() {
        return ((MainActivity)context).getKey();
    }


    //todo: move to main activity with the help of a interface
/*
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_logout) {
            this.logout();
            return true;
        }
        if (id == R.id.action_refresh) {
            this.refreshList();
            this.connectToSocket();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/


    private class RCSwitchArrayAdapter extends ArrayAdapter<RCSwitch> {

        List<RCSwitch> objects;

        public RCSwitchArrayAdapter(Context context, int textViewResourceId,
                                List<RCSwitch> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RCSwitchHolder holder = null;
            RCSwitch pos = objects.get(position);

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
                row = inflater.inflate(R.layout.fragment_switches, parent, false);

                holder = new RCSwitchHolder();
                holder.pin = (TextView)row.findViewById(R.id.pin);
                holder.description = (TextView)row.findViewById(R.id.description);

                row.setTag(holder);

                holder.description.setText(pos.getDescription());
                holder.pin.setText(pos.getPin());

                Switch sswitch = (Switch)row.findViewById(R.id.pinSwitch);
                sswitch.setChecked(pos.getValue());
                sswitch.setTag(pos);
                sswitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        RCSwitch item = (RCSwitch) buttonView.getTag();

                        notifyHeimcontrol(item, isChecked);
                    }
                });
            }
            else
            {
                holder = (RCSwitchHolder)row.getTag();
                Switch sswitch = (Switch)row.findViewById(R.id.pinSwitch);
                //sswitch.setChecked(pos.getValue());
            }

            return row;
        }

    }


    static class RCSwitchHolder
    {
        TextView _id;
        TextView description;
        TextView direction;
        TextView value;
        TextView pin;
    }

}


