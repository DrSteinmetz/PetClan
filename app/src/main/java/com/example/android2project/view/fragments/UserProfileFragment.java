package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.UserProfileViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    ImageView mProfileCoverPicIv;

    private final String TAG = "UserProfileFragment";

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.UserProfile)).get(UserProfileViewModel.class);

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile,
                container,false);

        final CoordinatorLayout coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);

        mProfileCoverPicIv = rootView.findViewById(R.id.cover_image_iv);

        String userProfileImageUri = mViewModel.getUserProfileImage();
        if (userProfileImageUri != null) {
            loadProfilePictureWithGlide(userProfileImageUri, mProfileCoverPicIv);
        }

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            //TODO: Start observation
        }
    }

    private void loadProfilePictureWithGlide(String uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(requireContext())
                .load(uri)
                .apply(options)
                .into(imageView);
    }
}