package com.martinadamek.jsonandroid;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class SmartJson implements TestJson {

    public String getName() {
        return "JSON.smart";
    }

    public List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
        
        try {
            JSONArray jsonArray = (JSONArray) p.parse(new InputStreamReader(inputStream));
            int size = jsonArray.size();

            for (int i = 0; i < size; i++) {
                Map map = new HashMap();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Set keys = jsonObject.keySet();
                for (Object key: keys) {
                    if ("user".equals(key)) {
                        JSONObject user = (JSONObject) jsonObject.get(key);
                        Set keys2 = user.keySet();
                        for (Object key2: keys2) {
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
}
