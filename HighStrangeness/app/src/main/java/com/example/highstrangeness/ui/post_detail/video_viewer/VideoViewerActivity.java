package com.example.highstrangeness.ui.post_detail.video_viewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.VideoStorageUtility;

import java.lang.reflect.Member;
import java.util.Objects;

public class VideoViewerActivity extends AppCompatActivity {

    public static final String TAG = "VideoViewerActivity";
    public static final String EXTRA_ID = "EXTRA_ID";

    VideoView videoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);

        Objects.requireNonNull(getSupportActionBar()).hide();

        if (getIntent() != null) {
            String id = getIntent().getStringExtra(EXTRA_ID);
            if (id != null) {
                Log.d(TAG, "onCreate: ");
                videoView = findViewById(R.id.videoViewPostVideo);
                mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);
                videoView.start();
                VideoStorageUtility.playVideo(id, videoView);
            }
        }
    }
}