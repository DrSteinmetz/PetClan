package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> mComments;

    private Context mContext;

    public CommentsAdapter(List<Comment> comments, Context context) {
        this.mComments = comments;
        this.mContext = context;
    }

    public interface CommentListener {
        void onOptionsBtnClicked(int position, View view);
    }

    private CommentListener listener;

    public void setCommentListener(CommentListener commentListener) {
        this.listener = commentListener;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView authorPicIv;
        TextView authorNameTv;
        TextView postTimeAgo;
        ShowMoreTextView contentTv;
        ImageButton optionsBtn;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            authorPicIv = itemView.findViewById(R.id.author_pic_iv);
            authorNameTv = itemView.findViewById(R.id.author_name_tv);
            postTimeAgo = itemView.findViewById(R.id.time_ago_tv);
            contentTv = itemView.findViewById(R.id.comment_content_tv);
            optionsBtn = itemView.findViewById(R.id.comment_options_menu);

            setContentTvProperties();

            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_cardview, parent, false);
        return new CommentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}
