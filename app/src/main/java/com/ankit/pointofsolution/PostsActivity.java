package com.ankit.pointofsolution;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostsActivity extends Activity {
	
	private static final String TAG = "PostsActivity";
	private List<Post> posts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posts);
		
		PostFetcher fetcher = new PostFetcher();
		fetcher.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.posts, menu);
		return true;
	}
	
	private void handlePostsList(List<Post> posts) {
		this.posts = posts;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for(Post post : PostsActivity.this.posts) {
					Toast.makeText(PostsActivity.this, post.storeName, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void failedLoadingPosts() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(PostsActivity.this, "Failed to load Posts. Have a look at LogCat.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	private class PostFetcher extends AsyncTask<Void, Void, String> {
		private static final String TAG = "PostFetcher";
		public static final String SERVER_URL = "https://stage.gogreenbasket.com/api/v1/pos_services/authenticate_pos";
		
		@Override
		protected String doInBackground(Void... params) {
			try {
				//Create an HTTP client
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(SERVER_URL);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("imei", "917239378970939"));
				nameValuePairs.add(new BasicNameValuePair("uid", "12345"));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				//Perform the request and check the status code
				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					System.out.print("content "+content.toString());
					try {
						//Read the server response and attempt to parse it as JSON
						Reader reader = new InputStreamReader(content);
						
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.setDateFormat("M/d/yy hh:mm a");
						Gson gson = gsonBuilder.create();
						List<Post> posts = Arrays.asList(gson.fromJson(reader, Post[].class));
						content.close();

						handlePostsList(posts);
					} catch (Exception ex) {
						Log.e(TAG, "Failed to parse JSON due to: " + ex);
						failedLoadingPosts();
					}
				} else {
					Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
					failedLoadingPosts();
				}
			} catch(Exception ex) {
				Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
				failedLoadingPosts();
			}
			return null;
		} 
	}
}
