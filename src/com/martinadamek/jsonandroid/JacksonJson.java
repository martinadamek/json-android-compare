package com.martinadamek.jsonandroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class JacksonJson implements TestJson {

    private static JsonFactory sJsonFactory = new JsonFactory();

    public String getName() {
        return "Jackson";
    }

    public List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        try {
        	Map map;
        	String key;
        	String key2;
        	
            JsonParser p = sJsonFactory.createJsonParser(inputStream);

            p.nextToken();

            while (p.nextToken() != JsonToken.END_ARRAY) {

                map = new HashMap();

                while (p.nextToken() != JsonToken.END_OBJECT) {

                    key = p.getCurrentName();
                    p.nextToken(); // move to value, or START_OBJECT/START_ARRAY

                    if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                        while (p.nextToken() != JsonToken.END_OBJECT) {
                            key2 = p.getCurrentName();
                            p.nextToken(); // move to value, or START_OBJECT/START_ARRAY
                            map.put("user." + key2, p.getText());
                        }
                    } else {
                        map.put(key, p.getText());
                    }

                }

                result.add(map);
            }

            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


}
