package com.example.android2project.view.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android2project.R;
import com.example.android2project.model.SocialTabAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.SocialViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;

public class SocialFragment extends Fragment {

    private SocialTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SocialViewModel mViewModel;

    public static final String CHAT_FRAG="conversation_fragment";

    public static SocialFragment newInstance() {
        return new SocialFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.social_fragment, container, false);

        viewPager = rootView.findViewById(R.id.view_pager);
        tabLayout = rootView.findViewById(R.id.tab_layout);

        adapter = new SocialTabAdapter(getChildFragmentManager());

        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new ChatClanFragment(), "ChatClan");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Social)).get(SocialViewModel.class);
        // TODO: Use the ViewModel
    }

//    @Override
//    public void onChatItemClickedListener(User user) {
//        ChatFragment.newInstance(user)
//                .show(getChildFragmentManager().beginTransaction(), CHAT_FRAG);
//    }

}