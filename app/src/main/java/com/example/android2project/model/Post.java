package com.example.android2project.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String mAuthorEmail;
    private String mAuthorName;
    private String mAuthorImageUri;
    private String mPostTimeAgo;
    private String mAuthorContent;
    private List<Comment> mComments = new ArrayList<>();
    private int mLikesCount = 0;

    public Post() {}

    public Post(String mAuthorName, String mAuthorImageUri, String mAuthorContent, String mPostTimeAgo) {
        this.mAuthorName = mAuthorName;
        this.mAuthorImageUri = mAuthorImageUri;
        this.mAuthorContent = mAuthorContent;
        this.mPostTimeAgo = mPostTimeAgo;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        this.mAuthorName = authorName;
    }

    public String getAuthorImageUri() {
        return mAuthorImageUri;
    }

    public void setAuthorImageUri(String authorImageUri) {
        this.mAuthorImageUri = authorImageUri;
    }

    public String getAuthorContent() {
        return mAuthorContent;
    }

    public void setAuthorContent(String authorContent) {
        this.mAuthorContent = authorContent;
    }

    public String getPostTimeAgo() {
        return mPostTimeAgo;
    }

    public void setPostTimeAgo(String postTimeAgo) {
        this.mPostTimeAgo = postTimeAgo;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        this.mComments = comments;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public void setLikesCount(int likesCount) {
        this.mLikesCount = likesCount;
    }
}
