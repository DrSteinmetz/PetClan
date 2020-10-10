package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.android2project.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PetsAdapter extends FirestoreRecyclerAdapter<Pet, PetsAdapter.PetViewHolder> {

    private boolean mIsMyPet;

    public PetsAdapter(final Context context, @NonNull FirestoreRecyclerOptions<Pet> options, String userEmail) {
        super(options);

        mIsMyPet = userEmail == null;
    }

    public interface PetsAdapterInterface {
        void onEditOptionClicked(int position, View view);
        void onDeleteOptionClicked(int position, View view);
    }

    private PetsAdapterInterface listener;

    public void setPetsAdapterListener(PetsAdapterInterface listener) {
        this.listener = listener;
    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        ImageSlider photoSlider;
        FloatingActionButton optionsBtn;
        TextView petName_tv;
        TextView petDescription_tv;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            photoSlider = itemView.findViewById(R.id.images_slider);
            optionsBtn = itemView.findViewById(R.id.pet_options_menu_btn);
            petName_tv = itemView.findViewById(R.id.pet_name_tv);
            petDescription_tv = itemView.findViewById(R.id.pet_description_tv);

            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpMenu(v);
                }
            });
        }

        private void showPopUpMenu(final View view) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), optionsBtn);
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
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_cardview, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PetViewHolder holder, int position, @NonNull Pet pet) {
        List<SlideModel> imagesList = new ArrayList<>();

        for (String uri : pet.getPhotoUri()) {
            imagesList.add(new SlideModel(uri, ScaleTypes.CENTER_CROP));
        }

        holder.photoSlider.setImageList(imagesList, ScaleTypes.CENTER_CROP);
        holder.petName_tv.setText(pet.getPetName());
        holder.petDescription_tv.setText(pet.getPetDescription());


        holder.optionsBtn.setVisibility(mIsMyPet ? View.VISIBLE : View.GONE);
    }
}
