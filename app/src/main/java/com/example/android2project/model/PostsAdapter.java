package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> mPosts;

    private Context mContext;

    public PostsAdapter(List<Post> mPosts, Context mContext) {
        this.mPosts = mPosts;
        this.mContext = mContext;
    }

    public interface PostListener {
        void onAuthorImageClicked(int position, View view);
        void onCommentsTvClicked(int position, View view);
        void onLikeBtnClicked(int position, View view);
        void onCommentBtnClicked(int position, View view);
    }

    private PostListener listener;

    public void setPostListener(PostListener postListener) {
        this.listener = postListener;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardLayout;
        ImageView authorPicIv;
        TextView authorNameTv;
        TextView postTimeAgo;
        ShowMoreTextView contentTv;
        ImageView likesAmountIv;
        TextView likesAmountTv;
        TextView commentsAmountTv;
        LinearLayout likeBtn;
        LinearLayout commentBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.post_card_layout);
            authorPicIv = itemView.findViewById(R.id.author_pic_iv);
            authorNameTv = itemView.findViewById(R.id.author_name_tv);
            postTimeAgo = itemView.findViewById(R.id.time_age_tv);
            contentTv = itemView.findViewById(R.id.post_content_tv);
            likesAmountIv = itemView.findViewById(R.id.like_amount_iv);
            likesAmountTv = itemView.findViewById(R.id.likes_amount_tv);
            commentsAmountTv = itemView.findViewById(R.id.comments_amount_tv);
            likeBtn = itemView.findViewById(R.id.post_like_btn);
            commentBtn = itemView.findViewById(R.id.post_comment_btn);

            setContentTvProperties();

            authorPicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAuthorImageClicked(getAdapterPosition(), v);
                    }
                }
            });

            commentsAmountTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCommentsTvClicked(getAdapterPosition(), v);
                    }
                }
            });

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onLikeBtnClicked(getAdapterPosition(), v);
                    }
                }
            });

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCommentBtnClicked(getAdapterPosition(), v);
                    }
                }
            });
        }

        private void setContentTvProperties() {
            contentTv.setShowingLine(5);
            contentTv.setShowMoreColor(mContext.getColor(R.color.colorPrimary));
            contentTv.setShowLessTextColor(mContext.getColor(R.color.colorPrimary));
            contentTv.addShowMoreText(mContext.getString(R.string.show_more));
            contentTv.addShowLessText(mContext.getString(R.string.show_less));
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_cardview, parent, false);
        return new PostViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mPosts.get(position);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(mContext)
                .load(post.getAuthorImageUri())
                .apply(options)
                .into(holder.authorPicIv);

        holder.authorNameTv.setText(post.getAuthorName());

        holder.postTimeAgo.setText(post.getPostTimeAgo());

        if (post.getComments().size() > 0) {
            holder.likesAmountIv.setVisibility(View.VISIBLE);
            holder.likesAmountTv.setVisibility(View.VISIBLE);
        } else {
            holder.likesAmountIv.setVisibility(View.GONE);
            holder.likesAmountTv.setVisibility(View.GONE);
        }

        if (post.getLikesCount() > 0) {
            holder.commentsAmountTv.setVisibility(View.VISIBLE);
        } else {
            holder.commentsAmountTv.setVisibility(View.GONE);
        }

        holder.contentTv.setText(post.getAuthorContent());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}
