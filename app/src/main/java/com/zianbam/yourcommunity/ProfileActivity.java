package com.zianbam.yourcommunity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.GridAdapter;
import com.zianbam.yourcommunity.Adapter.PostAdapter;
import com.zianbam.yourcommunity.Model.Grid;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ImageView image_profile, ionline,close, option;
    private Button B1, B2, B3, B4, btncheckout, btnfollow, btnmessage;
    private TextView Tusername, Tlookingfor, Tgender, Tabout;
    private DatabaseReference ref, reference;
    private FirebaseUser firebaseUser;
    private ProgressBar progress_bar;
    private RecyclerView recyclerView_gridView, recyclerView_singleView;
    private GridAdapter gridAdapter;
    private PostAdapter postAdapter;
    private List<Post> postList, postList2;
    private ScrollView scrollView;
    private TabLayout tabLayout;
    private String profileID;
    private LinearLayout layBottomNav;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Intent intent = getIntent();
        profileID = intent.getStringExtra("profileID");
        if (profileID == null){ profileID = firebaseUser.getUid(); }

        initialize();
        getUserProfileDetails(Tusername, Tlookingfor, Tgender, Tabout);
        getCountsforPFF();
        getPOST_IMAGES();
        OnTabchangesActivity();
        showUserList();
        OnClickActivities();
        isFollowing();
       hideBottomNavIfOwnprofilevisting();

        showPopupMenu();
        //goback
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        
    }

    private void hideBottomNavIfOwnprofilevisting() {
        if (!profileID.equals(firebaseUser.getUid())){
            layBottomNav.setVisibility(View.VISIBLE);
        }
    }


    private void showPopupMenu() {
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.unfollow:
                                RemoveFOLLOWNotification(profileID);
                                isFollowing();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.unfollow_menu);

                popupMenu.show();
            }
        });
    }

    private void OnClickActivities() {
        
        //follow a user
        btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileID);
                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                String currentDate = currentDateFormat.format(calForDate.getTime());
                String notificationId = reference.push().getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userid", firebaseUser.getUid());
                hashMap.put("text", "started following you");
                hashMap.put("postid", "");
                hashMap.put("ispost", false);
                hashMap.put("notificationid", notificationId);
                hashMap.put("time", currentTime);
                hashMap.put("date", currentDate);
                hashMap.put("isseen", "false");
                hashMap.put("timestamp", ServerValue.TIMESTAMP);
                reference.child(notificationId).setValue(hashMap);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                        .child(profileID);
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("id", notificationId);
                ref.child(firebaseUser.getUid()).setValue(hashMap1);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID).child("followers")
                        .child(firebaseUser.getUid());
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("id", notificationId);
                ref1.child(profileID).setValue(hashMap2);

                //getusername and set notification
                DatabaseReference refa = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                refa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            User user = dataSnapshot.getValue(User.class);
                            FOLLOW_PUSH_Notification( profileID, user.getUsername());
                            isFollowing();
                            Toast.makeText(ProfileActivity.this, "Followed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void FOLLOW_PUSH_Notification(final String publisher, final String myusername) {

        final String userid = firebaseUser.getUid();
        final String post_publisherid = profileID;
        final String type = "follow";
        final String post_id = "null";
        final String title = "Notification";
        final String body = "["+myusername+"] started following you.";
        final String postURL = "https://firebasestorage.googleapis.com/v0/b/zianbam-testing.appspot.com/o/icons%2Fadd_user.png?alt=media&token=3b322a91-e8aa-4d9f-960b-4d081882e42f";

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(userid, R.mipmap.ic_launcher, body,title, post_publisherid, postURL, type, post_id);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200){
                                        if (response.body().success !=1 ){
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }else {
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void isFollowing(){
         reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following").child(profileID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    btnfollow.setVisibility(View.GONE);
                    btnmessage.setVisibility(View.VISIBLE);
                    option.setVisibility(View.VISIBLE);
                }else {
                    DoIFollowBack();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void DoIFollowBack() {
        reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers").child(profileID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    btnfollow.setText("Follow back");
                }else {
                    btnfollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RemoveFOLLOWNotification(final String id) {
        reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers").child(firebaseUser.getUid()).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String notificationid = dataSnapshot.child("id").getValue().toString();

                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).child(notificationid).removeValue();


                    FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers")
                            .child(firebaseUser.getUid()).child(id).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(id).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Unfollowed!", Toast.LENGTH_SHORT).show();
                                    btnmessage.setVisibility(View.GONE);
                                    btnfollow.setVisibility(View.VISIBLE);
                                    option.setVisibility(View.INVISIBLE);
                                }
                            },100);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showUserList() {
        //show follower list
        B4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                intent.putExtra("title", "followers");
                intent.putExtra("profileID", profileID);
                startActivity(intent);
                fadein();
            }
        });
        //show following list
        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                intent.putExtra("title", "following");
                intent.putExtra("profileID", profileID);
                startActivity(intent);
                fadein();

            }
        });

    }

    private void scrollDown() {
            scrollView.postDelayed(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },500);
    }

    private void OnTabchangesActivity() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    recyclerView_singleView.setVisibility(View.GONE);
                    recyclerView_gridView.setVisibility(View.VISIBLE);
                }else if (tab.getPosition() == 1){
                    recyclerView_singleView.setVisibility(View.VISIBLE);
                    recyclerView_gridView.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollDown();
                        }
                    },400);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void getPOST_IMAGES() {

        //get images for grid view
        recyclerView_gridView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
//        gridLayoutManager.setReverseLayout(true);
        recyclerView_gridView.setLayoutManager(gridLayoutManager);
         postList = new ArrayList<>();
        gridAdapter= new GridAdapter(this, postList);
        recyclerView_gridView.setAdapter(gridAdapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    postList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Post post =  snapshot.getValue(Post.class);
                        if (post.getPublisher().equals(profileID)){
                            if (post.getDeleted().equals("false")){
                                if (post.getType().equals("photo_post")){
                                    postList.add(post);
                                }
                            }
                        }
                    }
                    gridAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // get single image recyclerivew
        recyclerView_singleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_singleView.setLayoutManager(linearLayoutManager);
        postList2 = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList2);
        recyclerView_singleView.setAdapter(postAdapter);

        ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    postList2.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Post post =  snapshot.getValue(Post.class);
                        if (post.getPublisher().equals(profileID)){
                            if (post.getDeleted().equals("false")){
                                if (post.getType().equals("photo_post")){
                                    postList2.add(post);
                                }
                            }
                        }
                    }
                    postAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getCountsforPFF() {
        //getNumber of followerss
        ref = FirebaseDatabase.getInstance().getReference("Follow").child(profileID).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    B4.setText(" "+dataSnapshot.getChildrenCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //get NUmber of following
        ref = FirebaseDatabase.getInstance().getReference("Follow").child(profileID).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    B3.setText(""+dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get Number of post
        ref = FirebaseDatabase.getInstance().getReference()
                .child("Posts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileID)){
                        if (post.getType().equals("photo_post")){
                            i++;
                        }
                    }
                }
                B2.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfileDetails(final TextView tusername,final TextView tlookingfor,final TextView tgender,final TextView tabout) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(profileID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    tusername.setText(user.getUsername());
                    tgender.setText("("+user.getGender()+")");
                    tabout.setText(user.getBio());

                    //set image profile
                    Glide.with(getApplicationContext()).load(user.getImageURL()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(image_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {
        layBottomNav = (LinearLayout) findViewById(R.id.layBottomNav);
        btnmessage = (Button) findViewById(R.id.btnmessage);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        recyclerView_singleView = (RecyclerView) findViewById(R.id.recylerview_postsingle);
        recyclerView_gridView = (RecyclerView) findViewById(R.id.recylerview_postgrids);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        close = (ImageView) findViewById(R.id.close);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        image_profile= (ImageView) findViewById(R.id.image_profile);
        ionline = (ImageView) findViewById(R.id.Ionline);
        option =  (ImageView) findViewById(R.id.option);
        B1 = (Button) findViewById(R.id.B1);
        B2 = (Button) findViewById(R.id.B2);
        B3 = (Button) findViewById(R.id.B3);
        B4 = (Button) findViewById(R.id.B4);
        btncheckout = (Button) findViewById(R.id.btncheckout);
        btnfollow = (Button) findViewById(R.id.btnfollow);
        Tusername = (TextView) findViewById(R.id.Tusername);
        Tlookingfor = (TextView) findViewById(R.id.Tlookingfor);;
        Tgender = (TextView) findViewById(R.id.Tgender);
        Tabout = (TextView) findViewById(R.id.Tabout);

    }

    private void fadein() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
