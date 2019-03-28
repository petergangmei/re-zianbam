package com.zianbam.yourcommunity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_field, pass1_field, pass2_field;
    private Button signup_btn;
    FirebaseAuth mAuth;
    private ImageView back;
    String youremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Whitetheme);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        
        Intent intent = getIntent();
        youremail = intent.getStringExtra("email");

        back = findViewById(R.id.backBtn);
        email_field = findViewById(R.id.email_field);
        pass1_field = findViewById(R.id.pass1_field);
        pass2_field = findViewById(R.id.pass2_field);
        signup_btn = findViewById(R.id.signupBtn);
        //add email to email field
        if (youremail == null){}else { email_field.setText(youremail); pass1_field.requestFocus(); }
        
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regsiterUser();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void regsiterUser() {
        if (!TextUtils.isEmpty(email_field.getText().toString())&& (!TextUtils.isEmpty(pass1_field.getText().toString()))&&(!TextUtils.isEmpty(pass2_field.getText().toString()))){
            Toast.makeText(this, "every fild is ready", Toast.LENGTH_SHORT).show();
            if (!pass1_field.getText().toString().equals(pass2_field.getText().toString())){
                Toast.makeText(this, "Your password does not match!", Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(RegisterActivity.this);
                dialog.setContentView(R.layout.dialog_simple_msg);
                TextView title = dialog.findViewById(R.id.textTitle);
                TextView msg = dialog.findViewById(R.id.textMsg);
                title.setText("Error!");
                msg.setText("Your password does not match, you need to enter the same password in both password fields.");
                dialog.show();
            }else {
                if (pass1_field.length()<=7){
                    Toast.makeText(this, "Your password must be atleast Eight characters.", Toast.LENGTH_SHORT).show();
                    Dialog dialog = new Dialog(RegisterActivity.this);
                    dialog.setContentView(R.layout.dialog_simple_msg);
                    TextView title = dialog.findViewById(R.id.textTitle);
                    TextView msg = dialog.findViewById(R.id.textMsg);
                    title.setText("Warrning!");
                    msg.setText("Your password must be atleast 8 characters long.");
                    dialog.show();
                }else {
                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setMessage("Please wait..");
                    pd.setCancelable(false);
                    pd.show();

                    mAuth.createUserWithEmailAndPassword(email_field.getText().toString(),pass1_field.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){


                                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("email", email_field.getText().toString());
                                        hashMap.put("id", mAuth.getUid());
                                        firebaseDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                        pd.setMessage("Proccessing..");
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(RegisterActivity.this, "Signup succes!", Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                    }
                                                },1500);
                                        startActivity(new Intent(getApplicationContext(), AccountSetupActivity.class));
                                                Intent intent = new Intent(getApplicationContext(), AccountSetupActivity.class);
                                                intent.putExtra("from", "register");
                                                startActivity(intent);
                                                finish();
                                            }
                                        });


                                    }else {
                                        Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        signup_btn.setEnabled(true);
                                    }
                                }
                            });


                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
