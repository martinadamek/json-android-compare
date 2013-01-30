package com.martinadamek.jsonandroid;

import java.util.Map;

public class StringUtils {
    public static String map2json(Map map) {
        StringBuilder sb = new StringBuilder("{");

        for (Object key: map.keySet()) {
            sb.append('"').append(key).append('"').append(':').append('"').append(map.get(key)).append('"').append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    public static String padLeft(String s, int n) {
    	return String.format("%1$" + n + "s", s);
   }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
   }
    
    public static String padLeft(long l, int n){
    	return String.format("%0"+n+"d", l);
    }
}
