package com.example.android2project.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;
import com.example.android2project.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AuthRepository {
    private static AuthRepository authRepository;

    private Context mContext;
    private FirebaseAuth mAuth;
    private String mUserToken = "";

    private FirebaseFirestore mCloudDB = FirebaseFirestore.getInstance();
    private CollectionReference mCloudUsers = mCloudDB.collection("users");

    public static CallbackManager mCallbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;

    private Uri mSelectedImage = Uri.parse("");

    private final String TAG = "AuthRepository";

    /**<-------Login interface------->**/
    public interface RepositoryLoginInterface {
        void onLoginSucceed(String uId);

        void onLoginFailed(String error);
    }

    private RepositoryLoginInterface mLoginListener;

    public void setLoginListener(RepositoryLoginInterface repositoryLoginInterface) {
        this.mLoginListener = repositoryLoginInterface;
    }

    /**<-------Registration interface------->**/
    public interface RepositoryRegistrationInterface {
        void onRegistrationSucceed(String uId);

        void onRegistrationFailed(String error);
    }

    private RepositoryRegistrationInterface mRegistrationListener;

    public void setRegistrationListener(RepositoryRegistrationInterface repositoryRegistrationInterface) {
        this.mRegistrationListener = repositoryRegistrationInterface;
    }

    /**<-------Details Setting interface------->**/
    public interface RepositoryDetailsSetInterface {
        void onDetailsSetSucceed(String uId);

        void onDetailsSetFailed(String error);
    }

    private RepositoryDetailsSetInterface mDetailsSetListener;

    public void setDetailsSetListener(RepositoryDetailsSetInterface repositoryDetailsSetInterface) {
        this.mDetailsSetListener = repositoryDetailsSetInterface;
    }

    /**<-------User Creation interface------->**/
    public interface RepositoryCreateUserInterface {
        void onCreateUserSucceed(boolean isDefaultPic);

        void onCreateUserFailed(String error);
    }

    private RepositoryCreateUserInterface mCreateUserListener;

    public void setCreateUserListener(RepositoryCreateUserInterface repositoryCreateUserInterface) {
        this.mCreateUserListener = repositoryCreateUserInterface;
    }

    /**<-------User Deletion interface------->**/
    public interface RepositoryDeleteUserInterface {
        void onDeleteUserSucceed(boolean value);
    }

    private RepositoryDeleteUserInterface mDeleteUserListener;

    public void setDeleteUserListener(RepositoryDeleteUserInterface repositoryDeleteUserInterface) {
        this.mDeleteUserListener = repositoryDeleteUserInterface;
    }

    /**<-------User Get User Name interface------->**/
    public interface RepositoryGetUserNameInterface {
        void onGetUserNameSucceed(String value);
    }

    private RepositoryGetUserNameInterface mGetUserNameListener;

    public void setGetUserNameListener(RepositoryGetUserNameInterface repositoryGetUserNameInterface) {
        this.mGetUserNameListener = repositoryGetUserNameInterface;
    }

    /**<-------Sign Out User interface------->**/
    public interface RepositorySignOutUserInterface {
        void onSignOutUserSucceed(boolean value);
    }

    private RepositorySignOutUserInterface mSignOutUserListener;

    public void setSignOutUserListener(RepositorySignOutUserInterface repositorySignOutUserInterface) {
        this.mSignOutUserListener = repositorySignOutUserInterface;
    }

    /**<-------Get All Users interface------->**/
    public interface RepositoryGetAllUsersInterface {
        void onGetAllUsersSucceed(ArrayList<User> value);
    }

    private RepositoryGetAllUsersInterface mGetAllUsersListener;

    public void setGetAllUsersListener(RepositoryGetAllUsersInterface repositoryGetAllUsersInterface) {
        this.mGetAllUsersListener = repositoryGetAllUsersInterface;
    }

    /**<-------Singleton------->**/
    public static AuthRepository getInstance(Context context) {
        if (authRepository == null) {
            authRepository = new AuthRepository(context);
        }
        return authRepository;
    }

    private AuthRepository(final Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Success: Login");

                        handleFacebookToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(context, "Login Canceled",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(context, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /**<-------Fire Base Authentication Methods------->**/
    public void registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (mRegistrationListener != null && user != null) {
                                mRegistrationListener.onRegistrationSucceed(user.getUid());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail: FAILURE", task.getException());
                            if (mRegistrationListener != null) {
                                mRegistrationListener.onRegistrationFailed(Objects
                                        .requireNonNull(task.getException()).getMessage());
                            }
                            /*Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });
    }

    public void signInExistingUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            //TODO: Enter to the app's Feed
                            if (user != null) {
                                if (mLoginListener != null) {
                                    mLoginListener.onLoginSucceed(user.getUid());
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail: FAILURE", task.getException());
                            if (mLoginListener != null) {
                                mLoginListener.onLoginFailed(Objects.
                                        requireNonNull(task.getException()).getMessage());
                            }
                            /*Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });
    }


    /**<-------Google Methods------->**/
    public void onGoogle(Fragment fragment) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

        googleSignIn(fragment);
    }

    private void googleSignIn(Fragment fragment) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser user = mAuth.getCurrentUser();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Signed in to Google with credentials successfully");

                            if (user != null) {
                                mCloudDB.collection("users")
                                        .document(Objects.requireNonNull(user.getEmail()))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                loginOrCreateNewUser(user, task);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Sign in with credentials FAILED");
                            if (mLoginListener != null) {
                                mLoginListener.onLoginFailed("Failed to login");
                            }
                            if (mRegistrationListener != null) {
                                mRegistrationListener.onRegistrationFailed("Failed to register");
                            }
                        }
                    }
                });
    }

    /**<-------Facebook Methods------->**/
    public void onFacebook(Fragment fragment) {
        LoginManager.getInstance().logInWithReadPermissions(fragment,
                Arrays.asList("email", "public_profile"));
    }

    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG, "handleFacebookToken: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser user = mAuth.getCurrentUser();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Signed in to Facebook with credentials successfully");

                            if (user != null) {
                                mCloudDB.collection("users")
                                        .document(Objects.requireNonNull(user.getEmail()))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                mSelectedImage = Uri.parse("facebook");
                                                loginOrCreateNewUser(user, task);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Sign in with credentials FAILED");
                            if (mLoginListener != null) {
                                mLoginListener.onLoginFailed("Failed to login");
                            }
                            if (mRegistrationListener != null) {
                                mRegistrationListener.onRegistrationFailed("Failed to register");
                            }
                        }
                    }
                });
    }

    /**<-------Update user details------->**/
    public void onUserDetailsInsertion(String firstName, String lastName) {
        final FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (mDetailsSetListener != null) {
                                    mDetailsSetListener.onDetailsSetSucceed(user.getUid());
                                }
                                Log.d(TAG, "Username: " + user.getDisplayName());
                            } else {
                                if (mDetailsSetListener != null) {
                                    mDetailsSetListener.onDetailsSetFailed(Objects.requireNonNull(task.
                                            getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }

    private void loginOrCreateNewUser(FirebaseUser user, Task<DocumentSnapshot> task) {
        DocumentSnapshot document = task.getResult();
        if (document != null && document.exists()) {
            if (mLoginListener != null) {
                mLoginListener.onLoginSucceed(user.getUid());
            }
            Log.d(TAG, "onComplete: sign up " + user.getUid());
        } else {
            if (mSelectedImage.toString().equals("facebook")) {
                mSelectedImage = Profile.getCurrentProfile().getProfilePictureUri(200, 200);
            } else {
                mSelectedImage = Uri.parse(Objects.requireNonNull(user.getPhotoUrl()).toString());
            }

            createNewCloudUser(user, false);
            Log.d(TAG, "onComplete: registration " + user.getUid());
        }
    }

    private void createNewCloudUser(final FirebaseUser firebaseUser, final boolean isDefaultPic) {
        final boolean[] isImageUploaded = {false};
        final boolean[] isUserCreatedInCloud = {false};

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mSelectedImage)
                .build();

        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isImageUploaded[0] = true;
                            if (mCreateUserListener != null && isUserCreatedInCloud[0]) {
                                mCreateUserListener.onCreateUserSucceed(isDefaultPic);
                            }

                            if (mRegistrationListener != null && isUserCreatedInCloud[0]) {
                                mRegistrationListener.onRegistrationSucceed(firebaseUser.getUid());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed to update user picture in Auth");
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });

            String[] fullName = Objects.requireNonNull(firebaseUser.getDisplayName()).split(" ");
            String firstName = fullName[0];
            String lastName = fullName[1];

            final User user = new User(firebaseUser.getEmail(), firstName, lastName,
                    mSelectedImage.toString(), mUserToken);

            mCloudUsers.document(user.getEmail()).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isUserCreatedInCloud[0] = true;
                            Log.d(TAG, "Photo URL: " + firebaseUser.getPhotoUrl());
                            if (mCreateUserListener != null && isImageUploaded[0]) {
                                mCreateUserListener.onCreateUserSucceed(isDefaultPic);
                            }

                            if (mRegistrationListener != null && isImageUploaded[0]) {
                                mRegistrationListener.onRegistrationSucceed(firebaseUser.getUid());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCreateUserListener != null) {
                                mCreateUserListener.onCreateUserFailed(e.getMessage());
                            }

                            if (mRegistrationListener != null) {
                                mRegistrationListener.onRegistrationFailed(e.getMessage());
                            }
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        }
    }

    public void createNewCloudUser(final String imagePath) {
        mSelectedImage = Uri.parse(imagePath);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            boolean isDefaultPic = false;
            if (imagePath.equals("/users_profile_picture/default_user_pic.png")) {
                isDefaultPic = true;
            }
            createNewCloudUser(user, isDefaultPic);
        }
    }

    public void setUserToken(final String userToken) {
        this.mUserToken = userToken;
    }

    public String getUserId() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    public boolean isUserLoggedIn() {
        boolean isUserLoggedIn = false;
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            isUserLoggedIn = true;
        }

        return isUserLoggedIn;
    }

    public void deleteUserFromAuth() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (mDeleteUserListener != null) {
                        mDeleteUserListener.onDeleteUserSucceed(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mDeleteUserListener != null) {
                        mDeleteUserListener.onDeleteUserSucceed(false);
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

    public String getUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = null;

        if (user != null) {
            userEmail = user.getEmail();
        }

        return userEmail;
    }

    public void getUserName() {
        String name = "No Name Found";
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            name = user.getDisplayName();
        }

        if (mGetUserNameListener != null) {
            mGetUserNameListener.onGetUserNameSucceed(name);
        }
    }

    public void signOutUser() {
        mAuth.signOut();
        FirebaseUser user = mAuth.getCurrentUser();
        boolean result = false;

        if (user == null) {
            result = true;
        }

        if (mSignOutUserListener != null) {
            mSignOutUserListener.onSignOutUserSucceed(result);
        }
    }

    public String getUserImageUri() {
        String imageUri = null;
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            Log.d(TAG, "getUserImageUri: " + firebaseUser.getPhotoUrl());
            imageUri = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
        }

        return imageUri;
    }


}
