package com.example.highstrangeness.ui.user_auth.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.user_auth.sign_up.SignUpFragment;
import com.example.highstrangeness.utilities.FormValidationUtility;
import com.example.highstrangeness.utilities.UserAuthUtility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public static final String INTENT_FILTER = "LOG IN RESPONSE";

    public interface LoginListener {
        void login(String email, String password);
    }

    public interface NavigateToResetPasswordActivity {
        void navigateToResetPasswordActivity();
    }

    public interface DisplaySignUpFragmentListener {
        void displaySignUp();
    }

    LoginListener loginListener;
    DisplaySignUpFragmentListener displaySignUpFragmentListener;
    NavigateToResetPasswordActivity navigateToResetPasswordActivity;
    LoginResponseReceiver loginResponseReceiver;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSignUp;
    Button buttonForgotPassword;
    Button skip;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            loginListener = (LoginListener) getActivity();
            displaySignUpFragmentListener = (DisplaySignUpFragmentListener) getActivity();
            navigateToResetPasswordActivity = (NavigateToResetPasswordActivity) getActivity();
            editTextEmail = (EditText) getActivity().findViewById(R.id.editTextTextEmailAddressLogIn);
            editTextPassword =  (EditText) getActivity().findViewById(R.id.editTextTextPasswordLogIn);
            loginResponseReceiver = new LoginFragment.LoginResponseReceiver();
            getActivity().registerReceiver(loginResponseReceiver, new IntentFilter(INTENT_FILTER));

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
                    FormValidationUtility.validatePassword(charSequence.toString(), editTextPassword);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //Login Button Tapped
            getActivity().findViewById(R.id.buttonLogInLogIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = editTextEmail.getText().toString();
                    String password  = editTextPassword.getText().toString();

                    //Validate input values
                    if (FormValidationUtility.validateEmail(email, editTextEmail) &&
                            FormValidationUtility.validatePassword(password, editTextPassword)) {
                        //Attempt to log in
                        loginListener.login(email, password);
                    }
                }
            });

            //Sign Up Button Tapped
            getActivity().findViewById(R.id.buttonSignUpLogIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Navigate to sign up screen
                    displaySignUpFragmentListener.displaySignUp();
                }
            });

            //Forgot password button tapped
             getActivity().findViewById(R.id.buttonForgotPassword).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     navigateToResetPasswordActivity.navigateToResetPasswordActivity();
                 }
             });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(loginResponseReceiver);
        }
    }

    public class LoginResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(INTENT_FILTER)) {
                editTextEmail.setError(intent.getStringExtra(UserAuthUtility.LOG_IN_INTENT_EXTRA));
            }
        }
    }
}