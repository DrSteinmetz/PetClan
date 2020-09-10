package com.example.android2project.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.model.User;
import com.example.android2project.view.fragments.LoginDetailsFragment;
import com.example.android2project.view.fragments.LoginRegistrationFragment;
import com.example.android2project.view.fragments.SignUpDetailsFragment;
import com.example.android2project.view.fragments.UserDetailsFragment;
import com.example.android2project.view.fragments.UserPictureFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        LoginRegistrationFragment.LoginRegisterFragmentListener,
        SignUpDetailsFragment.SignUpDetailsListener,
        UserDetailsFragment.UserDetailsListener,
        UserPictureFragment.UserPictureListener,
        LoginDetailsFragment.LoginDetailsListener {

    private final String SIGN_REG_FRAG = "sign_registration_fragment";
    private final String SIGN_UP_FRAG = "sign_up_fragment";
    private final String USER_DETAILS_FRAG = "user_details_fragment";
    private final String USER_PIC_FRAG = "user_picture_fragment";
    private final String LOGIN_DETAILS_FRAG = "login_details_fragment";

    private final String TAG = "MainActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore mCloudDB = FirebaseFirestore.getInstance();
    CollectionReference mCloudUsers = mCloudDB.collection("users");

    private File mFile;
    private Uri mSelectedImage;

    private ImageView mUserPicIv;

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_FRAG)
                .commit();

        //MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                mSelectedImage = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.android2project.provider", mFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImage);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                if (mUserPicIv != null) {
                    Glide.with(MainActivity.this)
                            .load(mSelectedImage)
                            .error(R.drawable.ic_petclan_logo)
                            .into(mUserPicIv);
                }
            } else if (requestCode == GALLERY_REQUEST) {
                if (data != null) {
                    mSelectedImage = data.getData();

                    if (mUserPicIv != null) {
                        Glide.with(MainActivity.this)
                                .load(mSelectedImage)
                                .error(R.drawable.ic_petclan_logo)
                                .into(mUserPicIv);
                    }
                }
            }
        }

        if (getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG) != null) {
            getSupportFragmentManager().findFragmentByTag(LOGIN_DETAILS_FRAG)
                    .onActivityResult(requestCode, resultCode, data);
        }
        if (getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG) != null) {
            getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG)
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
            //TODO: Move to app's feed
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
        if (screenName.equals("LoginDetails")) {
        } else if (screenName.equals("SignUpDetails")) {
        }
        //TODO: check if login or registration and move o next screen accordingly
    }

    @Override
    public void onGoogle(String screenName) {
        if (screenName.equals("LoginDetails")) {
        } else if (screenName.equals("SignUpDetails")) {
        }
        //TODO: check if login or registration and move o next screen accordingly
    }

    /**<-------Sets UserPictureFragment buttons------->**/
    @Override
    public void onGallery(ImageView userPicIv) {
        Toast.makeText(this, "Gallery!", Toast.LENGTH_SHORT).show();

        mUserPicIv = userPicIv;

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,
                "Choose your profile picture"), GALLERY_REQUEST);
    }

    @Override
    public void onCamera(ImageView userPicIv) {
        /**<-------Requesting user permissions------->**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_PERMISSION_REQUEST);
            } else {
                mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                mSelectedImage = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.android2project.provider", mFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImage);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }

        mUserPicIv = userPicIv;
    }

    @Override
    public void onFinish() {
        Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();

        final FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mSelectedImage)
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                createNewCloudUser(user);
                            }
                        }
                    });
        }
    }

    /*private void getCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Name, email address, and profile photo Url
             String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            Uri photoUrl = firebaseUser.getPhotoUrl();

            // Check if firebaseUser's email is verified
            boolean emailVerified = firebaseUser.isEmailVerified();

            // The firebaseUser's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = firebaseUser.getUid();
        }
    }*/

    private void createNewCloudUser(final FirebaseUser firebaseUser) {
        String[] fullName = firebaseUser.getDisplayName().split(" ");
        String firstName = fullName[0];
        String lastName = fullName[1];

        User user = new User(firebaseUser.getEmail(), firstName, lastName,
                mSelectedImage.toString());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user);
        mCloudUsers.document(user.getEmail()).set(userMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /**<-------If the new user returned to the SignUpDetailsFragment, delete this user------->**/
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAG);
        if (fragment != null && fragment.isVisible()) {
            Log.d(TAG, "onBackPressed: " + fragment.getTag() + "Visible: " + fragment.isVisible());
            if (mAuth.getCurrentUser() != null) {
                //TODO: add 'are you sure you want to go back? If you do, you will loose you progress' dialog
                mAuth.getCurrentUser().delete();
                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            }
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
        mAuth.signOut();

        super.onDestroy();
    }
}