package com.shoppinglist.rdproject.shoppinglist.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shoppinglist.rdproject.shoppinglist.MainScreen;
import com.shoppinglist.rdproject.shoppinglist.R;

import org.json.JSONObject;

import static com.shoppinglist.rdproject.shoppinglist.MainScreen.APP_PREFERENCES;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener {

    private final String TAG = "LoginActivityTAG";
    public static final int RC_SIGN_IN_FACEBOOK = 9002;
    private Button  customSigninButton, customSignupButton, logoutButton;
    private EditText emailEditText, passwordEditText;
    private ImageView userPic;
    private TextView appOrUserName, verifyEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleLoginHelper googleLoginHelper;
    private GoogleSignInAccount googleSignInAccount;
    private LoginButton facebookLoginButton;
    private SignInButton googleLoginButton;
    private CallbackManager mCallbackManager;
    private SharedPreferences mSettings;

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();
        setTitle(R.string.back_to_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bindViews();
        setListeners();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mCallbackManager = CallbackManager.Factory.create();

        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });
        // [END initialize_fblogin]
    }


    @Override
    protected void onStart() {
        super.onStart();
        refreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshLayout();
    }

    public void refreshLayout() {

        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().equals("")) {
                appOrUserName.setText(currentUser.getDisplayName());
            } else {
                if (currentUser.getEmail() != null && !currentUser.getEmail().equals("")){
                    appOrUserName.setText(currentUser.getEmail());
                } else {
                    appOrUserName.setText(R.string.app_name);
                }
            }
            facebookLoginButton.setVisibility(View.GONE);
            googleLoginButton.setVisibility(View.GONE);
            customSigninButton.setVisibility(View.GONE);
            customSignupButton.setVisibility(View.GONE);
            emailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
                if (!currentUser.isEmailVerified() && currentUser.getEmail() != null && !currentUser.getEmail().equals("")) {
                    verifyEmail.setVisibility(View.VISIBLE);
                    verifyEmail.setEnabled(true);
                } else {
                    verifyEmail.setVisibility(View.GONE);
                }
//            if (currentUser.getPhotoUrl() != null) {
//                Picasso.get().load(currentUser.getPhotoUrl()).into(userPic);
//            }
        } else {
            appOrUserName.setText(R.string.app_name);
            facebookLoginButton.setVisibility(View.VISIBLE);
            googleLoginButton.setVisibility(View.VISIBLE);
            customSigninButton.setVisibility(View.VISIBLE);
            customSignupButton.setVisibility(View.VISIBLE);
            emailEditText.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            verifyEmail.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleLoginHelper.RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                googleSignInAccount = task.getResult(ApiException.class);
                googleLoginHelper.firebaseAuthWithGoogle(googleSignInAccount);
                //initializeLocalUserFromGoogle();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                setCurrentUser(null);
                refreshLayout();
                return;
            }
        }
        else  {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setListeners() {
        //facebookLoginButton.setOnClickListener(this);
        googleLoginButton.setOnClickListener(this);
        customSigninButton.setOnClickListener(this);
        customSignupButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        verifyEmail.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (currentUser != null) {
            Intent intent = new Intent();
            intent.putExtra("userName", currentUser.getDisplayName());
            intent.putExtra("userMail", currentUser.getEmail());
            intent.putExtra("userPic", currentUser.getPhotoUrl());
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    private void bindViews() {
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        googleLoginButton =  findViewById(R.id.google_login_button);
        customSigninButton = (Button) findViewById(R.id.custom_signin_button);
        customSignupButton = (Button) findViewById(R.id.custom_signup_button);
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        logoutButton = (Button) findViewById(R.id.logout_button);
        appOrUserName = (TextView)findViewById(R.id.app_name_or_info);
        verifyEmail = (TextView)findViewById(R.id.verify);
        userPic = findViewById(R.id.app_or_user_picture);
    }

    @Override
    public void onClick(View view) {
        String email;
        String password;
        switch (view.getId()){
//            case  R.id.facebook_login_button:
//                break;
            case  R.id.google_login_button:{
                googleLoginHelper = new GoogleLoginHelper(this, mAuth);
                googleLoginHelper.signIn();
                break;
            }

            case  R.id.custom_signin_button: {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                customSignin(email, password);
                break;
            }
            case  R.id.custom_signup_button: {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                createAccount(email, password);
            }
            case  R.id.logout_button:{
                customSignOut();
                break;
            }
            case R.id.verify: {
                sendEmailVerification();
            }
        }
    }

    // [START sign_in_with_email]
    private void customSignin(String email, String password){
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in localUser's information
                            Log.d(TAG, "signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            refreshLayout();
                            hideKeyboard(passwordEditText);
                        } else {
                            // If sign in fails, display a message to the localUser.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            currentUser = null;
                            refreshLayout();
                        }
                        hideProgressDialog();
                    }
                });
    }
    // [END sign_in_with_email]

    // [START create_user_with_email]
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in localUser's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "You can sign in now!",
                                    Toast.LENGTH_SHORT).show();
                           refreshLayout();
                        } else {
                            // If sign in fails, display a message to the localUser.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            refreshLayout();
                        }
                        hideProgressDialog();
                    }
                });
    }
    // [END create_user_with_email]

    private void customSignOut() {
        mAuth.signOut();
        currentUser = null;
        try {
            googleLoginHelper.signOut();
        }
        catch (NullPointerException e){
            //do nothing
        }
        try {
            LoginManager.getInstance().logOut();
        }
        catch (NullPointerException e){
            //do nothing
        }
        try {
            removeUserMailFromPreferences();
        } catch (Exception e) {
            //do nothing
        }
        refreshLayout();
    }

    private void sendEmailVerification() {

        // Disable button
        verifyEmail.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //verifyEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setCurrentUser(user);
                            refreshLayout();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            setCurrentUser(null);
                            refreshLayout();
                        }
                        hideProgressDialog();
                    }
                });

        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String userEmail = object.getString("email");
                            saveUserMailToPreferences(userEmail);
                            Log.d(TAG, userEmail);
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void saveUserMailToPreferences(String userEmail) {
        SharedPreferences.Editor ed = mSettings.edit();
        ed.putString(MainScreen.APP_PREFERENCES_USER_EMAIL, userEmail);
        ed.apply();
    }

    private void removeUserMailFromPreferences() {
        SharedPreferences.Editor ed = mSettings.edit();
        ed.remove(MainScreen.APP_PREFERENCES_USER_EMAIL);
        ed.apply();
    }
 }


