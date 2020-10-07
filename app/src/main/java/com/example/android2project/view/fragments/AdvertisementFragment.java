package com.example.android2project.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.PhotosPreviewRecyclerview;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.AdvertisementViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdvertisementFragment extends DialogFragment {

    private static final String TAG = "AdvertisementFragment";
    private AdvertisementViewModel mViewModel;
    private Advertisement mAdvertisement;
    private File mFile;
    private List<String> mCityNames;
    private AlertDialog mLoadingDialog;
    private LocationUtils mLocationUtils;

    private ArrayAdapter<String> cityArrayAdapter;
    private LinearLayout locationLayout;
    private Button locationBtn;
    private AutoCompleteTextView locationAutoCompleteTv;
    private LinearLayout categoryLayout;
    private LinearLayout genderLayout;
    private TextInputLayout kindLayout;
    private TextInputLayout priceLayout;
    private TextInputLayout descriptionLayout;
    private RadioGroup actionRg;
    private RadioGroup categoryRg;
    private RadioGroup genderRg;
    private LinearLayout spinnerLayout;
    private Spinner typeSp;
    private TextInputEditText kindEt;
    private TextInputEditText priceEt;
    private TextInputEditText descriptionEt;
    private ImageButton addImageBtn;
    private Button publishBtn;
    private PhotosPreviewRecyclerview mImagePreviewRecycler;

    private Observer<Integer> mOnUploadingAdPhotosSucceed;
    private Observer<String> mOnUploadingAdPhotosFailed;
    private Observer<Advertisement> mOnUploadingAdSucceed;
    private Observer<String> mOnUploadingAdFailed;
    private Observer<Address> mOnLocationChanged;

    private final int WRITE_PERMISSION_REQUEST = 7;
    private final int IMAGE_VIEW_SIZE = 8;

    public interface AdvertisementInterface {
        void onAdUploadSucceed(Advertisement ad, AlertDialog loadingDialog);
    }

    private AdvertisementInterface listener;

    public AdvertisementFragment() {
    }

    public static AdvertisementFragment newInstance(Advertisement ad) {
        AdvertisementFragment fragment = new AdvertisementFragment();
        Bundle args = new Bundle();
        args.putSerializable("ad", ad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AdvertisementInterface) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement Advertisement Listener!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAdvertisement = (Advertisement) getArguments().getSerializable("ad");
        }

        mLocationUtils = LocationUtils.getInstance((Activity) requireContext());

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Advertisement)).get(AdvertisementViewModel.class);

        mCityNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.city_names)));

        cityArrayAdapter = new ArrayAdapter<String>
                (requireContext(), android.R.layout.select_dialog_item, mCityNames);

        mOnUploadingAdPhotosSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final boolean isSell = actionRg.getCheckedRadioButtonId() == R.id.sell_rb;
                final boolean isPet = categoryRg.getCheckedRadioButtonId() == R.id.pet_rb;
                final boolean isMale = genderRg.getCheckedRadioButtonId() == R.id.male_rb;
                final String itemType = typeSp.getSelectedItem().toString();
                final String kind = kindEt.getText().toString().trim();
                final String cityName = locationAutoCompleteTv.getText().toString();
                final int price = Integer.parseInt(priceEt.getText().toString());
                final String description = descriptionEt.getText().toString().trim();

                if (mAdvertisement == null) {
                    Advertisement advertisement = new Advertisement(mViewModel.getCurrentUser(),
                            itemType, cityName, price, isSell, description, isPet);

                    advertisement.setIsMale(isMale);
                    advertisement.setPetKind(kind);

                    mViewModel.addAdvertisement(advertisement);
                } else {
                    mAdvertisement.setIsSell(isSell);
                    mAdvertisement.setIsPet(isPet);
                    mAdvertisement.setIsMale(isMale);
                    mAdvertisement.setItemName(itemType);
                    mAdvertisement.setPetKind(kind);
                    mAdvertisement.setPrice(price);
                    mAdvertisement.setDescription(description);

                    mViewModel.addAdvertisement(mAdvertisement);
                }
            }
        };

        mOnUploadingAdPhotosFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUploadingAdSucceed = new Observer<Advertisement>() {
            @Override
            public void onChanged(Advertisement advertisement) {
                if (listener != null) {
                    listener.onAdUploadSucceed(advertisement, mLoadingDialog);
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            }
        };

        mOnUploadingAdFailed = new Observer<String>() {
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

        View rootView = inflater.inflate(R.layout.fragment_add_advertisement, container, false);

        categoryLayout = rootView.findViewById(R.id.category_layout);
        genderLayout = rootView.findViewById(R.id.gender_layout);
        actionRg = rootView.findViewById(R.id.action_rg);
        categoryRg = rootView.findViewById(R.id.category_rg);
        genderRg = rootView.findViewById(R.id.gender_rg);
        spinnerLayout = rootView.findViewById(R.id.spinner_layout);
        typeSp = rootView.findViewById(R.id.type_spinner);
        kindLayout = rootView.findViewById(R.id.pet_kind_et_layout);
        locationLayout = rootView.findViewById(R.id.location_layout);
        priceLayout = rootView.findViewById(R.id.pet_price_et_layout);
        descriptionLayout = rootView.findViewById(R.id.pet_description_et_layout);
        locationAutoCompleteTv = rootView.findViewById(R.id.location_auto_tv);
        kindEt = rootView.findViewById(R.id.pet_kind_et);
        priceEt = rootView.findViewById(R.id.pet_price_et);
        descriptionEt = rootView.findViewById(R.id.pet_description_et);
        locationBtn = rootView.findViewById(R.id.locate_btn);
        addImageBtn = rootView.findViewById(R.id.add_image_btn);
        mImagePreviewRecycler = rootView.findViewById(R.id.photos_preview);
        publishBtn = rootView.findViewById(R.id.publish_btn);

        if (mAdvertisement == null) {
            mImagePreviewRecycler.init(IMAGE_VIEW_SIZE);
        } else {
            mImagePreviewRecycler.initEdit(IMAGE_VIEW_SIZE, mAdvertisement);
        }

        locationAutoCompleteTv.setThreshold(1); // will start working from first character
        locationAutoCompleteTv.setAdapter(cityArrayAdapter); // setting the adapter data into the AutoCompleteTextView
        locationAutoCompleteTv.setTextColor(Color.BLACK);

        if (mAdvertisement == null) {
            actionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    categoryLayout.setVisibility(View.VISIBLE);
                }
            });
            categoryRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.pet_rb) {
                        genderLayout.setVisibility(View.VISIBLE);
                        kindLayout.setVisibility(View.VISIBLE);
                        spinnerLayout.setVisibility(View.VISIBLE);
                        List<String> list = Arrays.asList(getResources().getStringArray(R.array.pet_types));
                        ArrayAdapter<String> petTypes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
                        typeSp.setAdapter(petTypes);

                    } else if (checkedId == R.id.product_rb) {
                        if (genderLayout.getVisibility() == View.VISIBLE) {
                            genderLayout.setVisibility(View.GONE);
                        }
                        if (kindLayout.getVisibility() == View.VISIBLE) {
                            kindLayout.setVisibility(View.GONE);
                        }
                        spinnerLayout.setVisibility(View.VISIBLE);
                        List<String> list = Arrays.asList(getResources().getStringArray(R.array.product_types));
                        ArrayAdapter<String> productTypes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
                        typeSp.setAdapter(productTypes);
                    }
                    spinnerLayout.setVisibility(View.VISIBLE);
                    priceLayout.setVisibility(View.VISIBLE);
                    locationLayout.setVisibility(View.VISIBLE);
                    descriptionLayout.setVisibility(View.VISIBLE);
                    mImagePreviewRecycler.setVisibility(View.VISIBLE);
                    addImageBtn.setVisibility(View.VISIBLE);
                    publishBtn.setVisibility(View.VISIBLE);
                }
            });
        } else {
            categoryLayout.setVisibility(View.VISIBLE);
            priceLayout.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.VISIBLE);
            descriptionLayout.setVisibility(View.VISIBLE);
            mImagePreviewRecycler.setVisibility(View.VISIBLE);
            addImageBtn.setVisibility(View.VISIBLE);
            publishBtn.setVisibility(View.VISIBLE);
            spinnerLayout.setVisibility(View.VISIBLE);

            final int[] position = new int[1];
            categoryRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.pet_rb) {
                        genderLayout.setVisibility(View.VISIBLE);
                        kindLayout.setVisibility(View.VISIBLE);
                        List<String> list = Arrays.asList(getResources().getStringArray(R.array.pet_types));
                        position[0] = list.indexOf(mAdvertisement.getItemName());
                        ArrayAdapter<String> petTypes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
                        typeSp.setAdapter(petTypes);
                    } else if (checkedId == R.id.product_rb) {
                        if (genderLayout.getVisibility() == View.VISIBLE) {
                            genderLayout.setVisibility(View.GONE);
                        }
                        if (kindLayout.getVisibility() == View.VISIBLE) {
                            kindLayout.setVisibility(View.GONE);
                        }
                        List<String> list = Arrays.asList(getResources().getStringArray(R.array.product_types));
                        ArrayAdapter<String> products = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
                        position[0] = list.indexOf(mAdvertisement.getItemName());
                        typeSp.setAdapter(products);
                    }
                    spinnerLayout.setVisibility(View.VISIBLE);
                }
            });

            actionRg.check(mAdvertisement.getIsSell() ? R.id.sell_rb : R.id.hand_over_rb);
            categoryRg.check(mAdvertisement.getIsPet() ? R.id.pet_rb : R.id.product_rb);
            genderRg.check(mAdvertisement.getIsMale() ? R.id.male_rb : R.id.female_rb);
            typeSp.setSelection(position[0]);
            kindEt.setText(mAdvertisement.getPetKind());
            locationAutoCompleteTv.setText(mAdvertisement.getLocation());
            priceEt.setText(mAdvertisement.getPrice() + "");
            descriptionEt.setText(mAdvertisement.getDescription());
        }

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationUtils.isLocationEnabled() && requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mOnLocationChanged = new Observer<Address>() {
                        @Override
                        public void onChanged(Address address) {
                            locationAutoCompleteTv.setText(LocationUtils.getAddress().getLocality());
                        }
                    };
                    mLocationUtils.getLocationLiveData().observe(getViewLifecycleOwner(), mOnLocationChanged);
                } else if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mLocationUtils.requestLocationPermissions();
                } else {
                    Toast.makeText(requireContext(), "Please enable Location", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);

                CropImage.activity()
                        .setAspectRatio(4, 3)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
                        .start(requireContext(), AdvertisementFragment.this);
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
                final boolean isPet = categoryRg.getCheckedRadioButtonId() == R.id.pet_rb;
                final String kind = kindEt.getText().toString().trim();
                final String price = priceEt.getText().toString();
                final String description = descriptionEt.getText().toString().trim();
                final String cityName = locationAutoCompleteTv.getText().toString();

                if (isPet) {
                    if (genderRg.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(getContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                    }
                    if (kind.isEmpty()) {
                        kindEt.setError("Please enter kind");
                    } else {
                        kindEt.setError(null);
                    }
                }

                if (!mCityNames.contains(cityName)) {
                    locationAutoCompleteTv.setError("Please Enter a City from the list");
                } else {
                    locationAutoCompleteTv.setError(null);
                }

                if (price.isEmpty()) {
                    priceEt.setError("Please enter price");
                } else {
                    priceEt.setError(null);
                }

                if (description.isEmpty()) {
                    descriptionEt.setError("Please enter description");
                } else {
                    descriptionEt.setError(null);
                }

                if (isPet && genderRg.getCheckedRadioButtonId() != -1
                        && !kind.isEmpty() && !price.isEmpty() && !description.isEmpty() && mCityNames.contains(cityName)) {
                    mViewModel.uploadAdPhotos(mImagePreviewRecycler.getSelectedImageList());
                    showLoadingDialog();
                } else if (!isPet && !price.isEmpty() && !description.isEmpty() && mCityNames.contains(cityName)) {
                    mViewModel.uploadAdPhotos(mImagePreviewRecycler.getSelectedImageList());
                    showLoadingDialog();
                }
            }
        });

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getOnAdUploadPhotoSucceed().observe(this, mOnUploadingAdPhotosSucceed);
            mViewModel.getOnAdUploadPhotoFailed().observe(this, mOnUploadingAdPhotosFailed);
            mViewModel.getOnAdUploadSucceed().observe(this, mOnUploadingAdSucceed);
            mViewModel.getOnAdUploadFailed().observe(this, mOnUploadingAdFailed);
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
                        .setAspectRatio(4, 3)
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
                mImagePreviewRecycler.addPhoto(result.getUri());
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

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopObservation();
    }
}
