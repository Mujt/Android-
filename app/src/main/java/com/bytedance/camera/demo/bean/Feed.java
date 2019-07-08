package com.bytedance.camera.demo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Feed {
    @SerializedName("student_id")
    private String student_id;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("video_url")
    private String video_url;


    public String getImgUrl() { return image_url; }
    public String getVideoUrl() { return video_url; }
    // TODO-C2 (1) Implement your Feed Bean here according to the response json
}
