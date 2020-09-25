package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.ChatsAdapter;
import com.example.android2project.model.Conversation;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatsViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ChatsFragment extends Fragment {

    private ChatsViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private ChatsAdapter mChatsAdapter;

    private final String TAG = "ChatsFragment";

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Chats)).get(ChatsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chats_fragment, container, false);

        mRecyclerView = rootView.findViewById(R.id.chats_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Conversation> recyclerOptions = new FirebaseRecyclerOptions.Builder<Conversation>()
                .setQuery(mViewModel.ChatsQuery(), Conversation.class).build();

        mChatsAdapter = new ChatsAdapter(recyclerOptions);

        mRecyclerView.setAdapter(mChatsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mChatsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        mChatsAdapter.stopListening();
    }
}