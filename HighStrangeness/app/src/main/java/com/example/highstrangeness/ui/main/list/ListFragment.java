package com.example.highstrangeness.ui.main.list;

import android.os.Bundle;
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
import com.example.highstrangeness.adapters.PostAdapter;
import com.example.highstrangeness.objects.Post;

import java.util.List;

public class ListFragment extends Fragment {

    public interface GetPostsListener {
        List<Post> getPosts();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    GetPostsListener getPostsListener;
    OnItemClickListener onItemClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_posts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            getPostsListener = (GetPostsListener) getActivity();
            onItemClickListener = (OnItemClickListener) getActivity();
            RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerViewPostsMain);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new PostAdapter(getActivity(), getPostsListener.getPosts(), onItemClickListener));
        }
    }
}