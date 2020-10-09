package com.example.android2project.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.UserDetailsViewModel;
import com.example.android2project.model.ViewModelFactory;
import com.google.android.material.textfield.TextInputLayout;

public class UserDetailsFragment extends Fragment {

    private UserDetailsViewModel mViewModel;

    Observer<String> mOnDetailsSetSucceedObserver;
    Observer<String> mOnDetailsSetFailedObserver;

    private final String TAG = "UserDetailsFragment";

    public interface UserDetailsListener {
        void onNext(String screenName);
    }

    private UserDetailsListener listener;

    public UserDetailsFragment() {}

    public static UserDetailsFragment newInstance() {
        UserDetailsFragment fragment = new UserDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UserDetailsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement UserDetails Listener!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.UserDetails)).get(UserDetailsViewModel.class);

        mOnDetailsSetSucceedObserver = new Observer<String>() {
            @Override
            public void onChanged(String uId) {
                listener.onNext("UserDetails");
            }
        };

        mOnDetailsSetFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "User Details setting has FAILED");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        final Button nextBtn = rootView.findViewById(R.id.next_btn);
        final EditText firstNameEt = rootView.findViewById(R.id.first_name_et);
        final EditText lastNameEt = rootView.findViewById(R.id.last_name_et);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    startObservation();
                    String firstName = firstNameEt.getText().toString().trim();
                    String lastName = lastNameEt.getText().toString().trim();

                    if (firstName.length() > 0 && lastName.length() > 0 ) {
                        firstNameEt.setError(null);
                        lastNameEt.setError(null);

                        mViewModel.onUserDetailsInsertion(firstName, lastName);
                    } else {
                        if (firstName.length() < 1) {
                            firstNameEt.setError(getContext().getString(R.string.enter_first_name));
                        } else {
                            firstNameEt.setError(null);
                        }
                        if (lastName.length() < 1) {
                            lastNameEt.setError(getContext().getString(R.string.enter_last_name));
                        } else {
                            lastNameEt.setError(null);
                        }
                    }
                }
            }
        });
      
        return rootView;
    }

    private void startObservation() {
        mViewModel.getRegisterSucceed().observe(this, mOnDetailsSetSucceedObserver);
        mViewModel.getRegisterFailed().observe(this, mOnDetailsSetFailedObserver);
    }
}
