package com.example.highstrangeness.ui.main.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.MediaAdapter;
import com.example.highstrangeness.adapters.PostAdapter;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.utilities.PostUtility;

import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment {

    public static final String TAG = "ListFragment";

    public interface GetPostsListener {
        List<Post> getPosts();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    GetPostsListener getPostsListener;
    OnItemClickListener onItemClickListener;
    UpdatedListReceiver updatedListReceiver;
    RecyclerView recyclerView;
    Context context;

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
            context.registerReceiver(updatedListReceiver, new IntentFilter(MainActivity.ACTION_LIST_UPDATED));
            getPostsListener = (GetPostsListener) getActivity();
            onItemClickListener = (OnItemClickListener) getActivity();
            recyclerView = getActivity().findViewById(R.id.recyclerViewPostsMain);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            setRecycleView();
        }
    }

    private void setRecycleView() {
        recyclerView.setAdapter(new PostAdapter(getActivity(), getPostsListener.getPosts(), onItemClickListener));
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(updatedListReceiver, new IntentFilter(MainActivity.ACTION_LIST_UPDATED));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        context.unregisterReceiver(updatedListReceiver);
    }

    public class UpdatedListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(MainActivity.ACTION_LIST_UPDATED)) {
                Log.d(TAG, "onReceive: update");
                setRecycleView();
            }
        }
    }
}