package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.view.DeleteDialog;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.Post;
import com.example.android2project.model.PostsAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.FeedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private FeedViewModel mViewModel;

    private String mUserEmail;

    private String mCurrentUser;

    private RecyclerView mRecyclerView;
    private PostsAdapter mPostsAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Observer<List<Post>> mOnPostDownloadSucceed;
    private Observer<String> mOnPostDownloadFailed;

    private Observer<List<Post>> mOnUserPostDownloadSucceed;
    private Observer<String> mOnUserPostDownloadFailed;

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
    private Address mUserLocation;

    private final String TAG = "FeedFragment";

    public interface FeedInterface {
        void onComment(Post post);
    }

    private FeedInterface listener;

    public FeedFragment() {
    }

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
            listener = (FeedInterface) context;
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

        mCurrentUser = mViewModel.getMyEmail();

        mViewModel.refreshPosts();

        if (mUserEmail == null) {
            mLocationUtils.requestLocationPermissions();
        }

        mOnPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                onPostsDownloaded(posts.size());
            }
        };

        mOnPostDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUserPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                onPostsDownloaded(posts.size());
            }
        };

        mOnUserPostDownloadFailed = new Observer<String>() {
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

        mOnPostUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostUpdateSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                mPostsAdapter.notifyItemChanged(position);
            }
        };

        mOnPostUpdateFailed = new Observer<String>() {
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
                mUserLocation = address;
                mViewModel.updateUserLocation(address);
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


        if (mUserEmail != null) {
            addPostBtn.setVisibility(mViewModel.getMyEmail().equals(mUserEmail) ?
                    View.VISIBLE : View.GONE);
        }
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentUser.equals("a@gmail.com")) {
                    AddPostFragment.newInstance(mUserLocation, null)
                            .show(getParentFragmentManager()
                                    .beginTransaction(), "fragment_add_post");
                } else {
                    showGuestDialog();
                }
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
                    if (!mCurrentUser.equals("a@gmail.com")) {
                        final Post post = mViewModel.getPosts().get(position);
                        listener.onComment(post);
                    } else {
                        showGuestDialog();
                    }
                }
            }

            @Override
            public void onLikeBtnClicked(int position, View view, boolean isLike) {
                if (!mCurrentUser.equals("a@gmail.com")) {
                    mViewModel.updatePostLikes(isLike, position);
                } else {
                    showGuestDialog();
                }
            }

            @Override
            public void onCommentBtnClicked(int position, View view) {
                if (listener != null) {
                    if (!mCurrentUser.equals("a@gmail.com")) {
                        final Post post = mViewModel.getPosts().get(position);
                        listener.onComment(post);
                    } else {
                        showGuestDialog();
                    }
                }
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                final Post post = mViewModel.getPosts().get(position);
                AddPostFragment.newInstance(null, post)
                        .show(getParentFragmentManager()
                                .beginTransaction(), "fragment_edit_post");
            }

            @Override
            public void onDeleteOptionClicked(final int position, View view) {
                final Post postToDelete = mViewModel.getPosts().get(position);
                final DeleteDialog deleteDialog = new DeleteDialog(getContext());
                deleteDialog.setPromptText("Are You Sure You Want To Delete Your Post?");
                deleteDialog.setOnActionListener(new DeleteDialog.DeleteDialogActionListener() {
                    @Override
                    public void onYesBtnClicked() {
                        mViewModel.deletePost(postToDelete.getPostId(), position);
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onNoBtnClicked() {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();
            }

            @Override
            public void onPostImageClicked(int position, View view) {
                final String postImage = mViewModel.getPosts().get(position).getPostImageUri();
                new StfalconImageViewer.Builder<>(getContext(), Collections.singletonList(postImage),
                        new ImageLoader<String>() {
                            @Override
                            public void loadImage(ImageView imageView, String image) {
                                Glide.with(requireContext()).load(image).into(imageView);
                            }
                        }).withBackgroundColor(getResources().getColor(R.color.colorBlack, null))
                        .withTransitionFrom((ImageView) view)
                        .show();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refreshPosts();
            }
        });

        mRecyclerView.setAdapter(mPostsAdapter);

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getPostDownloadSucceed().observe(getViewLifecycleOwner(), mOnPostDownloadSucceed);
            mViewModel.getPostDownloadFailed().observe(getViewLifecycleOwner(), mOnPostDownloadFailed);
            mViewModel.getUserPostDownloadSucceed().observe(getViewLifecycleOwner(), mOnUserPostDownloadSucceed);
            mViewModel.getUserPostDownloadFailed().observe(getViewLifecycleOwner(), mOnUserPostDownloadFailed);
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
            mViewModel.getUserPostDownloadSucceed().removeObserver(mOnUserPostDownloadSucceed);
            mViewModel.getUserPostDownloadFailed().removeObserver(mOnUserPostDownloadFailed);
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

    private void onPostsDownloaded(final int postsCount) {
        mSwipeRefreshLayout.setRefreshing(false);
        mPostsAdapter.notifyDataSetChanged();

        if (mUserEmail != null) {
            int duration = postsCount * 10000;
            int length = getResources().getDisplayMetrics().widthPixels * postsCount;
            mRecyclerView.smoothScrollBy(length, 0, null, duration);
        }
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
