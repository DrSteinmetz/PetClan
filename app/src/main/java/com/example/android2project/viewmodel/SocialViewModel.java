package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.User;

import com.example.android2project.repository.AuthRepository;

import com.example.android2project.repository.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SocialViewModel extends ViewModel {
    private Repository mRepository;

    private MutableLiveData<List<User>> mUsersList;

    public SocialViewModel(final Context context) {
        mRepository = Repository.getInstance(context);
    }

    public MutableLiveData<List<User>> getUserList() {
        if (mUsersList == null) {
            mUsersList = new MutableLiveData<>();
            attachDownloadAllUsersListener();
            downloadAllUsers();
        }
        return mUsersList;
    }

    private void attachDownloadAllUsersListener() {
        mRepository.setDownloadAllUsersListener(new Repository.RepositoryDownloadAllUsersInterface() {
            @Override
            public void onDownloadAllUsersSucceed(List<User> users) {
                Collections.sort(users);
                mUsersList.setValue(users);
            }

            @Override
            public void onDownloadAllUsersFailed(String error) {
            }
        });
    }

    public void downloadAllUsers() {
        mRepository.downloadAllUsers();
    }

}

