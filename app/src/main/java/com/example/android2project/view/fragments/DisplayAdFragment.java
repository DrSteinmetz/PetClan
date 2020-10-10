package com.example.android2project.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.android2project.R;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DisplayAdFragment extends DialogFragment {

    private Advertisement mAdvertisement;

    private String mMyEmail;

    public DisplayAdFragment() {
    }

    public static DisplayAdFragment newInstance(Advertisement advertisement, String myEmail) {
        DisplayAdFragment fragment = new DisplayAdFragment();
        Bundle args = new Bundle();
        args.putSerializable("advertisement", advertisement);
        args.putString("my_email", myEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAdvertisement = (Advertisement) getArguments().getSerializable("advertisement");
            mMyEmail = getArguments().getString("my_email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display_ad, container, false);

        final ImageSlider imageSlider = rootView.findViewById(R.id.images_slider);
        final ImageView authorPic = rootView.findViewById(R.id.author_pic_iv);
        final TextView authorName = rootView.findViewById(R.id.author_name_tv);
        final ExtendedFloatingActionButton contactBtn  = rootView.findViewById(R.id.contact_btn);
        final Chip itemNameTv = rootView.findViewById(R.id.item_name_tv);
        final Chip isSellTv = rootView.findViewById(R.id.is_sell_tv);
        final TextView kindTv = rootView.findViewById(R.id.kind_tv);
        final LinearLayout isPetLayout = rootView.findViewById(R.id.is_pet_layout);
        final TextView genderTv = rootView.findViewById(R.id.gender_tv);
        final TextView priceTv = rootView.findViewById(R.id.price_tv);
        final TextView publicDateTv = rootView.findViewById(R.id.date_tv);
        final TextView descriptionTv = rootView.findViewById(R.id.description_tv);
        final TextView locationTv = rootView.findViewById(R.id.location_tv);


        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(requireContext())
                .load(mAdvertisement.getUser().getPhotoUri())
                .apply(options)
                .into(authorPic);
        if(mMyEmail.equals(mAdvertisement.getUser().getEmail())){
            authorName.setVisibility(View.GONE);
            authorPic.setVisibility(View.GONE);
        }
        authorName.setText(mAdvertisement.getUser().getFirstName()+"\n"+mAdvertisement.getUser().getLastName());
        itemNameTv.setText(mAdvertisement.getItemName());
        isSellTv.setText(mAdvertisement.getIsSell() ? getResources().getString(R.string.sell_ad_tv) :
                getResources().getString(R.string.hand_over_ad_tv));
        kindTv.setText(mAdvertisement.getPetKind());
        isPetLayout.setVisibility(mAdvertisement.getIsPet() ? View.VISIBLE : View.GONE);
        genderTv.setText(mAdvertisement.getIsMale() ? getResources().getString(R.string.male) :
                getResources().getString(R.string.female));
        priceTv.setText(String.valueOf(mAdvertisement.getPrice()));
        publicDateTv.setText(dateToFormatDate(mAdvertisement.getPublishDate()));
        descriptionTv.setText(mAdvertisement.getDescription());
        locationTv.setText(mAdvertisement.getLocation());

        final List<SlideModel> imagesList = new ArrayList<>();
        for (String uri : mAdvertisement.getImages()) {
            imagesList.add(new SlideModel(uri, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imagesList, ScaleTypes.CENTER_CROP);

        contactBtn.setVisibility(mMyEmail.equals(mAdvertisement.getUser().getEmail()) ?
                View.GONE : View.VISIBLE);

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User publisher = mAdvertisement.getUser();
                ConversationFragment.newInstance(publisher)
                        .show(getParentFragmentManager().beginTransaction(), "fragment_conversation");
            }
        });

        return rootView;
    }

    @SuppressLint("SimpleDateFormat")
    private String dateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
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
