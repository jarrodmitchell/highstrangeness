package com.example.highstrangeness.ui.post_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.MediaAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity implements LocationUtility.GetAddressAsyncTask.ReturnAddressListener, StorageUtility.GetPostImageNamesListener {

    public static final String TAG =  "PostDetailActivity";

    @Override
    public void getPostImagesName(List<String> imageNames) {
        MediaAdapter adapter = new MediaAdapter("image", this, imageNames, postId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void returnAddress(Address address) {
        ((Button) findViewById(R.id.buttonLocationPostDetail)).setText(address.getAddressLine(0));
    }

    RecyclerView recyclerView;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setTitle("");

        if (getIntent() != null) {
            Post post = getIntent().getParcelableExtra(MainActivity.EXTRA_POST);
            if (post != null) {
                postId = post.getId();

                LocationUtility.GetAddressAsyncTask getAddressAsyncTask = new LocationUtility.GetAddressAsyncTask(this);
                getAddressAsyncTask.execute(new LatLng(post.getLatitude(), post.getLongitude()));

                recyclerView = findViewById(R.id.recyclerViewImagesPostDetail);
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(PostDetailActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                StorageUtility.getListOfPostImages(postId, this);

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
}