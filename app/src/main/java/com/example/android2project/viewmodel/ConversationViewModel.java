package com.example.android2project.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.User;
import com.example.android2project.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConversationViewModel extends ViewModel {
    private Repository mRepository;
//    private FirebaseMessaging mFirebaseMessaging;
    private FirebaseUser mUser;
    private Context mContext;
    private String mRecipientEmail;

    private List<ChatMessage> mConversation = new ArrayList<>();

    private MutableLiveData<List<ChatMessage>> mDownloadConversationSucceed;
    private MutableLiveData<String> mDownloadConversationFailed;

    private MutableLiveData<ChatMessage> mUploadMessageSucceed;
    private MutableLiveData<String> mUploadMessageFailed;

    private final String TAG = "ConversationViewModel";

    public ConversationViewModel(Context context) {
        mRepository = Repository.getInstance(context);
//        mFirebaseMessaging = FirebaseMessaging.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext = context;
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
            public void onUploadMessageSucceed(ChatMessage message, boolean isMine) {
                Log.d(TAG, "onUploadMessageSucceed: message " + isMine);
                if (isMine) {
                    mConversation.add(message);
                    /*final JSONObject rootObject = new JSONObject();
                    try {
                        rootObject.put("to",
                                "/topics/" + message.getRecipient().getEmail());
                        rootObject.put("data", new JSONObject()
                                .put("message", message.getContent()));

                        final String url = "https://fcm.googleapis.com/v1/projects/petclan-2fdce/messages:send";

                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        StringRequest request = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "AAAAgHuON0g:APA91bH5HRhIng-B5_Zugw3c8RMJTn8YrbZgYbXRNglQayt6fKp3L0e-2bzNRyXUvaBx4sR2MwLI8oVO2Mkz4b0h5K8IZ27FROzg6vH4R64AOoUTpK8MTkftWbpOm9sCNyIB2jI0xCBO");
                                return headers;
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return rootObject.toString().getBytes();
                            }
                        };
                        queue.add(request);
                        queue.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
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

        mRepository.downloadMessagesFromDB(mRecipientEmail,mUser.getEmail());
//        mRepository.downloadConversation(chatId);
    }

    public void uploadChatMessage(final User userRecipient, final String messageContent) {
//        mRepository.uploadChatMessage(userRecipient, messageContent);
         mRepository.uploadMessageToDB(messageContent, mUser.getEmail(), userRecipient.getEmail());
    }
}