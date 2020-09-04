package com.example.android2project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android2project.fragments.LoginDetailsFragment;
import com.example.android2project.fragments.LoginRegistrationFragment;
import com.example.android2project.fragments.SignUpDetailsFragment;
import com.example.android2project.fragments.UserDetailsFragment;
import com.example.android2project.fragments.UserPictureFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements
        LoginRegistrationFragment.LoginRegisterFragmentListener,
        SignUpDetailsFragment.SignUpDetailsListener,
        UserDetailsFragment.UserDetailsListener,
        UserPictureFragment.UserPictureListener,
        LoginDetailsFragment.LoginDetailsListener {

    private final String SIGN_REG_TAG = "sign_registration_fragment";
    private final String SIGN_UP_TAG = "sign_up_fragment";
    private final String USER_DETAILS_TAG = "user_details_fragment";
    private final String USER_PIC_TAG = "user_picture_fragment";
    private final String LOGIN_DETAILS_TAG = "login_details_fragment";

    private final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_TAG)
                .commit();

        mAuth = FirebaseAuth.getInstance();
    }

    /**<-------Sets LoginRegistrationFragment buttons------->**/
    /**<-------Sets LoginDetailsFragment buttons------->**/
    @Override
    public void onJoin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, SignUpDetailsFragment.newInstance(), SIGN_UP_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignIn(String screenName) {
        if (screenName.equals("LoginRegistration")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, LoginDetailsFragment.newInstance(), LOGIN_DETAILS_TAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("LoginDetails"))
        Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
    }

    /**<-------Sets SignUpDetailsFragment buttons------->**/
    /**<-------Sets UserDetailsFragment buttons------->**/
    @Override
    public void onFacebook(String screenName) {
        //TODO: Check where the click came from
        Toast.makeText(this, "Facebook Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGoogle(String screenName) {
        //TODO: Check where the click came from
        Toast.makeText(this, "Google Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(String screenName) {
        if (screenName.equals("SignUpDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, UserDetailsFragment.newInstance(), USER_DETAILS_TAG)
                    .addToBackStack(null)
                    .commit();
        } else if (screenName.equals("UserDetails")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, UserPictureFragment.newInstance(), USER_PIC_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**<-------Sets UserPictureFragment buttons------->**/
    @Override
    public void onGallery() {
        Toast.makeText(this, "Gallery!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCamera() {
        Toast.makeText(this, "Camera!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinish() {
        Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}