package com.example.highstrangeness.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.example.highstrangeness.ui.post_detail.image_viewer.ImageViewerActivity;
import com.example.highstrangeness.ui.post_detail.video_viewer.VideoViewerActivity;
import com.example.highstrangeness.utilities.ImageStorageUtility;
import com.example.highstrangeness.utilities.VideoStorageUtility;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaAdapterHolder> {

    public static final String TAG = "MediaAdapter";
    String type;
    Context context;
    LayoutInflater layoutInflater;
    List<String> mediaList;
    String id;

    public MediaAdapter(String type, Context context, List<String> mediaList, String postId) {
        this.type = type;
        this.context = context;
        this.mediaList = mediaList;
        this.id = postId;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MediaAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycle_row_media, parent, false);
        return new MediaAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MediaAdapterHolder holder, final int position) {
        switch (type) {
            case "image":
                if (mediaList.size() > 0) {
                    ImageStorageUtility.setPostImage(mediaList.get(position), id, 2, holder.imageView);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ImageViewerActivity.class);
                            intent.putExtra(PostDetailActivity.EXTRA_IMAGE_NAME, mediaList.get(position));
                            intent.putExtra(PostDetailActivity.EXTRA_POST_ID, id);
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case "video":
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
                if (mediaList.size() > 0) {
//                    VideoStorageUtility.setVideoThumbnail(mediaList.get(position), holder.imageView);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, VideoViewerActivity.class);
                            intent.putExtra(VideoViewerActivity.EXTRA_ID, mediaList.get(position));
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case "audio":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class MediaAdapterHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MediaAdapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMediaThumb);
        }
    }
}
