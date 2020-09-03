package com.example.highstrangeness.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Filter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.ui.account.AccountActivity;
import com.example.highstrangeness.ui.main.filter.FilterActivity;
import com.example.highstrangeness.ui.main.list.ListFragment;
import com.example.highstrangeness.ui.main.map.MapFragment;
import com.example.highstrangeness.ui.main.media_capture.MediaCaptureFragment;
import com.example.highstrangeness.ui.main.new_post.NewPostActivity;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.example.highstrangeness.ui.user_auth.UserAuthActivity;
import com.example.highstrangeness.utilities.PostUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements PostUtility.SetPostListListener,
        ListFragment.GetPostsListener, ListFragment.OnItemClickListener, MapFragment.GetPostsListener,
        MapFragment.OnInfoViewClickListener {
    
    public static final String TAG = "MainActivity";
    public static final String EXTRA_POST = "EXTRA_POST";
    public static final String ACTION_LIST_UPDATED = "ACTION_LIST_UPDATED";
    public static final String FRAG_ID_MAP = "FRAG_ID_MAP";
    public static final String FRAG_ID_LIST = "FRAG_ID_LIST";
    public static final String FRAG_ID_MEDIA_CAP = "FRAG_ID_MEDIA_CAP";
    public static final int REQUEST_CODE_ACCOUNT_SCREEN = 0x034;


    @Override
    public void onClick(Post post) {
        Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
        intent.putExtra(EXTRA_POST, post);
        startActivity(intent);
    }

    @Override
    public List<Post> getPosts() {
        return postList;
    }

    @Override
    public void setPostListener(List<Post> posts) {
        if (Filter.filter != null) {
            postList = posts.stream().filter(new Predicate<Post>() {
                @Override
                public boolean test(Post post) {
                    if (Filter.filter.getTag() != null && !post.getTags().contains(Filter.filter.getTag())) {
                        return false;
                    }
                    if (Filter.filter.getStartDate() != null && post.getDate().getTime() < Filter.filter.getStartDate().getTime()) {
                        return false;
                    }
                    if (Filter.filter.getEndDate() != null && post.getDate().getTime() > Filter.filter.getEndDate().getTime()) {
                        return false;
                    }
                    if (Filter.filter.isHasImages() && !post.getContentTypes().contains("Image")) {
                        return false;
                    }
                    if (Filter.filter.isHasAudio() && !post.getContentTypes().contains("Audio")) {
                        return false;
                    }
                    if (Filter.filter.isHasVideo() && !post.getContentTypes().contains("Video")) {
                        return false;
                    }

                    return true;
                }
            }).collect(Collectors.toCollection(ArrayList<Post>::new));
        }else {
            postList = posts;
        }
        Intent intent = new Intent(ACTION_LIST_UPDATED);
        Log.d(TAG, "setPostListener: send");
        sendBroadcast(intent);
    }


    FloatingActionButton fab;
    List<Post> postList;
    BottomNavigationView navView;
    ListFragment listFragment = new ListFragment();
    MapFragment mapFragment = new MapFragment();
    MediaCaptureFragment mediaCaptureFragment = new MediaCaptureFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postList = new ArrayList<>();
        setTitle("");

        fab = findViewById(R.id.fabAddPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_nav,  mediaCaptureFragment, FRAG_ID_MEDIA_CAP);
        fragmentTransaction.add(R.id.frame_layout_nav, listFragment, FRAG_ID_LIST);
        fragmentTransaction.add(R.id.frame_layout_nav, mapFragment, FRAG_ID_MAP);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_media_capture:
                        getSupportActionBar().setShowHideAnimationEnabled(false);
                        Objects.requireNonNull(getSupportActionBar()).hide();
                        displayFragment(mediaCaptureFragment);
                        return true;
                    case R.id.navigation_map:
                        getSupportActionBar().setShowHideAnimationEnabled(false);
                        Objects.requireNonNull(getSupportActionBar()).show();
                        displayFragment(mapFragment);
                        return true;
                    case R.id.navigation_list:
                        getSupportActionBar().setShowHideAnimationEnabled(false);
                        Objects.requireNonNull(getSupportActionBar()).show();
                        displayFragment(listFragment);
                        return true;
                }
                return false;
            }
        });
        navView.setSelectedItemId(R.id.navigation_map);
    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_nav, fragment, null).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PostUtility.getAllPosts(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        if (Filter.filter != null) {

        }
        searchView.setQueryHint("Search Location...");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_account:
                Intent intentAccount = new Intent(MainActivity.this, AccountActivity.class);
                startActivityForResult(intentAccount, REQUEST_CODE_ACCOUNT_SCREEN);
                break;
            case R.id.app_bar_filter:
                Intent intentFilter = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intentFilter);
                break;
            case R.id.app_bar_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: result");
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ACCOUNT_SCREEN) {
                FirebaseAuth.getInstance().signOut();
                User.currentUser = null;
                setResult(UserAuthActivity.REQUEST_CODE_LOGGED_OUT);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mapFragment.isVisible()) {
            finish();
            moveTaskToBack(true);
        }
        navView.setSelectedItemId(R.id.navigation_map);
    }
}