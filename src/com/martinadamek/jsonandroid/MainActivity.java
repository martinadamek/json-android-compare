package com.martinadamek.jsonandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.*;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private LinearLayout mLayout;
    private LinearLayout.LayoutParams mLayoutParams;
    private String mPath;

    private final Runnable mTestTask = new Runnable() {
        public void run() {

            final Map<String, Long> results = new HashMap<String, Long>();

            testImpl(new AndroidJson(), results);
            testImpl(new SimpleJson(), results);
            testImpl(new GsonJson(), results);
            testImpl(new JacksonJson(), results);

            runOnUiThread(new Runnable() {
                public void run() {

                    mLayout.removeAllViews();

                    List<String> keys = new ArrayList<String>(results.keySet());
                    Collections.sort(keys);

                    for (String key: keys) {
                        TextView textView = new TextView(MainActivity.this);
                        textView.setText(key + ": " + results.get(key) + "ms");
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

    private void testImpl(TestJson testJson, Map<String, Long> results) {
        warmUp(testJson);
        long duration = test(testJson, 1);
        results.put("[1 run] " + testJson.getName(), duration);
        duration = test(testJson, 5);
        results.put("[5 runs] " + testJson.getName(), duration);
        duration = test(testJson, 100);
        results.put("[100 runs] " + testJson.getName(), duration);
    }

    private void warmUp(final TestJson testJson) {
        InputStream inputStream;
        for (int i = 0; i < 5; i++) {
            inputStream = getClass().getClassLoader().getResourceAsStream(mPath);
            testJson.parsePublicTimeline(inputStream);
        }
    }

    private long test(final TestJson testJson, int repeats) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(mPath);

        List<Map> result = testJson.parsePublicTimeline(inputStream);
        verify(result);

        long duration = 0;

        for (int i = 0; i < repeats; i++) {
            inputStream = getClass().getClassLoader().getResourceAsStream(mPath);
            long start = System.currentTimeMillis();
            testJson.parsePublicTimeline(inputStream);
            duration += (System.currentTimeMillis() - start);
        }

        return duration;
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
