package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.User;


import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.google.android.gms.auth.api.Auth;

import java.util.ArrayList;
import java.util.List;




public class ChatClanViewModel extends ViewModel {
    private static ChatClanViewModel chatClanViewModel;
    private Repository mRepository;
    private AuthRepository mAuth;
    private List<User> mUsers = new ArrayList<>();

    private MutableLiveData<List<User>> mUsersLiveData;

    public static ChatClanViewModel getInstance(Context context) {
        if (chatClanViewModel == null) {
            chatClanViewModel = new ChatClanViewModel(context);
        }
        return chatClanViewModel;
    }

    private ChatClanViewModel(Context context) {
        mRepository = Repository.getInstance(context);
        mAuth = AuthRepository.getInstance(context);
    }

    public MutableLiveData<List<User>> getUsersLiveData() {
        if (mUsersLiveData == null) {
            mUsersLiveData = new MutableLiveData<>();
        }
        return mUsersLiveData;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {

        if(!mUsers.isEmpty()) {
            this.mUsers.clear();
        }

        this.mUsers.addAll(users);
        mUsersLiveData.setValue(users);
    }

    public String getUserEmail(){
        return mAuth.getUserEmail();
    }

    public void signOutFromGuest() {
        mAuth.signOutUser();
    }
}
