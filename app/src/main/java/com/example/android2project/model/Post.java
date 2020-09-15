package com.example.android2project.model;

public class Post {
    private String mAuthorName;
    private String mAuthorImageUri;
    private String mAuthorContent;
    private String mPostTimeAgo;

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
}
