package com.example.highstrangeness.ui.account.edit_account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.utilities.FormValidationUtility;
import com.example.highstrangeness.utilities.StorageUtility;
import com.example.highstrangeness.utilities.UserAuthUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = "EditAccountActivity";
    public static final int REQUEST_CODE_FILE_PICKER = 0x0456;

    ImageView imageViewProfilePic;
    EditText editTextEmail;
    EditText editTextUsername;
    Button buttonEditProfilePic;
    Button buttonEditPassword;
    Button buttonSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        imageViewProfilePic = findViewById(R.id.imageViewProfilePictureEditAccountScreen);
        editTextEmail = findViewById(R.id.editTextEditEmail);
        editTextUsername = findViewById(R.id.editTextEditUsername);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonEditProfilePic = findViewById(R.id.buttonEditProfilePic);
        buttonEditPassword = findViewById(R.id.buttonEditPassword);

        StorageUtility.getProfileImage(User.currentUser.getId(), 2, imageViewProfilePic, this);
        editTextEmail.setText(User.currentUser.getEmail());
        editTextUsername.setText(User.currentUser.getUsername());

        setTitle(R.string.button_edit_account);

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkTextInputs();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkTextInputs();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        buttonEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
            }
        });

        buttonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updateEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email address updated");
                            }
                        }
                    });

                    //set username
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editTextUsername.getText().toString()).build();
                    user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Username updated");
                        }
                    });

                    Toast.makeText(getBaseContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkTextInputs() {
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        if ((!email.equals(User.currentUser.getEmail()) || !username.equals(User.currentUser.getUsername())) &&
                (FormValidationUtility.validateEmail(email, editTextEmail) &&
                        FormValidationUtility.validateUsername(username, editTextUsername))) {
            buttonSaveChanges.setVisibility(View.VISIBLE);
        }else {
            buttonSaveChanges.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {

            Uri image = data.getData();
            if (image != null) {
                boolean success = StorageUtility.updateProfileImage(image, this);
                if (success) {
                    Log.d(TAG, "onActivityResult: Path = " + image.toString());
                    StorageUtility.getProfileImage(User.currentUser.getId(), 2,
                            imageViewProfilePic, this);
                }
            }

        }
    }
}