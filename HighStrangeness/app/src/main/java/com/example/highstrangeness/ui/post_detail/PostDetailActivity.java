package com.example.highstrangeness.ui.post_detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.MediaAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.account.AccountActivity;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.main.new_post.NewPostActivity;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.PostUtility;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostDetailActivity extends AppCompatActivity implements LocationUtility.ReturnAddressListener, StorageUtility.GetPostImageNamesListener {

    public static final String TAG =  "PostDetailActivity";
    public static final String EXTRA_IMAGE_NAME = "EXTRA_IMAGE_NAME";
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";
    public static final String EXTRA_OLD_POST = "EXTRA_OLD_POST";
    public static final int REQUEST_CODE_EDIT_POST = 0x87;

    @Override
    public void getPostImagesName(List<String> imageNames) {
        MediaAdapter adapter = new MediaAdapter("image", this, imageNames, post.getId());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void returnAddress(Address address) {
        ((Button) findViewById(R.id.buttonLocationPostDetail)).setText(address.getAddressLine(0));
    }

    PostDeletedReceiver postDeletedReceiver;
    PostMediaChangedReceiver postMediaChangedReceiver;
    RecyclerView recyclerView;
    Post post;
    ImageView imageViewUserPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setTitle("");
        postDeletedReceiver = new PostDeletedReceiver();
        postMediaChangedReceiver = new PostMediaChangedReceiver();

        imageViewUserPic = findViewById(R.id.imageViewUserPostDetail);
        imageViewUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    Intent intent = new Intent(PostDetailActivity.this, AccountActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(PostDetailActivity.this, AccountActivity.class);
                    intent.putExtra("uid", post.getUserId());
                    startActivity(intent);
                }
            }
        });

        if (getIntent() != null) {
            setViews(getIntent());
        }
    }

    private void setViews(Intent intent) {
        post = intent.getParcelableExtra(MainActivity.EXTRA_POST);
        if (post != null) {
            LocationUtility.getAddress(this, new LatLng(post.getLatitude(), post.getLongitude()), this);

            recyclerView = findViewById(R.id.recyclerViewImagesPostDetail);
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(PostDetailActivity.this,
                            LinearLayoutManager.HORIZONTAL,
                            false));
            StorageUtility.getListOfPostImages(post.getId(), this);

            String id = post.getUserId();
            StorageUtility.setProfileImage(this, id, 1, imageViewUserPic);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(post.getUsername());
            stringBuilder.append(" ·");
            String username = stringBuilder.toString();
            String date = DateFormat.getDateInstance().format(post.getDate());
            String title = post.getTitle();
            stringBuilder = new StringBuilder();

            ArrayList<String> tags = post.getTags();
            for (int i = 0; i < tags.size(); i ++) {
                stringBuilder.append(tags.get(i));
                if (i != tags.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            String tagsString = stringBuilder.toString();
            String description = post.getDescription();

            TextView textViewUsername = findViewById(R.id.textViewUserNamePostDetail);

            if (textViewUsername.getText().toString().isEmpty()) {
                textViewUsername.setText(username);
            }
            ((TextView) findViewById(R.id.textViewDatePostDetail)).setText(date);
            ((TextView) findViewById(R.id.textViewTitlePostDetail)).setText(title);
            ((TextView) findViewById(R.id.textViewTagsPostDetail)).setText(tagsString);
            ((TextView) findViewById(R.id.textViewDescriptionPostDetail)).setText(description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && post.getUserId().equals(currentUser.getUid())) {
            getMenuInflater().inflate(R.menu.my_post_menu, menu);
        }else {
            getMenuInflater().inflate(R.menu.post_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_delete:
                showDeletePostDialog();
                break;
            case R.id.item_edit:
                navigateToNewPostActivity();
                break;
            case R.id.item_favorite:
                break;
            case R.id.item_report:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(postDeletedReceiver, new IntentFilter(PostUtility.ACTION_SEND_ALERT_POST_DELETED));
        registerReceiver(postMediaChangedReceiver, new IntentFilter(StorageUtility.ACTION_POST_IMAGES_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(postDeletedReceiver);
        unregisterReceiver(postMediaChangedReceiver);
    }

    private void navigateToNewPostActivity() {
        Intent intent = new Intent(PostDetailActivity.this, NewPostActivity.class);
        intent.putExtra(EXTRA_OLD_POST, post);
        startActivityForResult(intent, REQUEST_CODE_EDIT_POST);
    }

    private void showDeletePostDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete Post");
        alertDialog.setMessage("Are you sure that you want to delete the post?");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (Message) null);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePost();
            }
        });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            }
        });
        alertDialog.show();
    }

    private void deletePost() {
        PostUtility.deletePost(this, post.getId(), post.getContentTypes());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==  REQUEST_CODE_EDIT_POST && data != null) {
            setViews(data);
        }
        updateMedia();
    }

    private void closeActivity() {
        setResult(RESULT_OK);
        finish();
    }

    private class PostDeletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null &&
                    intent.getAction().equals(PostUtility.ACTION_SEND_ALERT_POST_DELETED)) {
                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
//                PostUtility.getAllPosts(context);
                closeActivity();
            }
        }
    }

    private class PostMediaChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null &&
                    intent.getAction().equals(StorageUtility.ACTION_POST_IMAGES_CHANGED)) {
                updateMedia();
            }
        }
    }

    private void updateMedia() {
        Log.d(TAG, "updateMedia: ");
        StorageUtility.getListOfPostImages(post.getId(), this);
    }

}