package com.example.android2project.model;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MessageListAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageListAdapter.ChatViewHolder> {

    private String mUserEmail;

    private final int TYPE_MESSAGE_SENT = 1;
    private final int TYPE_MESSAGE_RECEIVED = 2;

    public MessageListAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options, String userEmail) {
        super(options);
        this.mUserEmail = userEmail;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatMessage model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_MESSAGE_SENT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (Objects.equals(mUserEmail, message.getRecipientEmail()))
            return TYPE_MESSAGE_RECEIVED;
        else
            return TYPE_MESSAGE_SENT;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent, mTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mContent = itemView.findViewById(R.id.message_body_tv);
            this.mTime = itemView.findViewById(R.id.message_time_tv);
        }

        public void bind(ChatMessage message) {
            this.mContent.setText(message.getContent());
            this.mTime.setText(DateToFormatDate(message.getTime()));
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String DateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | dd/MM");
        return simpleDateFormat.format(date);
    }

}
