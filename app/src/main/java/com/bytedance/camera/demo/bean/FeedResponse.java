package com.bytedance.camera.demo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:17
 */
public class FeedResponse {
    /*@SerializedName("student_id")
    String id;
    @SerializedName("user_name")
    String name;
    @SerializedName("cover_image")
    File image;
    @SerializedName("video")
    File video;
    @SerializedName("success")
    Boolean success;
    @SerializedName("feeds")
    String feeds;*/
    @SerializedName("feeds")
    private List<Feed> feeds;
    @SerializedName("success")
    private boolean success;

    public List<Feed> getFeeds() {
        return feeds;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
}
