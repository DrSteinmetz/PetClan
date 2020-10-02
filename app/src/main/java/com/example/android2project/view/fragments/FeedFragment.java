package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
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
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.Post;
import com.example.android2project.model.PostsAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.FeedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FeedFragment extends Fragment {
    private FeedViewModel mViewModel;

    private String mUserEmail;

    private RecyclerView mRecyclerView;
    private PostsAdapter mPostsAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Observer<List<Post>> mOnPostDownloadSucceed;
    private Observer<String> mOnPostDownloadFailed;

    private Observer<Post> mOnPostUploadSucceed;
    private Observer<String> mOnPostUploadFailed;

    private Observer<Integer> mOnPostUpdateSucceed;
    private Observer<String> mOnPostUpdateFailed;

    private Observer<Integer> mOnPostLikesUpdateSucceed;
    private Observer<String> mOnPostLikesUpdateFailed;

    private Observer<Integer> mOnPostDeletionSucceed;
    private Observer<String> mOnPostDeletionFailed;

    private Observer<Address> mOnLocationChanged;

    private LocationUtils mLocationUtils;
    private String mUserLocation = "Unknown";

    private final String TAG = "FeedFragment";

    public interface FeedListener {
        void onComment(Post post);
    }

    private FeedListener listener;

    public FeedFragment() {}

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

        mLocationUtils = LocationUtils.getInstance(requireActivity());

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Feed)).get(FeedViewModel.class);
        mViewModel.setUserEmail(mUserEmail);
        mViewModel.refreshPosts();

        mLocationUtils.requestLocationPermissions();

        mOnPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                Log.d(TAG, "onChanged swipe: " + this.toString());
                mSwipeRefreshLayout.setRefreshing(false);
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
                mPostsAdapter.notifyItemInserted(0);
                mRecyclerView.smoothScrollToPosition(0);
            }
        };

        mOnPostUpdateSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                Log.d(TAG, "onChanged: " + position);
                mPostsAdapter.notifyItemChanged(position);
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

        mOnPostLikesUpdateSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                mPostsAdapter.notifyItemChanged(position);
            }
        };

        mOnPostLikesUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostDeletionSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                mPostsAdapter.notifyItemRemoved(position);
            }
        };

        mOnPostDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnLocationChanged = new Observer<Address>() {
            @Override
            public void onChanged(Address address) {
                mUserLocation = address.getLocality();
                Log.d(TAG, "onChanged: address: " + address.getLocality());
            }
        };
        mLocationUtils.getLocationLiveData().observe(this, mOnLocationChanged);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        final FloatingActionButton addPostBtn = rootView.findViewById(R.id.add_post_btn);
        mRecyclerView = rootView.findViewById(R.id.feed_recycler_view);
        mSwipeRefreshLayout = rootView.findViewById(R.id.feed_refresher);


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

        mPostsAdapter = new PostsAdapter(mViewModel.getPosts(), getContext(), mUserEmail);

        mPostsAdapter.setPostListener(new PostsAdapter.PostListener() {
            @Override
            public void onAuthorImageClicked(int position, View view) {
                final String userEmail = mViewModel.getPosts().get(position).getAuthorEmail();
                UserProfileFragment.newInstance(userEmail)
                        .show(getChildFragmentManager(), "profile_fragment");
            }

            @Override
            public void onCommentsTvClicked(int position, View view) {
                if (listener != null) {
                    final Post post = mViewModel.getPosts().get(position);
                    listener.onComment(post);
                }
            }

            @Override
            public void onLikeBtnClicked(int position, View view, boolean isLike) {
                mViewModel.updatePostLikes(isLike, position);
            }

            @Override
            public void onCommentBtnClicked(int position, View view) {
                if (listener != null) {
                    final Post post = mViewModel.getPosts().get(position);
                    listener.onComment(post);
                }
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                final Post post = mViewModel.getPosts().get(position);
                showPostEditingDialog(post, position);
            }

            @Override
            public void onDeleteOptionClicked(int position, View view) {
                final Post post = mViewModel.getPosts().get(position);
                showDeletePostDialog(post, position);
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
            mViewModel.getPostDownloadSucceed().observe(getViewLifecycleOwner(), mOnPostDownloadSucceed);
            mViewModel.getPostDownloadFailed().observe(getViewLifecycleOwner(), mOnPostDownloadFailed);
            mViewModel.getPostUploadSucceed().observe(getViewLifecycleOwner(), mOnPostUploadSucceed);
            mViewModel.getPostUploadFailed().observe(getViewLifecycleOwner(), mOnPostUploadFailed);
            mViewModel.getPostUpdateSucceed().observe(getViewLifecycleOwner(), mOnPostUpdateSucceed);
            mViewModel.getPostUpdatedFailed().observe(getViewLifecycleOwner(), mOnPostUpdateFailed);
            mViewModel.getPostLikesUpdateSucceed().observe(getViewLifecycleOwner(), mOnPostLikesUpdateSucceed);
            mViewModel.getPostLikesUpdateFailed().observe(getViewLifecycleOwner(), mOnPostLikesUpdateFailed);
            mViewModel.getPostDeletionSucceed().observe(getViewLifecycleOwner(), mOnPostDeletionSucceed);
            mViewModel.getPostDeletionFailed().observe(getViewLifecycleOwner(), mOnPostDeletionFailed);
        }
    }

    private void stopObservation() {
        if (mViewModel != null) {
            mViewModel.getPostDownloadSucceed().removeObserver(mOnPostDownloadSucceed);
            mViewModel.getPostDownloadFailed().removeObserver(mOnPostDownloadFailed);
            mViewModel.getPostUploadSucceed().removeObserver(mOnPostUploadSucceed);
            mViewModel.getPostUploadFailed().removeObserver(mOnPostUploadFailed);
            mViewModel.getPostUpdateSucceed().removeObserver(mOnPostUpdateSucceed);
            mViewModel.getPostUpdatedFailed().removeObserver(mOnPostUpdateFailed);
            mViewModel.getPostLikesUpdateSucceed().removeObserver(mOnPostLikesUpdateSucceed);
            mViewModel.getPostLikesUpdateFailed().removeObserver(mOnPostLikesUpdateFailed);
            mViewModel.getPostDeletionSucceed().removeObserver(mOnPostDeletionSucceed);
            mViewModel.getPostDeletionFailed().removeObserver(mOnPostDeletionFailed);
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


    private void showPostEditingDialog(final Post postToEdit, final int position) {
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
                mViewModel.updatePost(postToEdit, position);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showDeletePostDialog(final Post postToDelete, final int position) {
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
                mViewModel.deletePost(postToDelete.getPostId(), position);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        startObservation();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopObservation();
    }
}
