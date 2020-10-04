package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.google.firebase.firestore.Query;

public class MarketPlaceViewModel extends ViewModel {
    private static final String TAG = "MarketPlaceViewModel";
    private Repository mRepository;
    private AuthRepository mAuth;

    private MutableLiveData<Boolean> onAdDeletingSucceed;

    public MarketPlaceViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
    }

    public Query getAds() {
        return mRepository.getAllAds();
    }


    public MutableLiveData<Boolean> getOnAdDeletingSucceed() {
        if(onAdDeletingSucceed == null){
            onAdDeletingSucceed = new MutableLiveData<>();
            attachSetOnDeletingAdListener();
        }
        return onAdDeletingSucceed;
    }

    private void attachSetOnDeletingAdListener() {
        mRepository.setAdDeletingListener(new Repository.RepositoryAdDeletingInterface() {
            @Override
            public void onAdDeletingSucceed(boolean isSuccess) {
                onAdDeletingSucceed.setValue(isSuccess);
            }

            @Override
            public void onAdDeletingFailed(String error) {

            }
        });
    }


    public User getCurrentUser() {
        final String email = mAuth.getUserEmail();
        final String username = mAuth.getUserName();
        final String firstName = username.split(" ")[0];
        final String lastName = username.split(" ")[1];
        final String photoUri = mAuth.getUserImageUri();
        final String token = mAuth.getUserToken();
        return new User(email, firstName, lastName, photoUri, token);
    }



    public void deleteAdvertisement(Advertisement ad) {
        mRepository.deleteAdvertisement(ad);
    }
}
