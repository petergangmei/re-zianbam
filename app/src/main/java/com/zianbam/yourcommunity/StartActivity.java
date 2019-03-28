package com.zianbam.yourcommunity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.zianbam.yourcommunity.Adapter.GreetingslideAdapter;

public class StartActivity extends AppCompatActivity {
    VideoView videoBG;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    FrameLayout framlay1;
    private Button singupBtn;
    private TextView textHello, signinBtn;
    private Button signup;

    //slide
    private ViewPager mSlideViewPager;
    private LinearLayout mDotlayout;
    private GreetingslideAdapter sliderAdapter;
    private TextView[] mDots;
    private Button nextBtn , preBtn;
    private int mCurrentPage;

    //facebookgoogle loging



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Whitetheme);
        setContentView(R.layout.activity_start);

        signinBtn = findViewById(R.id.textlogin);
        signup = findViewById(R.id.signupBtn);


//shareprefs

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getApplicationContext(), RegisterActivity.class)); overridePendingTransition(R.anim.fade_in, R.anim.fade_out);    }
        });
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getApplicationContext(), LoginActivity.class));  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);  }
        });

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
}
