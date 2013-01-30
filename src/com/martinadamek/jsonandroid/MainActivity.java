package com.martinadamek.jsonandroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String ASSET_TWITTER_TIMELINE = "public_timeline.json";
	private static final String DATA_LINE_PADDING = "  ";
	private static final String TAG = MainActivity.class.getName();
	
	private TextView mTextView;
	private AssetManager mAssetsManager;
	
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
					String perRun;
					
					for (String key: keys) {
						result = results.get(key);

						if(runs != result.getTestRepeats()){
							writeToTextView(
									"\n" +
									label + result.getTestRepeats());
							
							runs = result.getTestRepeats();
						}
						
						perRun = " (" + result.getDuration() / result.getTestRepeats() + "ms/run)";
						
						writeToTextView(
								DATA_LINE_PADDING + 
								StringUtils.padRight(result.getParserName(), minKeyLength) + 
								": " + 
								StringUtils.padLeft(String.valueOf(result.getDuration()), minValueLength) +
								"ms" +
								perRun);					
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
		mAssetsManager = getAssets();
		
		mTextView.setText("Running tests (API Level: " +  android.os.Build.VERSION.SDK_INT + ")..." );
		writeToTextView("-----------------");

		new Thread(mTestTask).start();
	}

	private InputStream getAssetStream(String assetName){
		InputStream res;
		
		try {
			res = mAssetsManager.open( assetName );
		} catch (IOException e) {
			Log.e(TAG, "ERROR opening asset '" + assetName + "': " + e.getMessage(), e);
			res = null;
		}
		
		return res;
	}
	
	private long test(final TestJson testJson, int repeats) {
		InputStream inputStream = getAssetStream(ASSET_TWITTER_TIMELINE);

		List<Map> result = testJson.parsePublicTimeline(inputStream);
		verify(result);

		long duration = 0;

		for (int i = 0; i < repeats; i++) {
			inputStream = getAssetStream(ASSET_TWITTER_TIMELINE);
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
			inputStream = getAssetStream(ASSET_TWITTER_TIMELINE);
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
