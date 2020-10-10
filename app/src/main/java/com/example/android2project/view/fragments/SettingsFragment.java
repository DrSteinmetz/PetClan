package com.example.android2project.view.fragments;

import android.content.Context;
import android.content.Intent;
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
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.android2project.R;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.view.DeleteDialog;
import com.example.android2project.view.MainActivity;
import com.example.android2project.view.WelcomeActivity;
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

    private Observer<String> mOnUserDeletionSucceed;

    private Observer<Address> mOnLocationChanged;
    private Observer<String> mOnLocationTriggred;

    private Address mUserLocation;
    private Preference locationPref;

    private boolean mIsLocationDialogClicked = false;
    private String mLocationMode = null;

    private SwitchPreferenceCompat GPSwitch;

    private final String TAG = "SettingsFragment";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mLocationUtils = LocationUtils.getInstance(requireActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOnUpdateUserNameInCloudSucceed = new Observer<String>() {
            @Override
            public void onChanged(String newUsername) {
                Snackbar.make(requireView(), getResources().getString(R.string.user_name_change) + newUsername,
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
                Snackbar.make(requireView(), R.string.password_changed,
                        Snackbar.LENGTH_SHORT).show();
            }
        };

        mOnUpdatePasswordFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUserDeletionSucceed = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                startActivity(new Intent(getActivity(), WelcomeActivity.class));
                requireActivity().finish();
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

        setPreferencesFromResource(R.xml.preferences, rootKey);

        SeekBarPreference sbp = findPreference("distance_sb");
        if (sbp != null) {
            sbp.setSummary(sbp.getValue() + " " + getString(R.string.km));
        }

        EditTextPreference usernameEt = findPreference("username_et");
        if (usernameEt != null) {
            usernameEt.setText(mViewModel.getUsername());
        }

        locationPref = findPreference("location_pref");
        if (locationPref != null) {
            locationPref.setSummary(mUserLocation == null ? requireContext().getResources().getString(R.string.unknown) :
                    mUserLocation.getLocality());
        }

        GPSwitch = findPreference("gps_switch");
        if (GPSwitch != null && mLocationUtils != null) {
            GPSwitch.setChecked(mLocationUtils.isLocationEnabled());
        }

        Preference accountDeletionPref = findPreference("account_deletion_pref");
        if (accountDeletionPref != null) {
            accountDeletionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final DeleteDialog deleteDialog = new DeleteDialog(requireContext());
                    deleteDialog.setPromptText(getString(R.string.account_deletion_dialog));

                    deleteDialog.setOnActionListener(new DeleteDialog.DeleteDialogActionListener() {
                        @Override
                        public void onYesBtnClicked() {
                            mViewModel.deleteUser();
                            deleteDialog.dismiss();
                        }

                        @Override
                        public void onNoBtnClicked() {
                            deleteDialog.dismiss();
                        }
                    });

                    deleteDialog.show();

                    return false;
                }
            });
        }

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOnLocationTriggred = new Observer<String>() {
            @Override
            public void onChanged(String value) {
                mLocationMode = value;

                if (mLocationMode.equals("On")) {
                    GPSwitch.setChecked(true);
                } else {
                    GPSwitch.setChecked(false);
                }
            }
        };

        mLocationUtils.getSwitchLiveData().observe(getViewLifecycleOwner(), mOnLocationTriggred);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        Log.d(TAG, "onSharedPreferenceChanged: " + key);

        switch (key) {
            case "username_et":
                if (pref instanceof EditTextPreference) {
                    EditTextPreference etp = (EditTextPreference) pref;
                    final String username = etp.getText();
                    if (username.trim().length() > 0) {
                        mViewModel.updateUserName(username);
                    }
                }
                break;
            case "password_et":
                if (pref instanceof EditTextPreference) {
                    EditTextPreference etp = (EditTextPreference) pref;
                    final String password = etp.getText();
                    if (password.trim().length() > 0) {
                        mViewModel.updatePassword(password);
                    }
                }
                break;
            case "gps_switch":
                if (pref instanceof SwitchPreferenceCompat) {
                    ((MainActivity) requireActivity()).setLocationBuilderDeniedInterface(new MainActivity.LocationBuilderDeniedInterface() {
                        @Override
                        public void onLocationDenied(boolean isDenied) {
                            mIsLocationDialogClicked = isDenied;
                            GPSwitch.setChecked(!isDenied);
                        }
                    });

                    if (GPSwitch.isChecked() && !mLocationUtils.isLocationEnabled()) {
                        mLocationUtils.requestLocationPermissions();
                    } else if (mLocationMode != null && mLocationMode.equals("Off")) {
                        GPSwitch.setChecked(false);
                    } else if (mIsLocationDialogClicked && (mLocationMode != null && !mLocationMode.equals("On"))) {
                        if (GPSwitch.isChecked()) { // if was manually - not with dialog
                            mLocationUtils.turnGPSOff();
                        } else {
                            GPSwitch.setChecked(false);
                            mIsLocationDialogClicked = false;
                        }
                    } else if (!GPSwitch.isChecked() && !mIsLocationDialogClicked) {
                        mLocationUtils.turnGPSOff();
                    }
                }
                break;
            case "distance_sb":
                if (pref instanceof SeekBarPreference) {
                    SeekBarPreference sbp = (SeekBarPreference) pref;
                    final int value = ((sbp.getValue() / 25) * 25) == 0 ? 1 : ((sbp.getValue() / 25) * 25);
                    final String progress = Integer.toString(value);
                    sbp.setValue(value);
                    sbp.setSummary(progress + " " + getString(R.string.km));
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
            mViewModel.getUserDeletionSucceed().observe(this, mOnUserDeletionSucceed);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
