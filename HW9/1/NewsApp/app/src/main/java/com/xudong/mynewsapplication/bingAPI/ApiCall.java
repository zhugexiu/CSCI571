package com.xudong.mynewsapplication.bingAPI;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.xudong.mynewsapplication.VolleySingleton;

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
            String url = "https://xd-autosuggest.cognitiveservices.azure.com/bing/v7.0/suggestions?q=" + query;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    listener, errorListener){
                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Ocp-Apim-Subscription-Key", "bf10e89c81f64bcdbc4d9983c1fc163d");
                    return params;
                }};
            ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
        }
}
