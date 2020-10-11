package com.example.android2project.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Comment;
import com.example.android2project.model.NotificationUtils;
import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private Repository mRepository;

    private Post mPost;

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
        this.mContext = context;
        this.mRepository = Repository.getInstance(context);
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
        mRepository.setCommentDownloadListener(new Repository.RepositoryCommentDownloadInterface() {
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
        mRepository.setCommentUploadListener(new Repository.RepositoryCommentUploadInterface() {
            @Override
            public void onCommentUploadSucceed(final Comment comment) {
                if (!comment.getAuthorEmail().equals(mPost.getAuthorEmail())) {
                    sendCommentNotification(comment);
                }
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
        mRepository.setCommentUpdatingListener(new Repository.RepositoryCommentUpdatingInterface() {
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
        mRepository.setCommentDeletingListener(new Repository.RepositoryCommentDeletingInterface() {
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


    public Post getPost() {
        return mPost;
    }

    public void setPost(Post post) {
        this.mPost = post;
    }

    public void downloadComments() {
        mRepository.downloadComments(mPost);
    }

    public void uploadComment(final String commentContent) {
        mRepository.uploadComment(mPost, commentContent);
    }

    public void editComment(final String commentId, final String commentContent) {
        mRepository.updateComment(mPost, commentId, commentContent);
    }

    public void deleteComment(final String commentId) {
        mRepository.deleteComment(mPost, commentId);
    }

    private void sendCommentNotification(final Comment comment) {
        final JSONObject rootObject = new JSONObject();
        final JSONObject dataObject = new JSONObject();
        try {
            rootObject.put("to", mPost.getAuthorToken());

            dataObject.put("type", "comment");
            dataObject.put("post_id", mPost.getPostId());
            dataObject.put("email", mPost.getAuthorEmail());
            dataObject.put("name", comment.getAuthorName());
            dataObject.put("photo", mPost.getAuthorImageUri());
            dataObject.put("post_content", mPost.getAuthorContent());
            dataObject.put("comment", comment.getAuthorContent());

            rootObject.put("data", dataObject);

            NotificationUtils.sendNotification(mContext, rootObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
