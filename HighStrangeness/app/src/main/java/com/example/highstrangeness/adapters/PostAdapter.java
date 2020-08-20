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
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        if (posts.size() > position) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(posts.get(position).getUsername());
            stringBuilder.append(" ·");
            String username = stringBuilder.toString();
            String date = DateFormat.getDateInstance().format(posts.get(position).getDate());
            String title = posts.get(position).getTitle();
            stringBuilder = new StringBuilder();

            ArrayList<String> tags = posts.get(position).getTags();
            for (int i = 0; i < tags.size(); i ++) {
                stringBuilder.append(tags.get(i));
                if (i != tags.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            String tagsString = stringBuilder.toString();
            String description = posts.get(position).getDescription();

            holder.textViewUsername.setText(username);
            holder.textViewDate.setText(date);
            holder.textViewTitle.setText(title);
            holder.textViewTags.setText(tagsString);
            holder.textViewDescription.setText(description);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
