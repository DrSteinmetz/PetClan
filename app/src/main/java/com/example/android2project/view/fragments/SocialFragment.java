package com.example.android2project.view.fragments;

import androidx.lifecycle.Observer;
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
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatClanViewModel;
import com.example.android2project.viewmodel.ChatsViewModel;
import com.example.android2project.viewmodel.SocialViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SocialFragment extends Fragment {

    private SocialTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SocialViewModel mViewModel;

    private Observer<List<User>> mUserListObserver;

    public static final String CHAT_FRAG="conversation_fragment";

    public static SocialFragment newInstance() {
        return new SocialFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Social)).get(SocialViewModel.class);

        final ChatClanViewModel chatClanViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.ChatClan)).get(ChatClanViewModel.class);


        final ChatsViewModel chatsViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Chats)).get(ChatsViewModel.class);

        mUserListObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                chatClanViewModel.setUsers(users);

//                chatsViewModel.setActiveUsers(users);
            }
        };

        mViewModel.downloadAllUsers();

        mViewModel.getUserList().observe(this, mUserListObserver);
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

        // TODO: Use the ViewModel
    }

//    @Override
//    public void onChatItemClickedListener(User user) {
//        ChatFragment.newInstance(user)
//                .show(getChildFragmentManager().beginTransaction(), CHAT_FRAG);
//    }

}