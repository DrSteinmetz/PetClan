package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android2project.R;
import com.example.android2project.model.Post;
import com.example.android2project.model.PostsAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.view.MainActivity;
import com.example.android2project.viewmodel.FeedViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private FeedViewModel mViewModel;

    private String mUserEmail;

    RecyclerView mRecyclerView;
    private PostsAdapter mPostsAdapter;
    private List<Post> mPosts = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Observer<List<Post>> mOnPostDownloadSucceed;
    private Observer<String> mOnPostDownloadFailed;

    private Observer<Post> mOnPostUploadSucceed;
    private Observer<String> mOnPostUploadFailed;

    private Observer<Post> mOnPostUpdateSucceed;
    private Observer<String> mOnPostUpdateFailed;

    private Observer<Post> mOnPostLikesUpdateSucceed;
    private Observer<String> mOnPostLikesUpdateFailed;

    private Observer<String> mOnPostDeletionSucceed;
    private Observer<String> mOnPostDeletionFailed;

    private int mPosition;

    private String mUserLocation = "Unknown";

    private final String TAG = "FeedFragment";

    public interface FeedListener {
        void onComment(Post post);
    }

    private FeedListener listener;

    public static FeedFragment newInstance(final String userEmail) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString("posts", userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (FeedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement Feed Listener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserEmail = getArguments().getString("posts");
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Feed)).get(FeedViewModel.class);
        mViewModel.setUserEmail(mUserEmail);
        mViewModel.refreshPosts();

        mOnPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (!mPosts.isEmpty()) {
                    mPosts.clear();
                }
                Log.d(TAG, "onChanged: Swipe" + posts);
                mPosts.addAll(posts);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                mPostsAdapter.notifyDataSetChanged();
            }
        };

        mOnPostDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostUploadSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                mPosts.add(0, post);
                mPostsAdapter.notifyItemInserted(0);
            }
        };

        mOnPostUpdateSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post updatedPost) {
                Log.d(TAG, "onChanged: " + mPosition);
                mPosts.get(mPosition).setAuthorContent(updatedPost.getAuthorContent());
                mPosts.get(mPosition).setCommentsCount(updatedPost.getCommentsCount());
                mPostsAdapter.notifyItemChanged(mPosition);
            }
        };

        mOnPostUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostLikesUpdateSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                mPostsAdapter.notifyItemChanged(mPosition);
            }
        };

        mOnPostLikesUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostDeletionSucceed = new Observer<String>() {
            @Override
            public void onChanged(String postId) {
                if (mPosts.get(mPosition).getPostId().equals(postId)) {
                    mPosts.remove(mPosition);
                    mPostsAdapter.notifyItemRemoved(mPosition);
                }
            }
        };

        mOnPostDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        final FloatingActionButton addPostBtn = rootView.findViewById(R.id.add_post_btn);
        mRecyclerView = rootView.findViewById(R.id.feed_recycler_view);
        mSwipeRefreshLayout = rootView.findViewById(R.id.feed_refresher);

        ((MainActivity) requireActivity()).setLocationListener(new MainActivity.LocationInterface() {
            @Override
            public void onLocationChanged(String cityLocation) {
                mUserLocation = cityLocation;
            }
        });


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostAddingDialog();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                mUserEmail != null ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL,
                false));

        mPostsAdapter = new PostsAdapter(mPosts, getContext(), mUserEmail);

        mPostsAdapter.setPostListener(new PostsAdapter.PostListener() {
            @Override
            public void onAuthorImageClicked(int position, View view) {
                final String userEmail = mPosts.get(position).getAuthorEmail();
                UserProfileFragment.newInstance(userEmail)
                        .show(getChildFragmentManager(), "profile_fragment");
            }

            @Override
            public void onCommentsTvClicked(int position, View view) {
                mPosition = position;
                if (listener != null) {
                    listener.onComment(mPosts.get(position));
                }
            }

            @Override
            public void onLikeBtnClicked(int position, View view, boolean isLike) {
                mPosition = position;
                Post post = mPosts.get(position);
                mViewModel.updatePostLikes(post, isLike);
            }

            @Override
            public void onCommentBtnClicked(int position, View view) {
                mPosition = position;
                if (listener != null) {
                    listener.onComment(mPosts.get(position));
                }
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                mPosition = position;
                showPostEditingDialog(mPosts.get(position));
            }

            @Override
            public void onDeleteOptionClicked(int position, View view) {
                mPosition = position;
                showDeletePostDialog(mPosts.get(position));
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refreshPosts();
            }
        });

        mRecyclerView.setAdapter(mPostsAdapter);

        if (mUserEmail != null) {
            mRecyclerView.smoothScrollBy(2000, 0, null, 30000);
            // TODO: Improve this
        }

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getPostDownloadSucceed().observe(this, mOnPostDownloadSucceed);
            mViewModel.getPostDownloadFailed().observe(this, mOnPostDownloadFailed);
            mViewModel.getPostUploadSucceed().observe(this, mOnPostUploadSucceed);
            mViewModel.getPostUploadFailed().observe(this, mOnPostUploadFailed);
            mViewModel.getPostUpdateSucceed().observe(this, mOnPostUpdateSucceed);
            mViewModel.getPostUpdatedFailed().observe(this, mOnPostUpdateFailed);
            mViewModel.getPostLikesUpdateSucceed().observe(this, mOnPostLikesUpdateSucceed);
            mViewModel.getPostLikesUpdateFailed().observe(this, mOnPostLikesUpdateFailed);
            mViewModel.getPostDeletionSucceed().observe(this, mOnPostDeletionSucceed);
            mViewModel.getPostDeletionFailed().observe(this, mOnPostDeletionFailed);
        }
    }

    private void showPostAddingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_post_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText postContentEt = view.findViewById(R.id.new_post_content_et);
        final Button postBtn = view.findViewById(R.id.post_btn);
        postBtn.setText("Post");
        postBtn.setEnabled(false);

        final AlertDialog alertDialog = builder.create();

        postContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.uploadNewPost(postContentEt.getText().toString());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void showPostEditingDialog(final Post postToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_post_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText postContentEt = view.findViewById(R.id.new_post_content_et);
        postContentEt.setText(postToEdit.getAuthorContent());
        final Button updateBtn = view.findViewById(R.id.post_btn);
        updateBtn.setText("Update");
        updateBtn.setEnabled(false);

        final AlertDialog alertDialog = builder.create();

        postContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postToEdit.setAuthorContent(postContentEt.getText().toString());
                mViewModel.updatePost(postToEdit);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showDeletePostDialog(final Post postToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        ViewGroup root;
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_post_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText postContentEt = view.findViewById(R.id.new_post_content_et);
        postContentEt.setText(postToDelete.getAuthorContent());
        final Button updateBtn = view.findViewById(R.id.post_btn);

        final AlertDialog alertDialog = builder.create();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deletePost(postToDelete.getPostId());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
