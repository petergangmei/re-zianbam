package com.zianbam.yourcommunity2.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zianbam.yourcommunity2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SideMenuFragment extends Fragment {


    public SideMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_side_menu, container, false);
    }

}
