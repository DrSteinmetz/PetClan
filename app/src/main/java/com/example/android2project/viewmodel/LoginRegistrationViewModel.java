package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;

public class LoginRegistrationViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<String> mLoginSucceed;
    private MutableLiveData<String> mLoginFailed;

    private MutableLiveData<String> mRegisterSucceed;
    private MutableLiveData<String> mRegisterFailed;

    private final String TAG = "LoginDetailsViewModel";

    public LoginRegistrationViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<String> getLoginSucceed() {
        if (mLoginSucceed == null) {
            mLoginSucceed = new MutableLiveData<>();
            attachLoginListeners();
        }
        return mLoginSucceed;
    }

    public MutableLiveData<String> getLoginFailed() {
        if (mLoginFailed == null) {
            mLoginFailed = new MutableLiveData<>();
            attachLoginListeners();
        }
        return mLoginFailed;
    }

    private void attachLoginListeners() {
        mAuthRepository.setLoginListener(new AuthRepository.RepositoryLoginInterface() {
            @Override
            public void onLoginSucceed(String uId) {
                mLoginSucceed.setValue(uId);
            }

            @Override
            public void onLoginFailed(String error) {
                mLoginFailed.setValue(error);
            }
        });
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


    public void loginWithDetails(String email, String password) {
        mAuthRepository.signInExistingUser(email, password);
    }

    public void registerWithDetails(String email, String password) {
        mAuthRepository.registerNewUser(email, password);
    }

    public void onFacebook(Fragment fragment) {
        mAuthRepository.onFacebook(fragment);
    }

    public void onGoogle(Fragment fragment) {
        mAuthRepository.onGoogle(fragment);
    }

    public void firebaseAuthWithGoogle(String idToken) {
        mAuthRepository.firebaseAuthWithGoogle(idToken);
    }
}
