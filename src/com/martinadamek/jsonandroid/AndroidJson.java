package com.martinadamek.jsonandroid;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AndroidJson {

    public static List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();
        String json = convertStreamToString(inputStream);

        try {
            JSONArray jsonArray = new JSONArray(json);
            int length = jsonArray.length();

            for (int i = 0; i < length; i++) {
                Map map = new HashMap();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if ("user".equals(key)) {
                        JSONObject user = jsonObject.getJSONObject(key);
                        Iterator iterator2 = user.keys();
                        while (iterator2.hasNext()) {
                            String key2 = (String) iterator2.next();
                            map.put("user." + key2, user.get(key2));
                        }
                    } else {
                        map.put(key, jsonObject.get(key));
                    }
                }

                result.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // from http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
