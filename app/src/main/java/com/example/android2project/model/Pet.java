package com.example.android2project.model;

import java.util.ArrayList;

public class Pet {
    private String mPetName;
    private String mAnimalType;
    private ArrayList<String> mPhotoUri;
    private String mPetDescription;

    public Pet() {}

    public Pet(String mPetName, String mAnimalType, ArrayList<String> mPhotoUri) {
        this.mPetName = mPetName;
        this.mAnimalType = mAnimalType;
        this.mPhotoUri = mPhotoUri;
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
}
