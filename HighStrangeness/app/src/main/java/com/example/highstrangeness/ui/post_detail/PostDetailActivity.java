package com.example.highstrangeness.ui.post_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.main.MainActivity;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG =  "PostDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        if (getIntent() != null) {
            String id = getIntent().getStringExtra(MainActivity.EXTRA_POST_ID);
            Log.d(TAG, "onCreate: " + id);
        }
    }
}