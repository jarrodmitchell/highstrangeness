package com.example.highstrangeness.ui.account.my_posts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.account.FilteredPostListFragment;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MyPostsActivity extends AppCompatActivity implements FilteredPostListFragment.FilteredOnItemClickListener {

    public static final String TAG = "MyPostsActivity";
    public static final String EXTRA_POST = "EXTRA_POST";
    public static final int REQUEST_CODE = 0x113;

    String uid;

    @Override
    public void onClick(Post post) {
        Intent intent = new Intent(MyPostsActivity.this, PostDetailActivity.class);
        intent.putExtra(EXTRA_POST, post);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        if (getIntent() != null) {
            String title = getIntent().getStringExtra("title");
            setTitle(title);
            if (getIntent().hasExtra("uid")) {
                uid = getIntent().getStringExtra("uid");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference documentReference = db.collection("user").document(uid);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null &&
                                task.getResult().getData() != null) {
                            Log.d(TAG, "onComplete: success");
                            String username = (String) task.getResult().getData().get("username");
                            if (username != null) {
                                setTitle(username + "'s Posts");
                            }
                        }
                    }
                });
            }
        }
        loadFragment();
    }

    private void loadFragment() {
        if (uid == null && !getTitle().equals("Favorites")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMyPosts, new FilteredPostListFragment()).commit();
        }else if (uid == null && getTitle().equals("Favorites")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMyPosts, FilteredPostListFragment.newInstance(
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), true)).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMyPosts, FilteredPostListFragment.newInstance(uid, false)).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            loadFragment();
        }
    }
}