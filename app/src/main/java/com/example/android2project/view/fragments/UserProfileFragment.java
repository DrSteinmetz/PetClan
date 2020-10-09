package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.UserProfileViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
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

    private File mFile;
    private Uri mSelectedImage = Uri.parse("/users_profile_picture/default_user_pic.png");
    private String mCurrentUser;

    private Observer<User> mOnDownloadUserSucceed;
    private Observer<String> mOnDownloadUserFailed;

    private Observer<String> mOnUserImageUpdateSucceed;
    private Observer<String> mOnUserImageUpdateFailed;

    private final int WRITE_PERMISSION_REQUEST = 7;

    private final String TAG = "UserProfileFragment";

    public UserProfileFragment() {
    }

    public static UserProfileFragment newInstance(final String userEmail) {
        UserProfileFragment fragment = new UserProfileFragment();
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

        mCurrentUser = mViewModel.getMyDetails().getEmail();

        if (mUserEmail != null) {
            mViewModel.downloadUser(mUserEmail);
        }

        mOnDownloadUserSucceed = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;

                loadProfilePictureWithGlide(user.getPhotoUri(), mProfilePicIv);
                final String userName = user.getFirstName() + " " + user.getLastName();
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

        mOnUserImageUpdateSucceed = new Observer<String>() {
            @Override
            public void onChanged(String updatedUserProfilePicUri) {
                loadProfilePictureWithGlide(updatedUserProfilePicUri, mProfilePicIv);

                final ImageView mainUserIv = ((ImageView) requireActivity().findViewById(R.id.user_pic_iv));
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.ic_default_user_pic)
                        .error(R.drawable.ic_default_user_pic);

                Glide.with(requireContext())
                        .load(updatedUserProfilePicUri)
                        .apply(options)
                        .into(mainUserIv);
            }
        };

        mOnUserImageUpdateFailed = new Observer<String>() {
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
                container, false);

        final FloatingActionButton messageEditBtn = rootView.findViewById(R.id.message_edit_btn);
        final FloatingActionButton addPetBtn = rootView.findViewById(R.id.add_pet_btn);
        mUserNameTv = rootView.findViewById(R.id.user_name_tv);
        mProfilePicIv = rootView.findViewById(R.id.profile_image_iv);
        mPetsRecyclerView = rootView.findViewById(R.id.pets_recyclerview);
        mPetsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPetsRecyclerView.setHasFixedSize(true);

        if (mUserEmail == null) {
            mUser = mViewModel.getMyDetails();
            loadProfilePictureWithGlide(mUser.getPhotoUri(), mProfilePicIv);
            final String userName = mUser.getFirstName() + " " + mUser.getLastName();
            mUserNameTv.setText(userName);
            addPetBtn.setVisibility(View.VISIBLE);
            messageEditBtn.setImageResource(R.drawable.ic_round_photo_library_24);
            showUserFeed(mUser.getEmail());

            mRecyclerviewOptions = new FirestoreRecyclerOptions.Builder<Pet>()
                    .setQuery(mViewModel.getUserPets(mUser.getEmail()), Pet.class).build();
        } else {
            addPetBtn.setVisibility(View.GONE);
            messageEditBtn.setImageResource(R.drawable.ic_send_comment_btn);
            mRecyclerviewOptions = new FirestoreRecyclerOptions.Builder<Pet>()
                    .setQuery(mViewModel.getUserPets(mUserEmail), Pet.class).build();
        }

        mPetsAdapter = new PetsAdapter(mRecyclerviewOptions);
        mPetsAdapter.setPetsAdapterListener(new PetsAdapter.PetsAdapterInterface() {
            @Override
            public void onEditOptionClicked(int position, View view) {
                Pet pet = mPetsAdapter.getItem(position);
                AddPetFragment.newInstance(pet)
                        .show(getChildFragmentManager().beginTransaction(), "edit_pet_fragment");
            }

            @Override
            public void onDeleteOptionClicked(int position, View view) {
                Pet pet = mPetsAdapter.getItem(position);
                mViewModel.deletePet(pet);

            }
        });
        mPetsRecyclerView.setAdapter(mPetsAdapter);

        messageEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentUser.equals("a@gmail.com")) {
                    if (mUserEmail == null) {
                        mFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "petclan" + System.nanoTime() + "pic.jpg");
                        mSelectedImage = FileProvider.getUriForFile(requireContext(),
                                "com.example.android2project.provider", mFile);

                        CropImage.activity()
                                .setAspectRatio(1, 1)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setOutputUri(mSelectedImage)
                                .start(requireContext(), UserProfileFragment.this);
                    } else {
                        ConversationFragment.newInstance(mUser)
                                .show(getChildFragmentManager().beginTransaction(), "fragment_conversation");
                    }
                } else {
                    showGuestDialog();
                }
            }
        });

        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentUser.equals("a@gmail.com")) {
                    AddPetFragment.newInstance(null)
                            .show(getChildFragmentManager().beginTransaction(), "add_pet_fragment");
                } else {
                    showGuestDialog();
                }
            }
        });

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getDownloadUserSucceed().observe(this, mOnDownloadUserSucceed);
            mViewModel.getDownloadUserFailed().observe(this, mOnDownloadUserFailed);
            mViewModel.getUpdateUserImageSucceed().observe(this, mOnUserImageUpdateSucceed);
            mViewModel.getUpdateUserImageFailed().observe(this, mOnUserImageUpdateFailed);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFile = new File(requireContext().
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);

                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(mSelectedImage)
                        .start(requireContext(), this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                mSelectedImage = result.getUri();
                if (mViewModel != null) {
                    mViewModel.updateUserProfileImage(mSelectedImage);
                }
            }
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
                .replace(R.id.user_posts_fragment, FeedFragment.newInstance(userEmail), "fragment_user_feed")
                .commit();
    }

    private void showGuestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.guest_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog guestDialog = builder.create();

        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestDialog.dismiss();
            }
        });
        Button joinBtn = view.findViewById(R.id.join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOutFromGuest();
            }
        });
        guestDialog.show();
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
