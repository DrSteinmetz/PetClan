package com.example.android2project;

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
import com.example.android2project.fragments.LoginDetailsFragment;
import com.example.android2project.fragments.LoginRegistrationFragment;
import com.example.android2project.fragments.SignUpDetailsFragment;
import com.example.android2project.fragments.UserDetailsFragment;
import com.example.android2project.fragments.UserPictureFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Arrays;

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

    private FirebaseAuth mAuth;


    private CallbackManager mCallbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private File mFile;
    private Uri mSelectedImage;

    private ImageView mUserPicIv;

    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 7;


    private FirebaseDatabase database;
    private DatabaseReference myUsersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_FRAG)
                .commit();

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        database = FirebaseDatabase.getInstance();
        myUsersDB = database.getReference();


        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Success: Login");

                        handleFacebookToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login Canceled",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );


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
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else if (requestCode == CAMERA_REQUEST) {
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

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * <-------Sets LoginRegistrationFragment buttons------->
     **/
    @Override
    public void onJoin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, SignUpDetailsFragment.newInstance(), SIGN_UP_FRAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignIn(String screenName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginDetailsFragment.newInstance(), LOGIN_DETAILS_FRAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * <-------Sets LoginDetailsFragment buttons------->
     **/
    @Override
    public void onSignIn(String screenName, String email, String password) {
        signInExistingUser(email, password);
    }

    /**
     * <-------Sets SignUpDetailsFragment buttons------->
     **/
    @Override
    public void onNext(String screenName, String param1, String param2) {
        if (screenName.equals("UserDetails")){
            final FirebaseUser user=mAuth.getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(param1+""+param2)
                    .build();

            if(user!=null) {
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, UserPictureFragment.newInstance(), USER_PIC_FRAG)
                    .addToBackStack(null)
                    .commit();

        }
        else {
            createUserWithDetails(param1, param2);
        }
    }

    /**
     * <-------Sets UserDetailsFragment buttons------->
     **/
    @Override
    public void onFacebook(String screenName) {
        //TODO: Check where the click came from
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                Arrays.asList("email", "public_profile"));
    }

    @Override
    public void onGoogle(String screenName) {
        //TODO: Check where the click came from
        Toast.makeText(this, "Google Successfully", Toast.LENGTH_SHORT).show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn();
    }



    /**
     * <-------Sets UserPictureFragment buttons------->
     **/
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
            }
        }

        mUserPicIv = userPicIv;
    }

    @Override
    public void onFinish() {
        if(mUserPicIv!=null){
            FirebaseUser user=mAuth.getCurrentUser();

            Log.d("user",user.getEmail());

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(mSelectedImage)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "User pic updated", Toast.LENGTH_SHORT).show();
                                User nUser=new User(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getPhotoUrl().toString());
                                myUsersDB.child("users").child(mAuth.getCurrentUser().getUid()).setValue(nUser);

                            }
                        }
                    });
//            User nUser=new User(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getPhotoUrl().toString());
//            myUsersDB.child("users").child(mAuth.getCurrentUser().getUid()).setValue(nUser);
        }
//        Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();
        // TODO: Add the user to our users list

        // FirebaseUser user = mAuth.getCurrentUser();
    }


    /**
     * <-------Fire Base Authentication Methods------->
     **/
    private void createUserWithDetails(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // FirebaseUser user = mAuth.getCurrentUser();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.root_layout, UserDetailsFragment.newInstance(),
                                            USER_DETAILS_FRAG)
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

    /**
     * <-------Google sign in Methods------->
     **/
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Google Success",
                                    Toast.LENGTH_SHORT).show();
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential: Failure", task.getException());
                            Snackbar.make(findViewById(R.id.root_layout),
                                    "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * <-------Facebook login Methods------->
     **/
    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG, "handleFacebookToken: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "Sign in with credentials successfully", Toast.LENGTH_SHORT).show();
                    mSelectedImage = Uri.parse(mAuth.getCurrentUser().getPhotoUrl().toString() + "?type=large");
                    Log.d(TAG, "onComplete: " + mSelectedImage.toString());
                } else {
                    Log.d(TAG, "Sign in with credentials FAILED");
                }
            }
        });
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
            Toast.makeText(this, "Welecome "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuth.getCurrentUser()!=null){
            mAuth.signOut();
        }
    }
}