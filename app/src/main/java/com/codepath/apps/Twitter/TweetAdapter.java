package com.codepath.apps.Twitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.Twitter.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet t = tweets.get(position);
        holder.bind(t);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView tstamp;
        TextView name;
        TextView username;
        TextView tweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            tstamp = itemView.findViewById(R.id.timestamp);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            tweet = itemView.findViewById(R.id.tweet);
        }

        public void bind(Tweet t) {
            tweet.setText(t.body);
            username.setText(t.u.username);
            Glide.with(context).load(t.u.profileUrl).into(profile);
        }
    }
}
