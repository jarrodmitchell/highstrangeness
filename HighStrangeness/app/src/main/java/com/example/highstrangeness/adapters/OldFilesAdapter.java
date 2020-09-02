package com.example.highstrangeness.adapters;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.highstrangeness.ui.post.PostPt2Fragment;
import com.example.highstrangeness.utilities.StorageUtility;

import java.util.List;

public class OldFilesAdapter extends RecyclerView.Adapter<OldFilesAdapter.OldFilesHolder> {

    String type;
    Context context;
    LayoutInflater layoutInflater;
    List<String> postFileNames;
    String id;

    public OldFilesAdapter(String type, Context context, List<String> postFileNames, String postId) {
        this.type = type;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.postFileNames = postFileNames;
        this.id = postId;
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
        if (postFileNames.size() > position) {
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
                                StorageUtility.deleteImage(context, id, postFileNames.get(position));
                                postFileNames.remove(position);
                            }
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
