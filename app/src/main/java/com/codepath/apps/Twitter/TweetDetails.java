package com.codepath.apps.Twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.codepath.apps.Twitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetails extends AppCompatActivity {
    Tweet t;
    TwitterClient client;
    boolean favorited;
    boolean retweeted;
    int position;
    ImageView profile, media, heart, ret, reply;
    TextView tstamp, name, username, tweet, likes, retweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status));
        }
        getSupportActionBar().setTitle("Tweet");

        profile = findViewById(R.id.ivprofile);
        media = findViewById(R.id.ivMedia);
        heart = findViewById(R.id.ivlike);
        ret = findViewById(R.id.ivretweet);
        reply = findViewById(R.id.ivreply);
        tstamp = findViewById(R.id.tvtimestamp);
        name = findViewById(R.id.tvname);
        username = findViewById(R.id.tvusername);
        tweet = findViewById(R.id.tvtweet);
        likes = findViewById(R.id.tvlikes);
        retweets = findViewById(R.id.tvretweet);

        t = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra("pos", 0);
        client = TwitterApp.getRestClient(this);

        tweet.setText(t.fullbody);
        username.setText("@" + t.u.username);
        name.setText(t.u.name);
        tstamp.setText(t.createdAt);
        likes.setText("" + t.favoriteCount);
        retweets.setText("" + t.retweetCount);
        favorited = t.favorited;
        retweeted = t.retweeted;
        if (favorited)
            heart.setImageResource(R.drawable.ic_vector_heart);
        if (retweeted)
            ret.setImageResource(R.drawable.ic_vector_retweet_stroke2);
        Glide.with(TweetDetails.this).load(t.u.profileUrl).transform(new RoundedCornersTransformation(10, 10)).into(profile);
        if (t.mediaURL != null) {
            Glide.with(TweetDetails.this).load(t.mediaURL).transform(new RoundedCornersTransformation(10, 10)).into(media);
        } else {
            media.getLayoutParams().width = 0;
            media.getLayoutParams().height = 0;
        }
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment compose = ComposeFragment.newInstance("");
                Bundle bundle = new Bundle();
                bundle.putString("username", (String)username.getText());
                compose.setArguments(bundle);
                compose.show(fm, "composeFragment");


            }
        });
    }


    public void retweet(final View view) {
        if (!retweeted) {
            client.retweet(t.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    ret.setImageResource(R.drawable.ic_vector_retweet_stroke2);
                    Intent intent = new Intent("update");
                    intent.putExtra("pos", position);
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                    Intent intent2 = new Intent("add");
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent2);
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("retweet", "onFailure");
                }
            });
        }
    }

    public void updateLike(final View view) {
        final Intent intent = new Intent("update");
        intent.putExtra("pos", position);
        if (favorited) {
            client.unlikeTweet(t.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    heart.setImageResource(R.drawable.ic_vector_heart_stroke);
                    favorited = false;
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                    Log.d("unlike", "" + t.id);

                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("unlike", "onFailure");
                }
            });

        } else {
            client.likeTweet(t.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    heart.setImageResource(R.drawable.ic_vector_heart);
                    favorited = true;
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                    Log.d("like", "" + t.id);
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("like", "onFailure");
                }
            });
        }

    }
}