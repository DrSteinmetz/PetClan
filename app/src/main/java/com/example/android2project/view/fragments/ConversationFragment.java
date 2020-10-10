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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import com.example.android2project.model.ViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class ConversationFragment extends DialogFragment {
    private ConversationViewModel mViewModel;

    private MessageListAdapter mMessageAdapter;
    private RecyclerView mMessageRecycler;

    private Observer<String> mOnUploadMessageFailed;

    private EditText mChatBox;
    private ImageButton mSendBtn;
    private ExtendedFloatingActionButton mScrollDownBtn;
    private TextView mRecipientName;
    private ImageView mRecipientPicture;

    private LinearLayoutManager mLinearLayoutManager;

    private User mUserRecipient;

    public static String sConversationId = null;

    private boolean isShown = false;

    private static final String RECIPIENT = "recipient";

    private final String TAG = "ConversationFragment";

    public ConversationFragment() {
    }

    public static ConversationFragment newInstance(User recipient) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putSerializable(RECIPIENT, recipient);
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

        mOnUploadMessageFailed = new Observer<String>() {
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
        View rootView = inflater.inflate(R.layout.fragment_conversation, container, false);

        mMessageRecycler = rootView.findViewById(R.id.recycler_view_message_list);
        mChatBox = rootView.findViewById(R.id.chatbox_et);
        mSendBtn = rootView.findViewById(R.id.send_btn);
        mRecipientName = rootView.findViewById(R.id.tool_bar_username);
        mRecipientPicture = rootView.findViewById(R.id.tool_bar_picture);
        mScrollDownBtn = rootView.findViewById(R.id.scroll_down_btn);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(mLinearLayoutManager);

        final String chatId = mViewModel.generateChatId();
        Log.d(TAG, "onCreateView: matan? Conversation");
        FirebaseRecyclerOptions<ChatMessage> recyclerOptions = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(mViewModel.ConversationQuery(chatId), ChatMessage.class).build();

        mMessageAdapter = new MessageListAdapter(recyclerOptions, mViewModel.getUserEmail());

        final int[] newMessagesCount = {0};

        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messagesCount = mMessageAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messagesCount - 1)) &&
                                lastVisiblePosition == (positionStart - 1)) {
                    mMessageRecycler.scrollToPosition(positionStart);
                } else if (lastVisiblePosition < (messagesCount)) {
                    newMessagesCount[0]++;
                    mScrollDownBtn.show();
                    final String unreadMessagesCount = newMessagesCount[0] + " Unread Messages";
                    mScrollDownBtn.setText(unreadMessagesCount);
                    mScrollDownBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMessageRecycler.smoothScrollToPosition(positionStart);
                            mScrollDownBtn.hide();
                            newMessagesCount[0] = 0;
                        }
                    });
                }
            }
        });

        mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && mScrollDownBtn.isShown()) {
                    mScrollDownBtn.hide();
                    newMessagesCount[0] = 0;
                }
            }
        });


        if (getDialog() != null) {
            Window window = Objects.requireNonNull(getDialog()).getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
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
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom > bottom) {
                    mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                    mLinearLayoutManager.setStackFromEnd(true);
                    mMessageRecycler.setLayoutManager(mLinearLayoutManager);
                }

                mLinearLayoutManager.setStackFromEnd(false);
                mMessageRecycler.setLayoutManager(mLinearLayoutManager);
            }
        });


        mChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Animation animShow = new ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animShow.setDuration(200);
                animShow.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mSendBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                Animation animBegone = new ScaleAnimation(1f, 0f, 1f, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animBegone.setDuration(200);
                animBegone.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mSendBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                if (s.length() > 0 && !isShown) {
                    mSendBtn.startAnimation(animShow);
                    isShown = true;
                } else if (s.length() < 1) {
                    mSendBtn.startAnimation(animBegone);
                    isShown = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMessageRecycler.setAdapter(mMessageAdapter);

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getUploadMessageFailed().observe(this, mOnUploadMessageFailed);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.ConversationDialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        mMessageAdapter.startListening();
        if (getDialog() != null) {
            Window window = Objects.requireNonNull(getDialog()).getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }

        sConversationId = mViewModel.generateChatId();
    }

    @Override
    public void onStop() {
        super.onStop();

        mMessageAdapter.stopListening();
        sConversationId = null;
    }
}
