package com.example.highstrangeness.ui.user_auth.reset_password;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.highstrangeness.R;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutResetPassword, new ResetPasswordFragment()).commit();
    }
}