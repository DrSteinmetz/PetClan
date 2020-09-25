package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.google.firebase.database.Query;

public class ChatsViewModel extends ViewModel {
    private AuthRepository mAuth;
    private Repository mRepository;

    public ChatsViewModel(final Context context) {
        this.mAuth = AuthRepository.getInstance(context);
        mRepository = Repository.getInstance(context);
    }

    public Query ChatsQuery() {
        return mRepository.ChatsQuery();
    }
}