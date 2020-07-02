package com.codepath.apps.Twitter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.Twitter.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
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
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView profile;
        ImageView media;
        TextView tstamp;
        TextView name;
        TextView username;
        TextView tweet;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.ivprofile);
            media = itemView.findViewById(R.id.ivMedia);
            tstamp = itemView.findViewById(R.id.tvtimestamp);
            name = itemView.findViewById(R.id.tvname);
            username = itemView.findViewById(R.id.tvusername);
            tweet = itemView.findViewById(R.id.tvtweet);
            itemView.setOnClickListener(this);

        }

        public void bind(Tweet t) {
            tweet.setText(t.body);
            username.setText("@"+t.u.username);
            name.setText(t.u.name);
            tstamp.setText(t.timestamp);
            Glide.with(context).load(t.u.profileUrl).transform(new RoundedCornersTransformation(10, 10)).into(profile);
            if (t.mediaURL!= null){
                Glide.with(context).load(t.mediaURL).transform(new RoundedCornersTransformation(10, 10)).into(media);
            }
            else{
                media.getLayoutParams().width=0;
                media.getLayoutParams().height=0;
            }

        }
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetails.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }
}
