package com.example.android2project.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private Context mContext;
    private Map<String, User> mUserMap = new HashMap<>();
    private static List<Conversation> mConversations;

    public interface ChatAdapterInterface {
        void onClicked(int position, View view);
    }

    private ChatAdapterInterface listener;

    public void setChatAdapterListener(ChatAdapterInterface listener) {
        this.listener = listener;
    }

    public ChatsAdapter(Context context, List<Conversation> conversations, List<User> users) {
        this.mContext = context;
        mConversations = conversations;
        setUserMap(users);
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
            this.lastMessageTv = itemView.findViewById(R.id.last_message_tv);
            this.timeTv = itemView.findViewById(R.id.time_ago_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClicked(getAdapterPosition(), v);
                    }
                }
            });
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

        final User recipient = mUserMap.get(conversation.getSenderEmail()) == null ?
                mUserMap.get(conversation.getRecipientEmail()) : mUserMap.get(conversation.getSenderEmail());

        if (recipient != null) {
            RequestOptions options = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_user_pic)
                    .error(R.drawable.ic_default_user_pic);

            Glide.with(mContext)
                    .load(recipient.getPhotoUri())
                    .apply(options)
                    .into(holder.friendImageIv);

            final String userName = recipient.getFirstName() + " " + recipient.getLastName();
            holder.friendNameTv.setText(userName);

        }

        final String lastMessageContent = conversation.getLastMessage().getContent();
        holder.lastMessageTv.setText(lastMessageContent);

        final String lastMessageTime = dateToFormatDate(conversation.getLastMessage().getTime());
        holder.timeTv.setText(lastMessageTime);
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void setUserMap(List<User> userList) {
        if (!this.mUserMap.isEmpty()) {
            this.mUserMap.clear();
        }
        for (User user : userList) {
            this.mUserMap.put(user.getEmail(), user);
        }
    }

    public static void clearConversations() {
        if (mConversations != null && !mConversations.isEmpty()) {
            mConversations.clear();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String dateToFormatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | dd/MM");
        return simpleDateFormat.format(date);
    }
}
