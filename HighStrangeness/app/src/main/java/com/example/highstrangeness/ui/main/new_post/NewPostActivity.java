package com.example.highstrangeness.ui.main.new_post;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.adapters.OldFilesAdapter;
import com.example.highstrangeness.dialogs.DatePickerFragment;
import com.example.highstrangeness.objects.NewPost;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.location_picker.LocationPickerActivity;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.post.PostPt1Fragment;
import com.example.highstrangeness.ui.post.PostPt2Fragment;
import com.example.highstrangeness.ui.post_detail.PostDetailActivity;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.PostUtility;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewPostActivity extends AppCompatActivity implements PostPt1Fragment.DisplayPostPart2Listener,
        DatePickerDialog.OnDateSetListener, PostPt1Fragment.DisplayCalenderPickerListener,
        PostPt1Fragment.DisplayLocationPickerListener, LocationUtility.ReturnAddressListener,
        PostPt1Fragment.GetLatLngListener, PostPt1Fragment.SetNewPostListener, PostPt1Fragment.GetDateListener,
        PostPt2Fragment.AddPostToFirebase, PostPt2Fragment.SaveTagsListener, PostPt2Fragment.GetMediaListsListener,
        PostPt2Fragment.SetImageUriListListener, PostPt2Fragment.SetAudioUriListListener,
        PostPt2Fragment.SetVideoUriListListener, PostPt2Fragment.GetOldPostListener,
        PostPt2Fragment.GetNewPostListener, StorageUtility.GetPostImageNamesListener {

    public static final String TAG = "NewPostActivity";
    public static final String EXTRA_ADDRESS_RETURN = "EXTRA_ADDRESS_RETURN";
    public static final String ACTION_SEND_ADD_POST_RESULT = "ACTION_SEND_ADD_POST_RESULT";
    public static final int REQUEST_CODE_ADDRESS = 0x89;

    TextView textViewDate;
    TextView textViewLocation;
    EditText editTextTitle;
    EditText editTextDescription;
    SwitchMaterial switchFirstHand;

    NewPost newPost;
    Post oldPost;
    Date date;
    double latitude;
    double longitude;
    ArrayList<Uri> imageUris;
    ArrayList<Uri> audioUris;
    ArrayList<Uri> videoUris;
    AddPostResultReceiver addPostResultReceiver;

    @Override
    public void getPostImagesName(List<String> imageNames) {
        
        RecyclerView recyclerViewOldImages = findViewById(R.id.recyclerViewOldImages);

        if (!imageNames.isEmpty()) {
            Log.d(TAG, "getPostImagesName: in");
            recyclerViewOldImages.setVisibility(View.VISIBLE);
            Intent intent = new Intent(PostPt2Fragment.ACTION_UPDATE_RECYCLE_VIEW_OLD_POST);
            intent.putExtra(PostPt2Fragment.EXTRA_STRING_LIST, Lists.newArrayList(imageNames));
            sendBroadcast(intent);
        }else {
            Log.d(TAG, "getPostImagesName: out");
            recyclerViewOldImages.setVisibility(View.GONE);
        }
    }

    @Override
    public NewPost getNewPostValues() {
        Log.d(TAG, "getNewPostValues: " + newPost.getTags());
        return newPost;
    }

    @Override
    public Post getPostListener() {
        return oldPost;
    }

    @Override
    public void setImageUriList(ArrayList<Uri> imageUriList) {
        imageUris = imageUriList;
    }

    @Override
    public void setAudioUriList(ArrayList<Uri> audioUriList) {
        audioUris = audioUriList;
    }

    @Override
    public void setVideoUriList(ArrayList<Uri> videoUriList) {
        videoUris = videoUriList;
    }

    @Override
    public ArrayList<ArrayList<Uri>> getMediaLists() {
        ArrayList<ArrayList<Uri>> mediaLists = new ArrayList<>();
        mediaLists.add(imageUris);
        mediaLists.add(audioUris);
        mediaLists.add(videoUris);
        return mediaLists;
    }

    @Override
    public void saveTags(String tags) {
        Log.d(TAG, "saveTags: " + tags);
        newPost.setTags(tags);
    }

    @Override
    public void addPostToFirebase(String[] tags, String id, List<String> imageNameList) {
        if (newPost != null) {
            addPostResultReceiver = new AddPostResultReceiver();
            registerReceiver(addPostResultReceiver, new IntentFilter(ACTION_SEND_ADD_POST_RESULT));
            PostUtility.addPost(id, newPost.getTitle(), newPost.isFirstHand(), newPost.getDate(),
                    newPost.getLatitude(), newPost.getLongitude(), newPost.getDescription(), tags, imageUris,
                    audioUris, videoUris, this, imageNameList);
        }
    }

    public class AddPostResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("success")) {
                boolean success = intent.getBooleanExtra("success", false);
                if (success) {
                    Log.d(TAG, "onReceive: ");
                    if (oldPost == null) {
                        finish();
                    }else{
                        returnPost(intent);
                    }
                }
            }
        }
    }

    private void returnPost(Intent intent) {
        Log.d(TAG, "returnPost: ");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.EXTRA_POST, (Post) intent.getParcelableExtra(MainActivity.EXTRA_POST));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setNewPostValues(NewPost newPost) {
        String tags = null;
        if (this.newPost != null && this.newPost.getTags() != null &&  !this.newPost.getTags().isEmpty()) {
            tags = this.newPost.getTags();
            Log.d(TAG, "setNewPostValues: " + newPost.getTags());
        }
        this.newPost = newPost;
        if (tags != null) {
            newPost.setTags(tags);
        }
        Log.d(TAG, "setNewPostValues: " + newPost.getTags());
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public void returnAddress(Address address) {
        if (address != null) {
            if (oldPost == null) {
                Log.d(TAG, "returnAddress: " + address.getAddressLine(0));
                textViewLocation.setTextColor(Color.BLACK);
                textViewLocation.setText(address.getAddressLine(0));
            }else {
                textViewDate = findViewById(R.id.textViewDateFormat);
                editTextTitle =  findViewById(R.id.editTextTitle);
                editTextDescription = findViewById(R.id.editTextDescription);
                switchFirstHand = findViewById(R.id.switch_first_hand_experience);
                textViewLocation = findViewById(R.id.textViewLocationAddress);
                setValues(address);
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        textViewDate = findViewById(R.id.textViewDateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);

        textViewDate.setTextColor(Color.BLACK);
        date = calendar.getTime();
        textViewDate.setText(DateFormat.getDateInstance().format(date));
    }

    @Override
    public void displayLocationPicker() {
        Intent intent = new Intent(NewPostActivity.this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    @Override
    public void displayCalenderPicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), null);
    }

    @Override
    public void displayPostPart2() {
        displayPostPt2();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        setTitle("Add Encounter");

        if (getIntent() != null) {
            oldPost = getIntent().getParcelableExtra(PostDetailActivity.EXTRA_OLD_POST);
            if (oldPost != null) {
                setTitle("Edit Post");
                newPost = new NewPost(oldPost.getTitle(), oldPost.isFirstHand(), oldPost.getDate(), oldPost.getLatitude(), oldPost.getLongitude(),  oldPost.getDescription());
                latitude = oldPost.getLatitude();
                longitude = oldPost.getLongitude();

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < oldPost.getTags().size(); i++) {
                    stringBuilder.append(oldPost.getTags().get(i));
                    if (i != oldPost.getTags().size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                newPost.setTags(stringBuilder.toString());
                date = oldPost.getDate();
            }
        }
        displayPostPt1();
    }

    private void setValues(Address address) {
        textViewDate.setTextColor(Color.BLACK);
        textViewLocation.setTextColor(Color.BLACK);
        editTextTitle.setText(newPost.getTitle());
        editTextDescription.setText(newPost.getDescription());
        textViewDate.setText(DateFormat.getDateInstance().format(newPost.getDate()));
        switchFirstHand.setChecked(newPost.isFirstHand());
        textViewLocation.setText(address.getAddressLine(0));
    }

    public void displayPostPt1() {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutNewPost, PostPt1Fragment.newInstance(oldPost)).commit();
    }

    public void displayPostPt2() {
        if (oldPost != null) {
            StorageUtility.getListOfPostImages(oldPost.getId(), this);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutNewPost, PostPt2Fragment.newInstance()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADDRESS) {
            if (data != null) {
                double[] coordinates = data.getDoubleArrayExtra(EXTRA_ADDRESS_RETURN);
                if (coordinates != null && coordinates.length == 2) {
                    latitude = coordinates[0];
                    longitude = coordinates[1];
                    textViewLocation = findViewById(R.id.textViewLocationAddress);

                    LocationUtility.getAddress(this, new LatLng(latitude, longitude), this);
                }
            }
        }
    }
}