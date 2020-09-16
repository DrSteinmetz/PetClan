package com.example.android2project.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.Post;
import com.example.android2project.model.PostsAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PostsAdapter mPostsAdapter;
    private List<Post> mPosts = new ArrayList<>();

    public FeedFragment() {}

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPosts.add(new Post("John Doe", "", getString(R.string.large_text), "3 minutes ago"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = rootView.findViewById(R.id.feed_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPostsAdapter = new PostsAdapter(mPosts, getContext());

        mPostsAdapter.setPostListener(new PostsAdapter.PostListener() {
            @Override
            public void onAuthorImageClicked(int position, View view) {
            }

            @Override
            public void onLikeBtnClicked(int position, View view) {
            }

            @Override
            public void onCommentBtnClicked(int position, View view) {
            }
        });

        mRecyclerView.setAdapter(mPostsAdapter);

        return rootView;
    }
}