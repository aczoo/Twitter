package com.codepath.apps.Twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.Twitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    TweetAdapter adapter;
    RecyclerView rvt;
    ProgressBar pb;
    List<Tweet> tweets;
    EndlessRecyclerViewScrollListener scrollListener;
    private final int REQUEST_CODE=30;
    private SwipeRefreshLayout swipeContainer;
    public static final String TAG="TimelineActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {fetchTimelineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.blue);



        if (android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status));
        }
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        client = TwitterApp.getRestClient(this);
        rvt = findViewById(R.id.rv_tweets);
        pb = findViewById(R.id.progressBar);
        tweets= new ArrayList<>();
        adapter=new TweetAdapter(this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvt.setLayoutManager(layoutManager);
        rvt.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: "+ page);
                loadMoreData();
            }
        };
        rvt.addOnScrollListener(scrollListener);
        populateHomeTimeline();
    }

    private void loadMoreData() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loading more data: "+ json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets =Tweet.fromJsonArray(jsonArray,pb);
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure for loading more data");

            }
        }, tweets.get(tweets.size()-1).id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.etcompose){
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i,REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);*/
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment compose = ComposeFragment.newInstance("does not Know what this does");
        compose.show(fm, "composeFragment");
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE&& resultCode==RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tweet);
            adapter.notifyItemInserted(0);
            rvt.smoothScrollToPosition(0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void fetchTimelineAsync(int page) {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray, pb));
                    Log.d(TAG, "tweets size : "+tweets.size());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "gets JSON");

                } catch (JSONException e) {
                    Log.d(TAG, "JSON Exception");
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error");
            }
        });
    }


    private void populateHomeTimeline() {
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray, pb));
                    Log.d(TAG, "tweets size : "+tweets.size());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "gets JSON");

                } catch (JSONException e) {
                    Log.d(TAG, "JSON Exception");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure", throwable);
            }
        });
    }
}