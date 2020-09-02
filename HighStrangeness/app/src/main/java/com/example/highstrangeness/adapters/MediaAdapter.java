package com.example.highstrangeness.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.example.highstrangeness.ui.post_detail.image_viewer.ImageViewerActivity;
import com.example.highstrangeness.utilities.StorageUtility;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaAdapterHolder> {

    public static final String TAG = "MediaAdapter";
    String type;
    Context context;
    LayoutInflater layoutInflater;
    List<String> postImageNames;
    String id;

    public MediaAdapter(String type, Context context, List<String> postImageNames, String postId) {
        this.type = type;
        this.context = context;
        this.postImageNames = postImageNames;
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
        if (postImageNames.size() > 0) {
            StorageUtility.setPostImage(postImageNames.get(position), id, 2, holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra(PostDetailActivity.EXTRA_IMAGE_NAME, postImageNames.get(position));
                    intent.putExtra(PostDetailActivity.EXTRA_POST_ID, id);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postImageNames.size();
    }

    public static class MediaAdapterHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MediaAdapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMediaThumb);
        }
    }
}
