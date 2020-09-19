package com.example.android2project.model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post implements Comparable<Object> {
    private String mPostId;
    private String mAuthorEmail;
    private String mAuthorName;
    private String mAuthorImageUri;
    private Date mPostTime;
    private String mAuthorContent;
    private List<Comment> mComments = new ArrayList<>();
    private Map<String, Boolean> mLikesMap = new HashMap<>();
    private int mLikesCount = 0;

    public Post() {
    }

    public Post(String authorEmail, String authorName, String authorImageUri, String authorContent) {
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
        this.mLikesCount = likesCount >= 0 ? likesCount : 0;
    }

    public Map<String, Boolean> getLikesMap() {
        return mLikesMap;
    }

    public void setLikesMap(Map<String, Boolean> likesMap) {
        this.mLikesMap = likesMap;
    }

    @Override
    public String toString() {
        return "Post: {" +
                "AuthorEmail='" + mAuthorEmail + '\'' +
                ", AuthorName='" + mAuthorName + '\'' +
                ", AuthorImageUri='" + mAuthorImageUri + '\'' +
                ", PostTime=" + mPostTime +
                ", AuthorContent='" + mAuthorContent + '\'' +
                ", Comments=" + mComments +
                ", LikesMap=" + mLikesMap +
                ", LikesCount=" + mLikesCount +
                '}';
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof Post) {
            Post otherPost = (Post) object;
            return otherPost.getPostTime().compareTo(this.mPostTime);
        }
        return 0;
    }
}
