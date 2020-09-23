package com.example.android2project.view.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android2project.R;
import com.example.android2project.model.ChatFriendsAdapter;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatFriendsViewModel;
import com.example.android2project.viewmodel.FeedViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.util.ArrayList;

public class ChatFriendsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private ChatFriendsAdapter mAdapter;
    private ChatFriendsViewModel mViewModel;
    private Observer<ArrayList<User>> usersObserver;

    public static ChatFriendsFragment newInstance() {
        return new ChatFriendsFragment();
    }

//    public interface FriendItemListener{
//        void onChatItemClickedListener(User user);
//    }
//
//    private FriendItemListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//
//        try {
//            listener = (FriendItemListener) context;
//        } catch (ClassCastException ex) {
//            throw new ClassCastException("The activity must implement FriendItem Listener!");
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.ChatFriends)).get(ChatFriendsViewModel.class);
        mViewModel.getAllUsers();

        usersObserver = new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                mAdapter = new ChatFriendsAdapter(getContext(),users);
                mRecyclerview.setAdapter(mAdapter);
                mAdapter.setFriendItemListener(new ChatFriendsAdapter.FriendItemListener() {
                    @Override
                    public void onClicked(int position, View view) {
//                listener.onChatItemClickedListener(mViewModel.getFriends().get(position));
                        ChatFragment.newInstance(mViewModel.getFriends().get(position))
                                .show(getParentFragmentManager().beginTransaction(),"chat_fragment");
                    }
                });
            }
        };

        mViewModel.getFriendsMutableLiveData().observe(this,usersObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_friends_fragment, container, false);
        mRecyclerview = rootView.findViewById(R.id.friends_recycler_view);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onStop() {
        mViewModel.getFriendsMutableLiveData().removeObservers(this);
        super.onStop();
    }


}