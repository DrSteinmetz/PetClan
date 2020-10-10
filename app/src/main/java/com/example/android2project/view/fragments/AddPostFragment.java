package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.model.Post;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.AddPostViewModel;
import com.google.firebase.firestore.GeoPoint;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Objects;

public class AddPostFragment extends DialogFragment {

    private static final String TAG = "AddPostFragment";
    private AddPostViewModel mViewModel;
    private Uri mPicUri;

    private EditText postContentEt;
    private ImageButton imagePicker;
    private Button postBtn;
    private Button cancelBtn;
    private ImageView picPreview;

    private Address mUserLocation;
    private Post mCurrentPost;

    private File mFile;
    private AlertDialog mLoadingDialog;

    private boolean mIsEdit;

    private Observer<String> mOnUploadingPostPhotoSucceed;
    private Observer<String> mOnUploadingPostPhotoFailed;

    private final int WRITE_PERMISSION_REQUEST = 7;
    public AddPostFragment() {
    }

    public static AddPostFragment newInstance(final Address userLocation, final Post post) {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", userLocation);
        args.putSerializable("post", post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentPost = (Post) getArguments().getParcelable("post");
            mUserLocation = (Address) getArguments().getParcelable("location");
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Post)).get(AddPostViewModel.class);

        mOnUploadingPostPhotoSucceed = new Observer<String>() {
            @Override
            public void onChanged(String imageUri) {
                mCurrentPost.setPostImageUri(imageUri);
                if(!mIsEdit) {
                    mViewModel.uploadNewPost(mCurrentPost);
                }else{
                    mViewModel.updatePost(mCurrentPost);
                }
                mLoadingDialog.dismiss();
                dismiss();
            }
        };

        mViewModel.getOnPostUploadPhotoSucceed().observe(this, mOnUploadingPostPhotoSucceed);

        mOnUploadingPostPhotoFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mViewModel.getOnPostUploadPhotoFailed().observe(this, mOnUploadingPostPhotoFailed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_post_dialog, container, false);

        postContentEt = view.findViewById(R.id.new_post_content_et);
        imagePicker = view.findViewById(R.id.add_image_btn);
        postBtn = view.findViewById(R.id.post_btn);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        picPreview = view.findViewById(R.id.image_preview_iv);

        picPreview.setClipToOutline(true);

        if (mCurrentPost == null) {
            postBtn.setText(getString(R.string.post));
            postBtn.setEnabled(false);
        } else {
            postBtn.setText(getString(R.string.update));
            postContentEt.setText(mCurrentPost.getAuthorContent());
            if (mCurrentPost.getPostImageUri() != null) {
                Glide.with(requireContext())
                        .load(mCurrentPost.getPostImageUri())
                        .into(picPreview);

                picPreview.setVisibility(View.VISIBLE);
            }
        }

        postContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cvb");
                mFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);

                CropImage.activity()
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
                        .start(requireContext(), AddPostFragment.this);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPost == null) {
                    mCurrentPost = new Post(mViewModel.getMyEmail(), mViewModel.getMyName(),
                            mViewModel.getMyPhotoUri(), postContentEt.getText().toString());
                    mCurrentPost.setLocation(mUserLocation == null ? requireContext().getResources().getString(R.string.unknown) :
                            mUserLocation.getLocality());
                    mCurrentPost.setGeoPoint(mUserLocation == null ? null :
                            new GeoPoint(mUserLocation.getLatitude(), mUserLocation.getLongitude()));

                    if (mPicUri != null) {
                        showLoadingDialog();
                        mViewModel.uploadPostPhoto(mCurrentPost, mPicUri);
                    } else {
                        mViewModel.uploadNewPost(mCurrentPost);
                        dismiss();
                    }
                } else {
                    mCurrentPost.setAuthorContent(postContentEt.getText().toString());
                    if (mPicUri != null) {
                        showLoadingDialog();
                        mViewModel.uploadPostPhoto(mCurrentPost,mPicUri);
                        mIsEdit = true;
                    }else{
                        mViewModel.updatePost(mCurrentPost);
                        dismiss();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
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

                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
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
                picPreview.setVisibility(View.VISIBLE);
                mPicUri = result.getUri();
                Glide.with(requireContext())
                        .load(mPicUri)
                        .placeholder(R.drawable.ic_default_user_pic)
                        .into(picPreview);
            }
        }
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dog_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(false);
        mLoadingDialog = builder.create();
        mLoadingDialog.show();
        mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = Objects.requireNonNull(getDialog()).getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}
