package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.android2project.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class PetsAdapter extends FirestoreRecyclerAdapter<Pet, PetsAdapter.PetViewHolder> {

    public PetsAdapter(@NonNull FirestoreRecyclerOptions<Pet> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PetViewHolder holder, int position, @NonNull Pet pet) {
        List<SlideModel> imagesList = new ArrayList<>();
        for(String uri : pet.getPhotoUri()){
            imagesList.add(new SlideModel(uri,ScaleTypes.CENTER_CROP));
        }
        holder.photoSlider.setImageList(imagesList, ScaleTypes.CENTER_CROP);
        holder.petName_tv.setText(pet.getPetName());
        holder.petDescription_tv.setText(pet.getPetDescription());

    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_item, parent, false);
       return new PetViewHolder(view);
    }

    static class PetViewHolder extends RecyclerView.ViewHolder{
        ImageSlider photoSlider;
        TextView petName_tv;
        TextView petDescription_tv;
        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            photoSlider = itemView.findViewById(R.id.images_slider);
            petName_tv = itemView.findViewById(R.id.pet_name_tv);
            petDescription_tv = itemView.findViewById(R.id.pet_description_tv);
        }
    }
}
