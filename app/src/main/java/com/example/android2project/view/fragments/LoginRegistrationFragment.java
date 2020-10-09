package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;

public class LoginRegistrationFragment extends Fragment {

    private AlertDialog mLoadingDialog;

    public interface LoginRegisterFragmentListener {
        void onSignIn(String screenName);
        void onJoin();
        void onSignInAsGuest(AlertDialog loadingDialog);
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
        Button signInGuestBtn = rootView.findViewById(R.id.sign_in_guest_btn);

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

        signInGuestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    showLoadingDialog();
                    listener.onSignInAsGuest(mLoadingDialog);
                }
            }
        });

        return rootView;
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.loading_dog_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(false);
        mLoadingDialog = builder.create();
        mLoadingDialog.show();
        mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}