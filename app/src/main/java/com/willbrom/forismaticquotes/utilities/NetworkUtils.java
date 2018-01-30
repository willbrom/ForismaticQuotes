package com.willbrom.forismaticquotes.utilities;


import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public interface VollyCallbackListener {
        void onSuccess(String response);
        void onFailure(String error);
    }

    private static final String BASE_URL = "https://api.forismatic.com/api/1.0/";

    private static final String METHOD_QUERY_KEY = "method";
    private static final String FORMAT_QUERY_KEY = "format";
    private static final String KEY_QUERY_KEY = "key";
    private static final String LANG_QUERY_KEY = "lang";

    private static final String METHOD_QUERY_VALUE = "getQuote";
    private static final String FORMAT_QUERY_VALUE_JSON = "json";
    private static final String LANG_QUERY_VALUE_EN = "en";

    public static URL getQuoteUrl(String keyQueryValue) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(METHOD_QUERY_KEY, METHOD_QUERY_VALUE)
                .appendQueryParameter(FORMAT_QUERY_KEY, FORMAT_QUERY_VALUE_JSON)
                .appendQueryParameter(KEY_QUERY_KEY, keyQueryValue)
                .appendQueryParameter(LANG_QUERY_KEY, LANG_QUERY_VALUE_EN)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void getHttpResponse(Context context, final VollyCallbackListener callbackListener, URL url) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String urlToString = url.toString();

        StringRequest request = new StringRequest(Request.Method.GET, urlToString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callbackListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure(error.toString());
            }
        });

        requestQueue.add(request);
    }
}
