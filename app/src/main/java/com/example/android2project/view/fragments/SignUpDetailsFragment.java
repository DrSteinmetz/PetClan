package com.example.android2project.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.viewmodel.LoginRegistrationViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignUpDetailsFragment extends Fragment {

    private final String mEmailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    LoginRegistrationViewModel mViewModel;

    private Observer<String> mRegisterSucceedObserver;
    private Observer<String> mRegisterFailedObserver;

    private boolean mIsGoogle;
    private boolean mIsFacebook;

    private final String TAG = "SignUpDetailsFragment";

    public interface SignUpDetailsListener {
        void onFacebook(String screenName);
        void onGoogle(String screenName);
        void onNext(String screenName);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Login)).get(LoginRegistrationViewModel.class);

        mRegisterSucceedObserver = new Observer<String>() {
            @Override
            public void onChanged(String uId) {
                if (listener != null) {
                    if (!mIsGoogle && !mIsFacebook) {
                        listener.onNext("SignUpDetails");
                    } else if (mIsFacebook) {
                        listener.onFacebook("SignUpDetails");
                    } else {
                        listener.onGoogle("SignUpDetails");
                    }
                }
            }
        };

        mRegisterFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };
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
                mIsGoogle = false;
                mIsFacebook = true;
                startObservation();
                if (mViewModel != null) {
                    mViewModel.onFacebook(SignUpDetailsFragment.this);
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsGoogle = true;
                mIsFacebook = false;
                startObservation();
                if (mViewModel != null) {
                    mViewModel.onGoogle(SignUpDetailsFragment.this);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsGoogle = false;
                mIsFacebook = false;
                startObservation();
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

                        if (mViewModel != null) {
                            mViewModel.registerWithDetails(email, password);
                        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (resultCode == getActivity().RESULT_OK && requestCode == AuthRepository.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                mViewModel.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in FAILED", e);
            }
        }

        AuthRepository.mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startObservation() {
        mViewModel.getRegisterSucceed().observe(this, mRegisterSucceedObserver);
        mViewModel.getRegisterFailed().observe(this, mRegisterFailedObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.getRegisterSucceed().removeObserver(mRegisterSucceedObserver);
        mViewModel.getRegisterFailed().removeObserver(mRegisterFailedObserver);
    }
}