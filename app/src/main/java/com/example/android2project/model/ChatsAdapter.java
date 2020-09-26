package com.example.android2project.model;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private List<Conversation> mConversations;

    public ChatsAdapter(List<Conversation> conversations) {
        this.mConversations = conversations;
    }

    class ChatsViewHolder extends RecyclerView.ViewHolder {
        private ImageView friendImageIv;
        private TextView friendNameTv;
        private TextView lastMessageTv;
        private TextView timeTv;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.friendImageIv = itemView.findViewById(R.id.friend_pic_iv);
            this.friendNameTv = itemView.findViewById(R.id.friend_name_tv);
            this.lastMessageTv = itemView.findViewById(R.id.message_body_tv);
            this.timeTv = itemView.findViewById(R.id.message_time_tv);
        }
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_cardview, parent, false);

        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        final Conversation conversation = mConversations.get(position);

        holder.friendNameTv.setText(conversation.getRecipientEmail());
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    @SuppressLint("SimpleDateFormat")
    private String DateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | dd/MM");
        return simpleDateFormat.format(date);
    }
}
