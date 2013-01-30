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
	private static final String DATA_LINE_PADDING = "  ";
	
	private static final String TAG = MainActivity.class.getName();
	private String mPath;
	private TextView mTextView;

	private final Runnable mTestTask = new Runnable() {
		public void run() {

			final Map<String, ResultsContainer> results = new HashMap<String, ResultsContainer>();

			testImpl(new AndroidJson(), results);
			testImpl(new SimpleJson(), results);
			testImpl(new SmartJson(), results);
			testImpl(new GsonJson(), results);
			testImpl(new JacksonJson(), results);

			runOnUiThread(new Runnable() {
				public void run() {
					writeToTextView("== Done!");

					List<String> keys = new ArrayList<String>(results.keySet());
					Collections.sort(keys);


					int minKeyLength = 0;
					for (String key: keys) {
						int length = String.valueOf(results.get(key).getParserName()).length();

						if(length > minKeyLength){
							minKeyLength = length;
						}
					}

					int minValueLength = 0;
					for (String key: keys) {
						int length = String.valueOf(results.get(key).getDuration()).length();

						if(length > minValueLength){
							minValueLength = length;
						}
					}

					ResultsContainer result;
					final String label  = "Runs: ";
					
					int runs = -1;
					for (String key: keys) {
						result = results.get(key);

						if(runs != result.getTestRepeats()){
							writeToTextView(
									"\n" +
									label + result.getTestRepeats());
							
							runs = result.getTestRepeats();
						}
						
						writeToTextView(
								DATA_LINE_PADDING + 
								StringUtils.padRight(result.getParserName(), minKeyLength) + 
								": " + 
								StringUtils.padLeft(String.valueOf(result.getDuration()), minValueLength) +
								"ms");
						
						writeToTextView(DATA_LINE_PADDING + StringUtils.padRight(" ", minKeyLength) + " " + (result.getDuration() / result.getTestRepeats()) + "/run");						
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

	private void testImpl(final TestJson testJson, Map<String, ResultsContainer> results) {
		runOnUiThread(new Runnable() {
			public void run() {
				writeToTextView("== Testing '" + testJson.getName() + "'");
			}
		});

		warmUp(testJson);

		int runs = 1;
		long duration = test(testJson, runs);
		results.put(StringUtils.padLeft(runs, 5) + "_" + testJson.getName(), new ResultsContainer(testJson.getName(), duration, runs));
		
		runs = 5;
		duration = test(testJson, runs);
		results.put(StringUtils.padLeft(runs, 5) + "_" +  testJson.getName(), new ResultsContainer(testJson.getName(), duration, runs));
		
		runs = 100;
		duration = test(testJson, runs);
		results.put(StringUtils.padLeft(runs, 5) + "_" + testJson.getName(), new ResultsContainer(testJson.getName(), duration, runs));
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
