package com.example.highstrangeness.ui.post_detail.video_viewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.highstrangeness.R;

public class VideoViewerActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);

        videoView = findViewById(R.id.videoViewPostVideo);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}