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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.Comment;
import com.example.android2project.model.CommentsAdapter;
import com.example.android2project.view.DeleteDialog;
import com.example.android2project.model.Post;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.CommentsViewModel;
import com.example.android2project.model.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsFragment extends DialogFragment {
    private CommentsViewModel mViewModel;

    private CommentsAdapter mCommentsAdapter;

    private List<Comment> mComments = new ArrayList<>();

    private Observer<List<Comment>> mOnCommentsDownloadSucceed;
    private Observer<String> mOnCommentsDownloadFailed;

    private Observer<Comment> mOnCommentUploadSucceed;
    private Observer<String> mOnCommentUploadFailed;

    private Observer<String> mOnCommentUpdateSucceed;
    private Observer<String> mOnCommentUpdateFailed;

    private Observer<String> mOnCommentDeletionSucceed;
    private Observer<String> mOnCommentDeletionFailed;

    private TextView mNoCommentsTv;

    private int mPosition;

    private final static String POST = "post";

    private final String TAG = "CommentsFragment";

    public CommentsFragment() {}

    public static CommentsFragment newInstance(Post post) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Comments)).get(CommentsViewModel.class);

        if (getArguments() != null) {
            final Post post = (Post) getArguments().getSerializable(POST);
            mViewModel.setPost(post);
        }

        mViewModel.downloadComments();

        mOnCommentsDownloadSucceed = new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                if (!mComments.isEmpty()) {
                    mComments.clear();
                }
                mComments.addAll(comments);
                mNoCommentsTv.setVisibility(mComments.size() > 0 ? View.GONE : View.VISIBLE);

                mCommentsAdapter.notifyDataSetChanged();
            }
        };

        mOnCommentsDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mOnCommentUploadSucceed = new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                mComments.add(comment);
                mNoCommentsTv.setVisibility(View.GONE);
                mCommentsAdapter.notifyItemInserted(mComments.size() - 1);
            }
        };

        mOnCommentUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mOnCommentUpdateSucceed = new Observer<String>() {
            @Override
            public void onChanged(String updatedComment) {
                mComments.get(mPosition).setAuthorContent(updatedComment);
                mCommentsAdapter.notifyItemChanged(mPosition);
            }
        };

        mOnCommentUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        mOnCommentDeletionSucceed = new Observer<String>() {
            @Override
            public void onChanged(String commentId) {
                if (mComments.get(mPosition).getCommentId().equals(commentId)) {
                    mComments.remove(mPosition);

                    mNoCommentsTv.setVisibility(mComments.size() > 0 ? View.GONE : View.VISIBLE);

                    mCommentsAdapter.notifyItemRemoved(mPosition);
                }
            }
        };

        mOnCommentDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChanged: " + error);
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        final RecyclerView recyclerView = rootView.findViewById(R.id.comments_recycler_view);
        final EditText commentContentEt = rootView.findViewById(R.id.comment_content_et);
        final ImageButton sendCommentBtn = rootView.findViewById(R.id.send_comment_ib);
        mNoCommentsTv = rootView.findViewById(R.id.no_comments_tv);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mCommentsAdapter = new CommentsAdapter(mComments, getContext());

        mCommentsAdapter.setCommentListener(new CommentsAdapter.CommentListener() {
            @Override
            public void onAuthorImageClicked(int position, View view) {
                final String userEmail = mComments.get(position).getAuthorEmail();
                UserProfileFragment.newInstance(userEmail)
                        .show(getChildFragmentManager(), "profile_fragment");
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                mPosition = position;
                showCommentEditingDialog(mComments.get(position));
            }

            @Override
            public void onDeleteOptionClicked(final int position, View view) {
                mPosition = position;
                final DeleteDialog deleteDialog = new DeleteDialog(getContext());
                deleteDialog.setPromptText("Are You Sure You Want To Delete Your Comment?");
                deleteDialog.setOnActionListener(new DeleteDialog.DeleteDialogActionListener() {
                    @Override
                    public void onYesBtnClicked() {
                        mViewModel.deleteComment(mComments.get(position).getCommentId());
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onNoBtnClicked() {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();
            }
        });

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom > bottom) {
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.smoothScrollToPosition(mCommentsAdapter.getItemCount());
                }

                linearLayoutManager.setStackFromEnd(false);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });

        commentContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendCommentBtn.setVisibility(View.VISIBLE);
                } else {
                    sendCommentBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = commentContentEt.getText().toString();
                mViewModel.uploadComment(commentContent);
                commentContentEt.setText("");
            }
        });

        recyclerView.setAdapter(mCommentsAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireDialog().getWindow()
                .getAttributes().windowAnimations = R.style.ConversationDialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getCommentsDownloadSucceed().observe(this, mOnCommentsDownloadSucceed);
            mViewModel.getCommentsDownloadFailed().observe(this, mOnCommentsDownloadFailed);
            mViewModel.getCommentUploadSucceed().observe(this, mOnCommentUploadSucceed);
            mViewModel.getCommentUploadFailed().observe(this, mOnCommentUploadFailed);
            mViewModel.getCommentUpdateSucceed().observe(this, mOnCommentUpdateSucceed);
            mViewModel.getCommentUpdatedFailed().observe(this, mOnCommentUpdateFailed);
            mViewModel.getCommentDeletionSucceed().observe(this, mOnCommentDeletionSucceed);
            mViewModel.getCommentDeletionFailed().observe(this, mOnCommentDeletionFailed);
        }
    }

    private void showCommentEditingDialog(final Comment commentToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.edit_comment_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText commentContentEt = view.findViewById(R.id.new_post_content_et);
        final Button updateBtn = view.findViewById(R.id.post_btn);
        final Button cancelBtn = view.findViewById(R.id.cancel_btn);

        commentContentEt.setText(commentToEdit.getAuthorContent());
        updateBtn.setEnabled(false);

        final AlertDialog alertDialog = builder.create();

        commentContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentId = commentToEdit.getCommentId();
                final String commentContent = commentContentEt.getText().toString();
                mViewModel.editComment(commentId, commentContent);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
