package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Post;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

public class AddPostViewModel extends ViewModel {

    private static final String TAG = "AddPostViewModel";
    private Repository mRepository;
    private AuthRepository mAuthRepository;
    private StorageRepository mStorageRepository;

    private MutableLiveData<String> onPostUploadPhotoSucceed;
    private MutableLiveData<String> onPostUploadPhotoFailed;

    public AddPostViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuthRepository = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
    }

    public MutableLiveData<String> getOnPostUploadPhotoSucceed() {
        if(onPostUploadPhotoSucceed == null){
            onPostUploadPhotoSucceed = new MutableLiveData<>();
            attachUploadPostPhotoListener();
        }
        return onPostUploadPhotoSucceed;
    }

    public MutableLiveData<String> getOnPostUploadPhotoFailed() {
        if(onPostUploadPhotoFailed == null){
            onPostUploadPhotoFailed = new MutableLiveData<>();
            attachUploadPostPhotoListener();
        }
        return onPostUploadPhotoFailed;
    }

    private void attachUploadPostPhotoListener() {
        mStorageRepository.setPostUploadPicListener(new StorageRepository.StoragePostUploadPicInterface() {
            @Override
            public void onPostUploadPicSuccess(String imagePath) {
                onPostUploadPhotoSucceed.setValue(imagePath);
            }

            @Override
            public void onPostUploadPicFailed(String error) {
                onPostUploadPhotoFailed.setValue(error);
            }
        });
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

    public void uploadPostPhoto(Post post, Uri mPicUri) {
        mStorageRepository.uploadPostFile(mPicUri,post.getAuthorEmail(),post.getPostId());
    }

    public void uploadNewPost(Post post) {
        mRepository.uploadNewPost(post);
    }

    public void updatePost(Post post) {
        mRepository.updatePost(post);
    }
}
