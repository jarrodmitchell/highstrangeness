package com.example.highstrangeness.ui.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.PostAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.list.ListFragment;
import com.example.highstrangeness.utilities.PostUtility;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class FilteredPostListFragment extends Fragment {

    public static final String TAG = "FilteredPostListFragment";

    public static FilteredPostListFragment newInstance(String uid) {

        Bundle args = new Bundle();
        args.putString("uid", uid);
        FilteredPostListFragment fragment = new FilteredPostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FilteredPostListFragment() {
    }

    public interface FilteredOnItemClickListener {
        void onClick(Post post);
    }

    FilteredOnItemClickListener filteredOnItemClickListener;
    GetFilteredPostsReceiver getFilteredPostsReceiver;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_posts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null)  {
            filteredOnItemClickListener = (FilteredOnItemClickListener) getActivity();
            getFilteredPostsReceiver = new GetFilteredPostsReceiver();
            context = getActivity();
//            ((SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshPostsMain));
            getActivity().findViewById(R.id.buttonNewPostsMain).setVisibility(View.GONE);

            recyclerView = getActivity().findViewById(R.id.recyclerViewPostsMain);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            if (getArguments() != null && getArguments().getString("uid") == null) {
                PostUtility.getMyPosts(getActivity(), null);
            }else{
                PostUtility.getMyPosts(getActivity(), getArguments().getString("uid"));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(getFilteredPostsReceiver, new IntentFilter(PostUtility.ACTION_SEND_FILTERED_LIST));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(getFilteredPostsReceiver);
    }

    public class GetFilteredPostsReceiver  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: not  yet");
            if (intent != null && intent.getAction() != null && intent.getAction().equals(PostUtility.ACTION_SEND_FILTERED_LIST)) {
                Log.d(TAG, "onReceive: got");
                List<Post> posts = intent.getParcelableArrayListExtra(PostUtility.EXTRA_FILTERED_POSTS);
                if (posts != null) {
                    for (Post post: posts) {
                        Log.d(TAG, "onReceive: " + post.getTags().size());
                    }
                    Log.d(TAG, "onReceive: post count " + posts.size());
                    recyclerView.setAdapter(new PostAdapter(getActivity(), posts, null, filteredOnItemClickListener));
                }
            }
        }
    }
}
