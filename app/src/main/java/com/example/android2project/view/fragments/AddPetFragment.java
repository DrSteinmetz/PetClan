package com.example.android2project.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.android2project.model.Pet;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.PetViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddPetFragment extends DialogFragment {

    private PetViewModel mViewModel;
    private int mImageViewCounter = 0;
    private List<Uri> mSelectedImageList = new ArrayList<>();
    private List<ImageView> mImageViews = new ArrayList<>();
    private File mFile;
    private Observer<Integer> mDoneUploadingObserver;
    private AlertDialog mLoadingDialog;

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;

    public static AddPetFragment newInstance() {
        return new AddPetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Pet)).get(PetViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pet_fragment, container, false);
        final TextInputEditText petNameEt = rootView.findViewById(R.id.pet_name_et);
        final TextInputEditText petTypeEt = rootView.findViewById(R.id.pet_type_et);
        final TextInputEditText petDescriptionEt = rootView.findViewById(R.id.pet_description_et);
        final ImageButton cameraBtn = rootView.findViewById(R.id.camera_btn);
        final ImageButton galleryBtn = rootView.findViewById(R.id.gallery_btn);
        final Button addBtn = rootView.findViewById(R.id.add_pet_btn);
        initImageViews(rootView);

        mDoneUploadingObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final String petName = petNameEt.getText().toString().trim();
                final String petType = petTypeEt.getText().toString().trim();
                final String description = petDescriptionEt.getText().toString().trim();
                Pet pet = new Pet(petName, petType, description);
                Toast.makeText(getContext(), "Done Uploading", Toast.LENGTH_SHORT).show();
                mViewModel.addPetToUser(pet);
                mLoadingDialog.dismiss();
                dismiss();
            }
        };

        mViewModel.getOnPetUploadPhotoLiveData().observe(getViewLifecycleOwner(), mDoneUploadingObserver);


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageViewCounter < 8) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), GALLERY_REQUEST);
                } else {
                    Toast.makeText(getContext(), "Woof!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**<-------Requesting user permissions------->**/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mImageViewCounter < 8) {
                    int hasWritePermission = requireContext().
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_PERMISSION_REQUEST);
                    } else {
                        mFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "petclan" + System.nanoTime() + "pic.jpg");
                        Uri uri = FileProvider.getUriForFile(requireContext(),
                                "com.example.android2project.provider", mFile);
                        mSelectedImageList.add(uri);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo : check edit texts not empty
                final String petName = petNameEt.getText().toString().trim();
                final String petType = petTypeEt.getText().toString().trim();

                if (petName.length() > 0 && petType.length() > 0) {
                    mViewModel.uploadPetPhotos(mSelectedImageList);
                    showLoadingDialog();
                } else {
                    if (petName.length() < 1) {
                        petNameEt.setError("You must enter a name");
                    } else {
                        petNameEt.setError(null);
                    }
                    if (petType.length() < 1) {
                        petTypeEt.setError("You must enter a type");
                    } else {
                        petTypeEt.setError(null);
                    }
                }
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
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);
                mSelectedImageList.add(uri);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Glide.with(this)
                        .load(mSelectedImageList.get(mImageViewCounter))
                        .error(R.drawable.ic_petclan_logo)
                        .into(mImageViews.get(mImageViewCounter++));

            } else if (requestCode == GALLERY_REQUEST) {
                if (data != null) {
                    final Uri selectedImage = data.getData();
                    mSelectedImageList.add(selectedImage);


                    Glide.with(this)
                            .load(selectedImage)
                            .error(R.drawable.ic_petclan_logo)
                            .into(mImageViews.get(mImageViewCounter++));
                }
            }
        }
    }

    private void initImageViews(View rootView) {
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_1_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_2_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_3_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_4_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_5_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_6_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_7_preview));
        mImageViews.add((ImageView) rootView.findViewById(R.id.photo_8_preview));
        for (ImageView imageView : mImageViews) {
            imageView.setClipToOutline(true);
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
    }

}