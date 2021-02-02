package com.example.stockapp.API;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.stockapp.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class ApiCall {

    private static ApiCall mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    public ApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized ApiCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiCall(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = VolleySingleton.getInstance(mCtx).getmRequestQueue();
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
            String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/auto/" + query;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);
            //This is for Headers If You Needed
            ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

}
