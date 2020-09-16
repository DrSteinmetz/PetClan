package com.example.android2project.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.UserPictureViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.io.File;

public class UserPictureFragment extends Fragment {

    private UserPictureViewModel mViewModel;

    private Observer<String> mCreateUserSucceedObserver;
    private Observer<String> mCreateUserFailedObserver;
    private Observer<Boolean> mUploadUserPicSucceedObserver;
    private Observer<String> mUploadUserPicFailedObserver;

    ImageView mUserPictureIv;

    private File mFile;
    private Uri mSelectedImage = Uri.parse("/users_profile_picture/default_user_pic.jpg");

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;

    private final String TAG = "UserPictureFragment";

    public interface UserPictureListener {
        void onFinish();
    }

    private UserPictureListener listener;

    public UserPictureFragment() {}

    public static UserPictureFragment newInstance() {
        UserPictureFragment fragment = new UserPictureFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UserPictureListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement UserPicture Listener!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Picture)).get(UserPictureViewModel.class);

        mCreateUserSucceedObserver = new Observer<String>() {
            @Override
            public void onChanged(String uId) {
            }
        };

        mCreateUserFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mUploadUserPicSucceedObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (listener != null && aBoolean) {
                    //TODO: Finish loading animation
                    listener.onFinish();
                }
            }
        };

        mUploadUserPicFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startObservation();

        View rootView = inflater.inflate(R.layout.fragment_user_picture, container, false);

        mUserPictureIv = rootView.findViewById(R.id.user_pic);
        ImageButton galleryBtn = rootView.findViewById(R.id.gallery_btn);
        ImageButton cameraBtn = rootView.findViewById(R.id.camera_btn);
        Button finishBtn = rootView.findViewById(R.id.finish_btn);

        mUserPictureIv.setClipToOutline(true);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Choose your profile picture"), GALLERY_REQUEST);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**<-------Requesting user permissions------->**/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hasWritePermission = requireContext().
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_PERMISSION_REQUEST);
                    } else {
                        mFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "petclan" + System.nanoTime() + "pic.jpg");
                        mSelectedImage = FileProvider.getUriForFile(getContext(),
                                "com.example.android2project.provider", mFile);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImage);
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                }
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.createNewUser(mSelectedImage);
                //TODO: Start loading animation
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFile = new File(requireContext().
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                mSelectedImage = FileProvider.getUriForFile(getContext(),
                        "com.example.android2project.provider", mFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImage);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                if (mUserPictureIv != null) {
                    Glide.with(this)
                            .load(mSelectedImage)
                            .error(R.drawable.ic_petclan_logo)
                            .into(mUserPictureIv);
                }
            } else if (requestCode == GALLERY_REQUEST) {
                if (data != null) {
                    mSelectedImage = data.getData();

                    if (mUserPictureIv != null) {
                        Glide.with(this)
                                .load(mSelectedImage)
                                .error(R.drawable.ic_petclan_logo)
                                .into(mUserPictureIv);
                    }
                }
            }
        }
    }

    public void startObservation() {
        if (mCreateUserSucceedObserver != null) {
            mViewModel.getCreateUserSucceed().observe(getViewLifecycleOwner(), mCreateUserSucceedObserver);
        }
        if (mCreateUserFailedObserver != null) {
            mViewModel.getCreateUserFailed().observe(getViewLifecycleOwner(), mCreateUserFailedObserver);
        }
        if (mUploadUserPicSucceedObserver != null) {
            mViewModel.getUploadPicSucceed().observe(getViewLifecycleOwner(), mUploadUserPicSucceedObserver);
        }
        if (mUploadUserPicFailedObserver != null) {
            mViewModel.getUploadPicFailed().observe(getViewLifecycleOwner(), mUploadUserPicFailedObserver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.getCreateUserSucceed().removeObserver(mCreateUserSucceedObserver);
        mViewModel.getCreateUserFailed().removeObserver(mCreateUserFailedObserver);
    }
}