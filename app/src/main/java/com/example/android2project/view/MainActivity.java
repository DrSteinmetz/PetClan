package com.example.android2project.view;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android2project.R;
import com.example.android2project.model.MenuAdapter;
import com.example.android2project.repository.AuthRepository;

import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity {
    private DuoDrawerLayout mDrawerLayout;

    ArrayList<String> mMenuOptions = new ArrayList<>();

    private AuthRepository mAuthRepository;

    private RelativeLayout mHeader;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mHeader = (RelativeLayout) getResources().getLayout(R.layout.drawer_header);

        mAuthRepository = AuthRepository.getInstance(this);

        TextView userNameTv = findViewById(R.id.user_name_tv);
        userNameTv.setText(mAuthRepository.getUserName());

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
    }
}