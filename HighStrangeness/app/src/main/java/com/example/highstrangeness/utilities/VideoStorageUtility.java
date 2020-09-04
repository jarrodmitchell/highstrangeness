package com.example.highstrangeness.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoStorageUtility {
    public static final String TAG = "VideoStorageUtility";
    public static final String ACTION_POST_VIDEOS_CHANGED = "ACTION_POST_VIDEOS_CHANGED";

    public static final String FIELD_VIDEO_URL = "videoUrl";
    public static final String FIELD_THUMBNAIL_URL = "thumbnailUrl";

    public interface GetPostVideoNamesListener {
        void getPostVideoNames(List<String> videoNames);
    }

    private static StorageMetadata videoMetadata = new StorageMetadata.Builder()
            .setContentType("video/mp4")
            .build();

    private static StorageMetadata imageMetadata = new StorageMetadata.Builder()
            .setContentType("image/png")
            .build();

    public static void updatePostVideos(final Uri uri, String postId, final Context context) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference videosReference = storageReference.child("videos");
        StorageReference postImagesReference = videosReference.child("posts");
        StorageReference postImageReference = postImagesReference.child(postId);
        StorageReference video = postImageReference.child(uri.getLastPathSegment() + ".mp4");

        UploadTask uploadTask = video.putFile(uri, videoMetadata);

        final Map<String, Object> docData = new HashMap<>();

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: adding post image");
                Toast.makeText(context, "Video \"" + uri.getLastPathSegment() +  "\" was successfully uploaded", Toast.LENGTH_SHORT).show();
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();
                            if (downloadUri != null) {
                                docData.put(FIELD_VIDEO_URL, downloadUri.toString());
                                Log.d(TAG, "onComplete: Successfully retrieved video url " + downloadUri.getLastPathSegment());
                                String[] uriSegments = uri.getLastPathSegment().split("/");
                                saveThumbnail(downloadUri, postId, uriSegments[uriSegments.length - 1], docData);
                            }
                        }
                    }
                });

                Intent intent = new Intent(ACTION_POST_VIDEOS_CHANGED);
                context.sendBroadcast(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: adding post video");
                Toast.makeText(context, "Video \"" + uri.getLastPathSegment() +  "\" failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Bitmap getThumbnailFromVideo(String videoPath) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Log.d(TAG, "getThumbnailFromVideo: " + videoPath);
        mediaMetadataRetriever.setDataSource(videoPath, new HashMap<>());
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        mediaMetadataRetriever.release();
        if (bitmap != null) {
            Bitmap.createScaledBitmap(bitmap, 75, 75, false);
            return bitmap;
        }
        return null;
    }

    private static void saveThumbnail(final Uri uri, String postId, String imageName, final Map<String, Object> docData) {
        String path = uri.toString();
        Bitmap bitmap = getThumbnailFromVideo(path);
        if (bitmap != null) {
            Log.d(TAG, "saveThumbnail: bitmap created");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageReference = storageReference.child("images");
            StorageReference postImagesReference = imageReference.child("thumbnails");
            StorageReference postImageReference = postImagesReference.child(postId);
            StorageReference image = postImageReference.child(imageName + ".png");

            UploadTask uploadTask = image.putBytes(byteArray, imageMetadata);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        return image.getDownloadUrl();
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            docData.put(FIELD_THUMBNAIL_URL, downloadUri.toString());
                            Log.d(TAG, "onComplete: Successfully retrieved thumbnail url " + downloadUri.getLastPathSegment().toString());
                            saveVideoUrls(imageName, docData);
                        }
                    }else {
                        Log.d(TAG, "onComplete: Error getting thumbnail url");
                    }
                }
            });

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Video thumbnail saved successfully");
                    }else {
                        Log.d(TAG, "onComplete: Error saving video thumbnail");
                    }
                }
            });
        }
    }

    private static void saveVideoUrls(String videoName, final Map<String, Object> docData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("videos").document(videoName).set(docData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Video urls saved");
                }else{
                    Log.d(TAG, "onComplete: Error saving video urls");
                }
            }
        });
    }

    public static void getListOfPostVideos(String postId, Context context) {
        final GetPostVideoNamesListener getPostVideoNamesListener = (GetPostVideoNamesListener) context;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference videosReference = storageReference.child("videos");
        StorageReference postsReference = videosReference.child("posts");
        StorageReference listReference = postsReference.child(postId);
        Log.d(TAG, "getListOfPostVideos: here " + postId);

        listReference.list(40)
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d(TAG, "getListOfPostImages: in");

                        List<String> videoNames = new ArrayList<>();

                        for (StorageReference item : listResult.getItems()) {
                            String name = item.getName();
                            String[] splitName = name.split("\\.");
                            videoNames.add(splitName[0]);
                            Log.d(TAG, "onSuccess: getpostimagelistener send");
                            getPostVideoNamesListener.getPostVideoNames(videoNames);
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

    public static void setVideoThumbnail(String videoId, final ImageView imageView) {
        Log.d(TAG, "setVideoThumbnail: " + videoId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("videos").document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Successfully retrieved video data");
                    if (task.getResult() != null) {
                        String url = task.getResult().getString(FIELD_THUMBNAIL_URL);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(url));
                    }
                }else{
                    Log.d(TAG, "onComplete: Error retrieving video data");
                }
            }
        });
    }

    public static void playVideo(String videoId, VideoView videoView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("videos").document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Successfully retrieved video data");
                    if (task.getResult() != null) {
                        String url = task.getResult().getString(FIELD_VIDEO_URL);
                        videoView.setVideoPath(url);
                    }
                }else{
                    Log.d(TAG, "onComplete: Error retrieving video data");
                }
            }
        });
    }

    public static void deletePostVideos(final Context context, String id) {
        Log.d(TAG, "deletePostImages: in");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference videosReference = storageReference.child("videos");
        StorageReference postsReference = videosReference.child("posts");
        StorageReference postImagesReference = postsReference.child(id);
        Log.d(TAG, "deletePostImages: id = " + id);
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
                            final String name = reference.getName();
                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Success deleting images");
                                        String[] splitName = name.split("\\.");
                                        deleteThumbnail(context, id, splitName[0]);
                                        deleteVideoData(splitName[0]);
                                        if (index + 1 == max) {
                                            context.sendBroadcast(intent);
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

    public static void deleteThumbnail(final Context context, final String id, final String imageName) {

        Log.d(TAG, "deleteThumbnail: in");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagesReference = storageReference.child("images");
        StorageReference thumbnailsReference = imagesReference.child("thumbnails");
        StorageReference idReference = thumbnailsReference.child(id);

        Log.d(TAG, "deletePostThumbnail: id = " + id);
        Log.d(TAG, "deleteVideoThumbnail: name = " + imageName);
        deleteStorageItem(context, id, imageName, idReference);
    }

    public static void deleteVideo(final Context context, final String id, final String videoName) {

        Log.d(TAG, "deletePostVideo: in");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference videosReference = storageReference.child("videos");
        StorageReference postsReference = videosReference.child("posts");
        StorageReference postImagesReference = postsReference.child(id);
        Log.d(TAG, "deletePostVideos: id = " + id);
        deleteStorageItem(context, id, videoName, postImagesReference);
        deleteThumbnail(context, id, videoName);
        Log.d(TAG, "deleteVideo: name = " + videoName);
        deleteVideoData(videoName);
    }

    public static void deleteStorageItem(final Context context, final String id, final String itemName, StorageReference storageReference) {
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
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
                        String name = reference.getName();
                        if (name.contains(itemName)) {
                            Log.d(TAG, "onComplete: ref = " + reference.toString());
                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Success deleting items " + index);
                                        getListOfPostVideos(id, context);
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

    public static void deleteVideoData(String videoName) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("videos").document(videoName).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Video data successfully deleted");
                }else {
                    Log.d(TAG, "onComplete: Error deleting video data");
                }
            }
        });
    }
}
