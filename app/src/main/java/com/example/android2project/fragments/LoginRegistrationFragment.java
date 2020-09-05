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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginRegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public interface LoginRegisterFragmentListener {
        void onSignIn(String screenName);
        void onJoin();
    }

    private LoginRegisterFragmentListener listener;

    public LoginRegistrationFragment() {}

    // TODO: Rename and change types and number of parameters
    public static LoginRegistrationFragment newInstance() {
        LoginRegistrationFragment fragment = new LoginRegistrationFragment();
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
            listener = (LoginRegisterFragmentListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement LoginRegisterFragment Listener!");
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