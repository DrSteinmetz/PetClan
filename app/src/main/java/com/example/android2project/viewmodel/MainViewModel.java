package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.StorageRepository;

public class MainViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private StorageRepository mStorageRepository;

    private MutableLiveData<String> mGetUserName;

    private MutableLiveData<Boolean> mSignOutSucceed;

    private MutableLiveData<Uri> mDownloadPicSucceed;
    private MutableLiveData<String> mDownloadPicFailed;

    public MainViewModel(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
        mStorageRepository = StorageRepository.getInstance(context);
    }

    public MutableLiveData<String> getGetUserName() {
        if (mGetUserName == null) {
            mGetUserName = new MutableLiveData<>();
            attachGetUserNameListener();
        }
        return mGetUserName;
    }

    private void attachGetUserNameListener() {
        mAuthRepository.setGetUserNameListener(new AuthRepository.RepositoryGetUserNameInterface() {
            @Override
            public void onGetUserNameSucceed(String value) {
                mGetUserName.setValue(value);
            }
        });
    }

    public MutableLiveData<Boolean> getSignOutSucceed() {
        if (mSignOutSucceed == null) {
            mSignOutSucceed = new MutableLiveData<>();
            attachSignOutUserListener();
        }
        return mSignOutSucceed;
    }

    private void attachSignOutUserListener() {
        mAuthRepository.setSignOutUserListener(new AuthRepository.RepositorySignOutUserInterface() {
            @Override
            public void onSignOutUserSucceed(boolean value) {
                mSignOutSucceed.setValue(value);
            }
        });
    }

    public MutableLiveData<Uri> getDownloadPicSucceed() {
        if (mDownloadPicSucceed == null) {
            mDownloadPicSucceed = new MutableLiveData<>();
            attachDownloadPicListener();
        }
        return mDownloadPicSucceed;
    }

    public MutableLiveData<String> getDownloadPicFailed() {
        if (mDownloadPicFailed == null) {
            mDownloadPicFailed = new MutableLiveData<>();
            attachDownloadPicListener();
        }
        return mDownloadPicFailed;
    }

    private void attachDownloadPicListener() {
        mStorageRepository.setDownloadPicListener(new StorageRepository.StorageDownloadPicInterface() {
            @Override
            public void onDownloadPicSuccess(Uri uri) {
                mDownloadPicSucceed.setValue(uri);
            }

            @Override
            public void onDownloadPicFailed(String error) {
                mDownloadPicFailed.setValue(error);
            }
        });
    }

    public void getUserName() {
        mAuthRepository.getUserName();
    }

    public void signOutUser () {
        mAuthRepository.signOutUser();
    }

    public void downloadUserProfilePicture() {
        String userId = mAuthRepository.getUserId();
        mStorageRepository.downloadFile(userId);
    }
}
