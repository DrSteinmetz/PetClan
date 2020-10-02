package com.example.android2project.model;

import java.util.ArrayList;
import java.util.Date;

public class Advertisement implements Comparable<Object> {
    private User mUser;
    private String mLocation;
    private int mPrice;
    private String mPetType;
    private String mPetKind;
    private boolean mAdType;
    private String mDescription;
    private ArrayList<String> mImages;
    private boolean mGender;
    private boolean mIsPet;
    private Date mPublishDate;

    public Advertisement() {}

    public Advertisement(User user, String location, int price, boolean adType, String description, boolean isPet) {
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

    public boolean getAdType() {
        return mAdType;
    }

    public void setAdType(boolean adType) {
        this.mAdType = adType;
    }

    public String getPetKind() {
        return mPetKind;
    }

    public void setPetKind(String petKind) {
        this.mPetKind = petKind;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public ArrayList<String> getImages() {
        return mImages;
    }

    public void setImages(ArrayList<String> images) {
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

    @Override
    public int compareTo(final Object object) {
        if (object instanceof Advertisement) {
            Advertisement otherAd = (Advertisement) object;
            return this.mPublishDate.compareTo(otherAd.getPublishDate());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "mUser=" + mUser +
                ", mLocation='" + mLocation + '\'' +
                ", mPrice=" + mPrice +
                ", mPetType='" + mPetType + '\'' +
                ", mPetKind='" + mPetKind + '\'' +
                ", mAdType=" + mAdType +
                ", mDescription='" + mDescription + '\'' +
                ", mImages=" + mImages +
                ", mGender=" + mGender +
                ", mIsPet=" + mIsPet +
                ", mPublishDate=" + mPublishDate +
                '}';
    }
}
