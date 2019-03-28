package com.zianbam.yourcommunity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zianbam.yourcommunity.Adapter.FragmentAdapter;
import com.zianbam.yourcommunity.Fragments.AccountFragment;
import com.zianbam.yourcommunity.Fragments.HomeFragment;
import com.zianbam.yourcommunity.Fragments.NotificationFragment;
import com.zianbam.yourcommunity.Fragments.PostFragment;
import com.zianbam.yourcommunity.Fragments.SideMenuFragment;
import com.zianbam.yourcommunity.Fragments.TrendingFragment;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Token;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference ref;
    ViewPager viewPager;
    BottomNavigationView navigation;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {setTheme(R.style.Whitetheme);}else {setTheme(R.style.AppTheme);}
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        checkifdatabaseExist();
        updateToaken(FirebaseInstanceId.getInstance().getToken());



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

    private void checkifdatabaseExist() {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Intent intent = new Intent(getApplicationContext(), AccountSetupActivity.class);
                    intent.putExtra("from", "splash");
                    startActivity(intent);
                    finish();
                    fadein();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                    viewPager.setCurrentItem(0,false);
                    return true;

                case R.id.navigation_trending:

                    viewPager.setCurrentItem(1,false);
                    return true;
                case R.id.navigation_home:

                    viewPager.setCurrentItem(2,false);
                    return true;


                case R.id.navigation_notification:

                    viewPager.setCurrentItem(3,false);
                    return true;

                case R.id.navigation_account:

                    viewPager.setCurrentItem(4,false);
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
                    Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                    intent.putExtra("post_type", "new");
                    intent.putExtra("post_id", "null");
                    startActivity(intent);
                    slide_up();
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

    private void updateToaken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }

}
