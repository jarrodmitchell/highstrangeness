package com.example.highstrangeness.ui.account.my_posts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.account.FilteredPostListFragment;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;

public class MyPostsActivity extends AppCompatActivity implements FilteredPostListFragment.FilteredOnItemClickListener {

    public static final String TAG = "MyPostsActivity";
    public static final String EXTRA_POST = "EXTRA_POST";
    public static final int REQUEST_CODE = 0x113;

    @Override
    public void onClick(Post post) {
        Intent intent = new Intent(MyPostsActivity.this, PostDetailActivity.class);
        intent.putExtra(EXTRA_POST, post);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        setTitle("Posts");
        loadFragment();
    }

    private void loadFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMyPosts, new FilteredPostListFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            loadFragment();
        }
    }
}