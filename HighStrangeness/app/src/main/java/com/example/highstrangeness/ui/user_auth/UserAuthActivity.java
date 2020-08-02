package com.example.highstrangeness.ui.user_auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.ui.user_auth.login.LoginFragment;
import com.example.highstrangeness.ui.user_auth.reset_password.ResetPasswordActivity;
import com.example.highstrangeness.ui.user_auth.sign_up.SignUpFragment;
import com.example.highstrangeness.utilities.NetworkUtility;
import com.example.highstrangeness.utilities.UserAuthUtility;
import com.google.firebase.auth.FirebaseAuth;

public class UserAuthActivity extends AppCompatActivity implements UserAuthUtility.GetFirebaseAuthListener,
        UserAuthUtility.GetUserAuthActivityContextListener, LoginFragment.LoginListener,
        LoginFragment.DisplaySignUpFragmentListener, SignUpFragment.SignUpListener,
        SignUpFragment.DisplayLoginFragmentListener,
        UserAuthUtility.CheckWhetherAUserIsLoggedInListener,
        LoginFragment.NavigateToResetPasswordActivity {

    public static final String TAG = "UserAuthActivity";
    public static final int REQUEST_CODE_LOGGED_OUT = 0x068;



    @Override
    public void checkWhetherUserIsLoggedIn() {
        checkIfUserIsAlreadySignedIn();
    }

    @Override
    public void signUp(String email, String username, String password) {
        if (NetworkUtility.CheckNetworkConnection(this)) {
            userAuthUtility.signUp(email, username, password);
        };
    }

    @Override
    public void displayLogIn() {
        displayLogInFragment();
    }

    @Override
    public void displaySignUp() {
        displaySignUpFragment();
    }

    @Override
    public void login(String email, String password) {
        if (NetworkUtility.CheckNetworkConnection(this)) {
            userAuthUtility.login(email, password);
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
        ;
        mAuth = FirebaseAuth.getInstance();
        userAuthUtility  = new UserAuthUtility(this);
        userAuthUtility.checkForCurrentUser();
        checkIfUserIsAlreadySignedIn();
    }

    public void checkIfUserIsAlreadySignedIn() {
        if (User.currentUser != null) {
            Log.d(TAG, "checkIfUserIsAlreadySignedIn: true");
            navigateToMainActivity();
        }else {
            Log.d(TAG, "checkIfUserIsAlreadySignedIn: false");
            displayLogInFragment();
        }
    }

    public void displayLogInFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutUserAuth, LoginFragment.newInstance()).commit();
    }

    public void displaySignUpFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutUserAuth, SignUpFragment.newInstance()).commit();
    }

    public void navigateToResetPasswordActivity() {
        Intent intent = new Intent(UserAuthActivity.this, ResetPasswordActivity.class);
        UserAuthActivity.this.startActivity(intent);
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(UserAuthActivity.this, MainActivity.class);
        UserAuthActivity.this.startActivityForResult(intent, REQUEST_CODE_LOGGED_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGGED_OUT) {
            userAuthUtility.checkForCurrentUser();
            checkIfUserIsAlreadySignedIn();
        }
    }
}