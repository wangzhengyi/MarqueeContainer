package com.wzy.marquee.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by wzy on 2016/10/14.
 * Volley单例类.
 */
public final class VolleyManager {
    private static volatile VolleyManager mInstance;
    private RequestQueue mRequestQueue;

    private VolleyManager(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleyManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleyManager.class) {
                if (mInstance == null) {
                    mInstance = new VolleyManager(context);
                }
            }
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
