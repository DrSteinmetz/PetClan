package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Pet;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

import java.util.ArrayList;
import java.util.List;

public class PetViewModel extends ViewModel {
    private AuthRepository mAuth;
    private Repository mRepository;
    private StorageRepository mStorageRepository;
    private int mIterationCount = 0;
    private int mTotalCount = 0;

    private MutableLiveData<Integer> onPetUploadPhotoLiveData;
    private List<String> mPhotosDownloadString = new ArrayList<>();

    final String TAG = "PetViewModel";

    public PetViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
    }


    public void uploadPetPhotos(List<Uri> imageList) {
        final String userEmail = mAuth.getUserEmail();
        mTotalCount = imageList.size();
        Log.d(TAG, "uploadPetPhotos: " + mTotalCount);
        for (Uri uri : imageList) {
            mStorageRepository.uploadPetPhoto(uri, userEmail, 1);
        }
    }

    public MutableLiveData<Integer> getOnPetUploadPhotoLiveData() {
        if (onPetUploadPhotoLiveData == null) {
            onPetUploadPhotoLiveData = new MutableLiveData<>();
            attachUploadPetPhotosListener();
        }
        return onPetUploadPhotoLiveData;
    }

    private void attachUploadPetPhotosListener() {
        mStorageRepository.setPetUploadPicListener(new StorageRepository.StoragePetUploadPicInterface() {
            @Override
            public void onPetUploadPicSuccess(String imagePath, int iteration) {
                mIterationCount += iteration;
                mPhotosDownloadString.add(imagePath);
                if (mIterationCount == mTotalCount) {
                    onPetUploadPhotoLiveData.setValue(mIterationCount);
                    Log.d(TAG, "onPetUploadPicSuccess: " + mIterationCount);
                }

            }

            @Override
            public void onPetUploadPicFailed(String error) {
            }
        });

    }

    public List<String> getPhotosDownloadString() {
        return mPhotosDownloadString;
    }

    public void addPetToUser(Pet pet) {
        pet.setPhotoUri((ArrayList<String>) mPhotosDownloadString);
        mRepository.uploadPetToUser(pet);
        mIterationCount = 0;
        mTotalCount = 0;
        mPhotosDownloadString = null;

    }
}