package com.zianbam.yourcommunity.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.NotificationAdapter;
import com.zianbam.yourcommunity.Model.Notification;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationsList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Query query;
    private ArrayList<String> userList;
    ValueEventListener valueEventListener;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView= view.findViewById(R.id.recycle_view_notifications);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationsList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationsList);
        recyclerView.setAdapter(notificationAdapter);
        readNotification();
        refreshonSwip(swipeRefreshLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readNotification();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        return view;
    }

    private void refreshonSwip(final SwipeRefreshLayout swipeRefreshLayout) {

    }

    private void readNotification() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.keepSynced(true);
         query = reference.limitToLast(25);
          query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    notificationsList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Notification notification = snapshot.getValue(Notification.class);

                        reference =  FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(notification.getNotificationid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("isseen", "true");
                                    reference.updateChildren(hashMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        notificationsList.add(notification);
                    }
                    Collections.reverse(notificationsList);
                    notificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
