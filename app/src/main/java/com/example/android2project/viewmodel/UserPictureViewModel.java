package com.example.android2project.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.StorageRepository;

public class UserPictureViewModel extends AndroidViewModel {
    private MutableLiveData<String> mCreateUserSucceed;
    private MutableLiveData<String> mCreateUserFailed;

    private AuthRepository mAuthRepository;

    private StorageRepository mStorageRepository;

    private final String TAG = "UserPictureViewModel";

    public UserPictureViewModel(@NonNull Application application) {
        super(application);
        mAuthRepository = AuthRepository.getInstance(application.getApplicationContext());
        mStorageRepository = new StorageRepository(application.getApplicationContext());
    }

    public MutableLiveData<String> getCreateUserSucceed() {
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
            public void onCreateUserSucceed(String uId) {
                mCreateUserSucceed.setValue(uId);
            }

            @Override
            public void onCreateUserFailed(String error) {
                mCreateUserFailed.setValue(error);
            }
        });
    }

    public void createNewUser(Uri imageUri) {
        String userId = mAuthRepository.getUserId();
        String selectedImage = imageUri.toString();
        if (!selectedImage.equals("/users_profile_picture/default_user_pic.jpg")) {
            selectedImage = mStorageRepository.uploadFile(imageUri, userId);
        }
        mAuthRepository.createNewCloudUser(selectedImage);
    }
}
