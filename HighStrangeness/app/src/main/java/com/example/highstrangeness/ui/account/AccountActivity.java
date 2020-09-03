package com.example.highstrangeness.ui.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.Filter;
import com.example.highstrangeness.ui.account.edit_account.EditAccountActivity;
import com.example.highstrangeness.ui.account.my_posts.MyPostsActivity;
import com.example.highstrangeness.ui.main.filter.FilterActivity;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    
    public static final String TAG = "AccountActivity";
    public static final int REQUEST_CODE_EDIT_ACCOUNT = 0x0431;
    public static final int REQUEST_CODE_MY_POSTS = 0x0432;

    ImageView imageViewProfilePic;
    TextView textViewEmail;
    TextView textViewUsername;
    Button buttonEditAccount;
    Button buttonFavorites;
    Button buttonLogOut;
    String uId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textViewUsername = findViewById(R.id.textViewUsernameAccountScreen);
        textViewEmail = findViewById(R.id.textViewEmailAccountScreen);
        buttonEditAccount = findViewById(R.id.buttonEditAccount);
        buttonFavorites  = findViewById(R.id.buttonFavorites);
        buttonLogOut = findViewById(R.id.buttonLogOut);

        if (getIntent() != null && getIntent().hasExtra("uid")) {
            uId = getIntent().getStringExtra("uid");
            buttonEditAccount.setVisibility(View.GONE);
            buttonFavorites.setVisibility(View.GONE);
            buttonLogOut.setVisibility(View.GONE);
        }else {

            buttonEditAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountActivity.this, EditAccountActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_ACCOUNT);
                }
            });

        }
        findViewById(R.id.buttonMyPosts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MyPostsActivity.class);
                if (uId != null) {
                    intent.putExtra("uid", uId);
                }
                intent.putExtra("title", "My Posts");
                startActivityForResult(intent, REQUEST_CODE_MY_POSTS);
            }
        });
        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MyPostsActivity.class);
                intent.putExtra("title", "Favorites");
                startActivity(intent);
            }
        });
        updateViews();

        setTitle("");
    }

    private void updateViews()  {
        imageViewProfilePic = findViewById(R.id.imageViewAddProfilePictureAccountScreen);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (uId == null && user != null) {
            Log.d(TAG, "updateViews: username = " + user.getDisplayName());
            Log.d(TAG, "updateViews: email = " + user.getEmail());

            StorageUtility.setProfileImage(this, user.getUid(), 2, imageViewProfilePic);
            textViewUsername.setText(user.getDisplayName());
            textViewEmail.setText(user.getEmail());
            buttonLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }else{
            StorageUtility.setProfileImage(this, uId, 2, imageViewProfilePic);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = db.collection("user").document(uId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null &&
                            task.getResult().getData() != null) {
                        Log.d(TAG, "onComplete: success");
                        String username = (String) task.getResult().getData().get("username");
                        if (username != null) {
                            textViewUsername.setText(username);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_EDIT_ACCOUNT) {
                updateViews();
            }
        }
    }
}