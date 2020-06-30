package com.codepath.apps.Twitter.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String name;
    public String username;
    public String profileUrl;
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user=new User();
        user.name = jsonObject.getString("name");
        user.username= jsonObject.getString("screen_name");
        user.profileUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
