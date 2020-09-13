package com.example.android2project.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.view.fragments.LoginDetailsFragment;
import com.example.android2project.view.fragments.LoginRegistrationFragment;
import com.example.android2project.view.fragments.SignUpDetailsFragment;
import com.example.android2project.view.fragments.UserDetailsFragment;
import com.example.android2project.view.fragments.UserPictureFragment;
import com.example.android2project.viewmodel.WelcomeViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity implements
        LoginRegistrationFragment.LoginRegisterFragmentListener,
        SignUpDetailsFragment.SignUpDetailsListener,
        UserDetailsFragment.UserDetailsListener,
        UserPictureFragment.UserPictureListener,
        LoginDetailsFragment.LoginDetailsListener {

    private WelcomeViewModel mViewModel;
    Observer<Boolean> mUserDeletedObserver;

    private final String SIGN_REG_FRAG = "sign_registration_fragment";
    private final String SIGN_UP_FRAG = "sign_up_fragment";
    private final String USER_DETAILS_FRAG = "user_details_fragment";
    private final String USER_PIC_FRAG = "user_picture_fragment";
    private final String LOGIN_DETAILS_FRAG = "login_details_fragment";

    private final String TAG = "WelcomeActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Welcome)).get(WelcomeViewModel.class);

        if (mViewModel.isUserLoggedIn()) {
            startMainActivity();
        }

        mUserDeletedObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Snackbar.make(findViewById(R.id.root_layout), "Your Registration has been Canceled",
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        };

        mViewModel.getUserDeletionSucceed().observe(this, mUserDeletedObserver);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_FRAG)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG) != null) {
            Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG))
                    .onActivityResult(requestCode, resultCode, data);
        }
        if (getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG) != null) {
            Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG))
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    /**<-------Sets LoginRegistrationFragment buttons------->**/
    @Override
    public void onJoin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, SignUpDetailsFragment.newInstance(), SIGN_UP_FRAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignIn(String screenName) {
        if (screenName.equals("LoginRegistration")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, LoginDetailsFragment.newInstance(), LOGIN_DETAILS_FRAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("LoginDetails")) {
            startMainActivity();
        }
    }

    /**<-------Sets SignUpDetailsFragment buttons------->**/
    @Override
    public void onNext(String screenName) {
        if (screenName.equals("SignUpDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, UserDetailsFragment.newInstance(), USER_DETAILS_FRAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("UserDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, UserPictureFragment.newInstance(), USER_PIC_FRAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**<-------Sets UserDetailsFragment buttons------->**/
    @Override
    public void onFacebook(String screenName) {
        Log.d(TAG, "onFacebook");
        startMainActivity();
    }

    @Override
    public void onGoogle(String screenName) {
        startMainActivity();
    }

    @Override
    public void onFinish() {
        Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /**<-------If the new user returned to the SignUpDetailsFragment, delete this user------->**/
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG);
        if (fragment != null && fragment.isVisible()) {
            //TODO: add a dialog
            mViewModel.deleteUserFromAuth();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mViewModel.getUserDeletionSucceed().removeObserver(mUserDeletedObserver);
        super.onDestroy();
    }
}