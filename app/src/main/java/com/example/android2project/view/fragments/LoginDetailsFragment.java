package com.example.android2project.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.viewmodel.LoginRegistrationViewModel;
import com.example.android2project.model.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginDetailsFragment extends Fragment {

    private LoginRegistrationViewModel mViewModel;

    private Observer<String> mLoginSucceedObserver;
    private Observer<String> mLoginFailedObserver;

    private Observer<String> mRegisterSucceedObserver;
    private Observer<String> mRegisterFailedObserver;

    private boolean mIsGoogle;
    private boolean mIsFacebook;

    private final String TAG = "LoginDetailsFragment";

    public interface LoginDetailsListener {
        void onFacebook(String screenName);
        void onGoogle(String screenName);
        void onSignIn(String screenName);
    }

    private LoginDetailsListener listener;

    public LoginDetailsFragment() {}

    public static LoginDetailsFragment newInstance() {
        return new LoginDetailsFragment();
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

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.LoginRegistration)).get(LoginRegistrationViewModel.class);

        mLoginSucceedObserver = new Observer<String>() {
            @Override
            public void onChanged(String uId) {
                if (listener != null) {
                    if (!mIsGoogle && !mIsFacebook) {
                        listener.onSignIn("LoginDetails");
                    } else if (mIsFacebook) {
                        listener.onFacebook("LoginDetails");
                    } else {
                        listener.onGoogle("LoginDetails");
                    }
                }
            }
        };

        mLoginFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mRegisterSucceedObserver = new Observer<String>() {
            @Override
            public void onChanged(String uId) {
                if (listener != null) {
                    if (mIsFacebook) {
                        listener.onFacebook("LoginDetails");
                    } else if (mIsGoogle){
                        listener.onGoogle("LoginDetails");
                    }
                }
            }
        };

        mRegisterFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startObservation();

        View rootView = inflater.inflate(R.layout.fragment_login_details, container, false);

        final EditText emailEt = rootView.findViewById(R.id.email_et);
        final EditText passwordEt = rootView.findViewById(R.id.password_et);
        final ImageButton facebookBtn = rootView.findViewById(R.id.facebook_btn);
        final ImageButton googleBtn = rootView.findViewById(R.id.google_btn);
        final Button signInBtn = rootView.findViewById(R.id.sign_in_btn);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFacebook = true;
                mIsGoogle = false;
                if (mViewModel != null) {
                    mViewModel.onFacebook(LoginDetailsFragment.this);
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFacebook = false;
                mIsGoogle = true;
                if (mViewModel != null) {
                    mViewModel.onGoogle(LoginDetailsFragment.this);
                }
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFacebook = false;
                mIsGoogle = false;
                if (listener != null) {
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    if (email.trim().length() > 0 && password.trim().length() > 0) {
                        emailEt.setError(null);
                        passwordEt.setError(null);

                        mViewModel.loginWithDetails(email, password);
                    } else {
                        if (email.trim().length() < 1) {
                            emailEt.setError(getContext().getString(R.string.email));
                        } else {
                            emailEt.setError(null);
                        }
                        if (password.trim().length() < 1) {
                            passwordEt.setError(getContext().getString(R.string.password));
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
        mViewModel.getLoginSucceed().observe(getViewLifecycleOwner(), mLoginSucceedObserver);
        mViewModel.getLoginFailed().observe(getViewLifecycleOwner(), mLoginFailedObserver);
        mViewModel.getRegisterSucceed().observe(getViewLifecycleOwner(), mRegisterSucceedObserver);
        mViewModel.getRegisterFailed().observe(getViewLifecycleOwner(), mRegisterFailedObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.getLoginSucceed().removeObservers(this);
        mViewModel.getLoginFailed().removeObservers(this);
        mViewModel.getRegisterSucceed().removeObservers(this);
        mViewModel.getRegisterFailed().removeObservers(this);
    }
}
