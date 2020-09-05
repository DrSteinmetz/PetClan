package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;

public class LoginDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public interface LoginDetailsListener {
        void onFacebook(String screenName);
        void onGoogle(String screenName);
        void onSignIn(String screenName, String email, String password);
    }

    private LoginDetailsListener listener;

    public LoginDetailsFragment() {}

    // TODO: Rename and change types and number of parameters
    public static LoginDetailsFragment newInstance() {
        LoginDetailsFragment fragment = new LoginDetailsFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (LoginDetailsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement LoginDetails Listener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_details, container, false);

        final ImageButton facebookBtn = rootView.findViewById(R.id.facebook_btn);
        final ImageButton googleBtn = rootView.findViewById(R.id.google_btn);
        final Button signInBtn = rootView.findViewById(R.id.sign_in_btn);

        final EditText emailEt = rootView.findViewById(R.id.email_et);
        final EditText passwordEt = rootView.findViewById(R.id.password_et);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFacebook("LoginDetails");
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGoogle("LoginDetails");
                }
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    listener.onSignIn("LoginDetails", email, password);
                }
            }
        });

        return rootView;
    }
}