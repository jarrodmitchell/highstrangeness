package com.example.highstrangeness.ui.post_detail;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.MediaAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.PostUtility;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity implements LocationUtility.GetAddressAsyncTask.ReturnAddressListener, StorageUtility.GetPostImageNamesListener {

    public static final String TAG =  "PostDetailActivity";
    public static final String EXTRA_IMAGE_NAME = "EXTRA_IMAGE_NAME";
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";

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
    RecyclerView recyclerView;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setTitle("");
        postDeletedReceiver = new PostDeletedReceiver();

        if (getIntent() != null) {
            post = getIntent().getParcelableExtra(MainActivity.EXTRA_POST);
            if (post != null) {

                LocationUtility.GetAddressAsyncTask getAddressAsyncTask = new LocationUtility.GetAddressAsyncTask(this);
                getAddressAsyncTask.execute(new LatLng(post.getLatitude(), post.getLongitude()));

                recyclerView = findViewById(R.id.recyclerViewImagesPostDetail);
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(PostDetailActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                StorageUtility.getListOfPostImages(post.getId(), this);

                String id = post.getUserId();
                ImageView imageViewUserPic = findViewById(R.id.imageViewUserPostDetail);
                StorageUtility.setProfileImage(id, 1, imageViewUserPic);

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

                ((TextView) findViewById(R.id.textViewUserNamePostDetail)).setText(username);
                ((TextView) findViewById(R.id.textViewDatePostDetail)).setText(date);
                ((TextView) findViewById(R.id.textViewTitlePostDetail)).setText(title);
                ((TextView) findViewById(R.id.textViewTagsPostDetail)).setText(tagsString);
                ((TextView) findViewById(R.id.textViewDescriptionPostDetail)).setText(description);
            }
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
                break;
            case R.id.item_favorite:
                break;
            case R.id.item_report:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(postDeletedReceiver, new IntentFilter(PostUtility.ACTION_SEND_ALERT_POST_DELETED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(postDeletedReceiver);
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
        PostUtility.deletePost(this, post.getId());
    }

    private class PostDeletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null &&
                    intent.getAction().equals(PostUtility.ACTION_SEND_ALERT_POST_DELETED)) {
                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                Intent intentResult = new Intent();
                PostDetailActivity.this.setResult(RESULT_OK, intentResult);
                PostDetailActivity.this.finish();
            }
        }
    }

}