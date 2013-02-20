package com.martinadamek.jsonandroid;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleJson implements TestJson {

    public String getName() {
        return "JSON.simple";
    }

    public List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        JSONParser p = new JSONParser();
        try {
			Map map;
			Set keys;
			Set keys2;
			JSONObject user;
			JSONObject jsonObject;
        	
            JSONArray jsonArray = (JSONArray) p.parse(new InputStreamReader(inputStream));
            int size = jsonArray.size();

            for (int i = 0; i < size; i++) {
                map = new HashMap();
                jsonObject = (JSONObject) jsonArray.get(i);

                keys = jsonObject.keySet();
                for (Object key: keys) {
                    if ("user".equals(key)) {
                        user = (JSONObject) jsonObject.get(key);
                        keys2 = user.keySet();
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
