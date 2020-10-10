package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdViewHolder> {

    private Context mContext;

    private String mUserEmail;

    private List<Advertisement> advertisementList;

    private static final String TAG = "AdsAdapter";

    public AdsAdapter(Context mContext, String mUserEmail, List<Advertisement> advertisementList) {
        this.mContext = mContext;
        this.mUserEmail = mUserEmail;
        this.advertisementList = advertisementList;
    }

    public interface AdsAdapterInterface {
        void onAdClick(View view, int position);
        void onEditOptionClicked(int position, View view);
        void onDeleteOptionClicked(int position, View view);
    }

    private AdsAdapterInterface listener;

    public void setAdsAdapterListener(AdsAdapterInterface listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_cardview, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Advertisement ad=advertisementList.get(position);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        if (ad.getImages().size() > 0) {
            Glide.with(holder.adImageIv.getContext())
                    .load(ad.getImages().get(0))
                    .apply(options)
                    .into(holder.adImageIv);
        }

        holder.optionsBtn.setVisibility(mUserEmail.equals(ad.getUser().getEmail()) ?
                View.VISIBLE : View.GONE);

        holder.adPriceTv.setText(ad.getPrice() + " ₪");

        holder.adDescriptionTv.setText(ad.getDescription());


        holder.adLocationTv.setText(ad.getLocation());

        holder.adTypeTv.setText(ad.getIsSell() ? mContext.getString(R.string.ad_card_sell) : mContext.getString(R.string.ad_card_hand_hover));
    }

    @Override
    public int getItemCount() {
        return advertisementList.size();
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        private CardView cardLayout;
        private ImageView adImageIv;
        private TextView adPriceTv;
        private TextView adDescriptionTv;
        private TextView adLocationTv;
        private TextView adTypeTv;
        private ImageButton optionsBtn;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cardLayout = itemView.findViewById(R.id.card_layout);
            this.adImageIv = itemView.findViewById(R.id.ad_image);
            this.adPriceTv = itemView.findViewById(R.id.ad_price_tv);
            this.adDescriptionTv = itemView.findViewById(R.id.pet_description_tv);
            this.adLocationTv = itemView.findViewById(R.id.location_tv);
            this.adTypeTv = itemView.findViewById(R.id.ad_type_tv);
            this.optionsBtn = itemView.findViewById(R.id.ad_options_menu_btn);

            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) cardLayout.getLayoutParams();
            layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels / 2;
            cardLayout.setLayoutParams(layoutParams);

            cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAdClick(v, getAdapterPosition());
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
}
