package com.example.highstrangeness.ui.post;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.SelectedFilesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostPt2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostPt2Fragment extends Fragment {

    public static final String TAG = "PostPt2Fragment";
    public static final int REQUEST_CODE_IMAGE_FILE_PICKER = 0x076;
    public static final String EXTRA_STRING_LIST = "EXTRA_STRING_LIST";
    public static final String EXTRA_FILE_INDEX = "EXTRA_FILE_INDEX";
    public static final String ACTION_UPDATE_RECYCLE_VIEW = "ACTION_UPDATE_RECYCLE_VIEW";

    public PostPt2Fragment() {
    }

    public static PostPt2Fragment newInstance() {
        PostPt2Fragment fragment = new PostPt2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddPostToFirebase {
        void addPostToFirebase(String[] tags, ArrayList<Uri> imageUris, ArrayList<Uri> audioUris, ArrayList<Uri> videoUris);
    }

    AddPostToFirebase addPostToFirebase;
    UpdateRecycleViewReceiver updateRecycleViewReceiver;
    ArrayList<Uri> imageUris;
    ArrayList<Uri> audioUris;
    ArrayList<Uri> videoUris;
    Button buttonAddAudio;
    Button buttonAddVideo;
    RecyclerView recyclerViewImages;
    SelectedFilesAdapter selectedFilesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_pt2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            addPostToFirebase = (AddPostToFirebase) getActivity();

            recyclerViewImages = getActivity().findViewById(R.id.recyclerViewImages);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerViewImages.setLayoutManager(layoutManager);

            final EditText editTextTags =  getActivity().findViewById(R.id.editTextTags);

            getActivity().findViewById(R.id.buttonFinish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] tags = editTextTags.getText().toString().trim().split(",");
                    addPostToFirebase.addPostToFirebase(tags, imageUris, audioUris, videoUris);
                }
            });

            getActivity().findViewById(R.id.buttonAddImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context;
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    final View mediaCaptureDialog = layoutInflater.inflate(R.layout.dialog_media_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setView(mediaCaptureDialog);

                    mediaCaptureDialog.findViewById(R.id.buttonMediaFromFile).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(intent, REQUEST_CODE_IMAGE_FILE_PICKER);
                        }
                    });
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            if (alertDialog.getWindow() != null && getActivity() != null) {
                                alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),
                                        android.R.color.transparent));
                            }
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

     class UpdateRecycleViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String list = intent.getStringExtra(EXTRA_STRING_LIST);
            int index = intent.getIntExtra(EXTRA_FILE_INDEX, -1);
            Log.d(TAG, "onReceive: count");
            if (list != null && index > -1) {
                switch (list) {
                    case "images":
                        if (imageUris.size() > index) {
                            imageUris.remove(index);
                            Log.d(TAG, "onReceive: index = " + index);
                            Log.d(TAG, "onReceive: count = " + imageUris.size());
                        }
                        updateRecycleView(imageUris, recyclerViewImages, list);
                        break;
                    case "audios":
                        audioUris.remove(index);
//                        updateRecycleView(audioUris, recyclerViewAudio);
                        break;
                    case "videos":
                        videoUris.remove(index);
//                        updateRecycleView(videoUris, index, recyclerViewVideo);
                        break;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(updateRecycleViewReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            updateRecycleViewReceiver = new UpdateRecycleViewReceiver();
            getActivity().registerReceiver(updateRecycleViewReceiver, new IntentFilter(ACTION_UPDATE_RECYCLE_VIEW));
        }
    }

    void updateRecycleView(List<Uri> uris, RecyclerView recyclerView, String list) {
        if (uris != null) {
            selectedFilesAdapter = new SelectedFilesAdapter(getContext(), uris, list);
            recyclerView.setAdapter(selectedFilesAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_IMAGE_FILE_PICKER && data != null && data.getClipData() != null) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(TAG, "onActivityResult: yeeh");
            imageUris = new ArrayList<>();
            for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri image = data.getClipData().getItemAt(i).getUri();
                System.out.println("image" + i + "=" + image.toString());
                imageUris.add(image);
            }
            if (imageUris.size() > 0) {
                Log.d(TAG, "onActivityResult: size = " + imageUris.size());
                updateRecycleView(imageUris, recyclerViewImages, "images");
            }
        }
    }
}