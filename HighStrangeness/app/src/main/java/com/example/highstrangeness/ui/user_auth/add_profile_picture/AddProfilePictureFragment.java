package com.example.highstrangeness.ui.user_auth.add_profile_picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProfilePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProfilePictureFragment extends Fragment {

    public static final String TAG = "AddProfilePictureFrag";
    public static final int REQUEST_CODE_FILE_PICKER = 0x073;

    public interface NavigateToMainScreenListener {
        void navigateToMainScreenListener();
    }

    NavigateToMainScreenListener navigateToMainScreenListener;
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
            //From file button tapped
            getActivity().findViewById(R.id.buttonFromFileAddProfilePicScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
                }
            });

            //Skip button tapped
            getActivity().findViewById(R.id.buttonSkipAddProfilePicScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToMainScreenListener.navigateToMainScreenListener();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {


            Uri image = data.getData();
            if (image != null) {
                StorageUtility.updateProfileImage(image, getContext());
                Log.d(TAG, "onActivityResult: Path = " + image.toString());
            }
//            getActivity().findViewById(R.id.imageViewProfilePictureAccountScreen).
//            Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            try{
//                InputStream is;
//                Bitmap bitmap = MediaStore.Images.Media.getContentUri()
//            }catch (IOException e) {
//                Log.d(TAG, "onActivityResult: " + e.getMessage());
//            }

        }
    }
}