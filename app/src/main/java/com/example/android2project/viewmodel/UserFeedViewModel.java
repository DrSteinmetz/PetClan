package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Post;
import com.example.android2project.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class UserFeedViewModel extends ViewModel {
/*
    private Repository mRepository;
    protected String mUserEmail = null;

    protected List<Post> mPosts = new ArrayList<>();
    protected int mPosition;

    private MutableLiveData<List<Post>> mPostDownloadSucceed;
    private MutableLiveData<String> mPostDownloadFailed;

    private MutableLiveData<Post> mPostUploadSucceed;
    private MutableLiveData<String> mPostUploadFailed;

    private MutableLiveData<Integer> mPostUpdateSucceed;
    private MutableLiveData<String> mPostUpdatedFailed;

    private MutableLiveData<Integer> mPostLikesUpdateSucceed;
    private MutableLiveData<String> mPostLikesUpdateFailed;

    private MutableLiveData<Integer> mPostDeletionSucceed;
    private MutableLiveData<String> mPostDeletionFailed;

    private final String TAG = "UserFeedViewModel";

    public UserFeedViewModel(final Context context) {
        mRepository = Repository.getInstance(context);
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
        mRepository.setPostUploadListener(new Repository.RepositoryPostUploadInterface() {
            @Override
            public void onPostUploadSucceed(Post post) {
                mPosts.add(0, post);
                mPostUploadSucceed.setValue(post);
            }

            @Override
            public void onPostUploadFailed(String error) {
                mPostUploadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Integer> getPostUpdateSucceed() {
        if (mPostUpdateSucceed == null) {
            mPostUpdateSucceed = new MutableLiveData<>();
            attachSetPostUpdateListener();
        }
        return mPostUpdateSucceed;
    }

    public MutableLiveData<String> getPostUpdatedFailed() {
        if (mPostUpdatedFailed == null) {
            mPostUpdatedFailed = new MutableLiveData<>();
            attachSetPostUpdateListener();
        }
        return mPostUpdatedFailed;
    }

    private void attachSetPostUpdateListener() {
        mRepository.setPostUpdatingListener(new Repository.RepositoryPostUpdatingInterface() {
            @Override
            public void onPostUpdatingSucceed(Post updatedPost) {
                mPosts.get(mPosition).setAuthorContent(updatedPost.getAuthorContent());
                mPosts.get(mPosition).setCommentsCount(updatedPost.getCommentsCount());
                mPostUpdateSucceed.setValue(mPosition);
            }

            @Override
            public void onPostUpdatingFailed(String error) {
                mPostUpdatedFailed.setValue(error);
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
        mRepository.setPostDownloadListener(new Repository.RepositoryPostDownloadInterface() {
            @Override
            public void onPostDownloadSucceed(List<Post> posts) {
                if (!mPosts.isEmpty()) {
                    mPosts.clear();
                }
                mPosts.addAll(posts);
                mPostDownloadSucceed.setValue(mPosts);
            }

            @Override
            public void onPostDownloadFailed(String error) {
                mPostDownloadFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Integer> getPostLikesUpdateSucceed() {
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
        mRepository.setPostLikesUpdatingListener(new Repository.RepositoryPostLikesUpdatingInterface() {
            @Override
            public void onPostLikesUpdateSucceed(Post post) {
                mPostLikesUpdateSucceed.setValue(mPosition);
            }

            @Override
            public void onPostLikesUpdateFailed(String error) {
                mPostLikesUpdateFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<Integer> getPostDeletionSucceed() {
        if (mPostDeletionSucceed == null) {
            mPostDeletionSucceed = new MutableLiveData<>();
            attachSetPostDeletionListener();
        }
        return mPostDeletionSucceed;
    }

    public MutableLiveData<String> getPostDeletionFailed() {
        if (mPostDeletionFailed == null) {
            mPostDeletionFailed = new MutableLiveData<>();
            attachSetPostDeletionListener();
        }
        return mPostDeletionFailed;
    }

    private void attachSetPostDeletionListener() {
        mRepository.setPostDeletingListener(new Repository.RepositoryPostDeletingInterface() {
            @Override
            public void onPostDeletingSucceed(String postId) {
                if (mPosts.get(mPosition).getPostId().equals(postId)) {
                    mPosts.remove(mPosition);
                    mPostDeletionSucceed.setValue(mPosition);
                }
            }

            @Override
            public void onPostDeletingFailed(String error) {
                mPostDeletionFailed.setValue(error);
            }
        });
    }

    public void setUserEmail(final String userEmail) {
        this.mUserEmail = userEmail;
    }

    public void uploadNewPost(String postContent) {
        mRepository.uploadNewPost(postContent);
    }

    public void updatePost(Post post, final int position) {
        mPosition = position;
        mRepository.updatePost(post);
    }

    public void refreshPosts() {
        if (mUserEmail != null) {
            mRepository.downloadUserPosts(mUserEmail);
        } else {
            mRepository.downloadPosts();
        }
    }

    public void updatePostLikes(final boolean isLike, final int position) {
        mPosition = position;
        mRepository.updatePostLikes(mPosts.get(position), isLike);
    }

    public void deletePost(String postId, final int position) {
        mPosition = position;
        mRepository.deletePost(postId);
    }

    public void setPosts(List<Post> Posts) {
        this.mPosts = Posts;
    }

    public List<Post> getPosts() {
        return mPosts;
    }*/
}
