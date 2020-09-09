package com.example.android2project;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private String Email;
    private String photoUri;

    public User(String fullName, String email, String photoUri) {
        this.fullName = fullName;
        Email = email;
        this.photoUri = photoUri;
    }

    public User() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}


