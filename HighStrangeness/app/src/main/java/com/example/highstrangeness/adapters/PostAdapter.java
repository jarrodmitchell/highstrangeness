package com.example.highstrangeness.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.list.ListFragment;

import java.util.Calendar;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static final String TAG = "PostAdapter";

    Context context;
    LayoutInflater layoutInflater;
    List<Post> posts;
    static ListFragment.OnItemClickListener mOnItemClickListener;

    public PostAdapter(Context context, List<Post> posts, ListFragment.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.posts = posts;
        mOnItemClickListener = onItemClickListener;
    }



    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageButton buttonUser;
        public TextView textViewUsername;
        public TextView textViewDate;
        public TextView textViewTitle;
        public TextView textViewTags;
        public TextView textViewDescription;
        public TextView textViewContentTypes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            buttonUser = itemView.findViewById(R.id.buttonUserPost);
            textViewUsername = itemView.findViewById(R.id.textViewUserNamePost);
            textViewDate = itemView.findViewById(R.id.textViewDatePost);
            textViewTitle = itemView.findViewById(R.id.textViewTitlePost);
            textViewTags = itemView.findViewById(R.id.textViewTagsPost);
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionPost);
            textViewContentTypes = itemView.findViewById(R.id.textViewContentTypes);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            getAdapterPosition();
            mOnItemClickListener.onClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycle_row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (posts.size() > position) {
            holder.textViewTitle.setText(posts.get(position).getTitle());
            holder.textViewDescription.setText(posts.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
