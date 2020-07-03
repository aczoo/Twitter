package com.codepath.apps.Twitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.Twitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    //private static final int REQUEST_CODE = "20";
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
        View v = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profile, media, reply, retweet, like;
        TextView tstamp, name, username, tweet;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.ivprofile);
            media = itemView.findViewById(R.id.ivMedia);
            reply = itemView.findViewById(R.id.ivreply);
            retweet = itemView.findViewById(R.id.ivretweet);
            like = itemView.findViewById(R.id.ivlike);
            tstamp = itemView.findViewById(R.id.tvtimestamp);
            name = itemView.findViewById(R.id.tvname);
            username = itemView.findViewById(R.id.tvusername);
            tweet = itemView.findViewById(R.id.tvtweet);

        }

        public void bind(final Tweet t) {
            final TwitterClient client;
            tweet.setText(t.body);
            username.setText("@" + t.u.username);
            name.setText(t.u.name);
            tstamp.setText(t.timestamp);
            if (t.favorited)
                like.setImageResource(R.drawable.ic_vector_heart);
            if (t.retweeted)
                retweet.setImageResource(R.drawable.ic_vector_retweet_stroke2);
            Glide.with(context).load(t.u.profileUrl).transform(new RoundedCornersTransformation(10, 10)).into(profile);
            if (t.mediaURL != null) {
                Glide.with(context).load(t.mediaURL).transform(new RoundedCornersTransformation(10, 10)).into(media);
            } else {
                media.getLayoutParams().width = 0;
                media.getLayoutParams().height = 0;
            }
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    ComposeFragment compose = ComposeFragment.newInstance("");
                    Bundle bundle = new Bundle();
                    bundle.putString("username", (String) username.getText());
                    compose.setArguments(bundle);
                    compose.show(fm, "composeFragment");
                }
            });
            client = TwitterApp.getRestClient((AppCompatActivity) context);
            retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (!t.retweeted) {
                        client.retweet(t.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                retweet.setImageResource(R.drawable.ic_vector_retweet_stroke2);
                                Intent intent = new Intent("update");
                                intent.putExtra("pos", getAdapterPosition());
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
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (t.favorited) {
                        client.unlikeTweet(t.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                like.setImageResource(R.drawable.ic_vector_heart_stroke);
                                t.favorited = false;
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            }
                        });
                    } else {
                        client.likeTweet(t.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                like.setImageResource(R.drawable.ic_vector_heart);
                                t.favorited = true;
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            }
                        });
                    }

                }
            });
            itemView.setOnClickListener(this);

            /*itemView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick() {
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    DeleteFragment delete = DeleteFragment.newInstance(t.id);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", t.id);
                    bundle.putInt("position",getAdapterPosition());
                    delete.setArguments(bundle);
                    delete.show(fm, "DeleteFragment");
                    return true;
                }
            });
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(requestCode==REQUEST_CODE&& resultCode==RESULT_OK){
                tweets.remove(data.getInt("position"));

            }
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetails.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                intent.putExtra("pos", position);
                context.startActivity(intent);

            }
        }


    }

}
