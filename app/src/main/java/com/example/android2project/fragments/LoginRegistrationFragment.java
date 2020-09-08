package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;

public class LoginRegistrationFragment extends Fragment {

    public interface LoginRegisterFragmentListener {
        void onSignIn(String screenName);
        void onJoin();
    }

    private LoginRegisterFragmentListener listener;

    public LoginRegistrationFragment() {}

    public static LoginRegistrationFragment newInstance() {
        LoginRegistrationFragment fragment = new LoginRegistrationFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (LoginRegisterFragmentListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement LoginRegisterFragment Listener!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_login_registration, container, false);

        Button signInTv = rootView.findViewById(R.id.sign_in_btn);
        Button joinBtn = rootView.findViewById(R.id.join_btn);

        signInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSignIn("LoginRegistration");
                }
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onJoin();
                }
            }
        });

        return rootView;
    }
}