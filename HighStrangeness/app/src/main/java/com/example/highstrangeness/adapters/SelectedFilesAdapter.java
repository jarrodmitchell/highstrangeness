package com.example.highstrangeness.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.post.PostPt2Fragment;

import java.util.List;

public class SelectedFilesAdapter extends RecyclerView.Adapter<SelectedFilesAdapter.FileViewHolder> {

    public static final String TAG = "SelectedFilesAdapter";

    private Context context;
    private LayoutInflater inflater;
    private List<Uri> fileUris;
    private String list;

    public SelectedFilesAdapter(Context context, List<Uri> fileUris, String list) {
        Log.d(TAG, "SelectedFilesAdapter: in");
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fileUris = fileUris;
        this.list = list;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Button button;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textViewFileName);
            this.button = itemView.findViewById(R.id.buttonRemoveFile);
        }
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_row_add_post, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, final int position) {
        if (fileUris.size() >= position) {
            holder.textView.setText(fileUris.get(position).getLastPathSegment());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW_NEW_POST);
                    intent.putExtra(PostPt2Fragment.EXTRA_IMAGE_STRING_LIST, list);
                    intent.putExtra(PostPt2Fragment.EXTRA_FILE_INDEX, position);
                    context.sendBroadcast(intent);
                    Log.d(TAG, "onClick: where");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fileUris.size();
    }
}
