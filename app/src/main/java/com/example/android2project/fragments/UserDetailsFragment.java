package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android2project.R;
import com.google.android.material.textfield.TextInputLayout;

public class UserDetailsFragment extends Fragment {

    private final String TAG = "UserDetailsFragment";

    public interface UserDetailsListener {
<<<<<<< HEAD
        void onNext(String screenName,String firstName,String LastName);
=======
        void onNext(String screenName, String firstName, String lastName);
>>>>>>> 824ca9738a0e73dff2f8b5672e7df0c7a8d743a3
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);
        final EditText firstNameET = rootView.findViewById(R.id.first_name_et);
        final EditText lastNameET = rootView.findViewById(R.id.last_name_et);
        final Button nextBtn = rootView.findViewById(R.id.next_btn);
        final EditText firstNameEt = rootView.findViewById(R.id.first_name_et);
        final EditText lastNameEt = rootView.findViewById(R.id.last_name_et);
        final EditText businessNameEt = rootView.findViewById(R.id.business_name_et);
        final CheckBox businessCb = rootView.findViewById(R.id.is_business_cb);
        final TextInputLayout businessLayout = rootView.findViewById(R.id.business_name_layout);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
<<<<<<< HEAD
                    listener.onNext("UserDetails",firstNameET.getText().toString(),lastNameET.getText().toString());
=======
                    String firstName = firstNameEt.getText().toString().trim();
                    String lastName = lastNameEt.getText().toString().trim();
                    String businessName = null;
                    boolean business = true;
                    if (businessCb.isChecked()) {
                        business = false;
                        businessName = businessNameEt.getText().toString().trim();
                        if (businessName.length() > 0) {
                            business = true;
                        }
                    }

                    if (firstName.length() > 0 && lastName.length() > 0 && business) {
                        firstNameEt.setError(null);
                        lastNameEt.setError(null);

                        listener.onNext("UserDetails", firstName, lastName);
                    } else {
                        if (firstName.length() < 1) {
                            firstNameEt.setError("You must enter your first name!");
                        } else {
                            firstNameEt.setError(null);
                        }
                        if (lastName.length() < 1) {
                            lastNameEt.setError("You must enter your last name!");
                        } else {
                            lastNameEt.setError(null);
                        }
                        if (businessName.length() < 1) {
                            businessNameEt.setError("You must enter you business name!");
                        } else {
                            businessNameEt.setError(null);
                        }
                    }
>>>>>>> 824ca9738a0e73dff2f8b5672e7df0c7a8d743a3
                }
            }
        });

        businessCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    businessLayout.setVisibility(View.VISIBLE);
                } else {
                    businessLayout.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }
}