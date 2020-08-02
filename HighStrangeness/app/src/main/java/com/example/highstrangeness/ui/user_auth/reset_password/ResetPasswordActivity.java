package com.example.highstrangeness.ui.user_auth.reset_password;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.UserAuthUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements
        ResetPasswordFragment.ResetEmailListener {

    public static final String TAG = "ResetPasswordActivity";

    @Override
    public void resetEmail(String email) {
        sendResetPasswordEmail(email);
        Toast.makeText(this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutResetPassword, new ResetPasswordFragment()).commit();
    }



    public void sendResetPasswordEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}