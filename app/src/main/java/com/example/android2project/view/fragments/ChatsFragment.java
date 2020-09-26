package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatsViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.util.List;

public class ChatsFragment extends Fragment {

    private ChatsViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private ChatsAdapter mChatsAdapter;

    private Observer<List<Conversation>> mOnDownloadGetActiveConversationsSucceed;
    private Observer<String> mOnDownloadGetActiveConversationsFailed;

    private final String TAG = "ChatsFragment";

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Chats)).get(ChatsViewModel.class);

        mViewModel.getActiveChats();

        mOnDownloadGetActiveConversationsSucceed = new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                Log.d(TAG, "onChanged: " + conversations);
                mChatsAdapter.notifyDataSetChanged();
            }
        };

        mOnDownloadGetActiveConversationsFailed = new Observer<String>() {
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
        View rootView = inflater.inflate(R.layout.chats_fragment, container, false);

        mRecyclerView = rootView.findViewById(R.id.chats_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mChatsAdapter = new ChatsAdapter(mViewModel.getConversations());

        mRecyclerView.setAdapter(mChatsAdapter);

        return rootView;
    }

    public void startObservation() {
        if (mViewModel != null) {
            mViewModel.getGetActiveConversationsSucceed().observe(this, mOnDownloadGetActiveConversationsSucceed);
            mViewModel.getGetActiveConversationsFailed().observe(this, mOnDownloadGetActiveConversationsFailed);
        }
    }
}