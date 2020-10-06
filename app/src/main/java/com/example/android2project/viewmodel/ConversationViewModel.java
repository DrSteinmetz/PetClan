package com.example.android2project.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.R;
import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.NotificationUtils;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationViewModel extends ViewModel {

    private Repository mRepository;
    private AuthRepository mAuthRepository;
    private FirebaseUser mUser;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private User mRecipient;
    private String mRecipientEmail;

    private List<ChatMessage> mConversation = new ArrayList<>();

    private MutableLiveData<ChatMessage> mUploadMessageSucceed;
    private MutableLiveData<String> mUploadMessageFailed;

    private final String TAG = "ConversationViewModel";

    public ConversationViewModel(Context context) {
        mRepository = Repository.getInstance(context);
        mAuthRepository = AuthRepository.getInstance(context);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext = context;
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
                sendMessageNotification(message);
                //mUploadMessageSucceed.setValue(message);
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

    public void uploadChatMessage(final User userRecipient, final String messageContent) {
        mRecipient = userRecipient;
        mRepository.uploadMessageToDB(messageContent,
                Objects.requireNonNull(mUser.getEmail()),
                userRecipient.getEmail());
    }

    public Query ConversationQuery(String chatId) {
        return mRepository.ConversationQuery(chatId);
    }

    public String getUserEmail() {
        String userEmail = null;

        if (mUser != null) {
            userEmail = mUser.getEmail();
        }

        return userEmail;
    }

    private void sendMessageNotification(final ChatMessage message) {
        final JSONObject rootObject = new JSONObject();
        final JSONObject dataObject = new JSONObject();
        final JSONObject notificationObject = new JSONObject();
        try {
            rootObject.put("to", mRecipient.getToken());
            notificationObject.put("title", mUser.getDisplayName());
            notificationObject.put("body", message.getContent());
            notificationObject.put("tag", mUser.getEmail());
            notificationObject.put("icon", R.drawable.ic_petclan_logo);
            notificationObject.put("click_action", "OPEN_MAIN_ACTIVITY");

            dataObject.put("type", "chat");
            dataObject.put("email", mUser.getEmail());
            dataObject.put("name", mUser.getDisplayName());
            dataObject.put("photo", mUser.getPhotoUrl());
            dataObject.put("chat_id", generateChatId());
            dataObject.put("message", message.getContent());
            dataObject.put("token", mAuthRepository.getUserToken());

            //rootObject.put("notification", notificationObject);
            rootObject.put("data", dataObject);

            NotificationUtils.sendNotification(mContext, rootObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String generateChatId() {
        final String id1 = Objects.requireNonNull(mUser.getEmail()).replace(".", "");
        final String id2 = mRecipientEmail.replace(".", "");

        String chatId = id2 + "&" + id1;
        if (Objects.requireNonNull(id1).compareTo(id2) < 0) {
            chatId = id1 + "&" + id2;
        }

        return chatId;
    }
}
