package com.example.test;

public class Complain {
    public String id, content, postId,userId;
    public Complain() {
    }

    public Complain(String id, String content, String postId, String userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
