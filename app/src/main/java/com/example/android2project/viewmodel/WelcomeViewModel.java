package com.example.android2project.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;

public class WelcomeViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<Boolean> mUserDeletionSucceed;
    private MutableLiveData<String> mUserSignInSucceed;

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

    public MutableLiveData<String> getUserSignInSucceed() {
        if(mUserSignInSucceed==null){
            mUserSignInSucceed = new MutableLiveData<>();
            attachSignInListener();
        }
        return mUserSignInSucceed;
    }

    private void attachSignInListener() {
        mAuthRepository.setLoginListener(new AuthRepository.RepositoryLoginInterface() {
            @Override
            public void onLoginSucceed(String uId) {
                mUserSignInSucceed.setValue(uId);
            }

            @Override
            public void onLoginFailed(String error) {
            }
        });
    }

    public void setUserToken(final String token) {
        mAuthRepository.setUserToken(token);
    }

    public void deleteUserFromAuth() {
        mAuthRepository.deleteUserFromAuth();
    }

    public boolean isUserLoggedIn() {
        return mAuthRepository.isUserLoggedIn();
    }

    public void signInAsGuest() {
        mAuthRepository.signInExistingUser("a@gmail.com","12345678");
    }
}
