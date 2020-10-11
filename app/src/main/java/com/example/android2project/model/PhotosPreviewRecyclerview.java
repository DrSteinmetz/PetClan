package com.example.android2project.model;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.StorageRepository;

import java.util.ArrayList;
import java.util.List;

public class PhotosPreviewRecyclerview extends RecyclerView {
    private static final String TAG = "PhotosPreviewRecyclerview";
    private int IMAGE_VIEW_SIZE = 8;
    private List<String> mSelectedImageList = new ArrayList<>();
    private PreviewImagesAdapter mImagePreviewAdapter;
    private int mImageViewCounter = 0;
    private StorageRepository mStorageRepository;

    public PhotosPreviewRecyclerview(@NonNull Context context) {
        super(context);
        mStorageRepository = StorageRepository.getInstance(context);
    }

    public PhotosPreviewRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mStorageRepository = StorageRepository.getInstance(context);
    }

    public PhotosPreviewRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStorageRepository = StorageRepository.getInstance(context);
    }

    public void init(int size){
        IMAGE_VIEW_SIZE = size;

        for (int i = 0; i < IMAGE_VIEW_SIZE; i++) {
            mSelectedImageList.add(null);
        }

        mImagePreviewAdapter = new PreviewImagesAdapter(getContext(),mSelectedImageList);
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        mImagePreviewAdapter.setListener(new PreviewImagesAdapter.DeletePreviewInterface() {
            @Override
            public void onDelete(int position, View view) {
                mSelectedImageList.remove(position);
                mSelectedImageList.add(null);
                mImagePreviewAdapter.notifyItemRemoved(position);
                if (mImageViewCounter > 0) {
                    mImageViewCounter--;
                }
            }
        });

        setAdapter(mImagePreviewAdapter);
    }

    public void initEdit(int size, final Object object){
        if(object instanceof Advertisement) {
            final Advertisement ad = (Advertisement) object;
            IMAGE_VIEW_SIZE = size;
            for (int i = 0; i < IMAGE_VIEW_SIZE; i++) {
                if (i < ad.getImages().size()) {
                    mSelectedImageList.add(ad.getImages().get(i));
                    mImageViewCounter++;
                } else {
                    mSelectedImageList.add(null);
                }
            }

            mImagePreviewAdapter = new PreviewImagesAdapter(getContext(), mSelectedImageList);
            setHasFixedSize(true);
            setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            mImagePreviewAdapter.setListener(new PreviewImagesAdapter.DeletePreviewInterface() {
                @Override
                public void onDelete(int position, View view) {
                    String uri = mSelectedImageList.get(position);
                    if (ad != null && uri.contains("https://firebasestorage.googleapis.com/v0/b/petclan-2fdce.appspot.com")) {
                        mStorageRepository.deletePhotoFromStorage(ad.getStoragePath(ad.getUser().getEmail(), uri));
                    }
                    mSelectedImageList.remove(position);
                    mSelectedImageList.add(null);
                    mImagePreviewAdapter.notifyItemRemoved(position);
                    if (mImageViewCounter > 0) {
                        mImageViewCounter--;
                    }
                }
            });
            setAdapter(mImagePreviewAdapter);

        } else if (object instanceof Pet){
            final Pet pet = (Pet) object;

            IMAGE_VIEW_SIZE = size;
            for (int i = 0; i < IMAGE_VIEW_SIZE; i++) {
                if (i < pet.getPhotoUri().size()) {
                    mSelectedImageList.add(pet.getPhotoUri().get(i));
                    mImageViewCounter++;
                } else {
                    mSelectedImageList.add(null);
                }
            }

            mImagePreviewAdapter = new PreviewImagesAdapter(getContext(), mSelectedImageList);
            setHasFixedSize(true);
            setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            mImagePreviewAdapter.setListener(new PreviewImagesAdapter.DeletePreviewInterface() {
                @Override
                public void onDelete(int position, View view) {
                    String uri = mSelectedImageList.get(position);
                    if (pet != null && uri.contains("https://firebasestorage.googleapis.com/v0/b/petclan-2fdce.appspot.com")) {
                        mStorageRepository.deletePhotoFromStorage(pet.getStoragePath(AuthRepository.getInstance(getContext()).getUserEmail(), uri));
                    }
                    mSelectedImageList.remove(position);
                    mSelectedImageList.add(null);
                    mImagePreviewAdapter.notifyItemRemoved(position);
                    if (mImageViewCounter > 0) {
                        mImageViewCounter--;
                    }
                }
            });

            setAdapter(mImagePreviewAdapter);
        }

    }

    public void addPhoto(Uri uri){
        mSelectedImageList.remove(mImageViewCounter);
        mSelectedImageList.add(mImageViewCounter, uri.toString());
        mImagePreviewAdapter.notifyItemChanged(mImageViewCounter++);
        Log.d(TAG, "addPhoto: " + uri.toString());
    }

    public List<String> getSelectedImageList(){
        return mSelectedImageList;
    }

    public int getImageCounter(){
        return mImageViewCounter;
    }
}
