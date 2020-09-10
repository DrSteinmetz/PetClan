package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;

public class UserDetailsViewModel extends ViewModel {
    private MutableLiveData<String> mRegisterSucceed;
    private MutableLiveData<String> mRegisterFailed;

    private AuthRepository mAuthRepository;

    public UserDetailsViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<String> getRegisterSucceed() {
        if (mRegisterSucceed == null) {
            mRegisterSucceed = new MutableLiveData<>();
            attachRegistrationListener();
        }
        return mRegisterSucceed;
    }

    public MutableLiveData<String> getRegisterFailed() {
        if (mRegisterFailed == null) {
            mRegisterFailed = new MutableLiveData<>();
            attachRegistrationListener();
        }
        return mRegisterFailed;
    }

    private void attachRegistrationListener() {
        mAuthRepository.setRegistrationListener(new AuthRepository.RepositoryRegistrationInterface() {
            @Override
            public void onRegistrationSucceed(String uId) {
                mRegisterSucceed.setValue(uId);
            }

            @Override
            public void onRegistrationFailed(String error) {
                mRegisterFailed.setValue(error);
            }
        });
    }
}
