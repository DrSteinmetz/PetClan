package com.example.android2project.view;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.MenuAdapter;
import com.example.android2project.model.Post;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewPagerAdapter;
import com.example.android2project.view.fragments.CommentsFragment;
import com.example.android2project.view.fragments.ConversationFragment;
import com.example.android2project.view.fragments.FeedFragment;
import com.example.android2project.view.fragments.SettingsFragment;
import com.example.android2project.view.fragments.SocialFragment;
import com.example.android2project.view.fragments.UserProfileFragment;
import com.example.android2project.viewmodel.MainViewModel;
import com.example.android2project.viewmodel.UserPictureViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements
        FeedFragment.FeedListener {
    private DuoDrawerLayout mDrawerLayout;

    private MainViewModel mViewModel;
    private UserPictureViewModel mUserPictureViewModel;

    private Observer<Boolean> mSignOutUserObserver;

    private ArrayList<String> mMenuOptions = new ArrayList<>();

    private ViewPager mViewPager;
    private ViewPagerAdapter mPageAdapter;
    private SmoothBottomBar mBottomBar;

    private Handler mHandler;
    private Geocoder mGeoCoder;
    private String mCityName = null;
    private LocationCallback mLocationCallback;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private TextView userLocationTv;

    private final String FEED_FRAG = "feed_fragment";
    private final String COMMENTS_FRAG = "comments_fragment";

    private final int LOCATION_REQUEST_CODE = 1;
    private final int REQUEST_CHECK_SETTINGS = 2;

    private final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeoCoder = new Geocoder(this, Locale.getDefault());
        userLocationTv = findViewById(R.id.location_tv);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
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
       //mViewPager.setOffscreenPageLimit(1);

        mBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                mViewPager.setCurrentItem(i);
                return false;
            }
        });

        mViewPager.setAdapter(mPageAdapter);

        mSignOutUserObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Intent welcomeIntent = new Intent(MainActivity.this,
                        WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
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

        final DuoMenuView duoMenuView = (DuoMenuView) findViewById(R.id.menu);
        final DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mMenuOptions.add("Feed");
        mMenuOptions.add("Chats");
        mMenuOptions.add("Profile");
        mMenuOptions.add("Market Place");
        mMenuOptions.add("Settings");

        duoMenuView.setOnMenuClickListener(new DuoMenuView.OnMenuClickListener() {
            @Override
            public void onFooterClicked() {}

            @Override
            public void onHeaderClicked() {}

            @Override
            public void onOptionClicked(int position, Object objectClicked) {
                if (position < 4) {
                    mViewPager.setCurrentItem(position);
                    mDrawerLayout.closeDrawer();
                } else if (position == 4) {
                    mDrawerLayout.closeDrawer();
                    getSupportFragmentManager().beginTransaction()
                            .add(android.R.id.content, SettingsFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        final MenuAdapter menuAdapter = new MenuAdapter(mMenuOptions);
        duoMenuView.setAdapter(menuAdapter);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        userNameTv.setText(mViewModel.getUserName());

        String userProfileImageUri = mViewModel.downloadUserProfilePicture();
        if (userProfileImageUri != null) {
            Log.d(TAG, "URL of downloaded picture: " + userProfileImageUri);
            loadProfilePictureWithGlide(userProfileImageUri, userProfilePictureIv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //TODO alert dialog that explains that we need permissions.
                Toast.makeText(this, "In order to have functionality you must provide location", Toast.LENGTH_SHORT).show();
            } else {
                startLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            startLocation();
        }
    }

    private void startObservation() {
        mViewModel.getSignOutSucceed().observe(this, mSignOutUserObserver);
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
        fragmentList.add(FeedFragment.newInstance(null));
        fragmentList.add(SocialFragment.newInstance());
        fragmentList.add(UserProfileFragment.newInstance(null));

        return fragmentList;
    }

    @Override
    public void onComment(Post post) {
        CommentsFragment.newInstance(post)
                .show(getSupportFragmentManager().beginTransaction(), COMMENTS_FRAG);
    }

    public interface LocationInterface {
        void onLocationChanged(String cityLocation);

    }

    private LocationInterface mLocationListener;

    public void setLocationListener(LocationInterface locationListener) {
        this.mLocationListener = locationListener;
    }

    public void startLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<Address> addressList = mGeoCoder
                                        .getFromLocation(locationResult.getLastLocation().getLatitude(),
                                                locationResult.getLastLocation().getLongitude(), 1);
                                mCityName = addressList.get(0).getLocality();
                                if (mCityName != null) {
                                    if (mLocationListener != null) {
                                        mLocationListener.onLocationChanged(mCityName);
                                        mHandler.removeCallbacks(this);
                                        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                        userLocationTv.setText(mCityName);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(500);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                    }
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location preferences are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            //TODO:make a custom window.
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            final String userName = bundle.getString("name");
            if (userName != null) {
                final String email = bundle.getString("email");
                final String firstName = userName.split(" ")[0];
                final String lastName = userName.split(" ")[1];
                final String photoPath = bundle.getString("photo");
                final String token = bundle.getString("token");
                User recipient = new User(email, firstName, lastName, photoPath, token);
                Log.d(TAG, "onNewIntent: matan? " + recipient.toString());
                ConversationFragment.newInstance(recipient)
                        .show(getSupportFragmentManager()
                                .beginTransaction(), "conversation_fragment");
            }
        }
    }
}
