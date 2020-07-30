package com.example.highstrangeness.ui.user_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.user_auth.login.LoginFragment;
import com.example.highstrangeness.utilities.NetworkUtility;
import com.example.highstrangeness.utilities.UserAuthUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class UserAuthActivity extends AppCompatActivity implements UserAuthUtility.GetFirebaseAuthListener,
        UserAuthUtility.GetUserAuthActivityContext,
        LoginFragment.LoginListener {

    public static final String TAG = "UserAuthActivity";

    @Override
    public void login(String email, String password) {
        try {
            if (NetworkUtility.CheckNetworkConnection(this)) {
                checkIfUserIsAlreadySignedIn(userAuthUtility.login(email, password));
            };
        }catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    @Override
    public Context getContext() {
        return this;
    }


    private FirebaseAuth mAuth;
    private UserAuthUtility userAuthUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        mAuth = FirebaseAuth.getInstance();
        userAuthUtility  = new UserAuthUtility(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        checkIfUserIsAlreadySignedIn(currentUser);
    }

    public void checkIfUserIsAlreadySignedIn(FirebaseUser currentUser) {

        if (currentUser != null) {
            String id = currentUser.getUid();
            String email = currentUser.getEmail();
            String username = currentUser.getDisplayName();
            if (email != null && username != null) {
                User user = new User(id, email, username);
                user.setCurrentUser(user);
                navigateToMainActivity();
            }
        }else {
            displayLogInFragment();
        }
    }

    public void displayLogInFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutUserAuth, LoginFragment.newInstance()).commit();
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(UserAuthActivity.this, MainActivity.class);
        UserAuthActivity.this.startActivity(intent);
    }
}