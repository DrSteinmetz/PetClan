package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;

public class FeedViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<Post> mPostUploadSucceed;
    private MutableLiveData<String> mPostUploadFailed;

    public FeedViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<Post> getPostUploadSucceed() {
        if (mPostUploadSucceed == null) {
            mPostUploadSucceed = new MutableLiveData<>();
            attachSetPostUploadListener();
        }
        return mPostUploadSucceed;
    }

    public MutableLiveData<String> getPostUploadFailed() {
        if (mPostUploadFailed == null) {
            mPostUploadFailed = new MutableLiveData<>();
            attachSetPostUploadListener();
        }
        return mPostUploadFailed;
    }

    public void attachSetPostUploadListener() {
        mAuthRepository.setPostUploadListener(new AuthRepository.RepositoryPostUploadInterface() {
            @Override
            public void onPostUploadSucceed(Post post) {
                mPostUploadSucceed.setValue(post);
            }

            @Override
            public void onPostUploadFailed(String error) {
                mPostUploadFailed.setValue(error);
            }
        });
    }

    public void uploadNewPost(String postContent) {
        mAuthRepository.uploadNewPost(postContent);
    }
}
