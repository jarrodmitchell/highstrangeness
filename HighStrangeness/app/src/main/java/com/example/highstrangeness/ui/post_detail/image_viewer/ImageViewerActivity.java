package com.example.highstrangeness.ui.post_detail.image_viewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.example.highstrangeness.utilities.ImageStorageUtility;

import java.util.Objects;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Objects.requireNonNull(getSupportActionBar()).hide();

        if (getIntent() != null) {
            String name = getIntent().getStringExtra(PostDetailActivity.EXTRA_IMAGE_NAME);
            String id = getIntent().getStringExtra(PostDetailActivity.EXTRA_POST_ID);
            ImageView imageView = findViewById(R.id.imageViewPostImage);
            setTitle(name);
            ImageStorageUtility.setPostImage(name, id, -1, imageView);
        }
    }
}