package com.example.android2project.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.MenuAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.MainViewModel;
import com.example.android2project.viewmodel.UserPictureViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity {
    private DuoDrawerLayout mDrawerLayout;

    private MainViewModel mViewModel;
    private UserPictureViewModel mUserPictureViewModel;

    private Observer<String> mGetUserNameObserver;
    private Observer<Boolean> mSignOutUserObserver;
    private Observer<Uri> mDownloadUserProfilePicSucceedObserver;
    private Observer<String> mDownloadUserProfilePicFailedObserver;

    ArrayList<String> mMenuOptions = new ArrayList<>();

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView userProfilePictureIv = findViewById(R.id.user_pic_iv);
        final TextView userNameTv = findViewById(R.id.user_name_tv);
        final Button logOutBtn = findViewById(R.id.log_out_btn);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Main)).get(MainViewModel.class);
        mUserPictureViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Picture)).get(UserPictureViewModel.class);

        mGetUserNameObserver = new Observer<String>() {
            @Override
            public void onChanged(String username) {
                userNameTv.setText(username);
            }
        };

        mSignOutUserObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Intent welcomeIntent = new Intent(MainActivity.this,
                        WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
            }
        };

        mDownloadUserProfilePicSucceedObserver = new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Log.d(TAG, "URL of downloaded picture: " + uri.toString());

                loadProfilePictureWithGlide(uri.toString(), userProfilePictureIv);
            }
        };

        mDownloadUserProfilePicFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Log.d(TAG, "On Downloading User Profile Picture: " + error);
            }
        };

        mViewModel.getGetUserName().observe(this, mGetUserNameObserver);
        mViewModel.getSignOutSucceed().observe(this, mSignOutUserObserver);
        mViewModel.getDownloadPicSucceed().observe(this, mDownloadUserProfilePicSucceedObserver);
        mViewModel.getDownloadPicFailed().observe(this, mDownloadUserProfilePicFailedObserver);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOutUser();
            }
        });

        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DuoMenuView duoMenuView = (DuoMenuView) findViewById(R.id.menu);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mMenuOptions.add("Settings");
        mMenuOptions.add("Profile");

        MenuAdapter menuAdapter = new MenuAdapter(mMenuOptions);
        duoMenuView.setAdapter(menuAdapter);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mViewModel.getUserName();

        String imageUri = mViewModel.downloadUserProfilePicture();
        if (imageUri != null) {
            imageUri += "?height=1000";
            Log.d(TAG, "URL of downloaded picture: " + imageUri);

            loadProfilePictureWithGlide(imageUri, userProfilePictureIv);
        }
    }

    private void loadProfilePictureWithGlide(String uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(MainActivity.this)
                .load(uri)
                .apply(options)
                .into(imageView);
    }
}