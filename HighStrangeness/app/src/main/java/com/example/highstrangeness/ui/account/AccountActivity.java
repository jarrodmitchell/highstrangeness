package com.example.highstrangeness.ui.account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.account.edit_account.EditAccountActivity;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    
    public static final String TAG = "AccountActivity";
    public static final int REQUEST_CODE = 0x0321;

    ImageView imageViewProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        updateViews();

        findViewById(R.id.buttonEditAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, EditAccountActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        setTitle("");
    }

    private void updateViews()  {
        imageViewProfilePic = ((ImageView) findViewById(R.id.imageViewProfilePictureAccountScreen));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "updateViews: username = " + user.getDisplayName());
            Log.d(TAG, "updateViews: email = " + user.getEmail());


            StorageUtility.setProfileImage(user.getUid(), 2, imageViewProfilePic);
            ((TextView) findViewById(R.id.textViewUsernameAccountScreen)).setText(user.getDisplayName());
            ((TextView) findViewById(R.id.textViewEmailAccountScreen)).setText(user.getEmail());
            ((Button) findViewById(R.id.buttonLogOut)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    setResult(MainActivity.REQUEST_CODE_ACCOUNT_SCREEN);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateViews();
    }
}