package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;

public class UserDetailsViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<String> mSetDetailsSucceed;
    private MutableLiveData<String> mSetDetailsFailed;

    public UserDetailsViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<String> getRegisterSucceed() {
        if (mSetDetailsSucceed == null) {
            mSetDetailsSucceed = new MutableLiveData<>();
            attachSetDetailsListener();
        }
        return mSetDetailsSucceed;
    }

    public MutableLiveData<String> getRegisterFailed() {
        if (mSetDetailsFailed == null) {
            mSetDetailsFailed = new MutableLiveData<>();
            attachSetDetailsListener();
        }
        return mSetDetailsFailed;
    }

    private void attachSetDetailsListener() {
        mAuthRepository.setDetailsSetListener(new AuthRepository.RepositoryDetailsSetInterface() {
            @Override
            public void onDetailsSetSucceed(String uId) {
                mSetDetailsSucceed.setValue(uId);
            }

            @Override
            public void onDetailsSetFailed(String error) {
                mSetDetailsFailed.setValue(error);
            }
        });
    }

    public void onUserDetailsInsertion(String firstName, String lastName) {
        mAuthRepository.onUserDetailsInsertion(firstName, lastName);
    }
}
