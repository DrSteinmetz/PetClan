package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.android2project.repository.Repository;

public class MarketPlaceViewModel extends ViewModel {
    private Repository mRepository;

    public MarketPlaceViewModel(final Context context) {
        this.mRepository = Repository.getInstance(context);
    }
}
