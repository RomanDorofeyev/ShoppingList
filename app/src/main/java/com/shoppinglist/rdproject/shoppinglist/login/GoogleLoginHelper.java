package com.shoppinglist.rdproject.shoppinglist.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shoppinglist.rdproject.shoppinglist.MainScreen;
import com.shoppinglist.rdproject.shoppinglist.R;

import static com.shoppinglist.rdproject.shoppinglist.MainScreen.APP_PREFERENCES;

public class GoogleLoginHelper {
    private static final String TAG = "GoogleActivity";
    public static final int RC_SIGN_IN_GOOGLE = 9001;
    private FirebaseAuth mAuth;
    private LoginActivity activity;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences mSettings;

    public GoogleLoginHelper(Activity activity, FirebaseAuth mAuth) {
        this.mAuth = mAuth;
        this.activity = (LoginActivity)activity;
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mSettings = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }
    // [START auth_with_google]
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        activity.showProgressDialog();
        // [END_EXCLUDE]
            String userEmail = acct.getEmail();
            saveUserMailToPreferences(userEmail);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            activity.setCurrentUser(user);
                            activity.refreshLayout();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            activity.setCurrentUser(null);
                            activity.refreshLayout();
                        }

                        // [START_EXCLUDE]
                        activity.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //activity.refreshLayout();
                    }
                });
    }

    private void saveUserMailToPreferences(String userEmail) {
        SharedPreferences.Editor ed = mSettings.edit();
        ed.putString(MainScreen.APP_PREFERENCES_USER_EMAIL, userEmail);
        ed.apply();
    }

}
