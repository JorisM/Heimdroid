package com.heimcontrol.mobile;


import android.content.Context;

import com.loopj.android.http.*;

import org.apache.http.entity.StringEntity;

public class RestClient
{

    private static final String BASE_URL = "http://thedoorpi/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        String authKey = User.getKey();
        if(authKey != null)
        {
            client.addHeader("authorization", authKey);
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler)
    {
        String authKey = User.getKey();
        if(authKey != null)
        {
            client.addHeader("authorization", authKey);
        }
        client.get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        String authKey = User.getKey();
        if(authKey != null)
        {
            client.addHeader("authorization", authKey);
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler)
    {
        String authKey = User.getKey();
        if(authKey != null)
        {
            client.addHeader("authorization", authKey);
        }
        client.post(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void postJSON(Context context, String url, StringEntity params, AsyncHttpResponseHandler responseHandler)
    {
        String authKey = User.getKey();
        if(authKey != null)
        {
            client.addHeader("authorization", authKey);
        }
        client.post(context, getAbsoluteUrl(url), params, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}