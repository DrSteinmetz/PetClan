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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private static AuthRepository authRepository;

    private Context mContext;
    private FirebaseAuth mAuth;

    FirebaseFirestore mCloudDB = FirebaseFirestore.getInstance();
    CollectionReference mCloudUsers = mCloudDB.collection("users");

    public static CallbackManager mCallbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;

    private Uri mSelectedImage;

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
                            mRegistrationListener.onRegistrationSucceed(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail: FAILURE", task.getException());
                            mRegistrationListener.onRegistrationFailed(task.getException().getMessage());
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
                                mLoginListener.onLoginFailed(task.getException().getMessage());
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
                                mCloudDB.collection("users").document(user.getEmail())
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
                        mCloudDB.collection("users").document(user.getEmail())
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

    private void loginOrCreateNewUser(FirebaseUser user, Task<DocumentSnapshot> task) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
            //TODO: User exists and move on to app's feed
            if (mLoginListener != null) {
                mLoginListener.onLoginSucceed(user.getUid());
            }
            Log.d(TAG, "onComplete: sign up " + user.getUid());
        } else {
            //TODO: Put photo in Storage
            mSelectedImage = Uri.parse(user.getPhotoUrl().toString() + "?type=large");
            createNewCloudUser(user);
            if (mRegistrationListener != null) {
                mRegistrationListener.onRegistrationSucceed(user.getUid());
            }
            Log.d(TAG, "onComplete: registration " + user.getUid());
        }
    }


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
}
