package com.willbrom.forismaticquotes.utilities;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<String> parseJson(String rawJson) {
        ArrayList<String> quoteData = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            quoteData.add(jsonObject.getString("quoteText"));
            quoteData.add(jsonObject.getString("quoteAuthor"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return quoteData;
    }
}
