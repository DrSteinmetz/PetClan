package com.example.android2project.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.NotificationUtils;
import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private Repository mRepository;
    private AuthRepository mAuthRepository;
    private StorageRepository mStorageRepository;
    protected String mUserEmail = null;
    private boolean mIsLike;

    protected List<Post> mPosts = new ArrayList<>();
    protected int mPosition;

    private MutableLiveData<List<Post>> mPostDownloadSucceed;
    private MutableLiveData<String> mPostDownloadFailed;

    private MutableLiveData<List<Post>> mUserPostDownloadSucceed;
    private MutableLiveData<String> mUserPostDownloadFailed;

    private MutableLiveData<Post> mPostUploadSucceed;
    private MutableLiveData<String> mPostUploadFailed;

    private MutableLiveData<Integer> mPostUpdateSucceed;
    private MutableLiveData<String> mPostUpdatedFailed;

    private MutableLiveData<Integer> mPostLikesUpdateSucceed;
    private MutableLiveData<String> mPostLikesUpdateFailed;

    private MutableLiveData<Integer> mPostDeletionSucceed;
    private MutableLiveData<String> mPostDeletionFailed;

    private final String PATH = "posts";

    private final String TAG = "FeedViewModel";

    public FeedViewModel(final Context context) {
        this.mContext = context;
        this.mRepository = Repository.getInstance(context);
        this.mAuthRepository = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
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
        Observer<List<Post>> onPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (mUserEmail == null) {
                    if (!mPosts.isEmpty()) {
                        mPosts.clear();
                    }
                    mPosts.addAll(posts);
                    mPostDownloadSucceed.setValue(mPosts);
                }
            }
        };
        mRepository.getRepositoryPostDownloadSucceedMLD().observeForever(onPostDownloadSucceed);

        Observer<String> onPostDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mPostDownloadFailed.setValue(error);
            }
        };
        mRepository.getRepositoryPostDownloadFailedMLD().observeForever(onPostDownloadFailed);
    }

    public MutableLiveData<List<Post>> getUserPostDownloadSucceed() {
        if (mUserPostDownloadSucceed == null) {
            mUserPostDownloadSucceed = new MutableLiveData<>();
        }
        attachSetUserPostDownloadListener();
        return mUserPostDownloadSucceed;
    }

    public MutableLiveData<String> getUserPostDownloadFailed() {
        if (mUserPostDownloadFailed == null) {
            mUserPostDownloadFailed = new MutableLiveData<>();
        }
        attachSetUserPostDownloadListener();
        return mUserPostDownloadFailed;
    }

    private void attachSetUserPostDownloadListener() {
        Observer<List<Post>> onUserPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (mUserEmail != null) {
                    if (!mPosts.isEmpty()) {
                        mPosts.clear();
                    }
                    mPosts.addAll(posts);
                    mUserPostDownloadSucceed.setValue(mPosts);
                }
            }
        };
        mRepository.getRepositoryUserPostDownloadSucceedMLD().observeForever(onUserPostDownloadSucceed);

        Observer<String> onUserPostDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mUserPostDownloadFailed.setValue(error);
            }
        };
        mRepository.getRepositoryUserPostDownloadFailedMLD().observeForever(onUserPostDownloadFailed);
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
        Observer<Post> onPostUploadSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if (mPosts.isEmpty() || !mPosts.get(0).getPostId().equals(post.getPostId())) {
                    mPosts.add(0, post);
                    mPostUploadSucceed.setValue(post);
                }
            }
        };
        mRepository.getRepositoryPostUploadSucceedMLD().observeForever(onPostUploadSucceed);

        Observer<String> onPostUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mPostUploadFailed.setValue(error);
            }
        };
        mRepository.getRepositoryPostUploadFailedMLD().observeForever(onPostUploadFailed);
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
        Observer<Post> onPostUpdateSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post updatedPost) {
                if (!mPosts.isEmpty()) {
                    mPosts.get(mPosition).setAuthorContent(updatedPost.getAuthorContent());
                    mPosts.get(mPosition).setCommentsCount(updatedPost.getCommentsCount());
                    if(mPosts.get(mPosition).getPostImageUri()!=null){
                        mPosts.get(mPosition).setPostImageUri(updatedPost.getPostImageUri());
                    }
                    mPostUpdateSucceed.setValue(mPosition);
                }
            }
        };
        mRepository.getRepositoryPostUpdateSucceedMLD().observeForever(onPostUpdateSucceed);

        Observer<String> onPostUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mPostUpdatedFailed.setValue(error);
            }
        };
        mRepository.getRepositoryPostUpdateFailedMLD().observeForever(onPostUpdateFailed);
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
        Observer<Post> onPostLikesUpdateSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                final String myEmail = mAuthRepository.getUserEmail();
                if (!post.getAuthorEmail().equals(myEmail) && mIsLike) {
                    sendLikeNotification(post);
                }
                mPostLikesUpdateSucceed.setValue(mPosition);
            }
        };
        mRepository.getRepositoryPostLikesUpdateSucceedMLD().observeForever(onPostLikesUpdateSucceed);

        Observer<String> onPostLikesUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mPostLikesUpdateFailed.setValue(error);
            }
        };
        mRepository.getRepositoryPostLikesUpdateFailedMLD().observeForever(onPostLikesUpdateFailed);
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
        Observer<String> onPostDeletionSucceed = new Observer<String>() {
            @Override
            public void onChanged(String postId) {
                if (!mPosts.isEmpty() && mPosts.get(mPosition).getPostId().equals(postId)) {
                    mPosts.remove(mPosition);
                    mPostDeletionSucceed.setValue(mPosition);
                }
            }
        };
        mRepository.getRepositoryPostDeletionSucceedMLD().observeForever(onPostDeletionSucceed);

        Observer<String> onPostDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                mPostDeletionFailed.setValue(error);
            }
        };
        mRepository.getRepositoryPostDeletionFailedMLD().observeForever(onPostDeletionFailed);

    }

    public List<Post> getPosts() {
        return mPosts;
    }

    public void setUserEmail(final String userEmail) {
        this.mUserEmail = userEmail;
    }

    public void uploadNewPost(final Post post) {
        post.setAuthorToken(mAuthRepository.getUserToken());
        mRepository.uploadNewPost(post);
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
        mIsLike = isLike;
        mRepository.updatePostLikes(mPosts.get(position), isLike);
    }

    public void deletePost(String postId, final int position) {
        mPosition = position;
        Post postToDelete = mPosts.get(mPosition);
        mRepository.deletePost(postId);
        if(postToDelete.getPostImageUri()!=null) {
            String pathToDelete = postToDelete.getStoragePath(postToDelete.getAuthorEmail(),postToDelete.getPostImageUri());
            mStorageRepository.deletePhotoFromStorage(pathToDelete);
        }
    }

    public void updateUserLocation(Address address) {
        mRepository.updateUserLocation(address);
    }

    public String getMyEmail() {
        return mAuthRepository.getUserEmail();
    }

    public String getMyName() {
       return mAuthRepository.getUserName();
    }

    public String getMyPhotoUri() {
        return mAuthRepository.getUserImageUri();
    }

    private void sendLikeNotification(final Post post) {
        final JSONObject rootObject = new JSONObject();
        final JSONObject dataObject = new JSONObject();
        try {
            rootObject.put("to", post.getAuthorToken());

            dataObject.put("type", "like");
            dataObject.put("name", mAuthRepository.getUserName());

            rootObject.put("data", dataObject);

            NotificationUtils.sendNotification(mContext, rootObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
