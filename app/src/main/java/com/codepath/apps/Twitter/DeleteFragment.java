package com.codepath.apps.Twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;

public class DeleteFragment extends DialogFragment {
    private static final int REQUEST_CODE = 20;
    TwitterClient client;
    Button delete;

    public DeleteFragment() {

    }

    public static DeleteFragment newInstance(long id) {
        DeleteFragment frag = new DeleteFragment();
        Bundle args = new Bundle();
        args.putLong("id", id);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.fragment_delete, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client= TwitterApp.getRestClient(getActivity());
        delete = view.findViewById(R.id.btndelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.deleteTweet(getArguments().getLong("id"), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("deleted", "onSuccess");
                        Intent intent = new Intent();
                        intent.putExtra("position",getArguments().getInt("position"));
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("delete", "onFailure");
                    }
                });

            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
