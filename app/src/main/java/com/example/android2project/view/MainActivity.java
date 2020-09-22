package com.example.android2project.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.MenuAdapter;
import com.example.android2project.model.Post;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.view.fragments.CommentsFragment;
import com.example.android2project.view.fragments.FeedFragment;
import com.example.android2project.viewmodel.MainViewModel;
import com.example.android2project.viewmodel.UserPictureViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ibrahimsn.lib.SmoothBottomBar;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements
        FeedFragment.FeedListener {
    private DuoDrawerLayout mDrawerLayout;


    private MainViewModel mViewModel;
    private UserPictureViewModel mUserPictureViewModel;

    private Observer<String> mGetUserNameObserver;
    private Observer<Boolean> mSignOutUserObserver;
    private Observer<Uri> mDownloadUserProfilePicSucceedObserver;
    private Observer<String> mDownloadUserProfilePicFailedObserver;

    private ArrayList<String> mMenuOptions = new ArrayList<>();

    private String bestProvider;

    private ViewPager mViewPager;
    private ViewPagerAdapter mPageAdapter;
    private SmoothBottomBar mBottomBar;

    private final int LOCATION_REQUEST_CODE = 1;

    private Handler mHandler;
    private Geocoder mGetoCoder;
    private String cityName=null;
    private LocationCallback mLocationCallback;

    private FusedLocationProviderClient mfusedLocationProviderClient;
    private final String FEED_FRAG = "feed_fragment";
    private final String COMMENTS_FRAG = "comments_fragment";

    private final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGetoCoder=new Geocoder(this, Locale.getDefault());

        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPremission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPremission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }

        final ImageView userProfilePictureIv = findViewById(R.id.user_pic_iv);
        final TextView userNameTv = findViewById(R.id.user_name_tv);
        final Button logOutBtn = findViewById(R.id.log_out_btn);
        mViewPager = findViewById(R.id.view_pager);
        mBottomBar = findViewById(R.id.bottomBar);
        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getFragments());

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Main)).get(MainViewModel.class);
        mUserPictureViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Picture)).get(UserPictureViewModel.class);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mBottomBar.setItemActiveIndex(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setAdapter(mPageAdapter);

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

        startObservation();

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

        mMenuOptions.add("Profile");
        mMenuOptions.add("Settings");

        MenuAdapter menuAdapter = new MenuAdapter(mMenuOptions);
        duoMenuView.setAdapter(menuAdapter);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mViewModel.getUserName();

        String imageUri = mViewModel.downloadUserProfilePicture();
        if (imageUri != null) {
            Log.d(TAG, "URL of downloaded picture: " + imageUri);
            loadProfilePictureWithGlide(imageUri, userProfilePictureIv);
        }
    }

    private void startObservation() {
        mViewModel.getGetUserName().observe(this, mGetUserNameObserver);
        mViewModel.getSignOutSucceed().observe(this, mSignOutUserObserver);
        mViewModel.getDownloadPicSucceed().observe(this, mDownloadUserProfilePicSucceedObserver);
        mViewModel.getDownloadPicFailed().observe(this, mDownloadUserProfilePicFailedObserver);
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

    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(FeedFragment.newInstance());

        return fragmentList;
    }

    @Override
    public void onComment(Post post) {
        CommentsFragment.newInstance(post)
                .show(getSupportFragmentManager().beginTransaction(), COMMENTS_FRAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "In order to have functionallity you must provide loation", Toast.LENGTH_SHORT).show();
            } else {
                startLocation();
            }
        }
    }


    private void startLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//        mfusedLocationProviderClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//
//                        }
//                    }
//                });

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<Address> addressList = mGetoCoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1);
                                cityName = addressList.get(0).getLocality();
                                Log.d(TAG, cityName + "");
                                if (cityName != null) {
                                    mHandler.removeCallbacks(this);
                                    mfusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            };
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);

            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mfusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
            }

        }
    }
}



