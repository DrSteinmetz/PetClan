package com.example.android2project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Pet implements Serializable {
    private String mPetId;
    private String mPetName;
    private String mAnimalType;
    private ArrayList<String> mPhotoUri;
    private String mPetDescription;

    public Pet() {}

    public Pet(String mPetName, String mAnimalType, String petDescription) {
        this.mPetId = String.valueOf(System.nanoTime());
        this.mPetName = mPetName;
        this.mAnimalType = mAnimalType;
        this.mPetDescription = petDescription;
    }

    public String getPetId() {
        return mPetId;
    }

    public void setPetId(String petId) {
        this.mPetId = petId;
    }

    public String getPetName() {
        return mPetName;
    }

    public void setPetName(String petName) {
        this.mPetName = petName;
    }

    public String getAnimalType() {
        return mAnimalType;
    }

    public void setAnimalType(String animalType) {
        this.mAnimalType = animalType;
    }

    public ArrayList<String> getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(ArrayList<String> photoUri) {
        this.mPhotoUri = photoUri;
    }

    public String getPetDescription() {
        return mPetDescription;
    }

    public void setPetDescription(String petDescription) {
        this.mPetDescription = petDescription;
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

        return myEmail + "/pets/" + myEmail + id + ".jpg";
    }
}
