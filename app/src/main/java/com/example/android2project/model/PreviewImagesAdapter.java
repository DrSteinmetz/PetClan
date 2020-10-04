package com.example.android2project.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PreviewImagesAdapter extends RecyclerView.Adapter<PreviewImagesAdapter.ViewHolder> {

    private static final String TAG = "Adapter";
    private List<String> mImagesPath;
    private Context mContext;

    public PreviewImagesAdapter(Context context ,List<String> imagesPath) {
        this.mImagesPath = imagesPath;
        this.mContext = context;
        Log.d(TAG, "PreviewImagesAdapter: list "+imagesPath.size());
    }

    public interface DeletePreviewInterface {
        void onDelete(int position, View view);
    }

    private DeletePreviewInterface listener;

    public void setListener(DeletePreviewInterface listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private FloatingActionButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_iv);
            this.deleteBtn = itemView.findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDelete(getAdapterPosition(), v);
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deletable_imageview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUri = mImagesPath.get(position);
        Log.d(TAG, "onBindViewHolder: " + imageUri);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);
        Glide.with(mContext)
                .load(imageUri)
                .apply(options)
                .into(holder.imageView);

        if(imageUri!=null){
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }else{
            holder.deleteBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mImagesPath.size();
    }
}
