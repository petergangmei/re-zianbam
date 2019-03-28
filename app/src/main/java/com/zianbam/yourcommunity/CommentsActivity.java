package com.zianbam.yourcommunity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
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
import com.zianbam.yourcommunity.Adapter.CommentAdapter;
import com.zianbam.yourcommunity.Model.Comment;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {
    private PhotoView post_image;
    private ImageView close, comment_send,scrolltop;
    private CircleImageView publisher_image, user_image;
    private EditText comment_text;
    private TextView caption, publisher_name, likes, comments, post_text;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref, reference;
    private String post_id, publisher_id, scrollDown;
    private ProgressBar pb_post_image;
    private ScrollView scrollView;
    private RecyclerView recyclerView_comments;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList;
    private Post post;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        post_id = intent.getStringExtra("postid");
        publisher_id = intent.getStringExtra("publisherid");
        scrollDown = intent.getStringExtra("scrollDown");

        post_image = findViewById(R.id.post_image);
        close = findViewById(R.id.close);
        caption = findViewById(R.id.caption);
        pb_post_image = findViewById(R.id.progress_bar_post_image);
        publisher_image = findViewById(R.id.publisher_pic);
        publisher_name = findViewById(R.id.publisher_name);
        user_image=findViewById(R.id.user_profile_pic);
//        like = findViewById(R.id.like);
        likes = findViewById(R.id.likes);
        comments = findViewById(R.id.comments);
        comment_send = findViewById(R.id.send_comment);
        comment_text = findViewById(R.id.comment_text);
        recyclerView_comments = findViewById(R.id.recycle_view_comments);
        scrollView = findViewById(R.id.scrollview);
        scrolltop = findViewById(R.id.scrolltop);
        post_text = findViewById(R.id.post_text);


        checklikestatus();
        getsetletedPost();
        getpublisherProfile(publisher_id);
        getuserProfile();
        getLikesCount();
        getCommentsCount();
//        handlelikeanddislike();

        sendComments();
        hidesoftkeyboard();
        getPostDetails();


        recyclerView_comments = findViewById(R.id.recycle_view_comments);
        recyclerView_comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_comments.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, post_id);
        recyclerView_comments.setAdapter(commentAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scrolltop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrolltoTop();
            }
        });


    }

    private void getPostDetails() {
        ref = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    post = dataSnapshot.getValue(Post.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//endof onCreaate class


    private void scrollDown() {
        if (scrollDown == null){}else if (scrollDown.equals("true")){
            scrollView.postDelayed(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },500);
        }
    }

    private void scrolltoTop() {
        View view = findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        scrollView.postDelayed(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                comment_text.clearFocus();
                scrolltop.setVisibility(View.GONE);
            }
        },400);
    }
    private void hidesoftkeyboard() {
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardCode();
            }

        });

        scrollView.setSmoothScrollingEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {setTheme(R.style.Whitetheme);
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == 0){
                        View view = findViewById(android.R.id.content);
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
        }
    }
    private void hideKeyboardCode() {
        scrolltop.setVisibility(View.GONE);
        comment_text.clearFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sendComments() {
        comment_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.postDelayed(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        comment_text.requestFocus();
                        scrolltop.setVisibility(View.VISIBLE);
                    }
                },400);
                return false;
            }
        });
        comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.postDelayed(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        comment_text.requestFocus();
                        scrolltop.setVisibility(View.VISIBLE);
                    }
                },400);
            }
        });
        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertindatabase();
            }

            private void insertindatabase() {

                scrollView.postDelayed(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        comment_text.requestFocus();
                        scrolltop.setVisibility(View.VISIBLE);
                    }
                },100);

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                String currentDate = currentDateFormat.format(calForDate.getTime());

                reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_id);
                final String commentid = reference.push().getKey();

                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("comment", comment_text.getText().toString());
                hashMap1.put("postid", post_id);
                hashMap1.put("commentid", commentid);
                hashMap1.put("publisherid", firebaseUser.getUid());
                hashMap1.put("userid", publisher_id);
                hashMap1.put("reported", 0);
                hashMap1.put("time", currentTime);
                hashMap1.put("date", currentDate);
                hashMap1.put("timestamp", ServerValue.TIMESTAMP);
                reference.child(commentid).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Notifypublisher_comment(comment_text.getText().toString(), commentid);
                        comment_text.setText("");
                    }
                });



            }
        });
        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {
             textvalidate(s);
            }

            private void textvalidate(Editable s) {
                if (s.length() >0){
                    comment_send.setVisibility(View.VISIBLE);
                    scrolltop.setVisibility(View.INVISIBLE);
                }else if (s.length() == 0){
                    comment_send.setVisibility(View.INVISIBLE);
                    scrolltop.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void Notifypublisher_comment(final String msg, String notificationId) {

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            final String currentTime =currentTimeFormat.format(calForTime.getTime());

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            if (!publisher_id.equals(firebaseUser.getUid())){
                reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisher_id);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userid", firebaseUser.getUid());
                hashMap.put("text", "Commented: "+msg);
                hashMap.put("postid", post_id);
                hashMap.put("notificationid",notificationId );
                hashMap.put("pointerid", "null");
                hashMap.put("type", "comment_on_post");
                hashMap.put("ispost", true);
                hashMap.put("time", currentTime);
                hashMap.put("date", currentDate);
                hashMap.put("isseen", "false");
                hashMap.put("timestamp", ServerValue.TIMESTAMP);
                reference.child(notificationId).setValue(hashMap);
                

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //getusername and send notifcation to the post publisher
                        final String myusername = dataSnapshot.child("username").getValue().toString();
                        COMMENT_PUSH_Notification( myusername, post, msg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
    }

    private  void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                   if (comment.getReported()<5){
                       commentList.add(comment);
                   }
                }

//                checkin here
                scrollDown();
                pb_post_image.setVisibility(View.GONE);
                commentAdapter.notifyDataSetChanged();




            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void checklikestatus() {
        //check if the post is already liked
//        ref = FirebaseDatabase.getInstance().getReference("Likes").child(post_id);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
//                    like.setImageResource(R.drawable.ic_fav_red);
//                    like.setTag("liked");
//                    getLikesCount();
//                }else {
//                    like.setImageResource(R.drawable.ic_fav_light);
//                    like.setTag("like");
//                    getLikesCount();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    private void handlelikeanddislike() {

//        like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (like.getTag().toString().equals("like")){
//                    String notificationId = ref.push().getKey();
//                     ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(post_id)
//                            .child(firebaseUser.getUid());
//                    HashMap<Object, String> hashMap1 = new HashMap<>();
//                    hashMap1.put("notificationid", notificationId);
//                    ref.setValue(hashMap1);
//                    Notifypublisher_like(notificationId);
//                    checklikestatus();
//                }else if (like.getTag().toString().equals("liked")){
//                    RemoveNotifypublisher_like();
//                    checklikestatus();
//                }
//            }
//        });


    }

    private void Notifypublisher_like(String notificationId) {

        if (!publisher_id.equals(firebaseUser.getUid())){
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            final String currentTime =currentTimeFormat.format(calForTime.getTime());
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisher_id);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", "like your post");
            hashMap.put("postid", post_id);
            hashMap.put("notificationid", notificationId);
            hashMap.put("pointerid", "null");
            hashMap.put("type", "like_on_post");
            hashMap.put("ispost", true);
            hashMap.put("time", currentTime);
            hashMap.put("date", currentDate);
            hashMap.put("isseen", "false");
            hashMap.put("timestamp", System.currentTimeMillis());
            reference.child(notificationId).setValue(hashMap);

            //getusername and send notifcation to the post publisher
            ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final String myusername = dataSnapshot.child("username").getValue().toString();
                        LIKE_PUSH_Notification( myusername);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void RemoveNotifypublisher_like() {
        ref = FirebaseDatabase.getInstance().getReference("Likes").child(post_id).child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String notificationid = dataSnapshot.child("notificationid").getValue().toString();
                    FirebaseDatabase.getInstance().getReference("Notifications").child(publisher_id).child(notificationid).removeValue();
                    FirebaseDatabase.getInstance().getReference("Likes").child(post_id).child(firebaseUser.getUid()).removeValue();
                    checklikestatus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void LIKE_PUSH_Notification( final String  myusername) {

        final String userid = firebaseUser.getUid();
        final String post_publisherid = post.getPublisher();
        final String type = "post";
        final String post_id = post.getPostid();
        final String title = "Notification";
        final String body = "["+myusername+"] liked your post  "+post.getPost_text()+" ";
        final String postURL = post.getImageUrl();

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(post.getPublisher());
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
                                            Toast.makeText(getApplicationContext(), "Failed like", Toast.LENGTH_SHORT).show();
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

    private void COMMENT_PUSH_Notification(String myusername, Post post,String msg) {
        final String userid = firebaseUser.getUid();
        final String post_publisherid = post.getPublisher();
        final String type = "comment";
        final String post_id = post.getPostid();
        final String title = "Notification";
        final String body = "["+myusername+"] commented: "+msg+" ";
        final String postURL = post.getImageUrl();

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(post.getPublisher());
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
                                            Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_SHORT).show();
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

    private void getCommentsCount() {
        ref = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   if (dataSnapshot.getChildrenCount()>1){
                       comments.setText(dataSnapshot.getChildrenCount() + " comments");
                   }else if (dataSnapshot.getChildrenCount() ==1){
                       comments.setText(dataSnapshot.getChildrenCount() + " comment");
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikesCount() {
        ref = FirebaseDatabase.getInstance().getReference("Likes").child(post_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getChildrenCount() == 0){
                        likes.setText("");
                    } else if (dataSnapshot.getChildrenCount()>1) {
                        likes.setText(dataSnapshot.getChildrenCount() + " likes");
                    }else if (dataSnapshot.getChildrenCount() == 1){
                        likes.setText(dataSnapshot.getChildrenCount() + " like");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getuserProfile() {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(user_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getpublisherProfile(String pID) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(pID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    publisher_name.setText(user.getUsername());
                    Glide.with(getApplicationContext()).load(user.getImageURL())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(publisher_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getsetletedPost() {
        ref = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);
             if (dataSnapshot.exists()){
                 if (post.getType().equals("photo_post")) {

                     Glide.with(getApplicationContext()).load(post.getImageUrl())
                             .listener(new RequestListener<Drawable>() {
                                 @Override
                                 public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                     return false;
                                 }
                                 @Override
                                 public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                     caption.setVisibility(View.VISIBLE);
                                     caption.setText(post.getPost_text());
                                     post_image.setVisibility(View.VISIBLE);
                                     return false;
                                 }
                             }).into(post_image);
                     readComments();
                 }else if (post.getType().equals("text_post")){
                     pb_post_image.setVisibility(View.GONE);
                     caption.setVisibility(View.GONE);
                     post_text.setVisibility(View.VISIBLE);
                     readComments();
                     if (post.getPost_text().length() <10){
                         post_text.setTextSize(20);
                         post_text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                         post_text.setText(post.getPost_text());
                     }else {
                         post_text.setText(post.getPost_text());
                     }

                 }
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View view = findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onTouchEvent(event);
    }

}
