package com.example.android2project.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StorageRepository {
    private StorageReference mStorage;

    private Context mContext;

    private final int COMPRESS_PERCENTAGE = 20;
    private final String TAG = "StorageRepository";

    public StorageRepository(Context context) {
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
                            stringToReturn[0] = downloadUrl;
                            Log.d(TAG, "onSuccess: " + downloadUrl);
                        }
                    });
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringToReturn[0];
    }

    public void downloadFile() {
        StorageReference fileToDownload = mStorage.child("users_profile_picture");

        try {
            File localFile = File.createTempFile("image", ".jpg");
            fileToDownload.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
