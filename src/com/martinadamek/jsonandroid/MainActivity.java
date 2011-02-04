package com.martinadamek.jsonandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private LinearLayout mLayout;
    private LinearLayout.LayoutParams mLayoutParams;
    private String mPath;

    private final Runnable mTestTask = new Runnable() {
        public void run() {

            final Map<String, Long> results = new HashMap<String, Long>();

            TestJson testJson = new AndroidJson();
            warmUp(testJson);
            long duration = test(testJson);
            results.put(testJson.getName(), duration);

            testJson = new SimpleJson();
            warmUp(testJson);
            duration = test(testJson);
            results.put(testJson.getName(), duration);

            testJson = new GsonJson();
            warmUp(testJson);
            duration = test(testJson);
            results.put(testJson.getName(), duration);

            testJson = new JacksonJson();
            warmUp(testJson);
            duration = test(testJson);
            results.put(testJson.getName(), duration);

            runOnUiThread(new Runnable() {
                public void run() {

                    mLayout.removeAllViews();

                    for (Map.Entry<String, Long> entry: results.entrySet()) {
                        TextView textView = new TextView(MainActivity.this);
                        textView.setText(entry.getKey() + ": " + entry.getValue() + "ms");
                        mLayout.addView(textView, mLayoutParams);
                    }

                }
            });

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mLayout = (LinearLayout) findViewById(R.id.layout);
        mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        mPath = "com/martinadamek/jsonandroid/public_timeline.json";

        TextView textView = new TextView(MainActivity.this);
        textView.setText("Running tests...");
        mLayout.addView(textView, mLayoutParams);

        new Thread(mTestTask).start();

    }

    private void warmUp(final TestJson testJson) {
        InputStream inputStream;
        for (int i = 0; i < 5; i++) {
            inputStream = getClass().getClassLoader().getResourceAsStream(mPath);
            testJson.parsePublicTimeline(inputStream);
        }
    }

    private long test(final TestJson testJson) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(mPath);

        long start = System.currentTimeMillis();
        List<Map> result = testJson.parsePublicTimeline(inputStream);
        long end = System.currentTimeMillis();

        verify(result);

        return end - start;
    }

    private static void verify(List<Map> result) {
        if (result.size() != 20) {
            throw new IllegalStateException("Expected 20 but was " + result.size());
        }
        for (Map map: result) {
            if (map.size() != 52) {
                throw new IllegalStateException("Expected 52 but was " + result.size());
            }

        }
    }

    private static String map2json(Map map) {
        StringBuilder sb = new StringBuilder("{");

        for (Object key: map.keySet()) {
            sb.append('"').append(key).append('"').append(':').append('"').append(map.get(key)).append('"').append(",");
        }

        sb.append("}");
        return sb.toString();
    }

}
