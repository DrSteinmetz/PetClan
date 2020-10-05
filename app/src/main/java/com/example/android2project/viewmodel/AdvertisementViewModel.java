package com.example.android2project.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementViewModel extends ViewModel implements LocationUtils.LocationDetected {
    private static final String TAG = "AdvertismentViewModel";
    private Repository mRepository;
    private AuthRepository mAuth;
    private StorageRepository mStorageRepository;
    private LocationUtils mLocationUtils;

    private int mTotalCount = 0;
    private int mIterationCount = 0;
    private final String PATH = "ads";

    private List<String> mPhotosDownloadString = new ArrayList<>();

    private MutableLiveData<Integer> onAdUploadPhotoSucceed;
    private MutableLiveData<String> onAdUploadPhotoFailed;

    private MutableLiveData<Advertisement> onAdUploadSucceed;
    private MutableLiveData<String> onAdUploadFailed;

    public AdvertisementViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
        this.mLocationUtils=LocationUtils.getInstance((Activity) context);
        mLocationUtils.setLocationListener(this);
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
                Log.d(TAG, "onAdUploadPicSuccess: qwerty" + imagePath);
                if (mIterationCount == mTotalCount) {
                    onAdUploadPhotoSucceed.setValue(mIterationCount);
                    Log.d(TAG, "onAdUploadPicSuccess: qwerty " + mIterationCount);
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
                Log.d(TAG, "onUploadAdSucceed: zxc");
                mLocationUtils.getGeoPointFromCity(advertisement);
                onAdUploadSucceed.setValue(advertisement);
            }

            @Override
            public void onUploadAdFailed(String error) {
                onAdUploadFailed.setValue(error);
            }
        });
    }


    public void uploadAdPhotos(List<String> imageList) {
        final String userEmail = mAuth.getUserEmail();
        mTotalCount = numberOfImages(imageList);
        Log.d(TAG, "uploadAdPhotos: image list zxc" + imageList);
        for (String str : imageList) {
            if (str != null && !str.contains("https://firebasestorage.googleapis.com/v0/b/petclan-2fdce.appspot.com")) {
                Uri uri = Uri.parse(str);
                mStorageRepository.uploadPhoto(PATH, uri, userEmail, 1);
            } else if (str != null && str.contains("https://firebasestorage.googleapis.com/v0/b/petclan-2fdce.appspot.com")) {
                mPhotosDownloadString.add(str);
                mIterationCount++;
            }
            if (mIterationCount == mTotalCount) {
                onAdUploadPhotoSucceed.setValue(mIterationCount);
            }
            Log.d(TAG, "uploadAdPhotos: counter zxc" + mIterationCount);
        }
    }

    public void addAdvertisement(Advertisement advertisement) {
        advertisement.setImages((ArrayList<String>) mPhotosDownloadString);
        Log.d(TAG, "addAdvertisement: " + advertisement.getImages());
        mRepository.uploadAd(advertisement);
        mIterationCount = 0;
        mTotalCount = 0;
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


    public void deletePhotoFromStorage(String photoUri) {
        mStorageRepository.deletePhotoFromStorage(photoUri);
    }

    private int numberOfImages(List<String> imageList) {
        int counter = 0;
        for (String uri : imageList) {
            if (uri != null) {
                counter++;
            }
        }
        Log.d(TAG, "numberOfImages: zxc " + counter);
        return counter;
    }

    @Override
    public void onLocationChange(Address address,Advertisement advertisement) {
        Log.d(TAG, "onLocationChange: momo");
        mRepository.updateAdLocation(address,advertisement);
    }
}
