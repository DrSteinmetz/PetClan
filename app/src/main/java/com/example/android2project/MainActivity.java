package com.example.android2project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements
        LoginRegistrationFragment.LoginRegisterFragmentListener {

    private final String SIGN_REG_TAG = "sign_registration_fragment";

    private final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginRegistrationFragment.newInstance(), SIGN_REG_TAG).commit();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onJoin() {
        Toast.makeText(this, "Joined Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}