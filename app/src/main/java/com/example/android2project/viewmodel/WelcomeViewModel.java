package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;

public class WelcomeViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<Boolean> mUserDeletionSucceed;

    public WelcomeViewModel(final Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<Boolean> getUserDeletionSucceed() {
        if (mUserDeletionSucceed == null) {
            mUserDeletionSucceed = new MutableLiveData<>();
            attachDeleteUserListener();
        }
        return mUserDeletionSucceed;
    }

    private void attachDeleteUserListener() {
        mAuthRepository.setDeleteUserListener(new AuthRepository.RepositoryDeleteUserInterface() {
            @Override
            public void onDeleteUserSucceed(boolean value) {
                mUserDeletionSucceed.setValue(value);
            }
        });
    }

    public void deleteUserFromAuth() {
        mAuthRepository.deleteUserFromAuth();
    }

    public boolean isUserLoggedIn() {
        return mAuthRepository.isUserLoggedIn();
    }
}
