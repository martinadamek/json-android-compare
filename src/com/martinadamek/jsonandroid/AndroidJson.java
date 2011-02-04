package com.martinadamek.jsonandroid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class AndroidJson implements TestJson {

    public String getName() {
        return "Android";
    }

    public List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        try {
            String json = convertStreamToString(inputStream);
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

    private static String convertStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[1024];
        try {
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
        } finally {
            out.close();
            in.close();
        }
        return out.toString("UTF-8");
    }

}
