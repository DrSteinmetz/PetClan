package com.example.android2project.model;

import java.util.Date;

public class Comment {
    private String mAuthorEmail;
    private String mAuthorName;
    private String mAuthorImageUri;
    private String mAuthorContent;
    private Date mTime;
    private int mLikesCount = 0;

    public Comment() {}

    public Comment(String mAuthorEmail, String mAuthorName, String mAuthorImageUri, String mAuthorContent) {
        this.mAuthorEmail = mAuthorEmail;
        this.mAuthorName = mAuthorName;
        this.mAuthorImageUri = mAuthorImageUri;
        this.mAuthorContent = mAuthorContent;
        this.mTime = new Date(System.currentTimeMillis());
    }

    public String getAuthorEmail() {
        return mAuthorEmail;
    }

    public void setAuthorEmail(String mAuthorEmail) {
        this.mAuthorEmail = mAuthorEmail;
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

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date mTime) {
        this.mTime = mTime;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public void setLikesCount(int mLikesCount) {
        this.mLikesCount = mLikesCount;
    }
}
