package com.example.highstrangeness.ui.main.new_post;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.dialogs.DatePickerFragment;
import com.example.highstrangeness.objects.NewPost;
import com.example.highstrangeness.ui.location_picker.LocationPickerActivity;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.post.PostPt1Fragment;
import com.example.highstrangeness.ui.post.PostPt2Fragment;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.PostUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity implements PostPt1Fragment.DisplayPostPart2Listener,
        DatePickerDialog.OnDateSetListener, PostPt1Fragment.DisplayCalenderPickerListener,
        PostPt1Fragment.DisplayLocationPickerListener, LocationUtility.GetAddressAsyncTask.ReturnAddressListener,
        PostPt1Fragment.GetLatLngListener, PostPt1Fragment.SetNewPostListener, PostPt1Fragment.GetDateListener,
        PostPt2Fragment.AddPostToFirebase {

    public static final String TAG = "NewPostActivity";
    public static final String EXTRA_ADDRESS_RETURN = "EXTRA_ADDRESS_RETURN";
    public static final String ACTION_SEND_ADD_POST_RESULT = "ACTION_SEND_ADD_POST_RESULT";
    public static final int REQUEST_CODE_ADDRESS = 0x89;

    TextView textViewDate;
    TextView textViewLocation;
    NewPost newPost;
    Date date;
    double latitude;
    double longitude;
    AddPostResultReceiver addPostResultReceiver;

    @Override
    public void addPostToFirebase(String[] tags, ArrayList<Uri> imageUris, ArrayList<Uri> audioUris, ArrayList<Uri> videoUris) {
        if (newPost != null) {
            addPostResultReceiver = new AddPostResultReceiver();
            registerReceiver(addPostResultReceiver, new IntentFilter(ACTION_SEND_ADD_POST_RESULT));
            PostUtility.addPost(newPost.getTitle(), newPost.isFirstHand(), newPost.getDate(),
                    newPost.getLatitude(), newPost.getLongitude(), newPost.getDescription(), tags, imageUris,
                    audioUris, videoUris, this);
        }
    }

    public class AddPostResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("success")) {
                boolean success = intent.getBooleanExtra("success", false);
                if (success) {
                    Log.d(TAG, "onReceive: ");
                    unregisterReceiver(addPostResultReceiver);
                    finish();
                }
            }
        }
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setNewPost(NewPost newPost) {
        this.newPost = newPost;
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public void returnAddress(Address address) {
        if (address != null) {
            Log.d(TAG, "returnAddress: " + address.getAddressLine(0));
            textViewLocation.setTextColor(Color.BLACK);
            textViewLocation.setText(address.getAddressLine(0));
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);

        textViewDate = findViewById(R.id.textViewDateFormat);
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
        displayPostPt1();

    }

    public void displayPostPt1() {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutNewPost, new PostPt1Fragment()).commit();
    }

    public void displayPostPt2() {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutNewPost, new PostPt2Fragment()).addToBackStack(null).commit();
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
                    final LocationUtility.GetAddressAsyncTask addressAsyncTask = new LocationUtility.GetAddressAsyncTask(this);
                    addressAsyncTask.execute(new LatLng(latitude, longitude));
                }
            }
        }
    }
}