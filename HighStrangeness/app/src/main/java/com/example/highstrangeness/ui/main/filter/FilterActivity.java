package com.example.highstrangeness.ui.main.filter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.highstrangeness.R;
import com.example.highstrangeness.dialogs.DatePickerFragment;
import com.example.highstrangeness.objects.Filter;
import com.example.highstrangeness.ui.account.AccountActivity;
import com.example.highstrangeness.ui.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    
    public static final String TAG = "FilterActivity";

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);

        if (datePicked.equals("start")) {
            if (endDate != null && endDate.getTime() < calendar.getTime().getTime()) {
                Toast.makeText(FilterActivity.this, "The Start date should be before the End date", Toast.LENGTH_SHORT).show();
            }else{
                textViewStartDate.setTextColor(Color.BLACK);
                startDate = calendar.getTime();
                textViewStartDate.setText(DateFormat.getDateInstance().format(startDate));
            }
        }

        if (datePicked.equals("end")) {
            if (startDate != null && startDate.getTime() > calendar.getTime().getTime()) {
                Toast.makeText(FilterActivity.this, "The End date should be after the Start date", Toast.LENGTH_SHORT).show();
            }else{
                textViewEndDate.setTextColor(Color.BLACK);
                endDate = calendar.getTime();
                textViewEndDate.setText(DateFormat.getDateInstance().format(endDate));
            }
        }
    }

    Spinner spinner;
    TextView textViewStartDate;
    TextView textViewEndDate;
    CheckBox checkBoxImage;
    CheckBox checkBoxAudio;
    CheckBox checkBoxVideo;
    String datePicked = "";
    Date startDate;
    Date endDate;
    String tag;
    boolean hasImage;
    boolean hasAudio;
    boolean hasVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Log.d(TAG, "onCreate: ");

        spinner = findViewById(R.id.spinnerTag);
        textViewStartDate = findViewById(R.id.textViewStartDateFormat);
        textViewEndDate = findViewById(R.id.textViewEndDateFormat);


        final ArrayList<String> tagList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = db.collection("tags").document("tags");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null &&
                        task.getResult().getData() != null) {
                    Log.d(TAG, "onComplete: success");
                    tagList.add("Select Tag");
                    ArrayList<String> tags = (ArrayList<String>) task.getResult().getData().get("tags");
                    if (tags != null) {
                        tagList.addAll(tags);
                    }

                    spinner.setAdapter(new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_dropdown_item, tagList));

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i > 0) {
                                tag = tagList.get(i);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    if (Filter.filter != null && Filter.filter.getTag() != null) {
                        spinner.setSelection(tagList.indexOf(Filter.filter.getTag()));
                    }
                }
            }
        });

        findViewById(R.id.buttonPickStartDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), null);
                datePicked = "start";
            }
        });

        findViewById(R.id.buttonPickEndDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), null);
                datePicked = "end";
            }
        });

        checkBoxImage = findViewById(R.id.checkboxImage);
        checkBoxImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hasImage = b;
            }
        });

        checkBoxAudio = findViewById(R.id.checkboxAudio);
        checkBoxAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hasAudio = b;
            }
        });

        checkBoxVideo = findViewById(R.id.checkboxVideo);
        checkBoxVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hasVideo = b;
            }
        });

        if (Filter.filter != null) {
            if (Filter.filter.getStartDate() != null) {
                startDate = Filter.filter.getStartDate();
                textViewStartDate.setTextColor(Color.BLACK);
                textViewStartDate.setText(DateFormat.getDateInstance().format(startDate));
            }
            if (Filter.filter.getEndDate() != null) {
                endDate = Filter.filter.getEndDate();
                textViewEndDate.setTextColor(Color.BLACK);
                textViewEndDate.setText(DateFormat.getDateInstance().format(endDate));
            }
            hasImage = Filter.filter.isHasImages();
            hasAudio = Filter.filter.isHasAudio();
            hasVideo = Filter.filter.isHasVideo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_clear) {
            clearFilters();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearFilters() {
        spinner.setSelection(0);
        textViewStartDate.setText(R.string.mm_dd_yyyy);
        textViewStartDate.setTextColor(ContextCompat.getColor(this, R.color.gray));
        textViewEndDate.setText(R.string.mm_dd_yyyy);
        textViewEndDate.setTextColor(ContextCompat.getColor(this, R.color.gray));
        checkBoxImage.setChecked(false);
        checkBoxAudio.setChecked(false);
        checkBoxVideo.setChecked(false);

        datePicked = "";
        tag = null;
        startDate = null;
        endDate = null;
        hasImage = false;
        hasAudio = false;
        hasVideo = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tag != null || startDate != null || endDate != null || hasImage || hasAudio || hasVideo) {
            Filter.filter = new Filter(tag, startDate, endDate, hasImage, hasAudio, hasVideo);
        }else {
            Filter.filter = null;
        }
    }
}