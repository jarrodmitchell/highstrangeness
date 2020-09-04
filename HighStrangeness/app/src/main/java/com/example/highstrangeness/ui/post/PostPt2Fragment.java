package com.example.highstrangeness.ui.post;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.OldFilesAdapter;
import com.example.highstrangeness.adapters.SelectedFilesAdapter;
import com.example.highstrangeness.objects.NewPost;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.utilities.ImageStorageUtility;
import com.example.highstrangeness.utilities.VideoStorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostPt2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostPt2Fragment extends Fragment {

    public static final String TAG = "PostPt2Fragment";
    public static final int REQUEST_CODE_IMAGE_FILE_PICKER = 0x076;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0x077;
    public static final int REQUEST_CODE_VIDEO_FILE_PICKER = 0x078;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 0x079;
    public static final String EXTRA_IMAGE_STRING_LIST = "EXTRA_IMAGE_STRING_LIST";
    public static final String EXTRA_VIDEO_STRING_LIST = "EXTRA_VIDEO_STRING_LIST";
    public static final String EXTRA_FILE_INDEX = "EXTRA_FILE_INDEX";
    public static final String ACTION_UPDATE_RECYCLE_VIEW_NEW_POST = "ACTION_UPDATE_RECYCLE_VIEW";
    public static final String ACTION_SET_RECYCLE_VIEW_OLD_POST = "ACTION_SET_RECYCLE_VIEW_OLD_POST";
    public static final String ACTION_UPDATE_RECYCLE_VIEW_OLD_POST = "ACTION_UPDATE_RECYCLE_VIEW_OLD_POST";

    public PostPt2Fragment() {
    }

    public static PostPt2Fragment newInstance() {
        PostPt2Fragment fragment = new PostPt2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddPostToFirebase {
        void addPostToFirebase(String[] tags, String id, List<String> imageNameList, List<String> videoNameList);
    }

    public interface SaveTagsListener {
        void saveTags(String tags);
    }

    public interface GetNewPostListener {
        NewPost getNewPostValues();
    }

    public interface GetMediaListsListener {
        ArrayList<ArrayList<Uri>> getMediaLists();
    }

    public interface SetImageUriListListener {
        void setImageUriList(ArrayList<Uri> imageUriList);
    }

    public interface SetAudioUriListListener {
        void setAudioUriList(ArrayList<Uri> audioUriList);
    }

    public interface SetVideoUriListListener {
        void setVideoUriList(ArrayList<Uri> videoUriList);
    }

    public interface GetOldPostListener {
        Post getPostListener();
    }

    AddPostToFirebase addPostToFirebase;
    GetNewPostListener getNewPostListener;
    SaveTagsListener saveTagsListener;
    GetMediaListsListener getMediaListsListener;
    SetImageUriListListener setImageUriListListener;
    SetAudioUriListListener setAudioUriListListener;
    SetVideoUriListListener setVideoUriListListener;
    GetOldPostListener getOldPostListener;
    UpdateRecycleViewReceiver updateRecycleViewReceiver;
    Button buttonAddAudio;
    Button buttonAddVideo;
    RecyclerView recyclerViewImages;
    RecyclerView recyclerViewOldImages;
    RecyclerView recyclerViewVideos;
    RecyclerView recyclerViewOldVideos;
    SelectedFilesAdapter selectedFilesAdapter;
    OldFilesAdapter oldImageFilesAdapter;
    OldFilesAdapter oldVideoFilesAdapter;
    Context context;
    StorageReference storageReference;
    List<String> imageNameList;
    List<String> videoNameList;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            context = getActivity();
            storageReference = FirebaseStorage.getInstance().getReference();
            addPostToFirebase = (AddPostToFirebase) getActivity();
            saveTagsListener = (SaveTagsListener) getActivity();
            getMediaListsListener = (GetMediaListsListener)  getActivity();
            setImageUriListListener = (SetImageUriListListener) getActivity();
            setAudioUriListListener = (SetAudioUriListListener) getActivity();
            setVideoUriListListener = (SetVideoUriListListener) getActivity();
            getOldPostListener = (GetOldPostListener) getActivity();
            getNewPostListener = (GetNewPostListener) getActivity();

            //set up IMAGE recycler views
            recyclerViewImages = getActivity().findViewById(R.id.recyclerViewImages);
            recyclerViewOldImages = getActivity().findViewById(R.id.recyclerViewOldImages);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerViewImages.setLayoutManager(layoutManager);
            RecyclerView.LayoutManager layoutManagerOldImages = new LinearLayoutManager(getContext());
            recyclerViewOldImages.setLayoutManager(layoutManagerOldImages);

            //set up VIDEO recycler views
            recyclerViewVideos = getActivity().findViewById(R.id.recyclerViewVideos);
            recyclerViewOldVideos = getActivity().findViewById(R.id.recyclerViewOldVideos);
            RecyclerView.LayoutManager layoutManagerVideos = new LinearLayoutManager(getContext());
            recyclerViewVideos.setLayoutManager(layoutManagerVideos);
            RecyclerView.LayoutManager layoutManagerOldVideos = new LinearLayoutManager(getContext());
            recyclerViewOldVideos.setLayoutManager(layoutManagerOldVideos);

            final EditText editTextTags =  getActivity().findViewById(R.id.editTextTags);
            editTextTags.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!charSequence.toString().isEmpty()) {
                        saveTagsListener.saveTags(charSequence.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //FINISH button tapped
            Button buttonFinish = getActivity().findViewById(R.id.buttonFinish);
            buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] tags = editTextTags.getText().toString().trim().split(",");
                    Log.d(TAG, "onClick: tag " + tags[0].length());
                    final String[] trimmedTags = trimTags(tags);

                    //add tags to db
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference documentReference = db.collection("tags").document("tags");
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                ArrayList<String> tags1 = (ArrayList<String>) task.getResult().getData().get("tags");
                                if (tags1 != null) {
                                    for (String tag : trimmedTags) {
                                        if (!tags1.contains(tag)) {
                                            tags1.add(tag);
                                        }
                                    }
                                    final Map<String, Object> docData = new HashMap<>();
                                    docData.put("tags", Lists.newArrayList(tags1));
                                    documentReference.set(docData);
                                }
                            }
                        }
                    });

                    Log.d(TAG, "onClick: tag " + tags[0].length());
                    if (getOldPostListener.getPostListener() != null)  {
                        addPostToFirebase.addPostToFirebase(trimmedTags, getOldPostListener.getPostListener().getId(), imageNameList, videoNameList);
                    }else{
                        addPostToFirebase.addPostToFirebase(trimmedTags, null, null, null);
                    }
                }
            });

            //Add IMAGE button tapped
            getActivity().findViewById(R.id.buttonAddImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

                    mediaCaptureDialog.findViewById(R.id.buttonCaptureMedia).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
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

            //Add VIDEO button tapped
            getActivity().findViewById(R.id.buttonAddVideo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    final View mediaCaptureDialog = layoutInflater.inflate(R.layout.dialog_media_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setView(mediaCaptureDialog);

                    mediaCaptureDialog.findViewById(R.id.buttonMediaFromFile).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(intent, REQUEST_CODE_VIDEO_FILE_PICKER);
                        }
                    });

                    mediaCaptureDialog.findViewById(R.id.buttonCaptureMedia).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
                        }
                    });
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            ((Button) mediaCaptureDialog.findViewById(R.id.buttonCaptureMedia)).setText("Capture Video");
                            if (alertDialog.getWindow() != null && getActivity() != null) {
                                alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),
                                        android.R.color.transparent));
                            }
                        }
                    });
                    alertDialog.show();
                }
            });

            Post oldPost = getOldPostListener.getPostListener();
            NewPost newPost = getNewPostListener.getNewPostValues();
            if (newPost == null && oldPost != null && oldPost.getTags() != null && !oldPost.getTags().isEmpty()) {
                Log.d(TAG, "onActivityCreated: old");
                ImageStorageUtility.getListOfPostImages(oldPost.getId(), getActivity());
                VideoStorageUtility.getListOfPostVideos(oldPost.getId(), getActivity());

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < oldPost.getTags().size(); i++) {
                    stringBuilder.append(oldPost.getTags().get(i));
                    if (i != oldPost.getTags().size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                buttonFinish.setText(R.string.edit_post);
                editTextTags.setText(stringBuilder.toString());
            }else if (newPost != null && oldPost != null) {
                Log.d(TAG, "onActivityCreated: old and new");
                editTextTags.setText(newPost.getTags());
                buttonFinish.setText(R.string.edit_post);
                ImageStorageUtility.getListOfPostImages(oldPost.getId(), getActivity());
                VideoStorageUtility.getListOfPostVideos(oldPost.getId(), getActivity());
                updateMainRecycleView(newPost.getImageUris(), recyclerViewImages, "images");
                updateMainRecycleView(newPost.getVideoUris(), recyclerViewVideos, "videos");
            }else if (newPost != null) {
                Log.d(TAG, "onActivityCreated: new");
                editTextTags.setText(newPost.getTags());
                updateMainRecycleView(newPost.getImageUris(), recyclerViewImages, "images");
                updateMainRecycleView(newPost.getVideoUris(), recyclerViewVideos, "videos");
            }
        }
    }

    private String[] trimTags(String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].toLowerCase().trim();
        }
        return tags;
    }

     class UpdateRecycleViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                Log.d(TAG, "onReceive: Action = " + intent.getAction());
                switch (intent.getAction()) {
                    case PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW_NEW_POST:
                        Log.d(TAG, "onReceive: " + ACTION_UPDATE_RECYCLE_VIEW_NEW_POST);
                        String list = intent.getStringExtra(EXTRA_IMAGE_STRING_LIST);
                        int index = intent.getIntExtra(EXTRA_FILE_INDEX, -1);
                        Log.d(TAG, "onReceive: count");
                        if (list != null && index > -1) {
                            ArrayList<ArrayList<Uri>> mediaLists = getMediaListsListener.getMediaLists();
                            switch (list) {
                                case "images":
                                    if (mediaLists.get(0).size() > index) {
                                        mediaLists.get(0).remove(index);
                                        Log.d(TAG, "onReceive: index = " + index);
                                        Log.d(TAG, "onReceive: count = " + mediaLists.get(0).size());
                                    }
                                    updateMainRecycleView(mediaLists.get(0), recyclerViewImages, list);
                                    break;
                                case "audios":
//                        updateRecycleView(audioUris, recyclerViewAudio);
                                    break;
                                case "videos":
                                    if (mediaLists.get(2).size() > index) {
                                        mediaLists.get(2).remove(index);
                                        Log.d(TAG, "onReceive: index = " + index);
                                        Log.d(TAG, "onReceive: count = " + mediaLists.get(2).size());
                                    }
                                    updateMainRecycleView(mediaLists.get(2), recyclerViewVideos, list);
                                    break;
                            }
                        }
                        break;

                    case PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW_OLD_POST:
                        if (intent.hasExtra(PostPt2Fragment.EXTRA_IMAGE_STRING_LIST)) {
                            Log.d(TAG, "onReceive: has old images");
                            updateOldRecycleView(recyclerViewOldImages, intent.getStringArrayListExtra(PostPt2Fragment.EXTRA_IMAGE_STRING_LIST));
                        }
                        if (intent.hasExtra(PostPt2Fragment.EXTRA_VIDEO_STRING_LIST)) {
                            Log.d(TAG, "onReceive: has old videos");
                            updateOldRecycleView(recyclerViewOldVideos, intent.getStringArrayListExtra(PostPt2Fragment.EXTRA_VIDEO_STRING_LIST));
                        }
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
            getActivity().registerReceiver(updateRecycleViewReceiver, new IntentFilter(ACTION_UPDATE_RECYCLE_VIEW_NEW_POST));
            getActivity().registerReceiver(updateRecycleViewReceiver, new IntentFilter(ACTION_UPDATE_RECYCLE_VIEW_OLD_POST));
        }
    }

    void updateMainRecycleView(List<Uri> uris, RecyclerView recyclerView, String list) {
        if (uris != null) {
            selectedFilesAdapter = new SelectedFilesAdapter(getContext(), uris, list);
            recyclerView.setAdapter(selectedFilesAdapter);
        }
    }

    void updateOldRecycleView(RecyclerView recyclerView, List<String > list) {
        if (list != null) {
            switch (recyclerView.getId()) {
                case R.id.recyclerViewOldImages:
                    imageNameList = list;
                    oldImageFilesAdapter = new OldFilesAdapter("images", getContext(), imageNameList, getOldPostListener.getPostListener().getId());
                    Log.d(TAG, "updateOldRecycleView: image list size = "+imageNameList.size());
                    recyclerViewOldImages.setAdapter(oldImageFilesAdapter);
                    break;
                case R.id.recyclerViewOldAudios:
                    break;
                case R.id.recyclerViewOldVideos:
                    videoNameList = list;
                    oldImageFilesAdapter = new OldFilesAdapter("videos", getContext(), videoNameList, getOldPostListener.getPostListener().getId());
                    Log.d(TAG, "updateOldRecycleView: video list size = "+videoNameList.size());
                    recyclerViewOldVideos.setAdapter(oldImageFilesAdapter);
                    break;
            }
        }
    }

    public File createFile() {
        String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Log.d(TAG, "createFile: " + root);
        File appDirectory = new File(root + "/high_strangeness");
        String id = String.valueOf(new Date().getTime());
        if (!appDirectory.exists()) {
            boolean makeSuccess  = appDirectory.mkdir();
            if (makeSuccess) {
                Log.d(TAG, "createFile: success");
            }else{
                Log.d(TAG, "createFile: fail");
            }
        }
        try {
            Log.d(TAG, "createFile: " + id);
            File file = File.createTempFile(id, "", appDirectory);
            Log.d(TAG, "createFile: " + file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri> uriList = new ArrayList<>();

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: result ok");
            if (requestCode == REQUEST_CODE_IMAGE_FILE_PICKER && data != null && data.getClipData() != null) {
                ArrayList<Uri> uris = getMediaListsListener.getMediaLists().get(0);
                if (uris != null) {
                    uriList = uris;
                }

                for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri image = data.getClipData().getItemAt(i).getUri();
                    if (uriList != null && !uriList.contains(image)) {
                        System.out.println("image" + i + "=" + image.toString());
                        uriList.add(image);
                    }
                }
            }else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                ArrayList<Uri> uris = getMediaListsListener.getMediaLists().get(0);
                if (uris != null) {
                    uriList = uris;
                }

                //save image
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    Log.d(TAG, "onActivityResult: path = " + bitmap.getByteCount());
                    File file = createFile();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (file.exists()) {
                        Log.d(TAG, "onActivityResult: file here");
                        Uri uri = Uri.fromFile(file);
                        uriList.add(uri);
                    }else {
                        Log.d(TAG, "onActivityResult: no file");
                    }
                }
            }if (requestCode == REQUEST_CODE_VIDEO_FILE_PICKER && data != null && data.getClipData() != null) {
                ArrayList<Uri> uris = getMediaListsListener.getMediaLists().get(2);
                if (uris != null) {
                    uriList = uris;
                }

                for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri videoUri = data.getClipData().getItemAt(i).getUri();
                    if (uriList != null && !uriList.contains(videoUri)) {
                        System.out.println("image" + i + "=" + videoUri.toString());
                        uriList.add(videoUri);
                    }
                }
            }else if (requestCode == REQUEST_CODE_VIDEO_CAPTURE && data != null && data.getData() != null) {
                ArrayList<Uri> uris = getMediaListsListener.getMediaLists().get(2);
                if (uris != null) {
                    uriList = uris;
                }

                //save video
                Uri videoUri = data.getData();
                uriList.add(videoUri);
            }

            Log.d(TAG, "onActivityResult: past");
            if (uriList.size() > 0) {
                Log.d(TAG, "onActivityResult: size = " + uriList.size());

                switch (requestCode) {
                    case REQUEST_CODE_IMAGE_CAPTURE:
                    case REQUEST_CODE_IMAGE_FILE_PICKER:
                        setImageUriListListener.setImageUriList(uriList);
                        updateMainRecycleView(uriList, recyclerViewImages, "images");
                        break;
                    case REQUEST_CODE_VIDEO_CAPTURE:
                    case REQUEST_CODE_VIDEO_FILE_PICKER:
                        setVideoUriListListener.setVideoUriList(uriList);
                        updateMainRecycleView(uriList, recyclerViewVideos, "videos");
                }
            }
        }
    }
}