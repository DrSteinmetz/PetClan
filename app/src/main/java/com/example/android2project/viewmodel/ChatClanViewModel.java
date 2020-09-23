package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;

import java.util.ArrayList;

public class ChatClanViewModel extends ViewModel {
    private AuthRepository mAuth;
    private ArrayList<User> mFriends = new ArrayList<>();

    private MutableLiveData<ArrayList<User>> mFriendsMutableLiveData;

    public ChatClanViewModel(Context context) {
        mAuth = AuthRepository.getInstance(context);
    }

    public MutableLiveData<ArrayList<User>> getFriendsMutableLiveData() {
        if (mFriendsMutableLiveData == null) {
            mFriendsMutableLiveData = new MutableLiveData<>();
            attachGetAllUsersListener();
            getAllUsers();
        }
        return mFriendsMutableLiveData;
    }

    private void attachGetAllUsersListener() {
        mAuth.setGetAllUsersListener(new AuthRepository.RepositoryGetAllUsersInterface() {
            @Override
            public void onGetAllUsersSucceed(ArrayList<User> value) {
                mFriendsMutableLiveData.setValue(value);

                if (!mFriends.isEmpty()) {
                    mFriends.clear();
                }

                mFriends.addAll(value);
            }
        });
    }

    public void getAllUsers() {
        mAuth.getAllUsers();
    }

    public ArrayList<User> getFriends(){
        return mFriends;
    }
}
