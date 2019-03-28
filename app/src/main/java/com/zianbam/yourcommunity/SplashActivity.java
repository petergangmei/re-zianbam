package com.zianbam.yourcommunity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private int time = 0;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStart", true);



                if (firstStart) {
                    startActivity(new Intent(getApplicationContext(), WelcomeSlideActivity.class));
                    finish();
                }else {

                       if (firebaseUser == null){
                           startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                           fadein();
                           finish();
                       }else {
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           fadein();
                           finish();
//                           ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//                           ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                               @Override
//                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                   if (!dataSnapshot.exists()){
//                                       Intent intent = new Intent(getApplicationContext(), AccountSetupActivity.class);
//                                       intent.putExtra("from", "splash");
//                                       startActivity(intent);
//                                       finish();
//                                       fadein();
//                                   }else {
//                                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                       finish();
//                                       fadein();
//                                   }
//                               }
//                               @Override
//                               public void onCancelled(@NonNull DatabaseError databaseError) {
//                               }
//                           });
                       }
                   }



    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    private void fadein() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
