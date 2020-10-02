package com.example.android2project.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android2project.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

public class AdsAdapter extends FirestorePagingAdapter<Advertisement, AdsAdapter.AdViewHolder> {

    private static final String TAG = "AdsAdapter";

    public AdsAdapter(@NonNull FirestorePagingOptions<Advertisement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdViewHolder holder, int position, @NonNull Advertisement ad) {
        Glide.with(holder.adImageIv.getContext()).load(ad.getImages().get(0)).into(holder.adImageIv);
        holder.adPriceTv.setText(ad.getPrice() + " ₪");
        holder.adDescriptionTv.setText(ad.getDescription());
        holder.adLocationTv.setText(ad.getLocation());
        holder.adTypeTv.setText(ad.getAdType() ? " | Sell" : " | Hand Over");
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_cardview, parent, false);
        return new AdViewHolder(view);
    }


    class AdViewHolder extends RecyclerView.ViewHolder {
        private ImageView adImageIv;
        private TextView adPriceTv;
        private TextView adDescriptionTv;
        private TextView adLocationTv;
        private TextView adTypeTv;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            this.adImageIv = itemView.findViewById(R.id.ad_image);
            this.adPriceTv = itemView.findViewById(R.id.ad_price_tv);
            this.adDescriptionTv = itemView.findViewById(R.id.pet_description_tv);
            this.adLocationTv = itemView.findViewById(R.id.location_tv);
            this.adTypeTv = itemView.findViewById(R.id.ad_type_tv);
        }
    }
}
