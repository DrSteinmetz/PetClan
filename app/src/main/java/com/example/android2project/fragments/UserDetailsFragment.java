package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;
import com.google.android.material.textfield.TextInputLayout;

public class UserDetailsFragment extends Fragment {

    private final String TAG = "UserDetailsFragment";

    public interface UserDetailsListener {
        void onNext(String screenName);
    }

    private UserDetailsListener listener;

    public UserDetailsFragment() {}

    public static UserDetailsFragment newInstance() {
        UserDetailsFragment fragment = new UserDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UserDetailsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement UserDetails Listener!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        final Button nextBtn = rootView.findViewById(R.id.next_btn);
        final CheckBox businessCb = rootView.findViewById(R.id.is_business_cb);
        final TextInputLayout businessEt = rootView.findViewById(R.id.business_name_layout);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNext("UserDetails");
                }
            }
        });

        businessCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    businessEt.setVisibility(View.VISIBLE);
                } else {
                    businessEt.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }
}