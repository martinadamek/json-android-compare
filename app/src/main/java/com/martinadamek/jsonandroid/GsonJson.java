package com.martinadamek.jsonandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class GsonJson implements TestJson {

    public String getName() {
        return "Gson";
    }

    public List<Map> parsePublicTimeline(InputStream inputStream) {

        List<Map> result = new ArrayList<Map>();

        try {
            Map map;
            String name;
            String name2;

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            reader.beginArray();

            while (reader.hasNext()) {
                map = new HashMap();
                reader.beginObject();

                while (reader.hasNext()) {
                    name = reader.nextName();
                    if ("user".equals(name)) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name2 = reader.nextName();
                            map.put("user." + name2, getValue(reader));
                        }
                        reader.endObject();
                    } else {
                        map.put(name, getValue(reader));
                    }
                }

                reader.endObject();
                result.add(map);
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    static Object getValue(JsonReader r) throws IOException {
        Object value = null;
        JsonToken token = r.peek();

        switch (token) {
        case NULL:
            r.nextNull();
            break;
        case BOOLEAN:
            value = r.nextBoolean();
            break;
        default:
            value = r.nextString();
        }

        return value;
    }

}
