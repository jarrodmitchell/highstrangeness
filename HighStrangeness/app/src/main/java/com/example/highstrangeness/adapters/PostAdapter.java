package com.example.highstrangeness.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.account.FilteredPostListFragment;
import com.example.highstrangeness.ui.account.my_posts.MyPostsActivity;
import com.example.highstrangeness.ui.main.list.ListFragment;
import com.example.highstrangeness.utilities.StorageUtility;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static final String TAG = "PostAdapter";

    private LayoutInflater layoutInflater;
    private static List<Post> posts;
    private static ListFragment.OnItemClickListener mOnItemClickListener = null;
    private static FilteredPostListFragment.FilteredOnItemClickListener onFilteredItemClickListener = null;

    public PostAdapter(Context context, List<Post> posts, ListFragment.OnItemClickListener onItemClickListener,
                       FilteredPostListFragment.FilteredOnItemClickListener filteredOnItemClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        PostAdapter.posts = posts;
        mOnItemClickListener = onItemClickListener;
        onFilteredItemClickListener = filteredOnItemClickListener;
    }



    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageViewUserPic;
        public TextView textViewUsername;
        public TextView textViewDate;
        public TextView textViewTitle;
        public TextView textViewTags;
        public TextView textViewDescription;
        public TextView textViewContentTypes;
        public TextView textViewFirstHand;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageViewUserPic = itemView.findViewById(R.id.imageViewUserPost);
            textViewUsername = itemView.findViewById(R.id.textViewUserNamePost);
            textViewDate = itemView.findViewById(R.id.textViewDatePost);
            textViewTitle = itemView.findViewById(R.id.textViewTitlePost);
            textViewTags = itemView.findViewById(R.id.textViewTagsPost);
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionPost);
            textViewContentTypes = itemView.findViewById(R.id.textViewContentTypes);
            textViewFirstHand = itemView.findViewById(R.id.textViewFirstHandPost);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            getAdapterPosition();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(posts.get(getAdapterPosition()));
            }else {
                onFilteredItemClickListener.onClick(posts.get(getAdapterPosition()));
            }
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
            StorageUtility.setProfileImage(posts.get(position).getUserId(), 0, holder.imageViewUserPic);

            StringBuilder stringBuilder = new StringBuilder();
            Log.d(TAG, "onBindViewHolder: username" + posts.get(position).getUsername());
            Log.d(TAG, "onBindViewHolder: username" + posts.get(position).getUserId());
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
            SpannableString spannableString = new SpannableString(tagsString);
            spannableString.setSpan(new UnderlineSpan(), 0, tagsString.length(), 0);
            String description = posts.get(position).getDescription();

            ArrayList<String> contentTypesList = posts.get(position).getContentTypes();
            stringBuilder = new StringBuilder();

            Log.d(TAG, "onBindViewHolder: content type size " + contentTypesList.size());
            for (int i = 0; i < contentTypesList.size(); i++) {
                stringBuilder.append(contentTypesList.get(i));
                if (i != contentTypesList.size() - 1) {
                    stringBuilder.append(" · ");
                }
            }
            String contentTypes = stringBuilder.toString();
            boolean firstHand = posts.get(position).isFirstHand();

            holder.textViewUsername.setText(username);
            holder.textViewDate.setText(date);

            holder.textViewTitle.setText(title);
            if (!spannableString.toString().isEmpty()) {
                holder.textViewTags.setVisibility(View.VISIBLE);
                holder.textViewTags.setText(spannableString);
            }else {
                holder.textViewTags.setVisibility(View.GONE);
            }

            holder.textViewDescription.setText(description);
            if (!contentTypes.trim().isEmpty()) {
                holder.textViewContentTypes.setVisibility(View.VISIBLE);
                holder.textViewContentTypes.setText(contentTypes);
            }else {
                holder.textViewContentTypes.setVisibility(View.GONE);
            }

            if(firstHand) {
                holder.textViewFirstHand.setVisibility(View.VISIBLE);
            }else {
                holder.textViewFirstHand.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (posts != null) {
            return posts.size();
        }
        return 0;
    }
}
