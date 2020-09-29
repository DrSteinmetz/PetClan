package com.example.android2project.model;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

public class PetsAdapter extends FirestoreRecyclerAdapter<Pet, PetViewHolder> {

    @Override
    protected void onBindViewHolder(@NonNull PetViewHolder holder, int position, @NonNull Pet model) {

    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}
