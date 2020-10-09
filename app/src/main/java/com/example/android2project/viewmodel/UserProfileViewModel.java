package com.example.android2project.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Pet;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;
import com.google.firebase.firestore.Query;

import java.util.List;

public class UserProfileViewModel extends ViewModel {
    private Repository mRepository;

    private AuthRepository mAuthRepository;

    private StorageRepository mStorageRepository;

    private MutableLiveData<User> mDownloadUserSucceed;
    private MutableLiveData<String> mDownloadUserFailed;

    private MutableLiveData<String> mUpdateUserImageSucceed;
    private MutableLiveData<String> mUpdateUserImageFailed;

    private final String TAG = "UserProfileViewModel";

    public UserProfileViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuthRepository = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
    }

    private void attachUploadPicListener () {
        mStorageRepository.setUploadPicListener(new StorageRepository.StorageUploadPicInterface() {
            @Override
            public void onUploadPicSuccess(String selectedImage) {
                mRepository.updateUserProfileImage(selectedImage);
            }

            @Override
            public void onUploadPicFailed(String error) {
                Log.d(TAG, "onUploadPicFailed: UPLOAD FAILED");
            }
        });
    }

    public MutableLiveData<User> getDownloadUserSucceed() {
        if (mDownloadUserSucceed == null) {
            mDownloadUserSucceed = new MutableLiveData<>();
            attachSetDownloadUserListener();
        }
        return mDownloadUserSucceed;
    }

    public MutableLiveData<String> getDownloadUserFailed() {
        if (mDownloadUserFailed == null) {
            mDownloadUserFailed = new MutableLiveData<>();
            attachSetDownloadUserListener();
        }
        return mDownloadUserFailed;
    }

    public void attachSetDownloadUserListener() {
        mRepository.setDownloadUserListener(new Repository.RepositoryDownloadUserInterface() {
            @Override
            public void onDownloadUserSucceed(User user) {
                mDownloadUserSucceed.setValue(user);
            }

            @Override
            public void onDownloadUserFailed(String error) {
                mDownloadUserFailed.setValue(error);
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
            public void onUpdateUserImageSucceed(String newUserProfilePicUri) {
                mUpdateUserImageSucceed.setValue(newUserProfilePicUri);
            }

            @Override
            public void onUpdateUserImageFailed(String error) {
                mUpdateUserImageFailed.setValue(error);
            }
        });
    }


    public void downloadUser(String userEmail) {
        mRepository.downloadUser(userEmail);
    }

    public void updateUserProfileImage(final Uri imageUri) {
        final String userId = mAuthRepository.getUserId();
        attachUploadPicListener();
        mStorageRepository.uploadFile(imageUri, userId);
        mAuthRepository.updateUserImage(imageUri);
    }

    public User getMyDetails() {
        final String userEmail = mAuthRepository.getUserEmail();
        final String userName = mAuthRepository.getUserName();
        final String firstName = userName.split(" ")[0];
        final String lastName = userName.split(" ")[1];
        final String photoUri = mAuthRepository.getUserImageUri();
        final String token = mAuthRepository.getUserToken();

        return new User(userEmail, firstName, lastName, photoUri, token);
    }

    public Query getUserPets(String email) {
        return mRepository.PetsQuery(email);
    }

    public void deletePet(Pet pet) {
        mRepository.deletePet(pet.getPetId());
        for(String uri : pet.getPhotoUri()) {
            mStorageRepository.deletePhotoFromStorage(pet.getStoragePath(mAuthRepository.getUserEmail(),uri));
        }
    }

    public void signOutFromGuest() {
        mAuthRepository.signOutUser();
    }
}
