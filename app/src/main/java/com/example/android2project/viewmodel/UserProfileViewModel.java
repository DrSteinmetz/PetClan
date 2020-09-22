package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

public class UserProfileViewModel extends ViewModel {
    private Repository mRepository;

    private AuthRepository mAuthRepository;

    private StorageRepository mStorageRepository;

    private MutableLiveData<String> mUploadPicSucceed;
    private MutableLiveData<String> mUploadPicFailed;

    private MutableLiveData<String> mUpdateUserNameSucceed;
    private MutableLiveData<String> mUpdateUserNameFailed;

    private MutableLiveData<String> mUpdateUserImageSucceed;
    private MutableLiveData<String> mUpdateUserImageFailed;

    private MutableLiveData<String> mUpdateUserCoverImageSucceed;
    private MutableLiveData<String> mUpdateUserCoverImageFailed;

    private MutableLiveData<String> mUserDeletionSucceed;
    private MutableLiveData<String> mUserDeletionFailed;

    private final String TAG = "UserProfileViewModel";

    public UserProfileViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuthRepository = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
    }

    /**<-------Upload Image to Storage------->**/
    public MutableLiveData<String> getUploadPicSucceed() {
        if (mUploadPicSucceed == null) {
            mUploadPicSucceed = new MutableLiveData<>();
            attachUploadPicListener();
        }
        return mUploadPicSucceed;
    }

    public MutableLiveData<String> getUploadPicFailed() {
        if (mUploadPicFailed == null) {
            mUploadPicFailed = new MutableLiveData<>();
            attachUploadPicListener();
        }
        return mUploadPicFailed;
    }

    private void attachUploadPicListener () {
        mStorageRepository.setUploadPicListener(new StorageRepository.StorageUploadPicInterface() {
            @Override
            public void onUploadPicSuccess(String selectedImage) {
                mRepository.updateUserProfileImage(selectedImage);
                mUploadPicSucceed.setValue(selectedImage);
            }

            @Override
            public void onUploadPicFailed(String error) {
                mUploadPicFailed.setValue(error);
            }
        });
    }

    /**<-------Update User Name------->**/
    public MutableLiveData<String> getUpdateUserNameSucceed() {
        if (mUpdateUserNameSucceed == null) {
            mUpdateUserNameSucceed = new MutableLiveData<>();
            attachSetUpdateUserNameListener();
        }
        return mUpdateUserNameSucceed;
    }

    public MutableLiveData<String> getUpdateUserNameFailed() {
        if (mUpdateUserNameFailed == null) {
            mUpdateUserNameFailed = new MutableLiveData<>();
            attachSetUpdateUserNameListener();
        }
        return mUpdateUserNameFailed;
    }

    private void attachSetUpdateUserNameListener() {
        mRepository.setUpdateUserNameListener(new Repository.RepositoryUpdateUserNameInterface() {
            @Override
            public void onUpdateUserNameSucceed(String newUserName) {
                mUpdateUserNameSucceed.setValue(newUserName);
            }

            @Override
            public void onUpdateUserNameFailed(String error) {
                mUpdateUserNameFailed.setValue(error);
            }
        });
    }

    /**<-------Update User Profile Image------->**/
    public MutableLiveData<String> getUpdateUserImageSucceed() {
        if (mUpdateUserImageSucceed == null) {
            mUpdateUserImageSucceed = new MutableLiveData<>();
            attachSetUpdateUserImageListener();
        }
        return mUpdateUserImageSucceed;
    }

    public MutableLiveData<String> getUpdateUserImageFailed() {
        if (mUpdateUserImageFailed == null) {
            mUpdateUserImageFailed = new MutableLiveData<>();
            attachSetUpdateUserImageListener();
        }
        return mUpdateUserImageFailed;
    }

    private void attachSetUpdateUserImageListener() {
        mRepository.setUpdateUserImageListener(new Repository.RepositoryUpdateUserImageInterface() {
            @Override
            public void onUpdateUserImageSucceed(String newUserProfilePic) {
                mUpdateUserImageSucceed.setValue(newUserProfilePic);
            }

            @Override
            public void onUpdateUserImageFailed(String error) {
                mUpdateUserImageFailed.setValue(error);
            }
        });
    }

    /**<-------Update User Cover Image------->**/
    public MutableLiveData<String> getUpdateUserCoverImageSucceed() {
        if (mUpdateUserCoverImageSucceed == null) {
            mUpdateUserCoverImageSucceed = new MutableLiveData<>();
            attachSetUpdateUserCoverImageListener();
        }
        return mUpdateUserCoverImageSucceed;
    }

    public MutableLiveData<String> getUpdateUserCoverImageFailed() {
        if (mUpdateUserCoverImageFailed == null) {
            mUpdateUserCoverImageFailed = new MutableLiveData<>();
            attachSetUpdateUserCoverImageListener();
        }
        return mUpdateUserCoverImageFailed;
    }

    private void attachSetUpdateUserCoverImageListener() {
        mRepository.setUpdateUserCoverImageListener(new Repository.RepositoryUpdateUserCoverImageInterface() {
            @Override
            public void onUpdateUserCoverImageSucceed(String newUserProfileCoverPic) {
                mUpdateUserCoverImageSucceed.setValue(newUserProfileCoverPic);
            }

            @Override
            public void onUpdateUserCoverImageFailed(String error) {
                mUpdateUserCoverImageFailed.setValue(error);
            }
        });
    }

    /**<-------Delete User------->**/
    public MutableLiveData<String> getUserDeletionSucceed() {
        if (mUserDeletionSucceed == null) {
            mUserDeletionSucceed = new MutableLiveData<>();
            attachSetUserDeletionListener();
        }
        return mUserDeletionSucceed;
    }

    public MutableLiveData<String> getUserDeletionFailed() {
        if (mUserDeletionFailed == null) {
            mUserDeletionFailed = new MutableLiveData<>();
            attachSetUserDeletionListener();
        }
        return mUserDeletionFailed;
    }

    private void attachSetUserDeletionListener() {
        mRepository.setUserDeletionListener(new Repository.RepositoryUserDeletionInterface() {
            @Override
            public void onUserDeletionSucceed(String userId) {
                mUserDeletionSucceed.setValue(userId);
                mStorageRepository.deleteFile(userId);
            }

            @Override
            public void onUserDeletionFailed(String error) {
                mUserDeletionFailed.setValue(error);
            }
        });
    }


    public void updateUserName(final String newUserName) {
        mRepository.updateUserName(newUserName);
    }

    public void updateUserProfileImage(final Uri imageUri) {
        final String userId = mAuthRepository.getUserId();
        mStorageRepository.uploadFile(imageUri, userId);
        mAuthRepository.deleteUserFromAuth();
    }

    public void updateUserProfileCoverImage(final Uri imageUri) {
    }

    public void deleteUser() {
        mRepository.deleteUser();
    }

    public String getUserProfileImage() {
        return mAuthRepository.getUserImageUri();
    }
}