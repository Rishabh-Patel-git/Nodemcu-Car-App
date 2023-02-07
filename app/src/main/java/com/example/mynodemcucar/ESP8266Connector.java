package com.example.mynodemcucar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;

public class ESP8266Connector {
    private Context ctx;
    private String root;
    private RequestQueue mRequestQueue;

    private final String KEY_FORWARD = "F";
    private final String KEY_BACKWARD = "B";
    private final String KEY_RIGHT = "R";
    private final String KEY_LEFT = "L";
    private final String KEY_STOP = "S";
    private final String KEY_IP = "ipPlz";
    private final String KEY_Group = "groupPlz";


    public ESP8266Connector(Context context, String ip, String port) {
        this.ctx = context;
        this.root = "http://" + ip + ":" + port;
    }

    public void moveForward() {
        sendRequest(KEY_FORWARD);
    }

    public void moveBackward() {
        sendRequest(KEY_BACKWARD);
    }

    public void turnRight() {
        sendRequest(KEY_RIGHT);
    }

    public void turnLeft() {
        sendRequest(KEY_LEFT);
    }

    public void stopMoving() {
        sendRequest(KEY_STOP);
    }

    public void showIPAdd() {
        sendRequest(KEY_IP);
    }

    public void showGroup() {
        sendRequest(KEY_Group);
    }

    private void sendRequest(String stateValue) {
        String request = this.root + "/?State=" + stateValue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("send Request Response", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("send Request Error", error.toString());
                Toast.makeText(ctx, "Wrong IP address entered", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setShouldCache(false);
        getRequestQueue().add(stringRequest);
    }


    public void sendRequest(String stateValue, VolleyCallback callback) {
        String request = this.root + "/?State=" + stateValue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        callback.onSuccess(response);

                        Log.e("send Request Response", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("send Request Error", error.toString());
                callback.onError("Error");
            }
        });
        stringRequest.setShouldCache(false);
        getRequestQueue().add(stringRequest);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ctx);
        }
        return mRequestQueue;
    }

    public void clearRequestQueue() {
        getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }


}
