package com.example.highstrangeness.ui.user_auth.reset_password;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.FormValidationUtility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    public static final String TAG = "ResetPasswordFragment";

    public interface ResetEmailListener {
        void resetEmail(String email);
    }

    ResetEmailListener resetEmailListener;
    EditText editTextEmail;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            resetEmailListener = (ResetEmailListener) getActivity();
            editTextEmail = (EditText) getActivity().findViewById(R.id.editTextEmailReset);
            ((Button) getActivity().findViewById(R.id.buttonResetRequest)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: reset");
                    String email = editTextEmail.getText().toString();
                    if (FormValidationUtility.validateEmail(email, editTextEmail)) {
                        resetEmailListener.resetEmail(email);
                    }
                }
            });
        }

    }
}