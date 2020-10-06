package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;
import com.example.android2project.repository.StorageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarketPlaceViewModel extends ViewModel {
    private static final String TAG = "MarketPlaceViewModel";
    private Repository mRepository;
    private AuthRepository mAuth;
    private StorageRepository mStorageRepository;
    private int mPosition;

    private List<Advertisement> mAdList = new ArrayList<>();


    private MutableLiveData<List<Advertisement>> mDownloadAdsSucceed;
    private MutableLiveData<String> mDownloadAdsFailed;
    private MutableLiveData<Integer> mAdDeleteSucceed;
    private MutableLiveData<String> mAdDeleteFailed;




    public MarketPlaceViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
        getAllAds();
    }



    public MutableLiveData<Integer> getmAdDeleteSucceed() {
        if (mAdDeleteSucceed == null) {
            mAdDeleteSucceed = new MutableLiveData<>();
            attachSetOnDeletingAdListener();
        }
        return mAdDeleteSucceed;
    }

    private void attachSetOnDeletingAdListener() {
        mRepository.setAdDeletingListener(new Repository.RepositoryAdDeletingInterface() {
            @Override
            public void onAdDeletingSucceed(String adId) {
                if (!mAdList.isEmpty() && mAdList.get(mPosition).getAdvertisementId().equals(adId)) {
                    mAdList.remove(mPosition);
                    mAdDeleteSucceed.setValue(mPosition);
                }
            }

            @Override
            public void onAdDeletingFailed(String error) {
                mDownloadAdsFailed.setValue(error);
            }
        });
    }

    public MutableLiveData<String> getmAdDeleteFailed() {
        if (mAdDeleteFailed == null) {
            mAdDeleteFailed = new MutableLiveData<>();
            attachsetOnDownloadListener();
        }
        return mAdDeleteFailed;
    }

    public MutableLiveData<List<Advertisement>> getmDownloadAdsSucceed() {
        if (mDownloadAdsSucceed == null) {
            mDownloadAdsSucceed = new MutableLiveData<>();
            attachsetOnDownloadListener();
        }
        return mDownloadAdsSucceed;
    }

    public MutableLiveData<String> getmDownloadAdsFailed() {
        if (mDownloadAdsFailed == null) {
            mDownloadAdsFailed = new MutableLiveData<>();
            attachsetOnDownloadListener();
        }
        return mDownloadAdsFailed;
    }

    private void attachsetOnDownloadListener() {
        mRepository.setDownloadAdListener(new Repository.RepositoryDownloadAdInterface() {

            @Override
            public void onDownloadAdSucceed(List<Advertisement> adList) {
                if (!mAdList.isEmpty()) {
                    mAdList.clear();
                }
                mAdList.addAll(adList);

                Collections.sort(mAdList);

                mDownloadAdsSucceed.setValue(mAdList);
            }

            @Override
            public void onDownloadAdFailed(String error) {
                mDownloadAdsFailed.setValue(error);
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

    public void deleteAdvertisement(Advertisement ad, int position) {
        mPosition = position;
        for (String uri : ad.getImages()) {
            mStorageRepository.deletePhotoFromStorage(ad.getStoragePath(ad.getUser().getEmail(), uri));
        }
        mRepository.deleteAdvertisement(ad);
    }

    public List<Advertisement> getAdList() {
        return mAdList;
    }

    public void setAdList(List<Advertisement> mAdList) {
        this.mAdList = mAdList;
    }

    public void getAllAds(){
        mRepository.downloadAllAds();
    }
    public void getFilteredAds(final int orderBy, final boolean isDes) {
        final int date=0;
        final int price=1;
        final int distance=2;
        switch (orderBy) {
            case date:
                Collections.sort(mAdList);
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
            case price:
                Collections.sort(mAdList, new Advertisement.PriceComperator());
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
            case distance:
                Collections.sort(mAdList,new Advertisement.LocationComperator());
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
        }
        mDownloadAdsSucceed.setValue(mAdList);

    }
}
