package com.example.android2project.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.model.ViewModelEnum;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Context mContext;
    private Application mApplication;

    private ViewModelEnum mViewModelEnum;

    public ViewModelFactory(Context mContext, ViewModelEnum viewModelEnum) {
        this.mContext = mContext;
        this.mViewModelEnum = viewModelEnum;
    }

    public ViewModelFactory(Application mApplication, ViewModelEnum viewModelEnum) {
        this.mApplication = mApplication;
        this.mViewModelEnum = viewModelEnum;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T objToReturn = null;

        switch (mViewModelEnum) {
            case Login:
                if (modelClass.isAssignableFrom(LoginRegistrationViewModel.class)) {
                    objToReturn = (T) new LoginRegistrationViewModel(mContext);
                }
                break;
            case Register:
                /*if (modelClass.isAssignableFrom(.class)) {
                    objToReturn = (T) new LoginDetailsViewModel(mContext);
                }*/
                break;
        }

        return objToReturn;
    }
}
