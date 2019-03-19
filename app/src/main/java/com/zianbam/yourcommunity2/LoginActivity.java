package com.zianbam.yourcommunity2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 5;
    private LoginButton fb_loginBtn;
    private SignInButton google_loginBtn;
    private CallbackManager callbackManager;
    private ImageView image_profile;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TextView togglePass;
    private DatabaseReference ref, reference;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog pd;
    public Button fb, google, email_pass_signinBtn, email_pass_signupBtn;
    private EditText password_field, email_field;
    private RelativeLayout myLayout;
    int i;
    int[] image = {R.drawable.bg1, R.drawable.bg2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Whitetheme);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myLayout = findViewById(R.id.myLayout);
        fb_loginBtn = findViewById(R.id.fb_login_button);
        google_loginBtn = findViewById(R.id.google_login_button);

        fb = (Button) findViewById(R.id.fb);
        google = (Button) findViewById(R.id.google);
        email_pass_signinBtn = findViewById(R.id.email_pass_signinBtn);
        email_pass_signupBtn = findViewById(R.id.email_pass_signupBtn);

        togglePass = findViewById(R.id.togglePass);
        email_field = findViewById(R.id.email_field);
        password_field = findViewById(R.id.password_field);


        showandhidePass();
        configureFacebookLogin();
        configureGoogleLogin();
        fb_login();
        google_login();
        email_password_login();


        setBackgroundimage();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        email_field.clearFocus();
        password_field.clearFocus();

        email_pass_signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);}
        });

        if (firebaseUser == null){
        }else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Log in", Toast.LENGTH_SHORT).show();
        }

    }

    private void setBackgroundimage() {

//        for (i = 1; i<= image.length; ){
//            myLayout.setBackgroundResource(image[i]);
//            ++i;
//        }

    }

    private void email_password_login() {
        email_pass_signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                check neww or old user
                if (!TextUtils.isEmpty(email_field.getText().toString()) &&(!TextUtils.isEmpty(password_field.getText().toString()))){
                    email_pass_signinBtn.setEnabled(false);

                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Please wiat..");
                    pd.show();
                    mAuth.signInWithEmailAndPassword(email_field.getText().toString(), password_field.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
//                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                Toast.makeText(StartActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(LoginActivity.this, "Login succes!", Toast.LENGTH_SHORT).show();
                                    }else if (task.getException().getMessage().toString().equals("The password is invalid or the user does not have a password.")){

                                        pd.dismiss();

                                        Dialog dialog = new Dialog(LoginActivity.this);
                                        dialog.setContentView(R.layout.dialog_simple_msg);
                                        final TextView title = dialog.findViewById(R.id.textTitle);
                                        final TextView msg = dialog.findViewById(R.id.textMsg);
                                        final Button btn = dialog.findViewById(R.id.btn1);
                                        title.setText("Error!");
                                        title.setTextColor(Color.RED);
                                        msg.setText("The password you entered is incorrect.");
                                        msg.setTextColor(Color.RED);
                                        btn.setText("Reset password");
                                        btn.setHeight(10);
                                        btn.setVisibility(View.VISIBLE);
                                        dialog.show();
                                        email_pass_signinBtn.setEnabled(true);

                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                title.setText("Please wait..");
                                                title.setTextColor(Color.BLACK);
                                                msg.setVisibility(View.GONE);
                                                btn.setVisibility(View.GONE);
                                                mAuth.sendPasswordResetEmail(email_field.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            title.setText("We have sent reset password mail to our email address "+email_field.getText().toString() +".");

                                                        }else {
                                                            title.setText("Error!");
                                                            msg.setText(" error: "+task.getException().getMessage().toString());
                                                            msg.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                });

                                            }
                                        });


                                    }else if (task.getException().getMessage().toString().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){

                                        pd.dismiss();
                                        Dialog dialog = new Dialog(LoginActivity.this);
                                        dialog.setContentView(R.layout.dialog_simple_msg);
                                        final TextView title = dialog.findViewById(R.id.textTitle);
                                        final TextView msg = dialog.findViewById(R.id.textMsg);
                                        final Button btn = dialog.findViewById(R.id.btn1);
                                        email_pass_signinBtn.setEnabled(true);

                                        title.setText("Warrning!");
                                        title.setTextColor(Color.BLACK);
                                        msg.setText("There is no user record corresponding to this email [ "+email_field.getText().toString()+ " ].");
                                        msg.setTextColor(Color.BLACK);
                                        msg.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                                        btn.setText("Create account");
                                        btn.setHeight(10);
                                        btn.setVisibility(View.VISIBLE);
                                        dialog.show();

                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                                intent.putExtra("email", email_field.getText().toString());
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            }
                                        });
                                    }
                                }
                            });
                }
                
            }
        });
        
    }

    public void onClick(View v) {
        if (v == fb) {
            fb_loginBtn.performClick();
        }else if(v == google){
            google_login();
        }
    }


    private void showandhidePass() {
        togglePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (togglePass.getTag().toString().equals("show")){
                    togglePass.setTag("hide");
                    togglePass.setText("hide");
                    password_field.setInputType(InputType.TYPE_CLASS_TEXT );

                }else if (togglePass.getTag().toString().equals("hide")){
                    togglePass.setTag("show");
                    togglePass.setText("show");
                    password_field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void configureGoogleLogin() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void configureFacebookLogin() {
        //configure Facebook Sign in
        callbackManager = CallbackManager.Factory.create();
        fb_loginBtn.setReadPermissions(Arrays.asList("email", "public_profile"));
    }
    
    
    //login with email and password


    //facebooklogin
    private void fb_login() {
        fb_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPD(); fb_loginBtn.setVisibility(View.INVISIBLE);
            }
        });
      fb_loginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login cancelled!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                fb_loginBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Login error!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                fb_loginBtn.setVisibility(View.VISIBLE);

            }
        });
    }
    private void handleFacebookAccessToken(final AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            CheckAccountindatabase("facebook");


                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    //googlelogin
    private void google_login(){
                google.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPD();
                        google_loginBtn.setVisibility(View.INVISIBLE);
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            CheckAccountindatabase("google");

                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            google_loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }



    //check if your date exist in our database or not *if not exist send to account set up page
    private void CheckAccountindatabase(final String platform) {
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
//                    Intent intent = new Intent(LoginActivity.this, AccountSetupActivity.class);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginwith", platform);
                    startActivity(intent);
                    pd.dismiss();
                }else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    pd.dismiss();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void showPD() {
        pd = new ProgressDialog(LoginActivity.this, R.style.pdialog_width);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        pd.setIndeterminate(false);
        pd.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                pd.dismiss();
                Toast.makeText(this, "Google sign in Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                google_loginBtn.setVisibility(View.VISIBLE);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
//        @Override
//        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//        }
//    };
//
//    private void UserInfo(AccessToken newAccessToken) {
//        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                try {
//                    String first_name = object.getString("first_name");
//                    String last_name = object.getString("last_name");
//                    String email = object.getString("email");
//                    String id = object.getString("id");
//                    String image_url = "https://graph.facebook.com/"+id+"/picture?type=normal";
//
//                    Toast.makeText(LoginActivity.this, "Login success! p "+first_name + "p", Toast.LENGTH_SHORT).show();
//
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "first_name, last_name, email, id");
//        request.setParameters(parameters);
//        request.executeAsync();
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View view = findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return super.onTouchEvent(event);
    }


}
