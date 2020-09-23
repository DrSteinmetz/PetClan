package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android2project.R;

import java.util.List;

public class ChatClanAdapter extends RecyclerView.Adapter<ChatClanAdapter.ChatClanViewHolder> {
    private List<User> mFriends;
    private Context mContext;

    public ChatClanAdapter(Context context, List<User> friends) {
        this.mContext = context;
        this.mFriends = friends;
    }

    public interface FriendItemListener {
        void onClicked(int position, View view);
    }

    private FriendItemListener listener;

    public void setFriendItemListener(FriendItemListener friendItemListener) {
        this.listener = friendItemListener;
    }

    @NonNull
    @Override
    public ChatClanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.chat_clan_cardview,parent,false);
        return new ChatClanViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatClanViewHolder holder, int position) {
        User user = mFriends.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        Glide.with(mContext).load(user.getPhotoUri()).into(holder.userProfilePic);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ChatClanViewHolder extends RecyclerView.ViewHolder {
        private ImageView userProfilePic;
        private TextView userName;

        public ChatClanViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePic = itemView.findViewById(R.id.friend_pic_iv);
            userName = itemView.findViewById(R.id.friend_name_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onClicked(getAdapterPosition(),v);
                    }
                }
            });
        }
    }
}
