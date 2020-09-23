package com.example.android2project.view.fragments;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.example.android2project.model.MessageListAdapter;
import com.example.android2project.model.Post;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

import java.util.Objects;

public class ChatFragment extends DialogFragment {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private EditText mChatBox;
    private ImageButton mSendBtn;
    private TextView mUsername;
    private ImageView mUserPicture;

    private User mUserTalkTo;

    private ChatViewModel mViewModel;

    public static final String USER_TALK_TO = "chat";

    public static ChatFragment newInstance(User user) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_TALK_TO, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Chat)).get(ChatViewModel.class);


        mMessageRecycler = rootView.findViewById(R.id.reyclerview_message_list);
        mChatBox = rootView.findViewById(R.id.chatbox_et);
        mSendBtn = rootView.findViewById(R.id.send_btn);
        mUsername = rootView.findViewById(R.id.tool_bar_username);
        mUserPicture = rootView.findViewById(R.id.tool_bar_picture);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (getArguments() != null) {
            mUserTalkTo = (User) getArguments().getSerializable(USER_TALK_TO);
            if (mUserTalkTo != null) {
                mUsername.setText(mUserTalkTo.getFirstName() + " " + mUserTalkTo.getLastName());
                Glide.with(getContext()).load(mUserTalkTo.getPhotoUri()).into(mUserPicture);

            }
        }


        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChatBox.getText().length() > 0) {
                    //TODO: viewModel sent message, upload to firestore
                }
            }
        });


        mChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSendBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}