package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.User;
<<<<<<< HEAD
=======
import com.example.android2project.repository.AuthRepository;
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
import com.example.android2project.repository.Repository;

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
<<<<<<< HEAD
}
=======
}
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
