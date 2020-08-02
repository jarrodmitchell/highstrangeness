package com.example.highstrangeness.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.ui.main.MainActivity;

public class AccountActivity extends AppCompatActivity {
    
    public static final String TAG = "AccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ((TextView) findViewById(R.id.textViewUsernameAccountScreen)).setText(User.currentUser.getUsername());
        ((TextView) findViewById(R.id.textViewEmailAccountScreen)).setText(User.currentUser.getEmail());
        ((Button) findViewById(R.id.buttonLogOut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                setResult(MainActivity.REQUEST_CODE_ACCOUNT_SCREEN);
                finish();
            }
        });

        setTitle("");
    }
}