package com.example.android2project.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String mAuthorEmail;
    private String mAuthorName;
    private String mAuthorImageUri;
    private String mAuthorContent;
    private String mPostTimeAgo;
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

    public void setAuthorName(String mAuthorName) {
        this.mAuthorName = mAuthorName;
    }

    public String getAuthorImageUri() {
        return mAuthorImageUri;
    }

    public void setAuthorImageUri(String mAuthorImageUri) {
        this.mAuthorImageUri = mAuthorImageUri;
    }

    public String getAuthorContent() {
        return mAuthorContent;
    }

    public void setAuthorContent(String mAuthorContent) {
        this.mAuthorContent = mAuthorContent;
    }

    public String getPostTimeAgo() {
        return mPostTimeAgo;
    }

    public void setPostTimeAgo(String mPostTimeAgo) {
        this.mPostTimeAgo = mPostTimeAgo;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        this.mComments = comments;
    }
}
