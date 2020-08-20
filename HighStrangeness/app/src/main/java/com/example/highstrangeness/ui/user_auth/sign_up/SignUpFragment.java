package com.example.highstrangeness.ui.user_auth.sign_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.FormValidationUtility;
import com.example.highstrangeness.utilities.UserAuthUtility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class
SignUpFragment extends Fragment {

    public static final String INTENT_FILTER = "SIGN_UP_RESPONSE";

    public interface SignUpListener {
        public void signUp(String email, String username, String password);
    }

    public interface DisplayLoginFragmentListener {
        void displayLogIn();
    }

    SignUpListener signUpListener;
    DisplayLoginFragmentListener displayLoginFragmentListener;
    SignUpResponseReceiver signUpResponseReceiver;
    EditText editTextEmail;
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonSignUp;
    Button buttonLogIn;
    Button buttonSkip;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            signUpListener = (SignUpListener) getActivity();
            displayLoginFragmentListener = (DisplayLoginFragmentListener) getActivity();
            editTextEmail = (EditText) getActivity().findViewById(R.id.editTextTextEmailAddressSignUpScreen);
            editTextUsername = (EditText) getActivity().findViewById(R.id.editTextTextUsernameSignUpScreen);
            editTextPassword = (EditText) getActivity().findViewById(R.id.editTextTextPasswordSignUpScreen);
            signUpResponseReceiver = new SignUpResponseReceiver();
            getActivity().registerReceiver(signUpResponseReceiver, new IntentFilter(INTENT_FILTER));

            editTextEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FormValidationUtility.validateEmail(charSequence.toString(), editTextEmail);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            editTextPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FormValidationUtility.validatePasswordCreation(charSequence.toString(), editTextPassword);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            editTextUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FormValidationUtility.validateUsername(charSequence.toString(), editTextUsername);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            getActivity().findViewById(R.id.buttonSignUpSignUpScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = editTextEmail.getText().toString();
                    String username = editTextUsername.getText().toString();
                    String password = editTextPassword.getText().toString();

                    if (FormValidationUtility.validateEmail(email, editTextEmail) &&
                            FormValidationUtility.validateUsername(username, editTextUsername) &&
                            FormValidationUtility.validatePasswordCreation(password, editTextPassword)) {
                        signUpListener.signUp(email, username, password);
                    }
                }
            });
            getActivity().findViewById(R.id.buttonLogInSignUpScreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayLoginFragmentListener.displayLogIn();
                }
            });
            buttonSkip = (Button) getActivity().findViewById(R.id.buttonSkipSignUpScreen);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(signUpResponseReceiver);
        }
    }

    public class SignUpResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(INTENT_FILTER)) {
                editTextEmail.setError(intent.getStringExtra(UserAuthUtility.SIGN_UP_INTENT_EXTRA));
            }
        }
    }
}