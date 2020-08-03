package com.example.highstrangeness.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.highstrangeness.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageUtility {
    public static final String TAG = "StorageUtility";

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageMetadata metadata = new StorageMetadata.Builder()
            .setContentType("image/png")
            .build();

    public static void updateImage(String name ) {
        StorageReference storageReference = storage.getReference();
        StorageReference imageReference = storageReference.child("images");
    }

    public static void updateProfileImage(Uri uri, final Context context) {
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
                Toast.makeText(context, "Error Adding Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
