package com.zianbam.yourcommunity2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity2.Adapter.FragmentAdapter;
import com.zianbam.yourcommunity2.Fragments.AccountFragment;
import com.zianbam.yourcommunity2.Fragments.HomeFragment;
import com.zianbam.yourcommunity2.Fragments.NotificationFragment;
import com.zianbam.yourcommunity2.Fragments.PostFragment;
import com.zianbam.yourcommunity2.Fragments.SideMenuFragment;
import com.zianbam.yourcommunity2.Fragments.TrendingFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference ref;
    ViewPager viewPager;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {setTheme(R.style.Whitetheme);}else {setTheme(R.style.AppTheme);}
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


//        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    textView.setText("Welcome"+ dataSnapshot.child("username").getValue().toString());
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });




        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewpager); //Init Viewpager
        setupFm(getSupportFragmentManager(), viewPager); //Setup Fragment
        viewPager.setCurrentItem(2); //Set Currrent Item When Activity Start
        viewPager.setOnPageChangeListener(new PageChange()); //Listeners For Viewpager When Page Changed

        navigation.setSelectedItemId(R.id.navigation_home);
    }




    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new PostFragment(), "post");
        Adapter.add(new TrendingFragment(), "Page Two");
        Adapter.add(new HomeFragment(), "Page One");
        Adapter.add(new NotificationFragment(), "Page Three");
        Adapter.add(new AccountFragment(), "Page Four");
        Adapter.add(new SideMenuFragment(), "Side menu");

        viewPager.setAdapter(Adapter);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_post:

                    viewPager.setCurrentItem(0);
                    return true;

                case R.id.navigation_trending:

                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_home:

                    viewPager.setCurrentItem(2);
                    return true;


                case R.id.navigation_notification:

                    viewPager.setCurrentItem(3);
                    return true;

                case R.id.navigation_account:

                    viewPager.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };
    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    navigation.setSelectedItemId(R.id.navigation_post);
                    startActivity(new Intent(getApplicationContext(), CreatePostActivity.class ));
                    slide_up();
                    Toast.makeText(MainActivity.this, "new post", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    navigation.setSelectedItemId(R.id.navigation_trending);
                    break;
                case 2:
                    navigation.setSelectedItemId(R.id.navigation_home);
                    break;
                case 3:
                    navigation.setSelectedItemId(R.id.navigation_notification);
                    break;
                case 4:
                    navigation.setSelectedItemId(R.id.navigation_account);
                    break;
                case 5:
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void fadein() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    private void slide_up(){ overridePendingTransition(R.anim.slide_up, R.anim.slide_up);}

}
