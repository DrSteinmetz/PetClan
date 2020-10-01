package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.Pet;
import com.example.android2project.model.PetsAdapter;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.UserProfileViewModel;
import com.example.android2project.model.ViewModelFactory;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class UserProfileFragment extends DialogFragment {

    private UserProfileViewModel mViewModel;

    private User mUser;
    private String mUserEmail;
    private ImageView mProfilePicIv;
    private TextView mUserNameTv;
    private RecyclerView mPetsRecyclerView;
    private PetsAdapter mPetsAdapter;
    private FirestoreRecyclerOptions<Pet> mRecyclerviewOptions;

    private Observer<User> mOnDownloadUserSucceed;
    private Observer<String> mOnDownloadUserFailed;

    private Observer<String> mOnUserNameUpdateSucceed;
    private Observer<String> mOnUserNameUpdateFailed;

    private Observer<String> mOnUserImageUpdateSucceed;
    private Observer<String> mOnUserImageUpdateFailed;

    private Observer<String> mOnUserDeletionSucceed;
    private Observer<String> mOnUserDeletionFailed;

    private final String TAG = "UserProfileFragment";

    public static UserProfileFragment newInstance(final String userEmail) {
        UserProfileFragment fragment =  new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("user", userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserEmail = getArguments().getString("user");
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.UserProfile)).get(UserProfileViewModel.class);

        if (mUserEmail != null) {
            mViewModel.downloadUser(mUserEmail);
        }

        mOnDownloadUserSucceed = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;

                loadProfilePictureWithGlide(user.getPhotoUri(), mProfilePicIv);
                final String userName = user.getFirstName() + "\n" + user.getLastName();
                mUserNameTv.setText(userName);
                showUserFeed(user.getEmail());
            }
        };

        mOnDownloadUserFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUserNameUpdateSucceed = new Observer<String>() {
            @Override
            public void onChanged(String updatedUserName) {
            }
        };

        mOnUserNameUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUserImageUpdateSucceed = new Observer<String>() {
            @Override
            public void onChanged(String updatedUserProfilePicUri) {
            }
        };

        mOnUserImageUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUserDeletionSucceed = new Observer<String>() {
            @Override
            public void onChanged(String deletedUserId) {
            }
        };

        mOnUserDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile,
                container,false);

        final CoordinatorLayout coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        final FloatingActionButton addPostBtn = rootView.findViewById(R.id.message_edit_btn);
        mUserNameTv = rootView.findViewById(R.id.user_name_tv);
        mProfilePicIv = rootView.findViewById(R.id.profile_image_iv);
        mPetsRecyclerView = rootView.findViewById(R.id.pets_recyclerview);
        mPetsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPetsRecyclerView.setHasFixedSize(true);

        if (mUserEmail == null) {
            mUser = mViewModel.getMyDetails();
            loadProfilePictureWithGlide(mUser.getPhotoUri(), mProfilePicIv);
            final String userName = mUser.getFirstName() + "\n" + mUser.getLastName();
            mUserNameTv.setText(userName);
            addPostBtn.setImageResource(R.drawable.ic_round_settings_24);
            showUserFeed(mUser.getEmail());

            mRecyclerviewOptions = new FirestoreRecyclerOptions.Builder<Pet>()
                    .setQuery(mViewModel.getUserPets(mUser.getEmail()), Pet.class).build();
        } else {
            addPostBtn.setImageResource(R.drawable.ic_send_comment_btn);
            mRecyclerviewOptions = new FirestoreRecyclerOptions.Builder<Pet>()
                    .setQuery(mViewModel.getUserPets(mUserEmail), Pet.class).build();
        }

        mPetsAdapter = new PetsAdapter(mRecyclerviewOptions);
        mPetsRecyclerView.setAdapter(mPetsAdapter);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserEmail == null) {
                } else {
                    ConversationFragment.newInstance(mUser)
                            .show(getChildFragmentManager().beginTransaction(), "conversation_fragment");
                }
            }
        });

        mUserNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPetFragment.newInstance()
                        .show(getChildFragmentManager().beginTransaction(), "add_pet_fragment");
            }
        });
        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getDownloadUserSucceed().observe(this, mOnDownloadUserSucceed);
            mViewModel.getDownloadUserFailed().observe(this, mOnDownloadUserFailed);
            mViewModel.getUpdateUserNameSucceed().observe(this, mOnUserNameUpdateSucceed);
            mViewModel.getUpdateUserNameFailed().observe(this, mOnUserNameUpdateFailed);
            mViewModel.getUpdateUserImageSucceed().observe(this, mOnUserImageUpdateSucceed);
            mViewModel.getUpdateUserImageFailed().observe(this, mOnUserImageUpdateFailed);
            mViewModel.getUserDeletionSucceed().observe(this, mOnUserDeletionSucceed);
            mViewModel.getUserDeletionFailed().observe(this, mOnUserDeletionFailed);
        }
    }

    private void loadProfilePictureWithGlide(String uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(requireContext())
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    private void showUserFeed(final String userEmail) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.user_posts_fragment, UserFeedFragment.newInstance(userEmail), "user_feed_fragment")
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        mPetsAdapter.startListening();
        if (getDialog() != null) {
            Window window = Objects.requireNonNull(getDialog()).getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}
