package com.fun.chufengpro.pojo;

import java.io.Serializable;

/*
* 图片或者视频的实体类
* */
public class MyMedia implements Serializable {
    private String imageUrl;
    private String videoUrl;

    public MyMedia() {
    }

    public MyMedia(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MyMedia(String imageUrl, String videoUrl) {
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
