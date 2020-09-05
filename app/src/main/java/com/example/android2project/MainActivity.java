package com.example.android2project;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android2project.fragments.LoginDetailsFragment;
import com.example.android2project.fragments.LoginRegistrationFragment;
import com.example.android2project.fragments.SignUpDetailsFragment;
import com.example.android2project.fragments.UserDetailsFragment;
import com.example.android2project.fragments.UserPictureFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
    public void onSignIn(String screenName, String email, String password) {
        signInExistingUser(email, password);
    }

    @Override
    public void onSignIn(String screenName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginDetailsFragment.newInstance(), LOGIN_DETAILS_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**<-------Sets SignUpDetailsFragment buttons------->**/
    @Override
    public void onNext(String screenName, String email, String password) {
        createUserWithDetails(email, password);
    }

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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, UserPictureFragment.newInstance(), USER_PIC_TAG)
                .addToBackStack(null)
                .commit();
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

    private void createUserWithDetails(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // FirebaseUser user = mAuth.getCurrentUser();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.root_layout, UserDetailsFragment.newInstance(), USER_DETAILS_TAG)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInExistingUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this,
                                    "Signed In Successfully", Toast.LENGTH_SHORT).show();
                            // FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
             String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
    }
}