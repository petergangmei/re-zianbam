package com.zianbam.yourcommunity2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
    }







    @Override
    public void finish() {
        super.finish();
        fadein();
    }

    private void fadein() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
