package com.map.pandora.util;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * @author jayce
 * @date 2015/05/24
 */
public abstract class HttpHandler implements Response.Listener<String>,Response.ErrorListener{

    public static final String TAG="HttpHandler";

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Log.i(TAG,error.getLocalizedMessage()+error.getMessage());
        onFinish();
    }

    @Override
    public void onResponse(String response) {
        Log.i(TAG,"response:"+response);

        onFinish();
        onSuccess(response);
    }

    public abstract void onSuccess(String response);

    public void onStart(){};

    public void onFinish(){};
}
