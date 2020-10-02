package com.example.android2project.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.android2project.R;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        final ExtendedFloatingActionButton contactBtn  = rootView.findViewById(R.id.contact_btn);
        final TextView itemNameTv = rootView.findViewById(R.id.item_name_tv);
        final TextView kindTv = rootView.findViewById(R.id.kind_tv);
        final TextView genderTv = rootView.findViewById(R.id.gender_tv);
        final TextView priceTv = rootView.findViewById(R.id.price_tv);
        final TextView publicDateTv = rootView.findViewById(R.id.date_tv);
        final TextView descriptionTv = rootView.findViewById(R.id.description_tv);
        final TextView locationTv = rootView.findViewById(R.id.location_tv);

        itemNameTv.setText(mAdvertisement.getItemName());
        kindTv.setText(mAdvertisement.getPetKind());
        genderTv.setText(mAdvertisement.getIsMale() ? "Male" : "Female");
        priceTv.setText(mAdvertisement.getPrice() + "₪");
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
}
