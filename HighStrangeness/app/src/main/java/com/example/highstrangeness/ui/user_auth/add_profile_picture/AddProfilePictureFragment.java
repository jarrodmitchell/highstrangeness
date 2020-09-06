package com.example.highstrangeness.ui.user_auth.add_profile_picture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.ImageStorageUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProfilePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProfilePictureFragment extends Fragment {

    public static final String TAG = "AddProfilePictureFrag";
    public static final int REQUEST_CODE_FILE_PICKER = 0x073;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0x074;

    ImageView imageViewProfilePic;
    Context context;

    public interface NavigateToMainScreenListener {
        void navigateToMainScreen();
    }

    NavigateToMainScreenListener navigateToMainScreenListener;
    ProfilePicUpdatedReceiver profilePicUpdatedReceiver;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public AddProfilePictureFragment() {
        // Required empty public constructor
    }

    public static AddProfilePictureFragment newInstance() {
        AddProfilePictureFragment fragment = new AddProfilePictureFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity()!=null) {
            navigateToMainScreenListener = (NavigateToMainScreenListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_profile_picture, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            context = getActivity();
            profilePicUpdatedReceiver = new ProfilePicUpdatedReceiver();
            imageViewProfilePic = getActivity().findViewById(R.id.imageViewAddProfilePictureAccountScreen);

            //From file button tapped
            getActivity().findViewById(R.id.buttonFromFileAddProfilePicScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
                }
            });

            //Take photo button tapped
            getActivity().findViewById(R.id.buttonAddProfilePicScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
                }
            });

            //Skip button tapped
            getActivity().findViewById(R.id.buttonSkipAddProfilePicScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToMainScreenListener.navigateToMainScreen();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null) {
            getContext().registerReceiver(profilePicUpdatedReceiver, new IntentFilter(ImageStorageUtility.ACTION_PROFILE_PIC_UPDATED));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getContext() != null) {
            getContext().unregisterReceiver(profilePicUpdatedReceiver);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            switch (requestCode) {
                case REQUEST_CODE_FILE_PICKER:
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Uri image = data.getData();
                    if (getActivity() != null && image != null && user != null) {
                        InputStream inputStream = null;
                        try {
                            inputStream = getActivity().getContentResolver().openInputStream(image);
                            imageViewProfilePic.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                            ImageStorageUtility.updateProfileImage(image, getContext());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_IMAGE_CAPTURE:

                    //save image
                    if (data.getExtras() != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (bitmap != null) {File file = createFile();
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
                                imageViewProfilePic.setImageBitmap(bitmap);
                                ImageStorageUtility.updateProfileImage(uri, context);
                            }else {
                                Log.d(TAG, "onActivityResult: no file");
                            }
                        }
                    }
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

    private class ProfilePicUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ImageStorageUtility.ACTION_PROFILE_PIC_UPDATED)) {
                Log.d(TAG, "onReceive: update");
                navigateToMainScreenListener.navigateToMainScreen();
            }
        }
    }
}