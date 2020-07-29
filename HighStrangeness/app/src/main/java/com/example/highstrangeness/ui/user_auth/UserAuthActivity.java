package com.example.highstrangeness.ui.user_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.user_auth.login.LoginFragment;

public class UserAuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);
        displayLogInFragment();
    }

    public void displayLogInFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutUserAuth, new LoginFragment()).commit();
    }
}