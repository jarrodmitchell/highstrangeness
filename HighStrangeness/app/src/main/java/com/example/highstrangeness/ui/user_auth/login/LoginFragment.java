package com.example.highstrangeness.ui.user_auth.login;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogIn;
    Button buttonSignUp;
    Button buttonForgotPassword;
    Button skip;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
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
            editTextEmail = (EditText) getActivity().findViewById(R.id.editTextTextEmailAddressLogIn);
            editTextPassword =  (EditText) getActivity().findViewById(R.id.editTextTextPasswordLogIn);
            buttonLogIn = (Button) getActivity().findViewById(R.id.buttonLogInLogIn);
            buttonSignUp = (Button) getActivity().findViewById(R.id.buttonSignUpLogIn);
            buttonForgotPassword = (Button) getActivity().findViewById(R.id.buttonForgotPassword);
        }

    }
}