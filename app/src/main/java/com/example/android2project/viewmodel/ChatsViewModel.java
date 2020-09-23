package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;

public class ChatsViewModel extends ViewModel {
    private AuthRepository mAuth;
    // TODO: Implement the ViewModel


    public ChatsViewModel(final Context context) {
        this.mAuth = AuthRepository.getInstance(context);
    }
}