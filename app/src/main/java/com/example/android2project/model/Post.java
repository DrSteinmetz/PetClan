package com.example.android2project.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post implements Serializable, Comparable<Post> {
    private String mPostId;
    private String mAuthorEmail;
    private String mAuthorName;
    private String mAuthorImageUri;
    private String mAuthorToken;
    private Date mPostTime;
    private String mAuthorContent;
    private int mCommentsCount = 0;
    private Map<String, Boolean> mLikesMap = new HashMap<>();
    private int mLikesCount = 0;
    private String mLocation;
    private GeoPoint mGeoPoint;

    public Post() {}

    public Post(String authorEmail, String authorName, String authorImageUri, String authorContent) {
        this.mPostId = authorEmail + System.nanoTime();
        this.mAuthorEmail = authorEmail;
        this.mAuthorName = authorName;
        this.mAuthorImageUri = authorImageUri;
        this.mAuthorContent = authorContent;
        this.mPostTime = new Date();
    }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        this.mPostId = postId;
    }

    public String getAuthorEmail() {
        return mAuthorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.mAuthorEmail = authorEmail;
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

    public String getAuthorToken() {
        return mAuthorToken;
    }

    public void setAuthorToken(String authorToken) {
        this.mAuthorToken = authorToken;
    }

    public String getAuthorContent() {
        return mAuthorContent;
    }

    public void setAuthorContent(String authorContent) {
        this.mAuthorContent = authorContent;
    }

    public Date getPostTime() {
        return mPostTime;
    }

    public void setPostTime(Date postTime) {
        this.mPostTime = postTime;
    }

    public int getCommentsCount() {
        return mCommentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.mCommentsCount = Math.max(commentsCount, 0);
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public void setLikesCount(int likesCount) {
        this.mLikesCount = Math.max(likesCount, 0);
    }

    public Map<String, Boolean> getLikesMap() {
        return mLikesMap;
    }

    public void setLikesMap(Map<String, Boolean> likesMap) {
        this.mLikesMap = likesMap;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.mGeoPoint = geoPoint;
    }

    @Override
    public String toString() {
        return "Post{" +
                "mPostId='" + mPostId + '\'' +
                ", mAuthorEmail='" + mAuthorEmail + '\'' +
                ", mAuthorName='" + mAuthorName + '\'' +
                ", mAuthorImageUri='" + mAuthorImageUri + '\'' +
                ", mAuthorToken='" + mAuthorToken + '\'' +
                ", mPostTime=" + mPostTime +
                ", mAuthorContent='" + mAuthorContent + '\'' +
                ", mCommentsCount=" + mCommentsCount +
                ", mLikesMap=" + mLikesMap +
                ", mLikesCount=" + mLikesCount +
                ", mLocation='" + mLocation + '\'' +
                ", mGeoPoint=" + mGeoPoint +
                '}';
    }

    @Override
    public int compareTo(final Post otherPost) {
        if (otherPost != null) {
            return otherPost.getPostTime().compareTo(this.mPostTime);
        }
        return 0;
    }
}
