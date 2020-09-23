package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.User;
import com.example.android2project.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationViewModel extends ViewModel {
    private Repository mRepository;
    private FirebaseUser mUser;
    private String mRecipientEmail;

    private List<ChatMessage> mConversation = new ArrayList<>();

    private MutableLiveData<List<ChatMessage>> mDownloadConversationSucceed;
    private MutableLiveData<String> mDownloadConversationFailed;

    private MutableLiveData<ChatMessage> mUploadMessageSucceed;
    private MutableLiveData<String> mUploadMessageFailed;

    public ConversationViewModel(Context context) {
        mRepository = Repository.getInstance(context);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public MutableLiveData<List<ChatMessage>> getDownloadConversationSucceed() {
        if (mDownloadConversationSucceed == null) {
            mDownloadConversationSucceed = new MutableLiveData<>();
            attachSetDownloadConversationListener();
        }
        return mDownloadConversationSucceed;
    }

    public MutableLiveData<String> getDownloadConversationFailed() {
        if (mDownloadConversationFailed == null) {
            mDownloadConversationFailed = new MutableLiveData<>();
            attachSetDownloadConversationListener();
        }
        return mDownloadConversationFailed;
    }

    private void attachSetDownloadConversationListener() {
        mRepository.setDownloadConversationListener(new Repository.RepositoryDownloadConversationInterface() {
            @Override
            public void onDownloadConversationSucceed(List<ChatMessage> conversation) {
                mDownloadConversationSucceed.setValue(conversation);
                mConversation.clear();
                mConversation.addAll(conversation);
            }

            @Override
            public void onDownloadConversationFailed(String error) {
                mDownloadConversationFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<ChatMessage> getUploadMessageSucceed() {
        if (mUploadMessageSucceed == null) {
            mUploadMessageSucceed = new MutableLiveData<>();
            attachSetUploadMessageListener();
        }
        return mUploadMessageSucceed;
    }

    public MutableLiveData<String> getUploadMessageFailed() {
        if (mUploadMessageFailed == null) {
            mUploadMessageFailed = new MutableLiveData<>();
            attachSetUploadMessageListener();
        }
        return mUploadMessageFailed;
    }

    private void attachSetUploadMessageListener() {
        mRepository.setUploadMessageListener(new Repository.RepositoryUploadMessageInterface() {
            @Override
            public void onUploadMessageSucceed(ChatMessage message) {
                mConversation.add(message);
                mUploadMessageSucceed.setValue(message);
            }

            @Override
            public void onUploadMessageFailed(String error) {
                mUploadMessageFailed.setValue(error);
            }
        });
    }

    public List<ChatMessage> getConversation() {
        return mConversation;
    }

    public void setRecipientEmail(final String mRecipientEmail) {
        this.mRecipientEmail = mRecipientEmail;
    }

    public void setConversation(final List<ChatMessage> mConversation) {
        this.mConversation = mConversation;
    }

    public void downloadConversation() {
        String tempChatId = mRecipientEmail + "&" + mUser.getEmail();
        if (Objects.requireNonNull(mUser.getEmail()).compareTo(mRecipientEmail) < 0) {
            tempChatId = mUser.getEmail() + "&" + mRecipientEmail;
        }
        final String chatId = tempChatId;

        mRepository.downloadConversation(chatId);
    }

    public void uploadChatMessage(final User userRecipient, final String messageContent) {
        mRepository.uploadChatMessage(userRecipient, messageContent);
    }
}