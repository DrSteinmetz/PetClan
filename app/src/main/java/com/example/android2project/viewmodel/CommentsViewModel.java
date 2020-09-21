package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Comment;
import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {
    private AuthRepository mAuthRepository;

    private MutableLiveData<List<Comment>> mCommentsDownloadSucceed;
    private MutableLiveData<String> mCommentsDownloadFailed;

    private MutableLiveData<Comment> mCommentUploadSucceed;
    private MutableLiveData<String> mCommentUploadFailed;

    private MutableLiveData<String> mCommentUpdateSucceed;
    private MutableLiveData<String> mCommentUpdatedFailed;

    private MutableLiveData<String> mCommentDeletionSucceed;
    private MutableLiveData<String> mCommentDeletionFailed;

    private final String TAG = "FeedViewModel";

    public CommentsViewModel(final Context context) {
        this.mAuthRepository = AuthRepository.getInstance(context);
    }

    public MutableLiveData<List<Comment>> getCommentsDownloadSucceed() {
        if (mCommentsDownloadSucceed == null) {
            mCommentsDownloadSucceed = new MutableLiveData<>();
            attachSetCommentsDownloadListener();
        }
        return mCommentsDownloadSucceed;
    }

    public MutableLiveData<String> getCommentsDownloadFailed() {
        if (mCommentsDownloadFailed == null) {
            mCommentsDownloadFailed = new MutableLiveData<>();
            attachSetCommentsDownloadListener();
        }
        return mCommentsDownloadFailed;
    }

    private void attachSetCommentsDownloadListener() {
        mAuthRepository.setCommentDownloadListener(new AuthRepository.RepositoryCommentDownloadInterface() {
            @Override
            public void onCommentDownloadSucceed(List<Comment> comments) {
                mCommentsDownloadSucceed.setValue(comments);
            }

            @Override
            public void onCommentDownloadFailed(String error) {
                mCommentsDownloadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Comment> getCommentUploadSucceed() {
        if (mCommentUploadSucceed == null) {
            mCommentUploadSucceed = new MutableLiveData<>();
            attachSetCommentUploadListener();
        }
        return mCommentUploadSucceed;
    }

    public MutableLiveData<String> getCommentUploadFailed() {
        if (mCommentUploadFailed == null) {
            mCommentUploadFailed = new MutableLiveData<>();
            attachSetCommentUploadListener();
        }
        return mCommentUploadFailed;
    }

    private void attachSetCommentUploadListener() {
        mAuthRepository.setCommentUploadListener(new AuthRepository.RepositoryCommentUploadInterface() {
            @Override
            public void onCommentUploadSucceed(final Comment comment) {
                mCommentUploadSucceed.setValue(comment);
            }

            @Override
            public void onCommentUploadFailed(final String error) {
                mCommentUploadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<String> getCommentUpdateSucceed() {
        if (mCommentUpdateSucceed == null) {
            mCommentUpdateSucceed = new MutableLiveData<>();
            attachSetCommentUpdateListener();
        }
        return mCommentUpdateSucceed;
    }

    public MutableLiveData<String> getCommentUpdatedFailed() {
        if (mCommentUpdatedFailed == null) {
            mCommentUpdatedFailed = new MutableLiveData<>();
            attachSetCommentUpdateListener();
        }
        return mCommentUpdatedFailed;
    }

    private void attachSetCommentUpdateListener() {
        mAuthRepository.setCommentUpdatingListener(new AuthRepository.RepositoryCommentUpdatingInterface() {
            @Override
            public void onCommentUpdatingSucceed(String updatedCommentContent) {
                mCommentUpdateSucceed.setValue(updatedCommentContent);
            }

            @Override
            public void onCommentUpdatingFailed(String error) {
                mCommentUpdatedFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<String> getCommentDeletionSucceed() {
        if (mCommentDeletionSucceed == null) {
            mCommentDeletionSucceed = new MutableLiveData<>();
            attachSetCommentDeletionListener();
        }
        return mCommentDeletionSucceed;
    }

    public MutableLiveData<String> getCommentDeletionFailed() {
        if (mCommentDeletionFailed == null) {
            mCommentDeletionFailed = new MutableLiveData<>();
            attachSetCommentDeletionListener();
        }
        return mCommentDeletionFailed;
    }

    private void attachSetCommentDeletionListener() {
        mAuthRepository.setCommentDeletingListener(new AuthRepository.RepositoryCommentDeletingInterface() {
            @Override
            public void onCommentDeletingSucceed(String commentId) {
                mCommentDeletionSucceed.setValue(commentId);
            }

            @Override
            public void onCommentDeletingFailed(String error) {
                mCommentDeletionFailed.setValue(error);
            }
        });
    }

    public void downloadComments(final Post post) {
        mAuthRepository.downloadComments(post);
    }

    public void uploadComment(final Post post, final String commentContent) {
        mAuthRepository.uploadComment(post, commentContent);
    }

    public void editComment(final Post post, final String commentId, final String commentContent) {
        mAuthRepository.updateComment(post, commentId, commentContent);
    }

    public void deleteComment(final Post post, final String commentId) {
        mAuthRepository.deleteComment(post, commentId);
    }
}