package com.example.android2project.view.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.MessageListAdapter;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ConversationViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;
import java.util.Objects;

public class ConversationFragment extends DialogFragment {
    private ConversationViewModel mViewModel;

    private FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> mFirebaseAdapter;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private Observer<List<ChatMessage>> mOnDownloadConversationSucceed;
    private Observer<String> mOnDownloadConversationFailed;

    private Observer<ChatMessage> mOnUploadMessageSucceed;
    private Observer<String> mOnUploadMessageFailed;

    private EditText mChatBox;
    private ImageButton mSendBtn;
    private TextView mRecipientName;
    private ImageView mRecipientPicture;

    private User mUserRecipient;

    private static final String RECIPIENT = "recipient";

    private final String TAG = "ConversationFragment";

    public static ConversationFragment newInstance(User user) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putSerializable(RECIPIENT, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserRecipient = (User) getArguments().getSerializable(RECIPIENT);
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Conversation)).get(ConversationViewModel.class);

        mViewModel.setRecipientEmail(mUserRecipient.getEmail());

        mOnDownloadConversationSucceed = new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> chatMessages) {
                mMessageAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChanged: mOnDownloadConversationSucceed");
                if (chatMessages.size() > 0) {
                    mMessageRecycler.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }
        };

        mOnDownloadConversationFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnUploadMessageSucceed = new Observer<ChatMessage>() {
            @Override
            public void onChanged(ChatMessage message) {
                //mMessageAdapter.notifyDataSetChanged();
                mMessageAdapter.notifyItemInserted(mViewModel.getConversation().size() - 1);
                if (mViewModel.getConversation().size() > 0) {
                    mMessageRecycler.smoothScrollToPosition(mViewModel.getConversation().size() - 1);
                }
            }
        };

        mOnUploadMessageFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mViewModel.downloadConversation();

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conversation_fragment, container, false);

        mMessageRecycler = rootView.findViewById(R.id.recycler_view_message_list);
        mChatBox = rootView.findViewById(R.id.chatbox_et);
        mSendBtn = rootView.findViewById(R.id.send_btn);
        mRecipientName = rootView.findViewById(R.id.tool_bar_username);
        mRecipientPicture = rootView.findViewById(R.id.tool_bar_picture);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);

        mMessageAdapter = new MessageListAdapter(getContext(), mViewModel.getConversation());

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (mUserRecipient != null) {
            final String recipientName = mUserRecipient.getFirstName() +
                    " " + mUserRecipient.getLastName();
            mRecipientName.setText(recipientName);

            RequestOptions options = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_user_pic)
                    .error(R.drawable.ic_default_user_pic);

            Glide.with(requireContext())
                    .load(mUserRecipient.getPhotoUri())
                    .apply(options)
                    .into(mRecipientPicture);
        }

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChatBox.getText().length() > 0) {
                    final String messageContent = mChatBox.getText().toString();
                    mViewModel.uploadChatMessage(mUserRecipient, messageContent);

                    mChatBox.setText("");
                }
            }
        });

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom > bottom) {
                    mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                    linearLayoutManager.setStackFromEnd(true);
                    mMessageRecycler.setLayoutManager(linearLayoutManager);
                }

                linearLayoutManager.setStackFromEnd(false);
                mMessageRecycler.setLayoutManager(linearLayoutManager);
            }
        });

        mChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSendBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mMessageRecycler.setAdapter(mMessageAdapter);

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getDownloadConversationSucceed().observe(this, mOnDownloadConversationSucceed);
            mViewModel.getDownloadConversationFailed().observe(this, mOnDownloadConversationFailed);
            mViewModel.getUploadMessageSucceed().observe(this, mOnUploadMessageSucceed);
            mViewModel.getUploadMessageFailed().observe(this, mOnUploadMessageFailed);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            /*WindowManager.LayoutParams params = window.getAttributes();
            params.y = 300;
            window.setAttributes(params);*/
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}