package com.example.android2project.viewmodel;

import android.content.Context;
import android.util.Log;

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
    private List<Advertisement> mTempList = new ArrayList<>();

    private MutableLiveData<List<Advertisement>> mDownloadAdsSucceed;
    private MutableLiveData<String> mDownloadAdsFailed;

    private MutableLiveData<Integer> mAdDeletionSucceed;
    private MutableLiveData<String> mAdDeletionFailed;

    public MarketPlaceViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
        this.mAuth = AuthRepository.getInstance(context);
        this.mStorageRepository = StorageRepository.getInstance(context);
        getAllAds();
    }


    public MutableLiveData<List<Advertisement>> getDownloadAdsSucceed() {
        if (mDownloadAdsSucceed == null) {
            mDownloadAdsSucceed = new MutableLiveData<>();
            attachSetDownloadListener();
        }
        return mDownloadAdsSucceed;
    }

    public MutableLiveData<String> getDownloadAdsFailed() {
        if (mDownloadAdsFailed == null) {
            mDownloadAdsFailed = new MutableLiveData<>();
            attachSetDownloadListener();
        }
        return mDownloadAdsFailed;
    }

    private void attachSetDownloadListener() {
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

    public MutableLiveData<Integer> getAdDeletionSucceed() {
        if (mAdDeletionSucceed == null) {
            mAdDeletionSucceed = new MutableLiveData<>();
            attachSetDeletionAdListener();
        }
        return mAdDeletionSucceed;
    }

    public MutableLiveData<String> getAdDeletionFailed() {
        if (mAdDeletionFailed == null) {
            mAdDeletionFailed = new MutableLiveData<>();
            attachSetDeletionAdListener();
        }
        return mAdDeletionFailed;
    }

    private void attachSetDeletionAdListener() {
        mRepository.setAdDeletingListener(new Repository.RepositoryAdDeletingInterface() {
            @Override
            public void onAdDeletingSucceed(String adId) {
                if (!mAdList.isEmpty() && mAdList.get(mPosition).getAdvertisementId().equals(adId)) {
                    mAdList.remove(mPosition);
                    mAdDeletionSucceed.setValue(mPosition);
                }
            }

            @Override
            public void onAdDeletingFailed(String error) {
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

    public void getAllAds() {
        mRepository.downloadAllAds();
    }

    public void getFilteredAds(final int orderBy, final boolean isDes) {
        final int date = 0;
        final int price = 1;
        final int distance = 2;
        final int my_ads = 3;

        if (!mTempList.isEmpty()) {
            if (!mAdList.isEmpty()) {
                mAdList.clear();
            }
            mAdList.addAll(mTempList);
        }

        switch (orderBy) {
            case date:
                Collections.sort(mAdList);
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
            case price:
                Collections.sort(mAdList, new Advertisement.PriceComparator());
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
            case distance:
                Collections.sort(mAdList, new Advertisement.LocationComparator());
                if (!isDes) {
                    Collections.reverse(mAdList);
                }
                break;
            case my_ads:
                if (!mTempList.isEmpty()) {
                    mTempList.clear();
                }
                mTempList.addAll(mAdList);

                if (!mAdList.isEmpty()) {
                    mAdList.clear();
                }

                for (Advertisement ad : mTempList) {
                    if (mAuth.getUserEmail().equals(ad.getUser().getEmail())) {
                        mAdList.add(ad);
                    }
                }
                Collections.sort(mAdList);
                break;
        }

        mDownloadAdsSucceed.setValue(mAdList);
    }

    public void signOutFromGuest() {
        mAuth.signOutUser();
    }
}
