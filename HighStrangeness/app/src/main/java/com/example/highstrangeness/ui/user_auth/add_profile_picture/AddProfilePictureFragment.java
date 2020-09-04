package com.example.highstrangeness.ui.user_auth.add_profile_picture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.io.FileNotFoundException;
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

    ImageView imageViewProfilePic;

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

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {

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
        }
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