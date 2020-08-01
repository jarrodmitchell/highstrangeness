package com.example.highstrangeness.utilities;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

public class FormValidationUtility {

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
        }
        editTexUsername.setError(null);
        return true;
    }
}
