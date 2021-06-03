package com.fun.chufengpro.pojo;

public class Post {
    String postId;
    int postUserId;
    String postUser;
    String postContext;
    String postImages;
    String postTime;

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", postUserId=" + postUserId +
                ", postUser='" + postUser + '\'' +
                ", postContext='" + postContext + '\'' +
                ", postImages='" + postImages + '\'' +
                ", postTime='" + postTime + '\'' +
                '}';
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(int postUserId) {
        this.postUserId = postUserId;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public String getPostContext() {
        return postContext;
    }

    public void setPostContext(String postContext) {
        this.postContext = postContext;
    }

    public String getPostImages() {
        return postImages;
    }

    public void setPostImages(String postImages) {
        this.postImages = postImages;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
