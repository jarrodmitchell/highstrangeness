package com.example.highstrangeness.ui.main.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.PostAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;

import java.util.List;

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "ListFragment";
    private static final String LIST_STATE = "LIST_STATE";

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        setRecycleView();
        buttonNewPosts.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    public interface GetPostsListener {
        List<Post> getPosts();
    }

    public interface OnItemClickListener {
        void onClick(Post post);
    }


    GetPostsListener getPostsListener;
    OnItemClickListener onItemClickListener;
    SwipeRefreshLayout swipeRefreshLayout;
    UpdatedListReceiver updatedListReceiver;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button buttonNewPosts;
    Context context;
    Parcelable listState = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_posts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            context = getActivity();
            updatedListReceiver = new UpdatedListReceiver();
            getPostsListener = (GetPostsListener) getActivity();
            onItemClickListener = (OnItemClickListener) getActivity();
            buttonNewPosts = getActivity().findViewById(R.id.buttonNewPostsMain);
            buttonNewPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRecycleView();
                    buttonNewPosts.setVisibility(View.GONE);
                }
            });
            recyclerView = getActivity().findViewById(R.id.recyclerViewPostsMain);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            setRecycleView();
            swipeRefreshLayout = getActivity().findViewById(R.id.swipeRefreshPostsMain);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.pink, R.color.purple, R.color.colorPrimaryDark);
        }
    }

    private void setRecycleView() {
        recyclerView.setAdapter(new PostAdapter(getActivity(), getPostsListener.getPosts(), onItemClickListener, null));
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(updatedListReceiver, new IntentFilter(MainActivity.ACTION_LIST_UPDATED));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(updatedListReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE, listState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LIST_STATE));
        }
    }

    private class UpdatedListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(MainActivity.ACTION_LIST_UPDATED)) {
                Log.d(TAG, "onReceive: update");
//                buttonNewPosts.setVisibility(View.VISIBLE);
                setRecycleView();
            }
        }
    }
}