package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.ChatClanAdapter;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatClanViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.util.ArrayList;

public class ChatClanFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private ChatClanAdapter mAdapter;
    private ChatClanViewModel mViewModel;
    private Observer<ArrayList<User>> usersObserver;

    public static ChatClanFragment newInstance() {
        return new ChatClanFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.ChatClan)).get(ChatClanViewModel.class);
        mViewModel.getAllUsers();

        usersObserver = new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                mAdapter = new ChatClanAdapter(getContext(),users);
                mRecyclerview.setAdapter(mAdapter);
                mAdapter.setFriendItemListener(new ChatClanAdapter.FriendItemListener() {
                    @Override
                    public void onClicked(int position, View view) {
                        User recipient = mViewModel.getFriends().get(position);
                        ConversationFragment.newInstance(recipient)
                                .show(getParentFragmentManager().beginTransaction(),"conversation_fragment");
                    }
                });
            }
        };

        mViewModel.getFriendsMutableLiveData().observe(this,usersObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_clan_fragment, container, false);
        mRecyclerview = rootView.findViewById(R.id.chatclan_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onStop() {
        mViewModel.getFriendsMutableLiveData().removeObservers(this);
        super.onStop();
    }
}