package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceViewModel extends ViewModel {
    private static final String TAG = "MarketPlaceViewModel";
    private Repository mRepository;
    private StorageRepository mStorageRepository;
    private AuthRepository mAuth;
    private int mTotalCount = 0;
    private int mIterationCount = 0;
    private final String PATH = "ads";

    private List<String> mPhotosDownloadString = new ArrayList<>();

    private MutableLiveData<Integer> onAdUploadPhotoSucceed;
    private MutableLiveData<String> onAdUploadPhotoFailed;

    private MutableLiveData<Advertisement> onAdUploadSucceed;
    private MutableLiveData<String> onAdUploadFailed;

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

    public MutableLiveData<Integer> getOnAdUploadPhotoSucceed() {
        if (onAdUploadPhotoSucceed == null) {
            onAdUploadPhotoSucceed = new MutableLiveData<>();
            attachUploadAdPhotosListener();
        }
        return onAdUploadPhotoSucceed;
    }

    public MutableLiveData<String> getOnAdUploadPhotoFailed() {
        if (onAdUploadPhotoFailed == null) {
            onAdUploadPhotoFailed = new MutableLiveData<>();
            attachUploadAdPhotosListener();
        }
        return onAdUploadPhotoFailed;
    }

    private void attachUploadAdPhotosListener() {
        mStorageRepository.setAdUploadPicListener(new StorageRepository.StorageAdUploadPicInterface() {
            @Override
            public void onAdUploadPicSuccess(String imagePath, int iteration) {
                mIterationCount += iteration;
                mPhotosDownloadString.add(imagePath);
                Log.d(TAG, "onPetUploadPicSuccess iteration: " + iteration);
                Log.d(TAG, "onPetUploadPicSuccess: totalCount " + mTotalCount);
                if (mIterationCount == mTotalCount) {
                    onAdUploadPhotoSucceed.setValue(mIterationCount);
                    Log.d(TAG, "onAdUploadPicSuccess: " + mIterationCount);
                }

            }

            @Override
            public void onAdUploadPicFailed(String error) {
                onAdUploadPhotoFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Advertisement> getOnAdUploadSucceed() {
        if (onAdUploadSucceed == null) {
            onAdUploadSucceed = new MutableLiveData<>();
            attachSetOnUploadAdListener();
        }
        return onAdUploadSucceed;
    }

    public MutableLiveData<String> getOnAdUploadFailed() {
        if (onAdUploadFailed == null) {
            onAdUploadFailed = new MutableLiveData<>();
            attachSetOnUploadAdListener();
        }
        return onAdUploadFailed;
    }

    private void attachSetOnUploadAdListener() {
        mRepository.setUploadAdListener(new Repository.RepositoryUploadAdInterface() {
            @Override
            public void onUploadAdSucceed(Advertisement advertisement) {
                onAdUploadSucceed.setValue(advertisement);
            }

            @Override
            public void onUploadAdFailed(String error) {
                onAdUploadFailed.setValue(error);
            }
        });
    }


    public void addAdvertisement(Advertisement advertisement) {
        advertisement.setImages((ArrayList<String>) mPhotosDownloadString);
        Log.d(TAG, "addAdvertisement: " + advertisement.toString());
        mRepository.uploadAd(advertisement);
        mIterationCount = 0;
        mTotalCount = 0;
        if (!mPhotosDownloadString.isEmpty()) {
            mPhotosDownloadString.clear();
        }
    }

    public User getCurrentUser() {
        final String email = mAuth.getUserEmail();
        final String username = mAuth.getUserName();
        final String firstName = username.split(" ")[0];
        final String lastName = username.split(" ")[1];
        final String photoUri = mAuth.getUserImageUri();
        final String token = mAuth.getUserToken();
        return new User(email, firstName, lastName, photoUri, token);
    }
}
