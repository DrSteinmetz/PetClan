package com.example.android2project.view.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.android2project.R;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.SettingsViewModel;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsViewModel mViewModel;

    private LocationUtils mLocationUtils;

    private Observer<String> mOnUpdateUserNameInCloudSucceed;
    private Observer<String> mOnUpdateUserNameInCloudFailed;

    private Observer<String> mOnUpdateUserNameInAuthFailed;

    private Observer<String> mOnUpdatePasswordSucceed;
    private Observer<String> mOnUpdatePasswordFailed;

    private Observer<Address> mOnLocationChanged;
    private Address mUserLocation;
    private Preference locationPref;

    private final String TAG = "SettingsFragment";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationUtils = LocationUtils.getInstance(requireActivity());

        mOnUpdateUserNameInCloudSucceed = new Observer<String>() {
            @Override
            public void onChanged(String newUsername) {
                Snackbar.make(requireView(), "Your username has changed to " + newUsername,
                        Snackbar.LENGTH_LONG).show();
                ((TextView) requireActivity().findViewById(R.id.user_name_tv)).setText(newUsername);
            }
        };

        mOnUpdateUserNameInCloudFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUpdateUserNameInAuthFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        };

        mOnUpdatePasswordSucceed = new Observer<String>() {
            @Override
            public void onChanged(String newPassword) {
                Snackbar.make(requireView(), "Password changed successfully",
                        Snackbar.LENGTH_SHORT).show();
            }
        };

        mOnUpdatePasswordFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnLocationChanged = new Observer<Address>() {
            @Override
            public void onChanged(Address address) {
                mUserLocation = address;
                mViewModel.updateUserLocation(address);
                if (locationPref != null) {
                    locationPref.setSummary(address.getLocality());
                }
            }
        };
        mLocationUtils.getLocationLiveData().observe(this, mOnLocationChanged);

        startObservation();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Settings)).get(SettingsViewModel.class);

        //addPreferencesFromResource(R.xml.preferences);
        setPreferencesFromResource(R.xml.preferences, rootKey);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        SeekBarPreference sbp = findPreference("distance_sb");
        if (sbp != null) {
            sbp.setSummary(sbp.getValue() + " Km");
        }

        EditTextPreference usernameEt = findPreference("username_et");
        if (usernameEt != null) {
            usernameEt.setText(mViewModel.getUsername());
        }

        locationPref = findPreference("location_pref");
        if (locationPref != null) {
            locationPref.setSummary(mUserLocation == null ? "Unknown" : mUserLocation.getLocality());
        }

        SwitchPreferenceCompat GPSwitch = findPreference("gps_switch");
        if (GPSwitch != null && mLocationUtils != null) {
            GPSwitch.setChecked(mLocationUtils.isLocationEnabled());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        if (rootView != null) {
            rootView.setBackgroundColor(Color.WHITE);
        }

        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.getString(key, ""));
        Preference pref = findPreference(key);

        switch (key) {
            case "username_et":
                if (pref instanceof EditTextPreference) {
                    EditTextPreference etp = (EditTextPreference) pref;
                    final String username = etp.getText();
                    if (username.trim().length() > 0) {
                        Log.d(TAG, "onSharedPreferenceChanged: " + username);
                        mViewModel.updateUserName(username);
                    }
                }
                break;
            case "password_et":
                if (pref instanceof EditTextPreference) {
                    EditTextPreference etp = (EditTextPreference) pref;
                    final String password = etp.getText();
                    if (password.trim().length() > 0) {
                        Log.d(TAG, "onSharedPreferenceChanged: " + password);
                        mViewModel.updatePassword(password);
                    }
                }
                break;
            case "gps_switch":
                if (pref instanceof SwitchPreferenceCompat) {
                    SwitchPreferenceCompat GPSwitch = (SwitchPreferenceCompat) pref;

                    if (GPSwitch.isChecked()) {
                        mLocationUtils.requestLocationPermissions();
                    } else {
                        //TODO: mLocationUtils.turnOffLocation
                    }
                }
                break;
            case "distance_sb":
                if (pref instanceof SeekBarPreference) {
                    SeekBarPreference sbp = (SeekBarPreference) pref;
                    final int value = ((sbp.getValue() / 25) * 25) == 0 ? 1 : ((sbp.getValue() / 25) * 25);
                    final String progress = Integer.toString(value);
                    sbp.setValue(value);
                    sbp.setSummary(progress + " Km");
                }
                break;
            case "help":
                if (pref != null) {
                    Log.d(TAG, "onSharedPreferenceChanged: " + pref.getSummary());
                }
                break;
            case "account_deletion":
                if (pref != null) {
                    //TODO: make a dialog for mViewModel.deleteUser();
                }
                break;
        }
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getUpdateUserNameInCloudSucceed().observe(this, mOnUpdateUserNameInCloudSucceed);
            mViewModel.getUpdateUserNameInCloudFailed().observe(this, mOnUpdateUserNameInCloudFailed);
            mViewModel.getUpdateUserNameInAuthFailed().observe(this, mOnUpdateUserNameInAuthFailed);
            mViewModel.getUpdatePasswordSucceed().observe(this, mOnUpdatePasswordSucceed);
            mViewModel.getUpdatePasswordFailed().observe(this, mOnUpdatePasswordFailed);
        }
    }



    @Override
    public void onStop() {
        super.onStop();

        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
