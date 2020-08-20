package com.example.highstrangeness.ui.post;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.NewPost;
import com.example.highstrangeness.utilities.FormValidationUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostPt1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostPt1Fragment extends Fragment {

    public static final String TAG = "PostPt1Fragment";

    public interface DisplayPostPart2Listener {
        void displayPostPart2();
    }

    public interface DisplayCalenderPickerListener {
        void displayCalenderPicker();
    }

    public interface DisplayLocationPickerListener {
        void displayLocationPicker();
    }

    public interface GetLatLngListener {
        LatLng getLatLng();
    }

    public interface GetDateListener {
        Date getDate();
    }

    public interface SetNewPostListener {
        void setNewPost(NewPost newPost);
    }

    private DisplayPostPart2Listener displayPostPart2Listener;
    private DisplayCalenderPickerListener displayCalenderPickerListener;
    private DisplayLocationPickerListener displayLocationPickerListener;
    private GetLatLngListener getLatLng;
    private GetDateListener getDateListener;
    private SetNewPostListener setNewPostListener;

    public PostPt1Fragment() {
        // Required empty public constructor
    }

    public static PostPt1Fragment newInstance(String param1, String param2) {
        PostPt1Fragment fragment = new PostPt1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    EditText editTextTitle;
    SwitchMaterial switchFirstHand;
    TextView textViewDate;
    TextView textViewLocation;
    EditText editTextDescription;
    Button buttonNext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_pt1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            displayPostPart2Listener = (DisplayPostPart2Listener) getActivity();
            displayCalenderPickerListener = (DisplayCalenderPickerListener) getActivity();
            displayLocationPickerListener = (DisplayLocationPickerListener) getActivity();
            getLatLng = (GetLatLngListener) getActivity();
            getDateListener = (GetDateListener) getActivity();
            setNewPostListener = (SetNewPostListener) getActivity();

            editTextTitle = getActivity().findViewById(R.id.editTextTitle);
            editTextTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FormValidationUtility.validateTitle(charSequence.toString(), editTextTitle);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            editTextDescription = getActivity().findViewById(R.id.editTextDescription);
            editTextDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FormValidationUtility.validateDescription(charSequence.toString(), editTextDescription);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            switchFirstHand = getActivity().findViewById(R.id.switch_first_hand_experience);
            textViewDate = getActivity().findViewById(R.id.textViewDateFormat);
            textViewLocation = getActivity().findViewById(R.id.textViewLocationAddress);

            getActivity().findViewById(R.id.buttonPickDate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayCalenderPickerListener.displayCalenderPicker();
                }
            });

            getActivity().findViewById(R.id.buttonPickLocation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayLocationPickerListener.displayLocationPicker();
                }
            });

            buttonNext = getActivity().findViewById(R.id.buttonNext);
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();

                    if (FormValidationUtility.validateTitle(title, editTextTitle) &&
                    FormValidationUtility.validateDescription(description, editTextDescription) &&
                    FormValidationUtility.validateDate(textViewDate.getText().toString(), textViewDate) &&
                    FormValidationUtility.validateLocation(textViewLocation.getText().toString(), textViewLocation)) {
                        setNewPostListener.setNewPost(new NewPost(title, switchFirstHand.isChecked(), getDateListener.getDate(),
                                getLatLng.getLatLng().latitude, getLatLng.getLatLng().longitude, description));
                        displayPostPart2Listener.displayPostPart2();
                    }else {
                        setNewPostListener.setNewPost(null);
                    }
                }
            });
        }
    }
}