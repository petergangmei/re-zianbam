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

    //slide
    private ViewPager mSlideViewPager;
    private LinearLayout mDotlayout;
    private GreetingslideAdapter sliderAdapter;
    private TextView[] mDots;
    private Button nextBtn , preBtn;
    private int mCurrentPage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//shareprefs
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        boolean firstStart = prefs.getBoolean("firstStart", true);
//        if (firstStart) {
//            startActivity(new Intent(getApplicationContext(), WelcomeSlideActivity.class));
//        }
        videoBG = findViewById(R.id.videoView);
        framlay1 = findViewById(R.id.framlay1);
        singupBtn = findViewById(R.id.signupBtn);
        signinBtn = findViewById(R.id.textlogin);
        textHello = findViewById(R.id.textHello);
        mSlideViewPager = findViewById(R.id.viewPager);

        sliderAdapter = new GreetingslideAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);




        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        showWelcomeMessage();
       videoBackgroundActivity();

    }

    private void showWelcomeMessage() {

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                textHello.setTextSize(45);
//                textHello.setText("Welcome to zianbam! ");
//                textHello.append("\n");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        textHello.append("We are glad that you came to visit us. ");
//                        textHello.append("\n");
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                textHello.append("How do do come to khow about zianbam?");
//                            }
//                        },6000);
//                    }
//                },6000);
//            }
//        },4000);
    }

    private void videoBackgroundActivity() {
        Uri uri = Uri.parse("android.resource://" +getPackageName()
                +"/"
                +R.raw.video_bg);
        videoBG.setVideoURI(uri);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                framlay1.setVisibility(View.INVISIBLE);
                singupBtn.setVisibility(View.VISIBLE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        },1000);

        loadvideo();
    }

    private void loadvideo() {
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                mMediaPlayer.setVolume(0,0);
                mMediaPlayer.setLooping(true);
                if (mCurrentVideoPosition !=0){
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        videoBG.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoBG.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
