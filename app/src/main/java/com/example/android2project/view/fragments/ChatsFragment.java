package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.ChatsAdapter;
import com.example.android2project.model.Conversation;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.viewmodel.ChatsViewModel;
import com.example.android2project.model.ViewModelFactory;

import java.util.List;

public class ChatsFragment extends Fragment {

    private ChatsViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private ChatsAdapter mChatsAdapter;

    private Observer<List<User>> usersObserver;

    private Observer<List<Conversation>> mOnDownloadActiveConversationsSucceed;
    private Observer<String> mOnDownloadActiveConversationsFailed;

    private TextView mNoChatsTv;

    private final String CONVERSATION_FRAG = "fragment_conversation";

    private final String TAG = "ChatsFragment";

    public ChatsFragment() {}

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Chats)).get(ChatsViewModel.class);

        mViewModel.getActiveChats();

        usersObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mChatsAdapter.notifyDataSetChanged();
            }
        };

        mOnDownloadActiveConversationsSucceed = new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                if (mChatsAdapter != null) {
                    mChatsAdapter.setUserMap(mViewModel.getActiveUsers());
                    mChatsAdapter.notifyDataSetChanged();
                }

                mNoChatsTv.setVisibility(conversations.size() > 0 ? View.GONE : View.VISIBLE);
            }
        };

        mOnDownloadActiveConversationsFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        mNoChatsTv = rootView.findViewById(R.id.no_chats_tv);
        mRecyclerView = rootView.findViewById(R.id.chats_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mChatsAdapter = new ChatsAdapter(getContext(), mViewModel.getConversations(),
                mViewModel.getActiveUsers());

        mChatsAdapter.setChatAdapterListener(new ChatsAdapter.ChatAdapterInterface() {
            @Override
            public void onClicked(int position, View view) {
                User recipient = mViewModel.getActiveUsers().get(position);
                ConversationFragment.newInstance(recipient)
                        .show(getParentFragmentManager()
                                .beginTransaction(), CONVERSATION_FRAG);
            }
        });

        mRecyclerView.setAdapter(mChatsAdapter);

        return rootView;
    }

    public void startObservation() {
        if (mViewModel != null) {
            mViewModel.getUsersLiveData().observe(this, usersObserver);
            mViewModel.getDownloadActiveConversationsSucceed().observe(this, mOnDownloadActiveConversationsSucceed);
            mViewModel.getDownloadActiveConversationsFailed().observe(this, mOnDownloadActiveConversationsFailed);
        }
    }
}
