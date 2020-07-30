package com.example.highstrangeness.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.highstrangeness.objects.User;
import com.example.highstrangeness.ui.user_auth.UserAuthActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class UserAuthUtility {
    public static final String TAG = "UserAuthUtility";

    public UserAuthUtility(AppCompatActivity UserAuthActivity) {
        this.getFirebaseAuthListener = (GetFirebaseAuthListener) UserAuthActivity;
        this.getAuthActivityContext = (GetUserAuthActivityContext) UserAuthActivity;
    }

    public interface GetFirebaseAuthListener {
        public FirebaseAuth getFirebaseAuth();
    }
    public interface GetUserAuthActivityContext {
        public Context getContext();
    }

    private static FirebaseUser user;

    GetFirebaseAuthListener getFirebaseAuthListener;
    GetUserAuthActivityContext getAuthActivityContext;

    public FirebaseUser login(String email, String password) throws IOException {
        getFirebaseAuthListener.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getAuthActivityContext.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in succeeded
                            Log.d(TAG, "signIn: succeeded");
                            user = getFirebaseAuthListener.getFirebaseAuth().getCurrentUser();

                        } else {
                            // Sign in failed
                            Log.w(TAG, "signIn: failed", task.getException());
                            Toast.makeText(getAuthActivityContext.getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            user = null;
                        }
                    }
                });
        return user;
    }
}
