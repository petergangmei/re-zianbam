package com.zianbam.yourcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zianbam.yourcommunity.Adapter.SliderAdapter;

public class WelcomeSlideActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotlayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button nextBtn , preBtn;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_slide);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        nextBtn = findViewById(R.id.nextBtn);
        preBtn = findViewById(R.id.preBtn);

        mSlideViewPager = findViewById(R.id.viewPager);
        mDotlayout = findViewById(R.id.dotlayout);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewLister);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextBtn.getText().toString().equals("FINISH")){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                }else {
                    mSlideViewPager.setCurrentItem(mCurrentPage +1);
                }
            }
        });
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage -1);
            }
        });


    }
    public  void addDotsIndicator(int position){
        mDots = new TextView[4];
        mDotlayout.removeAllViews();
        for (int i = 0; i <mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGray));
            mDotlayout.addView(mDots[i]);

        }
        if (mDots.length> 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
    ViewPager.OnPageChangeListener viewLister = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;
            if (mCurrentPage == 0){
                nextBtn.setEnabled(true);
                preBtn.setEnabled(false);
                preBtn.setVisibility(View.INVISIBLE);
                nextBtn.setText("Next");
                preBtn.setText("");
            }else if (mCurrentPage == mDots.length - 1){
                nextBtn.setEnabled(true);
                preBtn.setEnabled(true);
                preBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("FINISH");
                preBtn.setText("Back");
            }else {
                nextBtn.setEnabled(true);
                preBtn.setEnabled(true);
                preBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("Next");
                preBtn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
