package com.zianbam.yourcommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.UserAdapter;
import com.zianbam.yourcommunity.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView close;
    private UserAdapter userAdapter;
    private List<User> userList;
    private String profileID, title;
    private TextView titleText;
    private ProgressBar progress_bar;
    private DatabaseReference reference, ref;
    List<String> idList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        profileID = intent.getStringExtra("profileID");
        title = intent.getStringExtra("title");
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);



        close = (ImageView) findViewById(R.id.close);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view_users);
        titleText = (TextView) findViewById(R.id.tile);
        userList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        userAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();
        switch (title) {
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;

        }


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getFollowers() {
        titleText.setText("Followers");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(profileID).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowing() {
        titleText.setText("Following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(profileID).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(profileID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id:idList){
                        if (user.getId().equals(id)){
                            userList.add(user);
                        }
                    }
                }
                progress_bar.setVisibility(View.GONE);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
