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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatsAdapter extends FirebaseRecyclerAdapter<Conversation, ChatsAdapter.ChatsViewHolder> {

    public ChatsAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
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

        public void bind(Conversation conversation) {
            this.friendNameTv.setText(conversation.getReceiverEmail());
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position,
                                    @NonNull Conversation conversation) {
        holder.bind(conversation);
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_cardview, parent, false);

        return new ChatsViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    private String DateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | dd/MM");
        return simpleDateFormat.format(date);
    }
}
