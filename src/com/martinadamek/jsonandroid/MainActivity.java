package com.martinadamek.jsonandroid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private String mPath;
    private TextView mTextView;
    
    private final Runnable mTestTask = new Runnable() {
        public void run() {

            final Map<String, Long> results = new HashMap<String, Long>();

            testImpl(new AndroidJson(), results);
            testImpl(new SimpleJson(), results);
            testImpl(new SmartJson(), results);
            testImpl(new GsonJson(), results);
            testImpl(new JacksonJson(), results);
            
            runOnUiThread(new Runnable() {
                public void run() {
                	writeToTextView("== Done!");
                	writeToTextView("\n");
                	
                    List<String> keys = new ArrayList<String>(results.keySet());
                    Collections.sort(keys);

                    for (String key: keys) {
                    	writeToTextView(padRight(key, 12) + ": " + results.get(key) + "ms");
                    }
                }
            });

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mTextView = (TextView) findViewById(R.id.text);
        
        mPath = "com/martinadamek/jsonandroid/public_timeline.json";

        mTextView.setText("Running tests...");
        writeToTextView("-----------------");
        
        new Thread(mTestTask).start();
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
    
    private void testImpl(final TestJson testJson, Map<String, Long> results) {
        runOnUiThread(new Runnable() {
            public void run() {
            	writeToTextView("== Running tests for '" + testJson.getName() + "'");
            }
        });
        
        warmUp(testJson);
                
        long duration = test(testJson, 1);
        results.put("[1 run]    " + testJson.getName(), duration);
        duration = test(testJson, 5);
        results.put("[5 runs]   " + testJson.getName(), duration);
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

    private void writeToTextView(String text){
    	mTextView.append("\n");
    	mTextView.append(text);
    }

    private static String map2json(Map map) {
        StringBuilder sb = new StringBuilder("{");

        for (Object key: map.keySet()) {
            sb.append('"').append(key).append('"').append(':').append('"').append(map.get(key)).append('"').append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
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

}
