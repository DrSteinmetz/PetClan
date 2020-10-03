package com.example.android2project.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class User implements Comparable<Object>, Serializable {
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPhotoUri;
    private boolean mIsBusiness = false;
    private String mBusinessName = null;
    private GeoPoint mGeoPoint;
    private String mToken;

    public User() {}

    public User(String email, String firstName, String lastName, String photoUri, String token) {
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhotoUri = photoUri;
        this.mToken = token;
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

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.mGeoPoint = geoPoint;
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

    @Override
    public String toString() {
        return "User{" +
                "mEmail='" + mEmail + '\'' +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mPhotoUri='" + mPhotoUri + '\'' +
                ", mIsBusiness=" + mIsBusiness +
                ", mBusinessName='" + mBusinessName + '\'' +

                ", mToken='" + mToken + '\'' +
                '}';
    }
}
