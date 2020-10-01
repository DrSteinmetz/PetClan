package com.example.android2project.model;

import java.util.Date;
import java.util.List;

public class Advertisement {
    private User mUser;
    private String mLocation;
    private int mPrice;
    private String mPetType;
    private String mAdType;
    private String mDescription;
    private List<String> mImages;
    private boolean mGender;
    private boolean mIsPet;
    private Date mPublishDate;

    public Advertisement() {
    }

    public Advertisement(User user, String location, int price, String adType, String description, boolean isPet) {
        this.mUser = user;
        this.mLocation = location;
        this.mPrice = price;
        this.mAdType = adType;
        this.mDescription = description;
        this.mIsPet = isPet;
        this.mPublishDate = new Date();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public String getPetType() {
        return mPetType;
    }

    public void setPetType(String petType) {
        this.mPetType = petType;
    }

    public String getAdType() {
        return mAdType;
    }

    public void setAdType(String adType) {
        this.mAdType = adType;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public List<String> getImages() {
        return mImages;
    }

    public void setImages(List<String> images) {
        this.mImages = images;
    }

    public boolean isGender() {
        return mGender;
    }

    public void setGender(boolean gender) {
        this.mGender = gender;
    }

    public boolean isPet() {
        return mIsPet;
    }

    public void setIsPet(boolean isPet) {
        this.mIsPet = isPet;
    }

    public Date getPublishDate() {
        return mPublishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.mPublishDate = publishDate;
    }
}
