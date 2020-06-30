package com.codepath.apps.Twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.Twitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    EditText compose;
    Button post;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        compose= findViewById(R.id.compose);
        post = findViewById(R.id.btntweet);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetcontent = compose.getText().toString();
                if (tweetcontent.isEmpty()||tweetcontent.length()>=280){
                    Toast.makeText(ComposeActivity.this, "Invalid tweet!",Toast.LENGTH_SHORT).show();
                    return;
                }
                client.publicTweet(tweetcontent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("compose", "onSuccess");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i("compose", "Published Tweet : "+ tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweetcontent));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("compose", "onFailure");
                    }
                });

        }
    });

}
}