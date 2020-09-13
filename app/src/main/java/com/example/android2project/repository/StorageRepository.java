package com.example.android2project.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class StorageRepository {
    private static StorageRepository storageRepository;

    private StorageReference mStorage;

    private Context mContext;

    private final int COMPRESS_PERCENTAGE = 20;
    private final String TAG = "StorageRepository";

    /**<-------Picture Download interface------->**/
    public interface StorageDownloadPicInterface {
        void onDownloadPicSuccess(Uri uri);
        void onDownloadPicFailed(String error);
    }

    StorageDownloadPicInterface mDownloadPicListener;

    public void setDownloadPicListener(StorageDownloadPicInterface storageDownloadPicInterface) {
        this.mDownloadPicListener = storageDownloadPicInterface;
    }

    /**<-------Picture Upload interface------->**/
    public interface StorageUploadPicInterface {
        void onUploadPicSuccess(boolean value);
        void onUploadPicFailed(String error);
    }

    StorageUploadPicInterface mUploadPicListener;

    public void setUploadPicListener(StorageUploadPicInterface storageUploadPicInterface) {
        this.mUploadPicListener = storageUploadPicInterface;
    }

    /**<-------Singleton------->**/
    public static StorageRepository getInstance(Context context) {
        if (storageRepository == null) {
            storageRepository = new StorageRepository(context);
        }
        return storageRepository;
    }

    private StorageRepository(Context context) {
        this.mStorage = FirebaseStorage.getInstance().getReference();
        this.mContext = context;
    }

    public String uploadFile(Uri uri, String uId) {
        StorageReference fileToUpload = mStorage.child("users_profile_picture/" + uId + ".jpg");

        final String[] stringToReturn = new String[1];
        stringToReturn[0] = "users_profile_picture/" + uId + ".jpg";

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_PERCENTAGE, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            fileToUpload.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = Objects.requireNonNull(
                                    Objects.requireNonNull(taskSnapshot.getMetadata()).getReference())
                                    .getDownloadUrl()
                                    .toString();
                            if (mUploadPicListener != null) {
                                mUploadPicListener.onUploadPicSuccess(true);
                            }
                            Log.d(TAG, "onSuccess: " + downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUploadPicListener != null) {
                                mUploadPicListener.onUploadPicFailed(e.getMessage());
                            }
                        }
                    });
            byteArrayOutputStream.close();
        } catch (IOException e) {
            if (mUploadPicListener != null) {
                mUploadPicListener.onUploadPicFailed(e.getMessage());
            }
            e.printStackTrace();
        }

        return stringToReturn[0];
    }

    public void downloadFile(String uId) {
        StorageReference fileToDownload = mStorage.child("users_profile_picture/" + uId + ".jpg");

        fileToDownload.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mDownloadPicListener != null) {
                            mDownloadPicListener.onDownloadPicSuccess(uri);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        if (mDownloadPicListener != null) {
                            mDownloadPicListener.onDownloadPicFailed(ex.getMessage());
                        }
                    }
                });

    }
}
