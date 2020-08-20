package com.example.highstrangeness.utilities;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

public class FormValidationUtility {

    public static final String TAG = "FormValidationUtility";

    public static boolean validateEmail(String email, EditText editTextEmail) {
        email = email.trim().toLowerCase();
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

    public static boolean validatePassword(String password, EditText editTextPassword)  {
        password = password.trim();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty");
            return false;
        }
        editTextPassword.setError(null);
        return true;
    }

    public static boolean validatePasswordCreation(String password, EditText editTextPassword)  {
        password = password.trim();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty");
            return false;
        }else if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        editTextPassword.setError(null);
        return true;
    }

    public static boolean validateUsername(String username, EditText editTexUsername)  {
        username = username.trim();
        if (TextUtils.isEmpty(username)) {
            editTexUsername.setError("Username cannot be empty");
            return false;
        }else if (username.length() > 15) {
            editTexUsername.setError("Username cannot exceed 15 characters");
            return false;
        }
        editTexUsername.setError(null);
        return true;
    }

    public static boolean validateTitle(String title, EditText editTextTitle)  {
        title = title.trim();
        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title cannot be empty");
            return false;
        }else if (title.length() > 150) {
            editTextTitle.setError("Title cannot exceed 150 characters");
            return false;
        }
        editTextTitle.setError(null);
        return true;
    }

    public static boolean validateDescription(String description, EditText editTextDescription)  {
        description = description.trim();
        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description cannot be empty");
            return false;
//        }else if (description.length() > 15) {
//            editTextDescription.setError("Description cannot exceed 15 characters");
//            return false;
        }
        editTextDescription.setError(null);
        return true;
    }

    public static boolean validateDate(String date, TextView textViewDate) {
        if (date.equals("MM/dd/yyyy")) {
            textViewDate.setError("A date must be selected");
            return false;
        }else {
            textViewDate.setError(null);
            return true;
        }
    }

    public static boolean validateLocation(String date, TextView textViewLocation) {
        if (date.equals("Address")) {
            textViewLocation.setError("A Location must be selected");
            return false;
        }else {
            textViewLocation.setError(null);
            return true;
        }
    }

}
