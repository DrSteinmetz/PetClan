package com.example.android2project.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.AdsAdapter;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.MarketPlaceViewModel;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private Observer<Integer> mOnUploadingAdPhotosSucceed;
    private Observer<String> mOnUploadingAdPhotosFailed;

    private Observer<Advertisement> mOnUploadingAdSucceed;
    private Observer<String> mOnUploadingAdFailed;

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;

    public MarketPlaceFragment() {}

    public static MarketPlaceFragment newInstance() {
        return new MarketPlaceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.MarketPlace)).get(MarketPlaceViewModel.class);

        mOnUploadingAdPhotosFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUploadingAdSucceed = new Observer<Advertisement>() {
            @Override
            public void onChanged(Advertisement advertisement) {
                mLoadingDialog.dismiss();
                mAdsAdapter.refresh();
                stopObservation();
            }
        };

        mOnUploadingAdFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_market_place, container, false);
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
        mAdsAdapter = new AdsAdapter(getContext(), mOptions);

        mAdsAdapter.setAdsAdapterListener(new AdsAdapter.AdsAdapterInterface() {
            @Override
            public void onAdClick(View view, int position) {
                final Advertisement ad = mAdsAdapter.getCurrentAd(position);

                DisplayAdFragment.newInstance(ad, mViewModel.getCurrentUser().getEmail())
                        .show(getChildFragmentManager().beginTransaction(), "");
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                final Advertisement ad = mAdsAdapter.getCurrentAd(position);

                showEditAdDialog(ad);
            }

            @Override
            public void onDeleteOptionClicked(int position, View view) {}
        });

        mRecycler.setAdapter(mAdsAdapter);

        addAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAdDialog();
            }
        });

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getOnAdUploadPhotoSucceed().observe(getViewLifecycleOwner(), mOnUploadingAdPhotosSucceed);
            mViewModel.getOnAdUploadPhotoFailed().observe(getViewLifecycleOwner(), mOnUploadingAdPhotosFailed);
            mViewModel.getOnAdUploadSucceed().observe(getViewLifecycleOwner(), mOnUploadingAdSucceed);
            mViewModel.getOnAdUploadFailed().observe(getViewLifecycleOwner(), mOnUploadingAdFailed);
        }
    }

    private void stopObservation() {
        if (mViewModel != null) {
            mViewModel.getOnAdUploadPhotoSucceed().removeObserver(mOnUploadingAdPhotosSucceed);
            mViewModel.getOnAdUploadPhotoFailed().removeObserver(mOnUploadingAdPhotosFailed);
            mViewModel.getOnAdUploadSucceed().removeObserver(mOnUploadingAdSucceed);
            mViewModel.getOnAdUploadFailed().removeObserver(mOnUploadingAdFailed);
        }
    }

    public void showAddAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_add_advertisement,
                        (RelativeLayout) requireActivity().findViewById(R.id.add_ad_container));

        builder.setView(view);
        builder.setCancelable(true);

        final RadioGroup actionRg = view.findViewById(R.id.action_rg);
        final RadioGroup categoryRg = view.findViewById(R.id.category_rg);
        final RadioGroup genderRg = view.findViewById(R.id.gender_rg);
        final TextInputEditText itemNameEt = view.findViewById(R.id.item_name_et);
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
            }
        });

        mOnUploadingAdPhotosSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final boolean isSell = actionRg.getCheckedRadioButtonId() == R.id.sell_rb;
                final boolean isPet = categoryRg.getCheckedRadioButtonId() == R.id.pet_rb;
                final boolean isMale = genderRg.getCheckedRadioButtonId() == R.id.male_rb;
                final String itemName = Objects.requireNonNull(itemNameEt.getText()).toString().trim();
                final String kind = kindEt.getText().toString().trim();
                final String price = priceEt.getText().toString();
                final String description = descriptionEt.getText().toString().trim();

                Advertisement advertisement = new Advertisement(mViewModel.getCurrentUser(),
                        itemName, "Unknown", price, isSell, description, isPet);
                advertisement.setIsMale(isMale);
                advertisement.setPetKind(kind);
                mViewModel.addAdvertisement(advertisement);
                alertDialog.dismiss();
            }
        };

        startObservation();

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

    public void showEditAdDialog(final Advertisement ad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_add_advertisement,
                        (RelativeLayout) requireActivity().findViewById(R.id.add_ad_container));

        builder.setView(view);
        builder.setCancelable(true);

        final RadioGroup actionRg = view.findViewById(R.id.action_rg);
        final RadioGroup categoryRg = view.findViewById(R.id.category_rg);
        final RadioGroup genderRg = view.findViewById(R.id.gender_rg);
        final TextInputEditText itemNameEt = view.findViewById(R.id.item_name_et);
        final TextInputEditText kindEt = view.findViewById(R.id.pet_kind_et);
        final TextInputEditText priceEt = view.findViewById(R.id.pet_price_et);
        final TextInputEditText descriptionEt = view.findViewById(R.id.pet_description_et);
        final ImageButton galleryBtn = view.findViewById(R.id.gallery_btn);
        final ImageButton cameraBtn = view.findViewById(R.id.camera_btn);
        final Button publishBtn = view.findViewById(R.id.publish_btn);
        initImageViews(view);

        Log.d(TAG, "showEditAdDialog: " + ad.toString());

        actionRg.check(ad.getIsSell() ? R.id.sell_rb : R.id.hand_over_rb);
        categoryRg.check(ad.getIsPet() ? R.id.pet_rb : R.id.product_rb);
        genderRg.check(ad.getIsMale() ? R.id.male_rb : R.id.female_rb);
        itemNameEt.setText(ad.getItemName());
        kindEt.setText(ad.getPetKind());
        priceEt.setText(ad.getPrice() + "₪");
        descriptionEt.setText(ad.getDescription());

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);
        for (int i = 0; i < ad.getImages().size(); i++) {
            Glide.with(requireContext())
                    .load(ad.getImages().get(i))
                    .apply(options)
                    .into(mImageViews.get(i));
        }

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
            }
        });

        mOnUploadingAdPhotosSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final boolean isSell = actionRg.getCheckedRadioButtonId() == R.id.sell_rb;
                final boolean isPet = categoryRg.getCheckedRadioButtonId() == R.id.pet_rb;
                final boolean isMale = genderRg.getCheckedRadioButtonId() == R.id.male_rb;
                final String itemName = Objects.requireNonNull(itemNameEt.getText()).toString().trim();
                final String kind = kindEt.getText().toString().trim();
                final String price = priceEt.getText().toString();
                final String description = descriptionEt.getText().toString().trim();

                ad.setIsSell(isSell);
                ad.setIsPet(isPet);
                ad.setIsMale(isMale);
                ad.setItemName(itemName);
                ad.setPetKind(kind);
                ad.setPrice(price);
                ad.setDescription(description);
                mViewModel.addAdvertisement(ad);
                alertDialog.dismiss();
            }
        };

        startObservation();

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                publishBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
