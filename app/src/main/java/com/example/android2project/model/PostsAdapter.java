package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.repository.AuthRepository;
import com.skyhope.showmoretextview.ShowMoreTextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private List<Post> mPosts;

    private Context mContext;

    private String mUserEmail;
    private String mMyEmail = null;

    private final String TAG = "PostsAdapter";

    public PostsAdapter(List<Post> posts, Context context, final String userEmail) {
        this.mPosts = posts;
        this.mContext = context;
        this.mMyEmail = AuthRepository.getInstance(context).getUserEmail();
        this.mUserEmail = userEmail;
    }

    public interface PostListener {
        void onAuthorImageClicked(int position, View view);
        void onCommentsTvClicked(int position, View view);
        void onLikeBtnClicked(int position, View view, boolean isLike);
        void onCommentBtnClicked(int position, View view);
        void onEditOptionClicked(int position, View view);
        void onDeleteOptionClicked(int position, View view);
    }

    private PostListener listener;

    public void setPostListener(PostListener postListener) {
        this.listener = postListener;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        CardView postCardLayout;
        ImageView authorPicIv;
        TextView authorNameTv;
        TextView postTimeAgo;
        ShowMoreTextView contentTv;
        ImageView likesAmountIv;
        TextView likesAmountTv;
        TextView commentsAmountTv;
        LinearLayout likeBtn;
        ImageView likeBtnIv;
        TextView likeBtnTv;
        LinearLayout commentBtn;
        ImageButton optionsBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postCardLayout = itemView.findViewById(R.id.post_card_layout);
            authorPicIv = itemView.findViewById(R.id.author_pic_iv);
            authorNameTv = itemView.findViewById(R.id.author_name_tv);
            postTimeAgo = itemView.findViewById(R.id.time_ago_tv);
            contentTv = itemView.findViewById(R.id.post_content_tv);
            likesAmountIv = itemView.findViewById(R.id.like_amount_iv);
            likesAmountTv = itemView.findViewById(R.id.likes_amount_tv);
            commentsAmountTv = itemView.findViewById(R.id.comments_amount_tv);
            likeBtn = itemView.findViewById(R.id.post_like_btn);
            likeBtnIv = itemView.findViewById(R.id.post_like_btn_iv);
            likeBtnTv = itemView.findViewById(R.id.post_like_btn_tv);
            commentBtn = itemView.findViewById(R.id.post_comment_btn);
            optionsBtn = itemView.findViewById(R.id.post_options_menu);

            setContentTvProperties();

            if (mUserEmail != null) {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) postCardLayout.getLayoutParams();
                final float density = mContext.getResources().getDisplayMetrics().density;
                final int margin = (int) (12 * density);
                layoutParams.setMargins(margin, 0, margin, 0);
                postCardLayout.setRadius(50);
                postCardLayout.requestLayout();
            }

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
                    Post post = mPosts.get(getAdapterPosition());
                    boolean isLike;

                    if (listener != null) {
                        if (likeBtnTv.getText().toString().equals("Like")) {
                            post.getLikesMap().put(mMyEmail, true);
                            isLike = true;
                        } else {
                            post.getLikesMap().remove(mMyEmail);
                            isLike = false;
                        }
                        listener.onLikeBtnClicked(getAdapterPosition(), v, isLike);
                    }

                    /**<-------In order to prevent double click------->**/
                    likeBtn.setEnabled(false);
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

            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpMenu(v);
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

        private void showPopUpMenu(final View view) {
            PopupMenu popupMenu = new PopupMenu(mContext, optionsBtn);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            if (listener != null) {
                                listener.onEditOptionClicked(getAdapterPosition(), view);
                            }
                            break;
                        case R.id.option_delete:
                            if (listener != null) {
                                listener.onDeleteOptionClicked(getAdapterPosition(), view);
                            }
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
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

        holder.postTimeAgo.setText(timestampToTimeAgo(post.getPostTime()));

        if (post.getAuthorEmail().equals(mMyEmail)) {
            holder.optionsBtn.setVisibility(View.VISIBLE);
        } else {
            holder.optionsBtn.setVisibility(View.GONE);
        }

        if (post.getAuthorEmail().equals(mUserEmail != null ? mUserEmail : mMyEmail)) {
            holder.authorPicIv.setClickable(false);
        } else {
            holder.authorPicIv.setClickable(true);
        }

        boolean isUserLikedPost = post.getLikesMap().containsKey(mMyEmail);
        holder.likeBtnTv.setText(isUserLikedPost ? "Unlike" : "Like");
        holder.likeBtnIv.setRotation(isUserLikedPost ? 180 : 0);
        if (post.getLikesCount() > 0) {
            String likeString = post.getLikesCount() + " Likes";
            holder.likesAmountTv.setText(likeString);
            holder.likesAmountIv.setVisibility(View.VISIBLE);
            holder.likesAmountTv.setVisibility(View.VISIBLE);
        } else {
            holder.likesAmountIv.setVisibility(View.GONE);
            holder.likesAmountTv.setVisibility(View.GONE);
        }

        if (post.getCommentsCount() > 0) {
            String commentString = post.getCommentsCount() + " Comments";
            holder.commentsAmountTv.setText(commentString);
            holder.commentsAmountTv.setVisibility(View.VISIBLE);
        } else {
            holder.commentsAmountTv.setVisibility(View.GONE);
        }

        holder.contentTv.setText(post.getAuthorContent());
        holder.setContentTvProperties();

        /**<-------In order to prevent double click------->**/
        holder.likeBtn.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private String timestampToTimeAgo(Date date) {
        String language = Locale.getDefault().getLanguage();
        PrettyTime prettyTime = new PrettyTime(new Locale(language));
        return prettyTime.format(date);
    }
}
