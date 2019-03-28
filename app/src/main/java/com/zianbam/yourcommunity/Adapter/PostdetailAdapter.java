package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.CreatePostActivity;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.Model.Notification;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PostdetailAdapter extends RecyclerView.Adapter<PostdetailAdapter.ViewHolder> {
    Context mContext;
    List<Post> mPost;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList;
    private String notificationId;
//
//    APIService apiService;
    boolean notify = false;

    public PostdetailAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.postdetail_item,viewGroup,false);
        return new PostdetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Post post = mPost.get(i);

//        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        if (post.getPost_text().length() <= 35){
            viewHolder.post_text.setTextSize(15);
            viewHolder.post_text.setTypeface(Typeface.DEFAULT_BOLD);
        }
        viewHolder.post_text.setText(post.getPost_text());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
               final User user = dataSnapshot.getValue(User.class);
                viewHolder.username.setText(user.getUsername());
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder)
                        .into(viewHolder.image_profile);


                if (post.getType().equals("feature_photo")) {
                    viewHolder.feature_photo.setVisibility(View.VISIBLE);
                    viewHolder.post_text.setVisibility(View.GONE);
                    Picasso.get().load(post.getImageUrl())
                            .placeholder(R.drawable.image_placeholder)
                            .into(viewHolder.feature_photo);
                    viewHolder.attribute_text.setText("shared a feature photo");
                    viewHolder.attribute_text.setVisibility(View.VISIBLE);

                    if (!post.getPost_text().equals("")){
                        viewHolder.caption.setVisibility(View.VISIBLE);
                        viewHolder.caption.setText(post.getPost_text());
                    }

                }else if (post.getType().equals("photo_post")){
                    viewHolder.feature_photo.setVisibility(View.VISIBLE);
                    viewHolder.post_text.setVisibility(View.GONE);
                    Picasso.get().load(post.getImageUrl())
                            .placeholder(R.drawable.image_placeholder)
                            .into(viewHolder.feature_photo);
                    viewHolder.attribute_text.setVisibility(View.VISIBLE);
                    viewHolder.attribute_text.setVisibility(View.VISIBLE);
                    viewHolder.attribute_text.setText("shared a post");
                    if (!post.getPost_text().equals("")){
                        viewHolder.caption.setVisibility(View.VISIBLE);
                        viewHolder.caption.setText(post.getPost_text());
                    }

                }else if (post.getType().equals("text_post")){
                    if (post.getPost_text().length() <= 35){
                        viewHolder.post_text.setTextSize(15);
                        viewHolder.post_text.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    viewHolder.feature_photo.setVisibility(View.GONE);
                    viewHolder.post_text.setText(post.getPost_text());
                    viewHolder.attribute_text.setVisibility(View.VISIBLE);
                    viewHolder.attribute_text.setText("shared a post");
                    viewHolder.post_text.setVisibility(View.VISIBLE);
                    viewHolder.caption.setVisibility(View.GONE);
                }

                isLiked(post.getPostid(), viewHolder.like);
                countLikes(viewHolder.likes, post.getPostid());
                getComments(post.getPostid(), viewHolder.comments,viewHolder.recyclerView_comments);
//                readComments(post.getPostid());
//                getImage(viewHolder.image_profile);

                viewHolder.feature_photo.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {

                    }

                    @Override
                    public void onDoubleClick(View view) {
                        notify = true;
                        if (viewHolder.like.getTag().toString().equals("like")){
                            like(post.getPublisher(), post.getPostid(), post.getPost_text(), post.getType());
                        }
                        if (viewHolder.like.getTag().toString().equals("liked")){
                            liked(post.getPublisher(), post.getPostid());
                        }
                    }
                },300));

                viewHolder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(mContext, StoryViewsActivity.class);
//                        intent.putExtra("id", post.getPostid());
//                        intent.putExtra("title", "likes");
//                        intent.putExtra("storyid", "none");
//                        mContext.startActivity(intent);
                    }
                });

                viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CommentsActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                    }
                });

                viewHolder.send_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (!post.getPublisher().equals(firebaseUser.getUid())){
//                            sendCommentNotification(post.getPublisher(), user.getUsername(),  viewHolder.comment_text.getText().toString());
//                        }
                        viewHolder.comment_text.setText("");
                        viewHolder.comment_text.clearFocus();
                    }
                });
                //display
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String time = dataSnapshot.child("timestamp").getValue().toString();
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long realtime = Long.parseLong(time);
                            String timeAgo = getTimeAgo.getTimeAgo(realtime, mContext);
                            viewHolder.timego.setText(timeAgo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("tag", "clicked");
                        final PopupMenu popupMenu = new PopupMenu(mContext, view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.edit:
                                        Intent intent = new Intent(mContext, CreatePostActivity.class);
                                        intent.putExtra("post_type", "update");
                                        intent.putExtra("post_id", post.getPostid());
                                        mContext.startActivity(intent);
                                        return true;
                                    case R.id.delete:

                                        removeNotificationItem(post.getPostid());
                                        FirebaseDatabase.getInstance().getReference("Posts")
                                                .child(post.getPostid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });
                                        return true;
                                    case R.id.save:
                                        FirebaseDatabase.getInstance().getReference("Saves")
                                                .child(mAuth.getUid()).child(post.getPostid()).setValue(true);
                                        Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
                                        return true;

                                    case R.id.saved:
                                        FirebaseDatabase.getInstance().getReference("Saves")
                                                .child(mAuth.getUid()).child(post.getPostid()).removeValue();
                                        Toast.makeText(mContext, "Unsaved!", Toast.LENGTH_SHORT).show();
                                        return true;

                                    case R.id.report:
                                        Toast.makeText(mContext, "reported!", Toast.LENGTH_SHORT).show();
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.inflate(R.menu.post_menu);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                                .child(mAuth.getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(post.getPostid()).exists()){
                                    popupMenu.getMenu().findItem(R.id.saved).setVisible(true);
                                    popupMenu.getMenu().findItem(R.id.save).setVisible(false);
                                }else {

                                    popupMenu.getMenu().findItem(R.id.saved).setVisible(false);
                                    popupMenu.getMenu().findItem(R.id.save).setVisible(true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if (!post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.delete).setVisible(false);

                        }else {
                            popupMenu.getMenu().findItem(R.id.save).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                        }
                        popupMenu.show();
                    }
                });

                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (viewHolder.like.getTag().toString().equals("like")){
                            like(post.getPublisher(), post.getPostid(), post.getPost_text(), post.getType());
                        }
                        if (viewHolder.like.getTag().toString().equals("liked")){
                            liked(post.getPublisher(), post.getPostid());
                        }
                    }
                });

            }

//            private void comment_activity(String s) {
//                if (!TextUtils.isEmpty(s)){
//                    viewHolder.send_comment.setVisibility(View.VISIBLE);
//                }else {
//                    viewHolder.send_comment.setVisibility(View.GONE);
//                }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeNotificationItem(final String postid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        final String key = snapshot.getKey();
                        DatabaseReference refa = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(key);
                        refa.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Notification n = dataSnapshot.getValue(Notification.class);
                                    if (n.getPostid().equals(postid)){
                                        FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(key)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                        Log.d("tag", "exactid: "+key);
                                    }else {
                                        Toast.makeText(mContext, "no exact match", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Log.d("tag", "Lodingids: "+key +"postid "+ postid);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //removecomment
        final DatabaseReference r = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    r.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //remove likes
        final DatabaseReference e = FirebaseDatabase.getInstance().getReference("Likes").child(postid);
        e.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    e.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username,post_text,likes,comments,edit,delete,save, attribute_text, timego, caption;
        ImageView image_profile,like,more,feature_photo;
        EditText comment_text;
        ImageButton send_comment;
        RecyclerView recyclerView_comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attribute_text = itemView.findViewById(R.id.attribue_text);
            feature_photo = itemView.findViewById(R.id.feature_photo);
            username = itemView.findViewById(R.id.username);
            post_text = itemView.findViewById(R.id.post_text);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            send_comment = itemView.findViewById(R.id.send_comment);
            more = itemView.findViewById(R.id.dot_menu);
            timego = itemView.findViewById(R.id.timego);
            caption = itemView.findViewById(R.id.caption);

        }
    }

    private  void  getComments(String postid, final TextView comments, RecyclerView recyclerView_comments){
        reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0){
                    comments.setVisibility(View.GONE);
                }else if (dataSnapshot.getChildrenCount() == 1){
                    comments.setVisibility(View.VISIBLE);
                    comments.setText( dataSnapshot.getChildrenCount() + " Comment");

                }else {
                    comments.setVisibility(View.VISIBLE);
                    comments.setText( dataSnapshot.getChildrenCount() + " Comments");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void isLiked(String postid, final ImageView imageView){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_fav_red);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_fav_light);
                    imageView.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countLikes(final TextView likes, String postid){
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
                if (dataSnapshot.getChildrenCount() == 0){
                    likes.setVisibility(View.GONE);
                }else if (dataSnapshot.getChildrenCount() == 1){
                    likes.setVisibility(View.VISIBLE);
                    likes.setText(dataSnapshot.getChildrenCount() + " like");
                }else {
                    likes.setVisibility(View.VISIBLE);
                    likes.setText(dataSnapshot.getChildrenCount() + " likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void liked(final String publisher, final String postid ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes").child(postid).child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String notificationid = dataSnapshot.child("notificationid").getValue().toString();
//                    removeNoty(publisher, postid, notificationid );
                    FirebaseDatabase.getInstance().getReference("Notifications").child(publisher).child(notificationid).removeValue();
                    FirebaseDatabase.getInstance().getReference("Likes").child(postid).child(firebaseUser.getUid()).removeValue();

                }else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void like(final String publisher, String postid, final String postext, final String postType) {
        if (!firebaseUser.getUid().equals(publisher)){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user1 = dataSnapshot.getValue(User.class);
//                    Toast.makeText(mContext, "publisherid: "+publisher + "username:- "+ " "+ user1.getUsername() + "post text: "+postext, Toast.LENGTH_SHORT).show();
//                    sendNotification(publisher, user1.getUsername(), postext, postType);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        }

        addNotification(publisher, postid);
    }

    private void addNotification(String userid, String postid  ){
        String notificationId = reference.push().getKey();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());


        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("notificationid", notificationId);
        reference.setValue(hashMap1);
        Log.d("tag", "adding notifciation");

        if (!userid.equals(firebaseUser.getUid())){
            reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", "like your post");
            hashMap.put("postid", postid);
            hashMap.put("notificationid", notificationId);
            hashMap.put("ispost", true);
            hashMap.put("time", currentTime);
            hashMap.put("date", currentDate);
            hashMap.put("isseen", "false");
            hashMap.put("timestamp", ServerValue.TIMESTAMP);
            reference.child(notificationId).setValue(hashMap);
        }
    }


//    private void sendNotification(final String publisher, final String username, final String msg, final String postType) {
//
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(publisher);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Token token = snapshot.getValue(Token.class);
//                    if (postType.equals("feature_photo")) {
//                        final   Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "["+username+"] liked your feature photo "+msg+" ", "Notification",
//                                publisher);
//                        final Sender sender = new Sender(data, token.getToken());
//                        apiService.sendNotification(sender)
//                                .enqueue(new Callback<MyResponse>() {
//                                    @Override
//                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                                        if (response.code() == 200){
//                                            if (response.body().success !=1 ){
//                                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//                                            }else {
//                                            }
//                                        }
//                                    }
//                                    @Override
//                                    public void onFailure(Call<MyResponse> call, Throwable t) {
//                                    }
//                                });
//                    }else {
//                      final  Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "["+username+"] liked your post ' "+msg+" '", "Notification",
//                                publisher);
//                        final Sender sender = new Sender(data, token.getToken());
//                        apiService.sendNotification(sender)
//                                .enqueue(new Callback<MyResponse>() {
//                                    @Override
//                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                                        if (response.code() == 200){
//                                            if (response.body().success !=1 ){
//                                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//                                            }else {
//                                            }
//                                        }
//                                    }
//                                    @Override
//                                    public void onFailure(Call<MyResponse> call, Throwable t) {
//                                    }
//                                });
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

//    private void sendCommentNotification(final String publisher, final String username, final String msg) {
//
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(publisher);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "["+username+"] Commented: ' "+msg+" '", "Notification",
//                            publisher);
//                    Sender sender = new Sender(data, token.getToken());
//
//
//
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                                    if (response.code() == 200){
//                                        if (response.body().success !=1 ){
//                                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//                                        }else {
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
}
