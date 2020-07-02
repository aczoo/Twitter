package com.codepath.apps.Twitter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

import static com.codepath.apps.Twitter.models.Tweet.fromJson;
import static com.codepath.apps.Twitter.models.Tweet.fromJsonArray;

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

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, new IntentFilter("update"));
        LocalBroadcastManager.getInstance(this).registerReceiver(addReceiver, new IntentFilter("add"));

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
                    List<Tweet> tweets = fromJsonArray(jsonArray,pb);
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
        ComposeFragment compose = ComposeFragment.newInstance("");
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
                    tweets.addAll(fromJsonArray(jsonArray, pb));
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
                    tweets.addAll(fromJsonArray(jsonArray, pb));
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

    public BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int position = intent.getIntExtra("pos",0);
            client.updateTweet(tweets.get(position).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess");
                    try {
                        tweets.set(position,fromJson(json.jsonObject));
                        adapter.notifyItemChanged(position);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i(TAG, "onFailure", throwable);
                }
            });

        }
    };
    public BroadcastReceiver addReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            client.myRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    try {
                        Log.d("hi",json.toString());
                        tweets.add(0,fromJsonArray(json.jsonArray, null).get(0));
                        adapter.notifyItemChanged(0);
                        rvt.smoothScrollToPosition(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i(TAG, "onFailure", throwable);

                }
            });

        }
    };


    public void onReply(View view){
        FragmentManager fm = getSupportFragmentManager();
        String u= ((TextView)(view.findViewById(R.id.tvusername))).getText().toString();
        ComposeFragment compose = ComposeFragment.newInstance(u);
        Bundle bundle = new Bundle();
        bundle.putString("username", String.valueOf(view.findViewById(R.id.tvusername)));
        compose.setArguments(bundle);
        compose.show(fm, "composeFragment");
    }
}