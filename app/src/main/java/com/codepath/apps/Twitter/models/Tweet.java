package com.codepath.apps.Twitter.models;

import android.text.format.DateUtils;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public String timestamp;
    public String mediaURL;
    public long id;
    public boolean favorited;
    public boolean retweeted;
    public int favoriteCount;
    public int retweetCount;
    public User u;

    public Tweet(){
    }
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("full_text");
        getRelativeTimeAgo(tweet, jsonObject.getString("created_at"));
        tweet.id = jsonObject.getLong("id");
        tweet.u = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.favorited=jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount= jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        if (jsonObject.getJSONObject("entities").has("media")&& jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).has("media_url_https"))
          {
            tweet.mediaURL = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }
        return tweet;
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray, ProgressBar pb) throws JSONException{
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i <jsonArray.length();i++){
            pb.setProgress(i*4);
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        pb.setVisibility(ProgressBar.INVISIBLE);
        return tweets;
    }
    public static void getRelativeTimeAgo(Tweet t,String rawJsonDate) {
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String detailedformat ="h:mm a d MMM yy";

        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        SimpleDateFormat sf2 = new SimpleDateFormat(detailedformat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            Date dateMillis = sf.parse(rawJsonDate);
            t.createdAt=sf2.format(dateMillis);
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis.getTime(),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (relativeDate.charAt(1) == ' '){
            t.timestamp=""+relativeDate.charAt(0)+relativeDate.charAt(2);

        }
        else if(relativeDate.charAt(2) == ' '){
            t.timestamp=""+relativeDate.substring(0,2) +relativeDate.charAt(3);

        }
        else{
            t.timestamp= relativeDate;
        }
    }

}
