package com.example.highstrangeness.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.new_post.NewPostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.DocumentTransform;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PostUtility {
    public static final String TAG = "PostUtility";

    public static final String FIELD_UID = "userId";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_FIRST_HAND = "firstHandExperience";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TAGS = "tags";

    public static void addPost(String title, boolean firstHand, Date date, double latitude,
                               double longitude, String description, String[] tags,
                               final ArrayList<Uri> imageUri, ArrayList<Uri> audioUri,
                               ArrayList<Uri> videoUri, final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> docData = new HashMap<>();
        docData.put(FIELD_UID, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        docData.put(FIELD_TITLE, title);
        docData.put(FIELD_FIRST_HAND, firstHand);
        docData.put(FIELD_DATE, new Timestamp(date.getTime()));
        docData.put(FIELD_LATITUDE, latitude);
        docData.put(FIELD_LONGITUDE, longitude);
        docData.put(FIELD_DESCRIPTION, description);
        if (tags != null && tags.length > 0) {
            docData.put(FIELD_TAGS, Arrays.asList(tags));
        }

        if (imageUri != null && imageUri.size() > 0) {
            final String id = db.collection("posts").document().getId();
            db.collection("posts").document(id)
                    .set(docData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(context, "Post Created", Toast.LENGTH_SHORT).show();

                            for (Uri uri : imageUri) {
                                StorageUtility.updatePostImages(uri, id, context);
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(NewPostActivity.ACTION_SEND_ADD_POST_RESULT);
                    intent.putExtra("success", true);
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
    }

    public interface SetPostListListener {
        void setPostListener(List<Post> posts);
    }

    public static void getAllPosts(Context context)  {
        final SetPostListListener setPostListListener = (SetPostListListener) context;

        final List<Post> posts = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    String userId = (String) document.getData().get(FIELD_UID);
                                    String title = (String) document.getData().get(FIELD_TITLE);
                                    Log.d(TAG, "onComplete: " + title);
                                    boolean firstHand = (boolean) document.getData().get(FIELD_FIRST_HAND);
                                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) document.getData().get(FIELD_DATE);
                                    Date date = new Date();
                                    if (timestamp != null) {
                                        date = timestamp.toDate();
                                    }
                                    double latitude = (double) document.getData().get(FIELD_LATITUDE);
                                    double longitude = (double) document.getData().get(FIELD_LONGITUDE);
                                    String description = (String) document.getData().get(FIELD_DESCRIPTION);
                                    ArrayList<String> tags = (ArrayList<String>) document.getData().get(FIELD_TAGS);

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    posts.add(new Post(id, userId, title, firstHand, date, latitude, longitude, description, tags));
                                    Log.d(TAG, "onComplete: posts count: " + posts.size());
                                }
                                setPostListListener.setPostListener(posts);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
