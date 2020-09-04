package com.example.highstrangeness.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.highstrangeness.ui.post.PostPt2Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ImageStorageUtility {
    public static final String TAG = "StorageUtility";
    public static final String ACTION_PROFILE_PIC_UPDATED = "ACTION_PROFILE_PIC_UPDATED";
    public static final String ACTION_POST_IMAGES_CHANGED = "ACTION_POST_IMAGES_CHANGED";

    public interface GetPostImageNamesListener {
        void getPostImagesName(List<String> imageNames);
    }

    private static StorageMetadata metadata = new StorageMetadata.Builder()
            .setContentType("image/png")
            .build();

    public static void updatePostImages(final Uri uri, String postId, final Context context) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference postImagesReference = imageReference.child("posts");
        StorageReference postImageReference = postImagesReference.child(postId);
        StorageReference image = postImageReference.child(uri.getLastPathSegment() + ".png");

        UploadTask uploadTask = image.putFile(uri, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: adding post image");
                Toast.makeText(context, "Image \"" + uri.getLastPathSegment() +  "\" was successfully uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ACTION_POST_IMAGES_CHANGED);
                context.sendBroadcast(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: adding post image");
                Toast.makeText(context, "Image \"" + uri.getLastPathSegment() +  "\" failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getListOfPostImages(String postId, Context context) {
        final GetPostImageNamesListener getPostImageNamesListener = (GetPostImageNamesListener) context;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagesReference = storageReference.child("images");
        StorageReference postsReference = imagesReference.child("posts");
        StorageReference listReference = postsReference.child(postId);

        listReference.list(40)
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d(TAG, "getListOfPostImages: in");

                        List<String> imageNames = new ArrayList<>();

                        for (StorageReference item : listResult.getItems()) {
                            String name = item.getName();
                            if (!(name.contains("_150x150") || name.contains("_75x75") || name.contains("_50x50"))) {
                                Log.d(TAG, "onSuccess:  "  + name);
                                String[] splitName = name.split("\\.");
                                Log.d(TAG, "onSuccess: split  " + splitName[0] );
                                imageNames.add(splitName[0]);
                            }
                            Log.d(TAG, "onSuccess: getpostimagelistener send");
                            getPostImageNamesListener.getPostImagesName(imageNames);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    public static void updateProfileImage(Uri uri, final Context context) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageReference = storageReference.child("images");
            StorageReference profileImagesReference = imageReference.child("profileImages");
            StorageReference userImageReference = profileImagesReference.child(user.getUid());
            StorageReference image = userImageReference.child(user.getUid() + ".png");

            UploadTask uploadTask = image.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Profile Picture Added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ACTION_PROFILE_PIC_UPDATED);
                    context.sendBroadcast(intent);
                    Log.d(TAG, "onSuccess: ");
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

    public static void setProfileImage(Context context, String id, int size, final ImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference profileImagesReference = imageReference.child("profileImages");
        StorageReference userImageReference = profileImagesReference.child(id);

        setImageToImageView(id, size, imageView, userImageReference);
    }

    public static void setPostImage(String name, String id, int size, final ImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference postsReference = imageReference.child("posts");
        StorageReference postImagesReference = postsReference.child(id);

        setImageToImageView(name, size, imageView, postImagesReference);
    }

    public static void setImageToImageView(String id, int size, final ImageView imageView, StorageReference reference) {
        Log.d(TAG, "setImageToImageView: id = " + id);
        Log.d(TAG, "setImageToImageView: ref = " + reference.toString());

        StringBuilder ref = new StringBuilder(id);
        switch (size) {
            case 0:
                ref.append("_50x50");
                break;
            case 1:
                ref.append("_75x75");
                break;
            case 2:
                ref.append("_150x150");
                break;
        }
        ref.append(".png");

        Log.d(TAG, "setImageToImageView: ref = " + ref);

        final long mb = 1024 * 1024;
        reference.child(ref.toString()).getBytes(mb).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Log.d(TAG, "onSuccess: set image");
            }
        });
    }

    public static void deletePostImages(final Context context, String id) {
        Log.d(TAG, "deletePostImages: in");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference postsReference = imageReference.child("posts");
        StorageReference postImagesReference = postsReference.child(id);
        Log.d(TAG, "deletePostImages: id = " + id);
        Log.d(TAG, "setImageToImageView: ref = " + postImagesReference.toString());
            postImagesReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    Log.d(TAG, "onComplete: list got");
                    if  (task.getResult() != null) {
                        Log.d(TAG, "onComplete: list result here");
                        final Intent intent = new Intent(PostUtility.ACTION_SEND_ALERT_POST_DELETED);
                        final int max = task.getResult().getItems().size();
                        Log.d(TAG, "onComplete: size =  " + max);
                        for (int i = 0; i < max; i++) {
                            final int index = i;
                            StorageReference reference = task.getResult().getItems().get(i);
                            Log.d(TAG, "onComplete: ref = " + reference.toString());
                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Success deleting images");
                                        if (index + 1 == max) {
//                                            context.sendBroadcast(intent);
                                        }
                                    }else {
                                        if (task.getException() != null) {
                                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
    }

    public static void deleteImage(final Context context, final String id, final String imageName) {

        Log.d(TAG, "deletePostImages: in");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images");
        StorageReference postsReference = imageReference.child("posts");
        StorageReference postImagesReference = postsReference.child(id);
        Log.d(TAG, "deletePostImages: id = " + id);
        Log.d(TAG, "setImageToImageView: ref = " + postImagesReference.toString());
        postImagesReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                Log.d(TAG, "onComplete: list got");
                if  (task.getResult() != null) {
                    Log.d(TAG, "onComplete: list result here");
                    final int max = task.getResult().getItems().size();
                    Log.d(TAG, "onComplete: size =  " + max);

                    for (int i = 0; i < max; i++) {
                        final int index = i;
                        StorageReference reference = task.getResult().getItems().get(i);
                        String  name = reference.getName();
                        if (name.contains(imageName)) {
                            Log.d(TAG, "onComplete: ref = " + reference.toString());
                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Success deleting images " + index);
                                        if (index + 1 == 4) {
                                            Log.d(TAG, "onComplete: " + index);
                                            getListOfPostImages(id, context);
                                        }
                                    }else {
                                        if (task.getException() != null) {
                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
