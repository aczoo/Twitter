package com.codepath.apps.Twitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.Twitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;

public class composeFragment extends DialogFragment {
    EditText compose;
    TextView countchar;
    Button post;
    TwitterClient client;

    public composeFragment() {
    }


    public static composeFragment newInstance(String title) {
        composeFragment frag = new composeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compose = view.findViewById(R.id.etcompose);
        countchar = view.findViewById(R.id.tvchar);
        compose.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countchar.setText(String.valueOf(280 - s.length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        client = TwitterApp.getRestClient(getActivity());
        post = view.findViewById(R.id.btntweet);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetcontent = compose.getText().toString();
                if (tweetcontent.isEmpty() || tweetcontent.length() >= 280) {
                    Toast.makeText(getActivity(), "Invalid tweet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                client.publicTweet(tweetcontent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("compose", "onSuccess");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i("compose", "Published Tweet : " + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweetcontent));
                            getActivity().setResult(RESULT_OK, intent);
                            getActivity().finish();
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
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}