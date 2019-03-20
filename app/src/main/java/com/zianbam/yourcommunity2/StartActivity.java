package com.zianbam.yourcommunity2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.ServerValue;
import com.zianbam.yourcommunity2.Adapter.GreetingslideAdapter;
import com.zianbam.yourcommunity2.Adapter.SliderAdapter;

import java.util.ArrayList;
import java.util.List;

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
}
