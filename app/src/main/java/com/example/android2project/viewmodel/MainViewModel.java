package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.ChatsAdapter;
import com.example.android2project.repository.AuthRepository;

public class MainViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<Boolean> mSignOutSucceed;

    public MainViewModel(Context context) {
        mAuthRepository = AuthRepository.getInstance(context);
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
                ChatsAdapter.clearConversations();
                mSignOutSucceed.setValue(value);
            }
        });
    }

    public String getUserId() {
        return mAuthRepository.getUserId();
    }

    public String getUserName() {
        return mAuthRepository.getUserName();
    }

    public void signOutUser () {
        mAuthRepository.signOutUser();
    }

    public String downloadUserProfilePicture() {
        return mAuthRepository.getUserImageUri();
    }
}
