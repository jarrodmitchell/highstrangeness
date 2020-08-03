package com.example.highstrangeness.utilities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.highstrangeness.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class StorageUtility {
    public static final String TAG = "StorageUtility";

    public static boolean success;
    public static Uri imageUri;

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageMetadata metadata = new StorageMetadata.Builder()
            .setContentType("image/png")
            .build();

    public static void updateImage(String name ) {
        StorageReference storageReference = storage.getReference();
        StorageReference imageReference = storageReference.child("images");
    }

    public static boolean updateProfileImage(Uri uri, final Context context) {
        StorageReference storageReference = storage.getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference profileImagesReference = imageReference.child("profileImages");
        StorageReference userImageReference = profileImagesReference.child(User.currentUser.getId());
        StorageReference image = userImageReference.child(User.currentUser.getId() + ".png");

        UploadTask uploadTask = image.putFile(uri, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: ");
                Toast.makeText(context, "Profile Picture Added", Toast.LENGTH_SHORT).show();
                success = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
                Toast.makeText(context, "Error Adding Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                success = false;
            }
        });
        return success;
    }

    public static void getProfileImage(String id, int size, final ImageView imageView, Context context) {
        StorageReference storageReference = storage.getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference profileImagesReference = imageReference.child("profileImages");
        StorageReference userImageReference = profileImagesReference.child(id);


        StringBuilder ref = new StringBuilder(id);
        switch (size) {
            case 0:
                ref.append("_35x35");
                break;
            case 1:
                ref.append("_50x50");
                break;
            case 2:
                ref.append("_100x100");
                break;
        }
        ref.append(".png");

            final long mb = 1024 * 1024;
            userImageReference.child(ref.toString()).getBytes(mb).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            });
    }
}
