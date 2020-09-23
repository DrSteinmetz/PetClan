package com.example.android2project.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Comparable<Object>, Serializable{
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPhotoUri;
    private boolean mIsBusiness = false;
    private String mBusinessName = null;
    private Map<String, User> mFriendsMap = new HashMap<>();
    private String mLocation;

    public User() {
    }

    public User(String email, String firstName, String lastName, String photoUri) {
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhotoUri = photoUri;
        this.mLocation="Unknown";
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.mPhotoUri = photoUri;
    }

    public boolean isBusiness() {
        return mIsBusiness;
    }

    public void setBusiness(boolean business) {
        mIsBusiness = business;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public void setBusinessName(String businessName) {
        this.mBusinessName = businessName;
    }

    public Map<String, User> getFriendsMap() {
        return mFriendsMap;
    }

    public void setFriendsMap(Map<String, User> friendsMap) {
        this.mFriendsMap = friendsMap;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof User) {
            User otherUser = (User) object;
            if (otherUser.getFirstName().compareTo(this.mFirstName) == 0) {
                return (otherUser.getLastName().compareTo(this.mLastName));
            } else {
                return (otherUser.getFirstName().compareTo(this.mFirstName));
            }
        }

        return 0;
    }
}
