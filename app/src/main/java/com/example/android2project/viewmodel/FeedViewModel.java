package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;

import java.util.List;

public class FeedViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<List<Post>> mPostDownloadSucceed;
    private MutableLiveData<String> mPostDownloadFailed;

    private MutableLiveData<Post> mPostUploadSucceed;
    private MutableLiveData<String> mPostUploadFailed;

    private MutableLiveData<Post> mPostLikesUpdateSucceed;
    private MutableLiveData<String> mPostLikesUpdateFailed;

    private final String TAG = "FeedViewModel";

    public FeedViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
        mAuthRepository.downloadPosts();
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

    private void attachSetPostUploadListener() {
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

    public MutableLiveData<List<Post>> getPostDownloadSucceed() {
        if (mPostDownloadSucceed == null) {
            mPostDownloadSucceed = new MutableLiveData<>();
            attachSetPostDownloadListener();
        }
        return mPostDownloadSucceed;
    }

    public MutableLiveData<String> getPostDownloadFailed() {
        if (mPostDownloadFailed == null) {
            mPostDownloadFailed = new MutableLiveData<>();
            attachSetPostDownloadListener();
        }
        return mPostDownloadFailed;
    }

    private void attachSetPostDownloadListener() {
        mAuthRepository.setPostDownloadListener(new AuthRepository.RepositoryPostDownloadInterface() {
            @Override
            public void onPostDownloadSucceed(List<Post> posts) {
                mPostDownloadSucceed.setValue(posts);
            }

            @Override
            public void onPostDownloadFailed(String error) {
                mPostDownloadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Post> getPostLikesUpdateSucceed() {
        if (mPostLikesUpdateSucceed == null) {
            mPostLikesUpdateSucceed = new MutableLiveData<>();
            attachSetPostLikesUpdateListener();
        }
        return mPostLikesUpdateSucceed;
    }

    public MutableLiveData<String> getPostLikesUpdateFailed() {
        if (mPostLikesUpdateFailed == null) {
            mPostLikesUpdateFailed = new MutableLiveData<>();
            attachSetPostLikesUpdateListener();
        }
        return mPostLikesUpdateFailed;
    }

    private void attachSetPostLikesUpdateListener() {
        mAuthRepository.setPostLikesUpdatingListener(new AuthRepository.RepositoryPostLikesUpdatingInterface() {
            @Override
            public void onPostLikesUpdateSucceed(Post post) {
                mPostLikesUpdateSucceed.setValue(post);
            }

            @Override
            public void onPostLikesUpdateFailed(String error) {
                mPostLikesUpdateFailed.setValue(error);
            }
        });
    }

    public void uploadNewPost(String postContent) {
        mAuthRepository.uploadNewPost(postContent);
    }

    public void refreshPosts() {
        mAuthRepository.downloadPosts();
    }

    public void updatePostLikes(Post post, final boolean isLike) {
        mAuthRepository.updatePostLikes(post, isLike);
    }
}
