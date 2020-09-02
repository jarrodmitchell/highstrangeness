package com.example.highstrangeness.ui.post;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0x077;
    public static final int REQUEST_CODE_VIDEO_FILE_PICKER = 0x078;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 0x079;
    public static final String EXTRA_STRING_LIST = "EXTRA_STRING_LIST";
    public static final String EXTRA_FILE_INDEX = "EXTRA_FILE_INDEX";
    public static final String ACTION_UPDATE_RECYCLE_VIEW = "ACTION_UPDATE_RECYCLE_VIEW";
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
        void addPostToFirebase(String[] tags, String id);
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
    SelectedFilesAdapter selectedFilesAdapter;
    OldFilesAdapter oldFilesAdapter;
    Context context;
    StorageReference storageReference;

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
            recyclerViewImages = getActivity().findViewById(R.id.recyclerViewImages);
            recyclerViewOldImages = getActivity().findViewById(R.id.recyclerViewOldImages);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerViewImages.setLayoutManager(layoutManager);
            RecyclerView.LayoutManager layoutManagerOldImages = new LinearLayoutManager(getContext());
            recyclerViewOldImages.setLayoutManager(layoutManagerOldImages);

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

            Button buttonFinish = getActivity().findViewById(R.id.buttonFinish);
            buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] tags = editTextTags.getText().toString().trim().split(",");
                    if (getOldPostListener.getPostListener() != null)  {
                        addPostToFirebase.addPostToFirebase(tags, getOldPostListener.getPostListener().getId());
                    }else{
                        addPostToFirebase.addPostToFirebase(tags, null);
                    }
                }
            });

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
                StorageUtility.getListOfPostImages(oldPost.getId(), getActivity());

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < oldPost.getTags().size(); i++) {
                    stringBuilder.append(oldPost.getTags().get(i));
                    if (i != oldPost.getTags().size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                buttonFinish.setText(R.string.edit_text);
                editTextTags.setText(stringBuilder.toString());
            }else if (newPost != null && oldPost != null) {
                Log.d(TAG, "onActivityCreated: old and new");
                editTextTags.setText(newPost.getTags());
                buttonFinish.setText(R.string.edit_text);
                StorageUtility.getListOfPostImages(oldPost.getId(), getActivity());
                updateMainRecycleView(newPost.getImageUris(), recyclerViewImages, "images");
            }else if (newPost != null) {
                Log.d(TAG, "onActivityCreated: new");
                editTextTags.setText(newPost.getTags());
                updateMainRecycleView(newPost.getImageUris(), recyclerViewImages, "images");
            }
        }
    }

     class UpdateRecycleViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW)) {
                    String list = intent.getStringExtra(EXTRA_STRING_LIST);
                    int index = intent.getIntExtra(EXTRA_FILE_INDEX, -1);
                    Log.d(TAG, "onReceive: count");
                    if (list != null && index > -1) {
                        switch (list) {
                            case "images":
                                ArrayList<ArrayList<Uri>> mediaLists = getMediaListsListener.getMediaLists();
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
//                        updateRecycleView(videoUris, index, recyclerViewVideo);
                                break;
                        }
                    }
                } else if (intent.getAction().equals(PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW_OLD_POST)) {
                    updateOldRecycleView(recyclerViewOldImages, intent.getStringArrayListExtra(PostPt2Fragment.EXTRA_STRING_LIST));
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
            oldFilesAdapter = new OldFilesAdapter("images", getContext(), list, getOldPostListener.getPostListener().getId());
            recyclerViewOldImages.setAdapter(oldFilesAdapter);
            Log.d(TAG, "updateOldRecycleView: "+list.size());
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

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: result ok");
            ArrayList<Uri> uriList = getMediaListsListener.getMediaLists().get(0);
            if (uriList == null) {
                uriList = new ArrayList<>();
            }
            if (requestCode == REQUEST_CODE_IMAGE_FILE_PICKER && data != null && data.getClipData() != null) {

                for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri image = data.getClipData().getItemAt(i).getUri();
                    if (uriList != null && !uriList.contains(image)) {
                        System.out.println("image" + i + "=" + image.toString());
                        uriList.add(image);
                    }
                }
            }else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE && data != null && data.getExtras() != null) {

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
            }
            Log.d(TAG, "onActivityResult: past");
            setImageUriListListener.setImageUriList(uriList);
            if (uriList.size() > 0) {
                Log.d(TAG, "onActivityResult: size = " + uriList.size());
                updateMainRecycleView(uriList, recyclerViewImages, "images");
            }
        }
    }
}