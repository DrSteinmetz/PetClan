package com.example.android2project.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Advertisement implements Serializable, Comparable<Advertisement> {
    private String mAdvertisementId;
    private User mUser;
    private GeoPoint mGeoPoint;
    private String mLocation;
    private int mPrice;
    private String mItemName;
    private String mPetKind;
    private boolean mIsSell;
    private String mDescription;
    private ArrayList<String> mImages;
    private boolean mIsMale;
    private boolean mIsPet;
    private Date mPublishDate;

    public Advertisement() {}

    public Advertisement(User user, String itemName, String location, int price, boolean isSell, String description, boolean isPet) {
        this.mAdvertisementId = user.getEmail() + System.nanoTime();
        this.mUser = user;
        this.mItemName = itemName;
        this.mLocation = location;
        this.mPrice = price;
        this.mIsSell = isSell;
        this.mDescription = description;
        this.mIsPet = isPet;
        this.mPublishDate = new Date();
    }

    public String getAdvertisementId() {
        return mAdvertisementId;
    }

    public void setAdvertisementId(String advertisementId) {
        mAdvertisementId = advertisementId;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.mGeoPoint = geoPoint;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public boolean getIsSell() {
        return mIsSell;
    }

    public void setIsSell(boolean isSell) {
        this.mIsSell = isSell;
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

    public boolean getIsMale() {
        return mIsMale;
    }

    public void setIsMale(boolean isMale) {
        this.mIsMale = isMale;
    }

    public boolean getIsPet() {
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

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    @Override
    public int compareTo(final Advertisement otherAd) {
        if (otherAd != null) {
            return otherAd.getPublishDate().compareTo(this.mPublishDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "mAdvertisementId='" + mAdvertisementId + '\'' +
                ", mUser=" + mUser +
                ", mGeoPoint=" + mGeoPoint +
                ", mLocation='" + mLocation + '\'' +
                ", mPrice=" + mPrice +
                ", mItemName='" + mItemName + '\'' +
                ", mPetKind='" + mPetKind + '\'' +
                ", mIsSell=" + mIsSell +
                ", mDescription='" + mDescription + '\'' +
                ", mImages=" + mImages +
                ", mIsMale=" + mIsMale +
                ", mIsPet=" + mIsPet +
                ", mPublishDate=" + mPublishDate +
                '}';
    }

    public String getStoragePath(String myEmail, String imageUri) {
        String s1 = imageUri.split("\\.jpg\\?alt=media&token=")[0];
        int i = 2;
        char c = s1.charAt(s1.length() - 1);
        StringBuilder id = new StringBuilder();
        while (Character.isDigit(c)) {
            id.append(c);
            c = s1.charAt(s1.length() - i++);
        }
        id = id.reverse();

        return myEmail + "/ads/" + myEmail + id + ".jpg";
    }

    public static class PriceComparator implements Comparator<Advertisement> {
        @Override
        public int compare(Advertisement advertisement, Advertisement t1) {
            return t1.getPrice() - advertisement.getPrice();
        }
    }

    public static class LocationComparator implements Comparator<Advertisement> {
        @Override
        public int compare(Advertisement advertisement, Advertisement t1) {
            return LocationUtils.getDistance(t1.getGeoPoint()) - LocationUtils.getDistance(advertisement.getGeoPoint());
        }
    }
}
