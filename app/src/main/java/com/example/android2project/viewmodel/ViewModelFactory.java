package com.example.android2project.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.model.ViewModelEnum;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Context mContext;
    //private Application mApplication;

    private ViewModelEnum mViewModelEnum;

    public ViewModelFactory(Context mContext, ViewModelEnum viewModelEnum) {
        this.mContext = mContext;
        this.mViewModelEnum = viewModelEnum;
    }

    /*public ViewModelFactory(Application mApplication, ViewModelEnum viewModelEnum) {
        this.mApplication = mApplication;
        this.mViewModelEnum = viewModelEnum;
    }*/

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T objToReturn = null;

        switch (mViewModelEnum) {
            case LoginRegistration:
                if (modelClass.isAssignableFrom(LoginRegistrationViewModel.class)) {
                    objToReturn = (T) new LoginRegistrationViewModel(mContext);
                }
                break;
            case UserDetails:
                if (modelClass.isAssignableFrom(UserDetailsViewModel.class)) {
                    objToReturn = (T) new UserDetailsViewModel(mContext);
                }
                break;
            case Picture:
                if (modelClass.isAssignableFrom(UserPictureViewModel.class)) {
                    objToReturn = (T) new UserPictureViewModel(mContext);
                }
                break;
            case Welcome:
                if (modelClass.isAssignableFrom(WelcomeViewModel.class)) {
                    objToReturn = (T) new WelcomeViewModel(mContext);
                }
                break;
            case Main:
                if (modelClass.isAssignableFrom(MainViewModel.class)) {
                    objToReturn = (T) new MainViewModel(mContext);
                }
                break;
            case Feed:
                if (modelClass.isAssignableFrom(FeedViewModel.class)) {
                    objToReturn = (T) new FeedViewModel(mContext);
                }
                break;
            case Comments:
                if (modelClass.isAssignableFrom(CommentsViewModel.class)) {
                    objToReturn = (T) new CommentsViewModel(mContext);
                }
                break;
            case UserProfile:
                if (modelClass.isAssignableFrom(UserProfileViewModel.class)) {
                    objToReturn = (T) new UserProfileViewModel(mContext);
                }
                break;
            case ChatClan:
                if (modelClass.isAssignableFrom(ChatClanViewModel.class)) {
                    objToReturn = (T) ChatClanViewModel.getInstance(mContext);
                }
                break;
            case Conversation:
                if (modelClass.isAssignableFrom(ConversationViewModel.class)) {
                    objToReturn = (T) new ConversationViewModel(mContext);
                }
                break;
            case Social:
                if (modelClass.isAssignableFrom(SocialViewModel.class)) {
                    objToReturn = (T) new SocialViewModel(mContext);
                }
                break;
            case Chats:
                if (modelClass.isAssignableFrom(ChatsViewModel.class)) {
                    objToReturn = (T) ChatsViewModel.getInstance(mContext);
                }
                break;
        }

        return objToReturn;
    }
}
