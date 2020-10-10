package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;

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
import com.example.android2project.model.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;

import java.util.List;

public class ChatClanFragment extends Fragment {
    private ChatClanViewModel mViewModel;
    private RecyclerView mRecyclerview;
    private ChatClanAdapter mAdapter;
    private String mCurrentUser;

    private final String TAG = "ChatClanFragment";


    private Observer<List<User>> usersObserver;

    public ChatClanFragment() {
    }

    public static ChatClanFragment newInstance() {
        return new ChatClanFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.ChatClan)).get(ChatClanViewModel.class);

        mCurrentUser = mViewModel.getUserEmail();

        usersObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter = new ChatClanAdapter(getContext(), users);
                mRecyclerview.setAdapter(mAdapter);

                mAdapter.setFriendItemListener(new ChatClanAdapter.FriendItemListener() {
                    @Override
                    public void onClicked(User user) {
                        if(!mCurrentUser.equals("a@gmail.com")) {
                            ConversationFragment.newInstance(user)
                                    .show(getParentFragmentManager()
                                            .beginTransaction(), "fragment_conversation");
                        } else {
                            showGuestDialog();
                        }
                    }
                });
                mRecyclerview.setAdapter(mAdapter);
            }
        };

        mViewModel.getUsersLiveData().observe(this, usersObserver);
        mViewModel.getUsersLiveData().observe(this, usersObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_clan, container, false);
        mRecyclerview = rootView.findViewById(R.id.chatclan_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        final SearchView searchView = rootView.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return rootView;
    }

    private void showGuestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.guest_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog guestDialog = builder.create();

        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestDialog.dismiss();
            }
        });
        Button joinBtn = view.findViewById(R.id.join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOutFromGuest();
            }
        });
        guestDialog.show();
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onStop() {
        mViewModel.getUsersLiveData().removeObservers(this);
        super.onStop();
    }
}
