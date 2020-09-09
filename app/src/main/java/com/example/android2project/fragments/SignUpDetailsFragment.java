package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android2project.R;

public class SignUpDetailsFragment extends Fragment {

    private final String mEmailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public interface SignUpDetailsListener {
        void onFacebook(String screenName);
        void onGoogle(String screenName);
        void onNext(String screenName, String email, String password);
    }

    private SignUpDetailsListener listener;

    public SignUpDetailsFragment() {}

    public static SignUpDetailsFragment newInstance() {
        SignUpDetailsFragment fragment = new SignUpDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SignUpDetailsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement SignUpDetails Listener!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_details, container, false);

        final EditText emailEt = rootView.findViewById(R.id.email_et);
        final EditText passwordEt = rootView.findViewById(R.id.password_et);

        final ImageView facebookBtn = rootView.findViewById(R.id.facebook_btn);
        final ImageView googleBtn = rootView.findViewById(R.id.google_btn);
        final Button nextBtn = rootView.findViewById(R.id.next_btn);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFacebook("SignUpDetails");
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGoogle("SignUpDetails");
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    if (email.trim().length() > 0 && password.trim().length() > 0) {
                        if (!email.matches(mEmailRegex) || password.trim().length() < 8) {
                            if (!email.matches(mEmailRegex)) {
                                emailEt.setError("You must enter a valid email!");
                            }
                            if (password.trim().length() < 8) {
                                passwordEt.setError("You must enter at least 8 characters!");
                            }
                            return;
                        }
                        emailEt.setError(null);
                        passwordEt.setError(null);

                        listener.onNext("SignUpDetails", email, password);
                    } else {
                        if (email.trim().length() < 1) {
                            emailEt.setError("You must enter email!");
                        } else {
                            emailEt.setError(null);
                        }
                        if (password.trim().length() < 1) {
                            passwordEt.setError("You must enter a password!");
                        } else {
                            passwordEt.setError(null);
                        }
                    }
                }
            }
        });

        return rootView;
    }
}