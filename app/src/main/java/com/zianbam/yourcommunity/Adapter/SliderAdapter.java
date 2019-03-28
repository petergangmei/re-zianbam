package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zianbam.yourcommunity.R;

public class SliderAdapter extends PagerAdapter  {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //array
    public int[] slide_images = {
            R.drawable.slide_boy,
            R.drawable.slide_sms,
            R.drawable.slide_project,
            R.drawable.slide_promotion
    };
    public String [] slide_headings ={
            "Connect",
            "Find",
            "Project",
            "Promotion"
    };
    public String [] slide_descs ={
            "Zianbam connect peoples who share common interest",
            "des 2",
            "des3",
            "des 4"

    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.title);
        TextView slide_desc = (TextView) view.findViewById(R.id.msg);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slide_desc.setText(slide_descs[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
