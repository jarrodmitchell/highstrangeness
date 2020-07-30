package com.example.highstrangeness.ui.user_auth.login;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.highstrangeness.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public interface LoginListener {
        void login(String email, String password);
    }

    LoginListener loginListener;
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
            editTextEmail = (EditText) getActivity().findViewById(R.id.editTextTextEmailAddressLogIn);
            editTextPassword =  (EditText) getActivity().findViewById(R.id.editTextTextPasswordLogIn);

            //Login Button Tapped
            getActivity().findViewById(R.id.buttonLogInLogIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = editTextEmail.getText().toString();
                    String password  = editTextPassword.getText().toString();

                    //Validate input values
                    if (validateEmail(email) && validatePassword(password)) {
                        loginListener.login(email, password);
                    }
                }
            });


            buttonSignUp = (Button) getActivity().findViewById(R.id.buttonSignUpLogIn);
            buttonForgotPassword = (Button) getActivity().findViewById(R.id.buttonForgotPassword);
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email cannot be empty");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid Email");
            return false;
        }
        editTextEmail.setError(null);
        return true;
    }

    private boolean validatePassword(String password)  {
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty");
            return false;
        }
        editTextPassword.setError(null);
        return true;
    }
}