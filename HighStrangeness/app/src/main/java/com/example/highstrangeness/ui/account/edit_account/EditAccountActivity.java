package com.example.highstrangeness.ui.account.edit_account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = "EditAccountActivity";
    public static final int REQUEST_CODE_FILE_PICKER = 0x0456;

    ImageView imageViewProfilePic;
    EditText editTextEmail;
    EditText editTextUsername;
    Button buttonEditProfilePic;
    Button buttonEditEmail;
    Button buttonEditPassword;
    Button buttonSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        setTitle(R.string.button_edit_account);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        imageViewProfilePic = findViewById(R.id.imageViewProfilePictureEditAccountScreen);
        editTextEmail = findViewById(R.id.editTextEditEmail);
        editTextUsername = findViewById(R.id.editTextEditUsername);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonEditProfilePic = findViewById(R.id.buttonEditProfilePic);
        buttonEditEmail = findViewById(R.id.buttonEditEmail);
        buttonEditPassword = findViewById(R.id.buttonEditPassword);

        StorageUtility.getProfileImage(User.currentUser.getId(), 2, imageViewProfilePic, this);
        if (user != null) {
            editTextEmail.setText(user.getEmail());
            editTextUsername.setText(user.getDisplayName());
        }


        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkUsernameInput();
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

        buttonEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassword("email");
            }
        });

        buttonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassword("password");
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = editTextUsername.getText().toString();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    if (!username.equals(user.getDisplayName())) {
                        //set username
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Username updated");
                                displayResults("Username Updated");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> docData = new HashMap<>();
                                docData.put("username", username);
                                db.collection("user").document(user.getUid()).set(docData);
                                buttonSaveChanges.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void checkUsernameInput() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = editTextEmail.getText().toString();
            String username = editTextUsername.getText().toString();
            if ((!email.equals(user.getEmail()) || !username.equals(user.getDisplayName())) &&
                    (FormValidationUtility.validateEmail(email, editTextEmail) &&
                            FormValidationUtility.validateUsername(username, editTextUsername))) {
                buttonSaveChanges.setVisibility(View.VISIBLE);
            }else {
                buttonSaveChanges.setVisibility(View.GONE);
            }
        }
    }

    private void getPassword(final String updateValue) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enter Password");
        final EditText editText = new EditText(this);
        alertDialog.setView(editText);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = editText.getText().toString();
                    Log.d(TAG, "onClick: in");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && user.getEmail() != null) {
                        if (password.trim().isEmpty()) {
                            password = "p";
                        }
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), password);

                        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: ");
                                switch (updateValue) {
                                    case "password":
                                        getNewPassword();
                                        break;
                                    case "email":
                                        getNewEmail();
                                        break;
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                                alertDialog.show();
                                editText.setError(e.getMessage());
                            }
                        });
                    }else {
                        Log.d(TAG, "onClick: didn't make it");
                    }
            }
        });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                setDialogButtonTextColors(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE),
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE));
            }
        });
        alertDialog.show();
    }

    public void setDialogButtonTextColors(Button buttonPositive, Button buttonNegative) {
        buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.pink));
        buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void getNewEmail() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enter New Email");
        final EditText editText = new EditText(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = editText.getText().toString();
                if (FormValidationUtility.validateEmail(email, editText)) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }else {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        alertDialog.setView(editText);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = editText.getText().toString();
                if (FormValidationUtility.validateEmail(email, editText)) {
                    updateEmail(email);
                    alertDialog.dismiss();
                }else {
                    alertDialog.show();
                    editText.setError("Invalid email");
                }
            }
        });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                setDialogButtonTextColors(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE),
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE));
            }
        });
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void updateEmail(String email) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && !email.equals(user.getEmail())) {
            Log.d(TAG, "onClick: " + user.getEmail());
            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Email address updated");
                    editTextEmail.setText(user.getEmail());
                    displayResults("Email Updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void getNewPassword() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enter New Password");
        final EditText editText = new EditText(this);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = editText.getText().toString();
                if (FormValidationUtility.validatePasswordCreation(password, editText)) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }else {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        alertDialog.setView(editText);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = editText.getText().toString();
                if (FormValidationUtility.validatePasswordCreation(password, editText)) {
                    updatePassword(password);
                }
            }
        });
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void updatePassword(String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Password updated");
                    displayResults("Password Updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    public void displayResults(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {

            Uri image = data.getData();
            if (image != null) {
                Log.d(TAG, "onActivityResult: image = " + image.getLastPathSegment());
                StorageUtility.updateProfileImage(image, this, imageViewProfilePic, 2);
            }

        }
    }
}