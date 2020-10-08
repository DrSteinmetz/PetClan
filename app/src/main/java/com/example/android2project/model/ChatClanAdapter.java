package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;

import java.util.ArrayList;
import java.util.List;

public class ChatClanAdapter extends RecyclerView.Adapter<ChatClanAdapter.ChatClanViewHolder> implements Filterable {
    private List<User> mFriends;
    private List<User> mFilteredFriends;
    private Context mContext;

    public ChatClanAdapter(Context context, List<User> friends) {
        this.mContext = context;
        this.mFriends = friends;
        this.mFilteredFriends = new ArrayList<>(mFriends);
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
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.chat_clan_cardview, parent, false);
        return new ChatClanViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatClanViewHolder holder, int position) {
        User user = mFriends.get(position);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(mContext)
                .load(user.getPhotoUri())
                .apply(options)
                .into(holder.userProfilePic);

        final String userName = user.getFirstName() + " " + user.getLastName();
        holder.userName.setText(userName);
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
                    if (listener != null) {
                        listener.onClicked(getAdapterPosition(), v);
                    }
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return filteredFriends;
    }

    private Filter filteredFriends = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredLists = new ArrayList<>();
            if (charSequence.length() == 0 || charSequence == null) {
                filteredLists.addAll(mFilteredFriends);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();
                for (User user : mFilteredFriends) {
                    if (user.getFirstName().toLowerCase().contains(pattern) || user.getLastName().toLowerCase()
                            .contains(pattern)) {
                        filteredLists.add(user);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredLists;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(!mFriends.isEmpty()) {
                mFriends.clear();
            }
            mFriends.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
