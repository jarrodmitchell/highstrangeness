package com.example.highstrangeness.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.main.new_post.NewPostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostUtility {
    public static final String TAG = "PostUtility";
    public static final String ACTION_SEND_FILTERED_LIST = "ACTION_SEND_FILTERED_LIST";
    public static final String EXTRA_FILTERED_POSTS = "EXTRA_FILTERED_POSTS";
    public static final String ACTION_SEND_ALERT_POST_DELETED = "ACTION_SEND_ALERT_POST_DELETED";

    public static final String FIELD_UID = "userId";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_FIRST_HAND = "firstHandExperience";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TAGS = "tags";
    public static final String FIELD_POST_DATE = "postDate";
    public static final String FIELD_CONTENT_TYPES = "contentTypes";

    public static void addPost(final String postId, final String title, final boolean firstHand, final Date date, final double latitude,
                               final double longitude, final String description, final String[] tags,
                               final ArrayList<Uri> imageUriList, ArrayList<Uri> audioUriList,
                               ArrayList<Uri> videoUriList, final Context context, List<String> listOldImages) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> docData = new HashMap<>();
        docData.put(FIELD_UID, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        docData.put(FIELD_TITLE, title);
        docData.put(FIELD_FIRST_HAND, firstHand);
        docData.put(FIELD_DATE, new Timestamp(date.getTime()));
        docData.put(FIELD_LATITUDE, latitude);
        docData.put(FIELD_LONGITUDE, longitude);
        docData.put(FIELD_DESCRIPTION, description);
        docData.put(FIELD_POST_DATE, new Timestamp(Calendar.getInstance().getTime().getTime()));
        final List<String> contentTypes = new ArrayList<>();
        if (postId == null) {
            if (imageUriList != null && !imageUriList.isEmpty()) {
                Log.d(TAG, "addPost: add image content type");
                contentTypes.add("Image");
            }
            if (audioUriList != null && !audioUriList.isEmpty()) {
                contentTypes.add("Audio");
            }
            if (videoUriList != null && !videoUriList.isEmpty()) {
                contentTypes.add("Video");
            }
        }else {
            if ((listOldImages != null && listOldImages.size() > 0) || (imageUriList != null && !imageUriList.isEmpty()) ) {
                contentTypes.add("Image");
            }
        }

        docData.put(FIELD_CONTENT_TYPES, contentTypes);
        if (tags != null && tags.length > 0) {
            docData.put(FIELD_TAGS, Arrays.asList(tags));
        }

            Log.d(TAG, "addPost: in");
            final String id = (postId != null) ? postId : db.collection("posts").document().getId();
            db.collection("posts").document(id)
                    .set(docData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(context, "Post Created", Toast.LENGTH_SHORT).show();

                            if (imageUriList != null && imageUriList.size() > 0) {
                                for (Uri uri : imageUriList) {
                                    StorageUtility.updatePostImages(uri, id, context);
                                }
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(NewPostActivity.ACTION_SEND_ADD_POST_RESULT);
                    intent.putExtra("success", true);
                    Post post = new Post(id, (String) docData.get(FIELD_UID), title, firstHand,
                            date, latitude, longitude, description,
                            Lists.newArrayList(tags != null ? tags : new String[0]), Lists.newArrayList(contentTypes));
                    intent.putExtra(MainActivity.EXTRA_POST, post);
                    context.sendBroadcast(intent);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error writing document", e);
                            Toast.makeText(context, "Error creating post", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewPostActivity.ACTION_SEND_ADD_POST_RESULT);
                            intent.putExtra("success", false);
                            context.sendBroadcast(intent);
                        }
                    });
    }

    public interface SetPostListListener {
        void setPostListener(List<Post> posts);
    }

    public static void getAllPosts(final Context context)  {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final SetPostListListener setPostListListener = (SetPostListListener) context;

        final List<Post> posts = new ArrayList<>();

        db.collection("posts").orderBy(FIELD_POST_DATE, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        final int max = task.getResult().size();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final Post post = getPost(document);

                            db.collection("user").document(post.getUserId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null) {
                                                    String username = task.getResult().getString("username");
                                                    post.setUsername(username);
                                                    posts.add(post);
                                                    if (max - 1 == posts.size()) {
                                                        setPostListListener.setPostListener(posts);
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                            Log.d(TAG, "onComplete: posts count: " + posts.size());
                        }
                    }
                }
            }
        });

    }

    public static void getMyPosts(final Context context, String uid) {
        Log.d(TAG, "getMyPosts: get mines");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id;
        if (uid == null) {
            id = Objects.requireNonNull(currentUser).getUid();
        }else{
            id = uid;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").whereEqualTo(FIELD_UID, id).orderBy(FIELD_POST_DATE, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: complete");
                if (task.isSuccessful()) {
                    parsePosts(context, task);
                }else {
                    if (task.getException() != null) {
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                    }
                }
            }
        });
    }

    private static void parsePosts(final Context context, Task<QuerySnapshot> task) {

        final ArrayList<Post> posts = new ArrayList<>();

        Log.d(TAG, "parsePosts: go");
        if (task.isSuccessful()) {
            Log.d(TAG, "parsePosts: success");
            if (task.getResult() != null) {
                final int max = task.getResult().getDocuments().size();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    final Post post = getPost(document);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user").document(post.getUserId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult() != null) {
                                            String username = task.getResult().getString("username");
                                            post.setUsername(username);
                                            posts.add(post);
                                            if (max == posts.size()) {
                                                Log.d(TAG, "parsePosts: um send");
                                                Intent intent = new Intent(ACTION_SEND_FILTERED_LIST);
                                                intent.putExtra(EXTRA_FILTERED_POSTS, posts);
                                                context.sendBroadcast(intent);
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                    Log.d(TAG, "onComplete: posts count: " + posts.size());
                }
            }
        } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
        }
    }

    private static Post getPost(QueryDocumentSnapshot document) {
        String id = document.getId();
        String userId = (String) document.getData().get(FIELD_UID);
        String title = (String) document.getData().get(FIELD_TITLE);
        Log.d(TAG, "onComplete: " + title);
        boolean firstHand = (boolean) document.getData().get(FIELD_FIRST_HAND);
        com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) document.getData().get(FIELD_DATE);
        assert timestamp != null;
        Date date = timestamp.toDate();
        double latitude = (double) document.getData().get(FIELD_LATITUDE);
        double longitude = (double) document.getData().get(FIELD_LONGITUDE);
        String description = (String) document.getData().get(FIELD_DESCRIPTION);
        ArrayList<String> tags = (ArrayList<String>) document.getData().get(FIELD_TAGS);
        ArrayList<String> contentTypes = (ArrayList<String>) document.getData().get(FIELD_CONTENT_TYPES);
        Log.d(TAG, "parsePosts: " + contentTypes.size());

        return new Post(id, userId, title, firstHand, date, latitude, longitude, description, tags, contentTypes);
    }

    public static void deletePost(final Context context, final String id, final ArrayList<String> contentTypes ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (contentTypes.contains("Image")) {
                        StorageUtility.deletePostImages(context, id);
                    }else {
                        Intent intent = new Intent(ACTION_SEND_ALERT_POST_DELETED);
                        context.sendBroadcast(intent);
                    }
                }else {
                    Log.d(TAG, "onComplete: "  + id);
                }
            }
        });
    }
}
