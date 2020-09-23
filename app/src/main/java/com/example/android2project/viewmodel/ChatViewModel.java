package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.ChatMessage;
import com.example.android2project.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private Repository mRepository;
    private FirebaseUser mUser;

    private MutableLiveData<List<ChatMessage>> mMessages;

    public MutableLiveData<List<ChatMessage>> getMessages() {
        if(mMessages==null){
            mMessages = new MutableLiveData<>();
        }
        return mMessages;
    }

    public ChatViewModel(Context context) {
        mRepository = Repository.getInstance(context);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        mRepository.downloadChatMessages(String userId);
    }

    public void uploadChatMessage(String userTalkToEmail, String messageContent){
        mRepository.uploadChatMessage(userTalkToEmail, messageContent);

    }
    // TODO: Implement the ViewModel
}