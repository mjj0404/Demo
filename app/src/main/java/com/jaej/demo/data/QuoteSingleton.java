package com.jaej.demo.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//modified Singleton pattern provided by Google Guides for Android Developers
//https://developer.android.com/training/volley/requestqueue
public class QuoteSingleton {

    private static QuoteSingleton instance;
    private RequestQueue requestQueue;

    private static Context ctx;

    private QuoteSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized QuoteSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new QuoteSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}