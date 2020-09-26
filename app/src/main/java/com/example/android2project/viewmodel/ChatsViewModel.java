package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Conversation;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class ChatsViewModel extends ViewModel {
    private AuthRepository mAuth;
    private Repository mRepository;

    private List<Conversation> mConversations = new ArrayList<>();

    private MutableLiveData<List<Conversation>> mDownloadActiveConversationsSucceed;
    private MutableLiveData<String> mDownloadActiveConversationsFailed;

    public ChatsViewModel(final Context context) {
        this.mAuth = AuthRepository.getInstance(context);
        mRepository = Repository.getInstance(context);
    }

    public MutableLiveData<List<Conversation>> getGetActiveConversationsSucceed() {
        if (mDownloadActiveConversationsSucceed == null) {
            mDownloadActiveConversationsSucceed = new MutableLiveData<>();
            attachSetGetActiveConversationsListener();
        }
        return mDownloadActiveConversationsSucceed;
    }

    public MutableLiveData<String> getGetActiveConversationsFailed() {
        if (mDownloadActiveConversationsFailed == null) {
            mDownloadActiveConversationsFailed = new MutableLiveData<>();
            attachSetGetActiveConversationsListener();
        }
        return mDownloadActiveConversationsFailed;
    }

    private void attachSetGetActiveConversationsListener() {
        mRepository.setDownloadActiveChatsListener(new Repository.RepositoryDownloadActiveChatsInterface() {
            @Override
            public void onDownloadActiveChatsSucceed(List<Conversation> conversations) {
                mConversations.clear();
                mConversations.addAll(conversations);
                mDownloadActiveConversationsSucceed.setValue(conversations);
            }

            @Override
            public void onDownloadActiveChatsFailed(String error) {
                mDownloadActiveConversationsFailed.setValue(error);
            }
        });
    }

    public List<Conversation> getConversations() {
        return mConversations;
    }

    public void getActiveChats() {
        mRepository.downloadActiveChats();
    }
}