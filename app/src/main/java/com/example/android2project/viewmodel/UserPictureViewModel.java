package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.StorageRepository;

public class UserPictureViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private StorageRepository mStorageRepository;

    private MutableLiveData<Boolean> mCreateUserSucceed;
    private MutableLiveData<String> mCreateUserFailed;

    private MutableLiveData<Boolean> mUploadPicSucceed;
    private MutableLiveData<String> mUploadPicFailed;

    private final String TAG = "UserPictureViewModel";

    public UserPictureViewModel(final Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
        mStorageRepository = StorageRepository.getInstance(context);
    }

    public MutableLiveData<Boolean> getCreateUserSucceed() {
        if (mCreateUserSucceed == null) {
            mCreateUserSucceed = new MutableLiveData<>();
            attachCreateUserListener();
        }
        return mCreateUserSucceed;
    }

    public MutableLiveData<String> getCreateUserFailed() {
        if (mCreateUserFailed == null) {
            mCreateUserFailed = new MutableLiveData<>();
            attachCreateUserListener();
        }
        return mCreateUserFailed;
    }

    private void attachCreateUserListener() {
        mAuthRepository.setCreateUserListener(new AuthRepository.RepositoryCreateUserInterface() {
            @Override
            public void onCreateUserSucceed(boolean isDefaultPic) {
                mCreateUserSucceed.setValue(isDefaultPic);
            }

            @Override
            public void onCreateUserFailed(String error) {
                mCreateUserFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Boolean> getUploadPicSucceed() {
        if (mUploadPicSucceed == null) {
            mUploadPicSucceed = new MutableLiveData<>();
            attachUploadPicListener();
        }
        return mUploadPicSucceed;
    }

    public MutableLiveData<String> getUploadPicFailed() {
        if (mUploadPicFailed == null) {
            mUploadPicFailed = new MutableLiveData<>();
            attachUploadPicListener();
        }
        return mUploadPicFailed;
    }

    private void attachUploadPicListener () {
        mStorageRepository.setUploadPicListener(new StorageRepository.StorageUploadPicInterface() {
            @Override
            public void onUploadPicSuccess(boolean value) {
                mUploadPicSucceed.setValue(value);
            }

            @Override
            public void onUploadPicFailed(String error) {
                mUploadPicFailed.setValue(error);
            }
        });
    }

    public void createNewUser(Uri imageUri) {
        String userId = mAuthRepository.getUserId();
        String selectedImage = imageUri.toString();
        if (!selectedImage.equals("/users_profile_picture/default_user_pic.png")) {
            selectedImage = mStorageRepository.uploadFile(imageUri, userId);
        }
        mAuthRepository.createNewCloudUser(selectedImage);
    }
}
