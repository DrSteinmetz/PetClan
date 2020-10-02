package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.Pet;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceViewModel extends ViewModel {
    private static final String TAG = "MarketPlaceViewModel";
    private Repository mRepository;
    private StorageRepository mStorageRepository;
    private AuthRepository mAuth;
    private int mTotalCount=0;
    private int mIterationCount = 0;
    private final String PATH = "ads";

    private List<String> mPhotosDownloadString = new ArrayList<>();
    private MutableLiveData<Integer> onAdUploadPhotoLiveData;

    public MarketPlaceViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
    }

    public Query getAds() {
        return mRepository.getAllAds();
    }

    public void uploadAdPhotos(List<Uri> imageList) {
        final String userEmail = mAuth.getUserEmail();
        mTotalCount = imageList.size();
        Log.d(TAG, "uploadAdPhotos: " + mTotalCount);
        for (Uri uri : imageList) {
            mStorageRepository.uploadPhoto(PATH, uri, userEmail, 1);
        }
    }

    public MutableLiveData<Integer> getOnAdUploadPhotoLiveData() {
        if (onAdUploadPhotoLiveData == null) {
            onAdUploadPhotoLiveData = new MutableLiveData<>();
            attachUploadAdPhotosListener();
        }
        return onAdUploadPhotoLiveData;
    }

    private void attachUploadAdPhotosListener() {
        mStorageRepository.setAdUploadPicListener(new StorageRepository.StorageAdUploadPicInterface() {
            @Override
            public void onAdUploadPicSuccess(String imagePath, int iteration) {
                mIterationCount += iteration;
                mPhotosDownloadString.add(imagePath);
                Log.d(TAG, "onPetUploadPicSuccess iteration: " + iteration);
                Log.d(TAG, "onPetUploadPicSuccess: totalCount "+mTotalCount);
                if (mIterationCount == mTotalCount) {
                    onAdUploadPhotoLiveData.setValue(mIterationCount);
                    Log.d(TAG, "onAdUploadPicSuccess: " + mIterationCount);
                }

            }

            @Override
            public void onAdUploadPicFailed(String error) {
            }
        });

    }

    public void addAdvertisement(Advertisement advertisement) {
        advertisement.setImages((ArrayList<String>) mPhotosDownloadString);
        Log.d(TAG, "addAdvertisement: "+advertisement.toString());
        mRepository.uploadAd(advertisement);
        mIterationCount = 0;
        mTotalCount = 0;
        if(!mPhotosDownloadString.isEmpty()){
            mPhotosDownloadString.clear();
        }
    }

    public User getCurrentUser(){
        final String email = mAuth.getUserEmail();
        final String username = mAuth.getUserName();
        final String firstName = username.split(" ")[0];
        final String lastName = username.split(" ")[1];
        final String photoUri = mAuth.getUserImageUri();
        final String token = mAuth.getUserToken();
        return new User(email,firstName,lastName,photoUri,token);
    }
}
