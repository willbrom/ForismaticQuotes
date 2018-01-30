package com.willbrom.forismaticquotes.utilities;


import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

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

}
