package com.example.android2project.view.fragments;

import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.model.AdsAdapter;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.MarketPlaceViewModel;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MarketPlaceFragment extends Fragment {
    private MarketPlaceViewModel mViewModel;
    private AdsAdapter mAdsAdapter;
    private RecyclerView mRecycler;

    private final String TAG = "MarketPlaceFragment";
    private int mImageViewCounter = 0;
    private File mFile;
    private List<ImageView> mImageViews = new ArrayList<>();
    private List<Uri> mSelectedImageList = new ArrayList<>();
    private AlertDialog mLoadingDialog;
    private FirestorePagingOptions<Advertisement> mOptions;

    private Observer<Integer> mDoneUploadingObserver;

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;

    public static MarketPlaceFragment newInstance() {
        return new MarketPlaceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.MarketPlace)).get(MarketPlaceViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.market_place_fragment, container, false);
        mRecycler = rootView.findViewById(R.id.ads_recycler);
        final ImageButton searchBtn = rootView.findViewById(R.id.search_btn);
        final Spinner optionsFilter = rootView.findViewById(R.id.filter_option_spinner);
        final RadioGroup radioGroup = rootView.findViewById(R.id.radio_group_rg);
        final FloatingActionButton addAdBtn = rootView.findViewById(R.id.add_ad_btn);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        mOptions = new FirestorePagingOptions.Builder<Advertisement>()
                .setLifecycleOwner(this)
                .setQuery(mViewModel.getAds(), config, Advertisement.class)
                .build();

        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecycler.setHasFixedSize(true);
        mAdsAdapter = new AdsAdapter(mOptions);
        mRecycler.setAdapter(mAdsAdapter);

        addAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAdDialog();
            }
        });

        return rootView;
    }

    public void showAddAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_advertisement_fragment,
                        (RelativeLayout) requireActivity().findViewById(R.id.add_ad_container));

        builder.setView(view);
        builder.setCancelable(true);

        final RadioGroup actionRg = view.findViewById(R.id.action_rg);
        final RadioGroup categoryRg = view.findViewById(R.id.category_rg);
        final RadioGroup genderRg = view.findViewById(R.id.gender_rg);
        final TextInputEditText typeEt = view.findViewById(R.id.pet_type_et);
        final TextInputEditText kindEt = view.findViewById(R.id.pet_kind_et);
        final TextInputEditText priceEt = view.findViewById(R.id.pet_price_et);
        final TextInputEditText descriptionEt = view.findViewById(R.id.pet_description_et);
        final ImageButton galleryBtn = view.findViewById(R.id.gallery_btn);
        final ImageButton cameraBtn = view.findViewById(R.id.camera_btn);
        final Button publishBtn = view.findViewById(R.id.publish_btn);
        initImageViews(view);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mImageViewCounter = 0;
                if (!mSelectedImageList.isEmpty()) {
                    mSelectedImageList.clear();
                }
                if (!mImageViews.isEmpty()) {
                    mImageViews.clear();
                }
                mViewModel.getOnAdUploadPhotoLiveData().removeObserver(mDoneUploadingObserver);
            }
        });

        mDoneUploadingObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final boolean isSell = actionRg.getCheckedRadioButtonId() == R.id.sell_rb;
                final boolean isPet = categoryRg.getCheckedRadioButtonId() == R.id.pet_rb;
                final boolean isMale = genderRg.getCheckedRadioButtonId() == R.id.male_rb;
                final String type = typeEt.getText().toString().trim();
                final String kind = kindEt.getText().toString().trim();
                final int price = Integer.parseInt(priceEt.getText().toString());
                final String description = descriptionEt.getText().toString().trim();

                Advertisement advertisement = new Advertisement(mViewModel.getCurrentUser(), "Unknown", price, isSell, description, isPet);
                advertisement.setGender(isMale);
                advertisement.setPetType(type);
                advertisement.setPetKind(kind);
                Toast.makeText(getContext(), "Done Uploading", Toast.LENGTH_SHORT).show();
                mViewModel.addAdvertisement(advertisement);
                mLoadingDialog.dismiss();
                alertDialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mAdsAdapter.notifyItemInserted(mAdsAdapter.getItemCount()-1);
                        mAdsAdapter.refresh();
                    }
                }, 2000);
            }
        };

        mViewModel.getOnAdUploadPhotoLiveData().observe(getViewLifecycleOwner(), mDoneUploadingObserver);

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

        descriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                publishBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: add checks for fields
                mViewModel.uploadAdPhotos(mSelectedImageList);
                showLoadingDialog();
            }
        });

        alertDialog.show();
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

    @Override
    public void onStart() {
        super.onStart();
        mAdsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        mAdsAdapter.stopListening();
    }
}
