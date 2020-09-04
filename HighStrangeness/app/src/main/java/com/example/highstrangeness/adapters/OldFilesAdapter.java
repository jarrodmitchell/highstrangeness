package com.example.highstrangeness.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.ImageStorageUtility;
import com.example.highstrangeness.utilities.VideoStorageUtility;

import java.util.List;

public class OldFilesAdapter extends RecyclerView.Adapter<OldFilesAdapter.OldFilesHolder> {
    public static final String TAG = "OldFilesAdapter";

    String type;
    Context context;
    LayoutInflater layoutInflater;
    List<String> postFileNames;
    String id;

    public OldFilesAdapter(String type, Context context, List<String> postFileNames, String postId) {
        Log.d(TAG, "OldFilesAdapter: in");
        this.type = type;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.postFileNames = postFileNames;
        this.id = postId;
        Log.d(TAG, "OldFilesAdapter: size = " + postFileNames.size());
    }

    public static class OldFilesHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button button;

        public OldFilesHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textViewFileName);
            this.button = itemView.findViewById(R.id.buttonRemoveFile);
        }
    }

    @NonNull
    @Override
    public OldFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycle_row_add_post, parent, false);
        return new OldFilesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OldFilesHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: position = " + position);

        if (postFileNames.size() > position) {
            Log.d(TAG, "onBindViewHolder: name = " + postFileNames.get(position));
            holder.textView.setText(postFileNames.get(position));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Are you sure you want to delete this file?");
                    alertDialog.setMessage("This is a permanent action");
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (Message) null);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (type.equals("images")) {
                                ImageStorageUtility.deleteImage(context, id, postFileNames.get(position));
                            }else if (type.equals("videos")) {
                                VideoStorageUtility.deleteVideo(context, id, postFileNames.get(position));
                            }
                            postFileNames.remove(position);
                        }
                    });
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.pink));
                            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postFileNames.size();
    }
}
