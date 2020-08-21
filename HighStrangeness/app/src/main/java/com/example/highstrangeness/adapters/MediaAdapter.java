package com.example.highstrangeness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
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
    public void onBindViewHolder(@NonNull MediaAdapterHolder holder, int position) {
        if (postImageNames.size() > 0) {
            StorageUtility.setPostImage(postImageNames.get(position), id, 2, holder.imageView);
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
