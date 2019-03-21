package com.zianbam.yourcommunity2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AccountSetupActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button conBtn;
    EditText username,displayname;
    TextView error_msg, error_msg2;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ProgressDialog pd;
  String myitent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Whitetheme);
        setContentView(R.layout.activity_account_setup);

        Intent intent = getIntent();
        myitent = intent.getStringExtra("from");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        final Intent intent = getIntent();
//        final String myintent = intent.getStringExtra("from");

        radioGroup = findViewById(R.id.gender_radio);
        conBtn = findViewById(R.id.continueBtn);
        username = findViewById(R.id.edit_username);
        displayname = findViewById(R.id.displayName);
        error_msg = findViewById(R.id.error_msg);
        error_msg2 = findViewById(R.id.error_msg2);
        displayname.setText(firebaseUser.getDisplayName());


//        username.setText(username.getText().toString().replaceAll(" ",""));


        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout layout_bar = (LinearLayout) findViewById(R.id.layout_bar);
                layout_bar.setVisibility(View.GONE);

                error_msg.setVisibility(View.GONE);

                conBtn.setEnabled(false);
                String myUsername = username.getText().toString();



                pd = new ProgressDialog(AccountSetupActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Please wait...");
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                pd.setIndeterminate(false);
                pd.show();


                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                String currentDate = currentDateFormat.format(calForDate.getTime());

                final long timestamp = System.currentTimeMillis()+ 3600000;//1hours


                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name",displayname.getText().toString());
                hashMap.put("id", mAuth.getUid());
                hashMap.put("username", myUsername);
                hashMap.put("email", mAuth.getCurrentUser().getEmail());
                hashMap.put("gender", radioButton.getTag().toString());
                hashMap.put("location", "Earth");
                hashMap.put("energy", "10");
                hashMap.put("active", timestamp);
                hashMap.put("energycharge", timestamp);
                hashMap.put("coins", 0);
                hashMap.put("time", currentTime);
                hashMap.put("date", currentDate);
                hashMap.put("subscription", 0);
                hashMap.put("subtype", "false");
                hashMap.put("pref", "none");
                hashMap.put("referralcode", "null");
                hashMap.put("referralclaim", "null");
                hashMap.put("emailverify", "null");
                hashMap.put("timestamp", System.currentTimeMillis());
                hashMap.put("bio", "Hey! I am newbie. Let's be friend.");
                hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/zianbamhangouts.appspot.com/o/user_avatar.png?alt=media&token=b7e499f4-5727-4bd8-bffc-83a572317f07");

                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (myitent.equals("splash")){
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent1);
                                finish();
                                fadein();
                        }




                    }
                });
                conBtn.setEnabled(false);

            }


        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searhUsers(charSequence.toString().toLowerCase());
                error_msg.setVisibility(View.GONE);

            }
            @Override
            public void afterTextChanged(Editable editable) {
                searhUsers(editable.toString().toLowerCase());
                error_msg.setVisibility(View.GONE);
                String s = editable.toString();

            }
        });


    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (editclicked) {
//            if (keyCode == KeyEvent.KEYCODE_SPACE) {
//                return false
//            }
//        } else {
//            super.onKeyDown(keyCode, event);
//        }
//    }

    private void searhUsers(String s) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("username")
                .equalTo(s);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User u = dataSnapshot.getValue(User.class);
//                String username = u.getUsername();
                if (dataSnapshot.hasChildren()){
                    error_msg.setText("Username already taken.");
                    error_msg.setVisibility(View.VISIBLE);
                    conBtn.setEnabled(false);
                }else{
                    error_msg.setVisibility(View.GONE);

//                    more checking
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);
                    String uname = username.getText().toString();

                    if(TextUtils.isEmpty(uname)){
                        error_msg.setVisibility(View.VISIBLE);
                        error_msg.setText("Please enter username.");
                        conBtn.setEnabled(false);
                    }else {
                        error_msg.setVisibility(View.GONE);
                        if (radioButton == null){
                            error_msg2.setVisibility(View.VISIBLE);
                            error_msg2.setText("Please select your gender.");
                            conBtn.setEnabled(false);
                        }else {
                            conBtn.setEnabled(true);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        error_msg2.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetupActivity.this);
        builder.setMessage("Do you really want to do this?");
        builder.setTitle("Exit Zianbam");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes, exit!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                moveTaskToBack(true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void fadein() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
