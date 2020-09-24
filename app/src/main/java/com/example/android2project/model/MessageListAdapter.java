package com.example.android2project.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<ChatMessage> mMessageList;
    private FirebaseUser mCurrentUser;

    public MessageListAdapter(Context context, List<ChatMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
        this.mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);

            return new SentMessageHolder(view);
        } else if (viewType == TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);

            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);

        if (message.getRecipientEmail().equals(mCurrentUser.getEmail())) {
            // If the current user is the sender of the message
            return TYPE_MESSAGE_RECEIVED;
        } else {
            // If some other user sent the message
            return TYPE_MESSAGE_SENT;
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_body_tv);
            timeText = itemView.findViewById(R.id.message_time_tv);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(DateToFormatDate(message.getTime()));
        }
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_body_tv);
            timeText = itemView.findViewById(R.id.message_time_tv);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(DateToFormatDate(message.getTime()));
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String DateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | dd/MM");
        return simpleDateFormat.format(date).toString();
    }
}