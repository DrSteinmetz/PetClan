package com.example.android2project.model;

public class User {
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPhotoUri;
    private boolean mIsBusiness = false;
    private String mBusinessName = null;

    public User() {}

    public User(String email, String firstName, String lastName, String photoUri) {
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhotoUri = photoUri;
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
}
