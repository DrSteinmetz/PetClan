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

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
public class ChatClanViewModel extends ViewModel {
    private static ChatClanViewModel chatClanViewModel;
    private Repository mRepository;
    private List<User> mUsers = new ArrayList<>();

    private MutableLiveData<List<User>> mUsersLiveData;

=======

public class ChatClanViewModel extends ViewModel {
    private static ChatClanViewModel chatClanViewModel;
    private Repository mRepository;
    private List<User> mUsers = new ArrayList<>();

    private MutableLiveData<List<User>> mUsersLiveData;

>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
    public static ChatClanViewModel getInstance(Context context) {
        if (chatClanViewModel == null) {
            chatClanViewModel = new ChatClanViewModel(context);
        }
        return chatClanViewModel;
    }

    private ChatClanViewModel(Context context) {
        mRepository = Repository.getInstance(context);
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
<<<<<<< HEAD
        this.mUsers.clear();
=======
        if(!mUsers.isEmpty()) {
            this.mUsers.clear();
        }
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
        this.mUsers.addAll(users);
        mUsersLiveData.setValue(users);
    }
}
