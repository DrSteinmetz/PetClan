package com.example.android2project.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;

public class UserPictureFragment extends Fragment {

    public interface UserPictureListener {
        void onGallery(ImageView imageView);
        void onCamera(ImageView imageView);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_picture, container, false);

        final ImageView userPicture = rootView.findViewById(R.id.user_pic);
        ImageButton galleryBtn = rootView.findViewById(R.id.gallery_btn);
        ImageButton cameraBtn = rootView.findViewById(R.id.camera_btn);
        Button finishBtn = rootView.findViewById(R.id.finish_btn);

        userPicture.setClipToOutline(true);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGallery(userPicture);
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCamera(userPicture);
                }
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });

        return rootView;
    }
}