package com.martinadamek.jsonandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        String path = "com/martinadamek/jsonandroid/public_timeline.json";

        // Android built-in ============================================================

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);

        long start = System.currentTimeMillis();
        List<Map> result = AndroidJson.parsePublicTimeline(inputStream);
        long end = System.currentTimeMillis();

        verify(result);

        TextView textView = new TextView(this);
        textView.setText("android built-in: " + (end - start) + "ms");
        layout.addView(textView, layoutParams);

        // json-simple =================================================================

        inputStream = getClass().getClassLoader().getResourceAsStream(path);

        start = System.currentTimeMillis();
        result = SimpleJson.parsePublicTimeline(inputStream);
        end = System.currentTimeMillis();

        verify(result);

        textView = new TextView(this);
        textView.setText("simple-json: " + (end - start) + "ms");
        layout.addView(textView, layoutParams);

        // jackson =====================================================================

        inputStream = getClass().getClassLoader().getResourceAsStream(path);

        start = System.currentTimeMillis();
        result = JacksonJson.parsePublicTimeline(inputStream);
        end = System.currentTimeMillis();

        verify(result);

        textView = new TextView(this);
        textView.setText("jackson: " + (end - start) + "ms");
        layout.addView(textView, layoutParams);

        // gson =========================================================================

        inputStream = getClass().getClassLoader().getResourceAsStream(path);

        start = System.currentTimeMillis();
        result = GsonJson.parsePublicTimeline(inputStream);
        end = System.currentTimeMillis();

        verify(result);

        textView = new TextView(this);
        textView.setText("gson: " + (end - start) + "ms");
        layout.addView(textView, layoutParams);

    }

    static void verify(List<Map> result) {
        if (result.size() != 20) {
            throw new IllegalStateException("Expected 20 but was " + result.size());
        }
        for (Map map: result) {
            if (map.size() != 52) {
                throw new IllegalStateException("Expected 52 but was " + result.size());
            }

        }
    }

    static String map2json(Map map) {
        StringBuilder sb = new StringBuilder("{");

        for (Object key: map.keySet()) {
            sb.append('"').append(key).append('"').append(':').append('"').append(map.get(key)).append('"').append(",");
        }

        sb.append("}");
        return sb.toString();
    }

}
