package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.Repository;
import com.example.android2project.repository.AuthRepository;

public class SettingsViewModel extends ViewModel {
    private Repository mRepository;
    private AuthRepository mAuthRepository;

    private MutableLiveData<String> mUpdateUserNameInCloudSucceed;
    private MutableLiveData<String> mUpdateUserNameInCloudFailed;

    private MutableLiveData<String> mUpdateUserNameInAuthSucceed;
    private MutableLiveData<String> mUpdateUserNameInAuthFailed;

    private MutableLiveData<String> mUpdatePasswordSucceed;
    private MutableLiveData<String> mUpdatePasswordFailed;

    public SettingsViewModel(final Context context) {
        mRepository = Repository.getInstance(context);
        mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<String> getUpdateUserNameInCloudSucceed() {
        if (mUpdateUserNameInCloudSucceed == null) {
            mUpdateUserNameInCloudSucceed = new MutableLiveData<>();
            attachSetUpdateUserNameInCloudListener();
        }
        getUpdateUserNameInAuthSucceed();
        return mUpdateUserNameInCloudSucceed;
    }

    public MutableLiveData<String> getUpdateUserNameInCloudFailed() {
        if (mUpdateUserNameInCloudFailed == null) {
            mUpdateUserNameInCloudFailed = new MutableLiveData<>();
            attachSetUpdateUserNameInCloudListener();
        }
        return mUpdateUserNameInCloudFailed;
    }

    private void attachSetUpdateUserNameInCloudListener() {
        mRepository.setUpdateUserNameListener(new Repository.RepositoryUpdateUserNameInterface() {
            @Override
            public void onUpdateUserNameSucceed(String newUserName) {
                mUpdateUserNameInCloudSucceed.setValue(newUserName);
            }

            @Override
            public void onUpdateUserNameFailed(String error) {
                mUpdateUserNameInCloudFailed.setValue(error);
            }
        });
    }

    private void getUpdateUserNameInAuthSucceed() {
        if (mUpdateUserNameInAuthSucceed == null) {
            mUpdateUserNameInAuthSucceed = new MutableLiveData<>();
            attachSetUpdateUserNameInAuthListener();
        }
    }

    public MutableLiveData<String> getUpdateUserNameInAuthFailed() {
        if (mUpdateUserNameInAuthFailed == null) {
            mUpdateUserNameInAuthFailed = new MutableLiveData<>();
            attachSetUpdateUserNameInAuthListener();
        }
        return mUpdateUserNameInAuthFailed;
    }

    private void attachSetUpdateUserNameInAuthListener() {
        mAuthRepository.setUpdateUserNameListener(new AuthRepository.RepositoryUpdateUserNameInterface() {
            @Override
            public void onUpdateUserNameSucceed(String newUsername) {
                mRepository.updateUserName(newUsername);
            }

            @Override
            public void onUpdateUserNameFailed(String error) {
                mUpdateUserNameInAuthFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<String> getUpdatePasswordSucceed() {
        if (mUpdatePasswordSucceed == null) {
            mUpdatePasswordSucceed = new MutableLiveData<>();
            attachSetUpdatePasswordListener();
        }
        return mUpdatePasswordSucceed;
    }

    public MutableLiveData<String> getUpdatePasswordFailed() {
        if (mUpdatePasswordFailed == null) {
            mUpdatePasswordFailed = new MutableLiveData<>();
            attachSetUpdatePasswordListener();
        }
        return mUpdatePasswordFailed;
    }

    private void attachSetUpdatePasswordListener() {
        mAuthRepository.setUpdatePasswordListener(new AuthRepository.RepositoryUpdatePasswordInterface() {
            @Override
            public void onUpdatePasswordSucceed(String newPassword) {
                mUpdatePasswordSucceed.setValue(newPassword);
            }

            @Override
            public void onUpdatePasswordFailed(String error) {
                mUpdatePasswordFailed.setValue(error);
            }
        });
    }


    public String getUsername() {
        return mAuthRepository.getUserName();
    }

    public void updateUserName(final String userName) {
        mAuthRepository.updateUserName(userName);
    }

    public void updatePassword(final String password) {
        mAuthRepository.updateUserPassword(password);
    }
}
