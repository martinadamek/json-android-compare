package com.martinadamek.jsonandroid;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonJson {

    public static List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        JsonFactory f = new JsonFactory();
        try {
            JsonParser p = f.createJsonParser(inputStream);

            p.nextToken();

            while (p.nextToken() != JsonToken.END_ARRAY) {

                Map map = new HashMap();

                while (p.nextToken() != JsonToken.END_OBJECT) {

                    String key = p.getCurrentName();
                    p.nextToken(); // move to value, or START_OBJECT/START_ARRAY

                    if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                        while (p.nextToken() != JsonToken.END_OBJECT) {
                            String key2 = p.getCurrentName();
                            p.nextToken(); // move to value, or START_OBJECT/START_ARRAY
                            map.put("user." + key2, p.getText());
                        }
                    } else {
                        map.put(key, p.getText());
                    }

                }

                result.add(map);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


}
