package com.zianbam.yourcommunity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.CreatePostActivity;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.Model.Notification;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.APIService;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.ProfileActivity;
import com.zianbam.yourcommunity.R;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
     Context mContext;
    List<Post> mPost;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, ref;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList;
    APIService apiService;
    boolean notify = false;
    private String notificationId;
    List<String> notificationlist;

    List<String> comment_publisher;


    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,viewGroup,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
       final Post post = mPost.get(i);
         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.like.getTag().toString().equals("like")) {
                    //checkback1
                    like(post.getPublisher(), post.getPostid(), post.getPost_text(),  mPost.get(i));
                }
                if (viewHolder.like.getTag().toString().equals("liked")) {
                    liked(post.getPublisher(), post.getPostid());
                } }});
        //display post_text, post_photo, feature_photo
        if (post.getType().equals("feature_photo")) {
            viewHolder.feature_photo.setVisibility(View.VISIBLE);
            viewHolder.post_text.setVisibility(View.GONE);
            Picasso.get().load(post.getImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(viewHolder.feature_photo);
            viewHolder.attribue_text.setText("shared a feature photo");
            viewHolder.attribue_text.setVisibility(View.VISIBLE);

            if (!post.getPost_text().equals("")){
                viewHolder.caption.setVisibility(View.VISIBLE);
                viewHolder.caption.setText(post.getPost_text());
            }

        }else if (post.getType().equals("photo_post")){

            viewHolder.feature_photo.setVisibility(View.VISIBLE);
            viewHolder.post_text.setVisibility(View.GONE);
            if (!post.getImageUrl().isEmpty()){
                Glide.with(mContext)
                        .load(post.getImageUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                viewHolder.pb_photo.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                viewHolder.pb_photo.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewHolder.feature_photo);
            }
            viewHolder.attribue_text.setVisibility(View.VISIBLE);
            viewHolder.attribue_text.setVisibility(View.VISIBLE);
            viewHolder.attribue_text.setText("shared a post");
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
            viewHolder.attribue_text.setVisibility(View.VISIBLE);
            viewHolder.attribue_text.setText("shared a post");
            viewHolder.post_text.setVisibility(View.VISIBLE);
            viewHolder.caption.setVisibility(View.GONE);
            viewHolder.pb_photo.setVisibility(View.GONE);
        }




        //display time ago
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
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


        isLiked(post.getPostid(), viewHolder.like);
        countLikes(viewHolder.likes, post.getPostid());
        getComments(post.getPostid(), viewHolder.comments,viewHolder.recyclerView_comments);





        viewHolder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { gowholikespage(post.getPostid()); }
        });



        viewHolder.feature_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_custom_layout);
                PhotoView photoView = dialog.findViewById(R.id.imageView);
                Glide.with(mContext).load(post.getImageUrl()).placeholder(R.drawable.image_placeholder).into(photoView);
                dialog.show();
                return false;
            }
        });

        viewHolder.feature_photo.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                notify = true;
                if (viewHolder.like.getTag().toString().equals("like")) {
                    like(post.getPublisher(), post.getPostid(), post.getPost_text(), mPost.get(i));
                }
                if (viewHolder.like.getTag().toString().equals("liked")) {
                    liked(post.getPublisher(), post.getPostid());
                }
            }
        },300));

        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { gotoCommentActivityForComments(post.getPostid(), post.getPublisher()); }});
        viewHolder.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { gotoCommentActivity(post.getPostid(), post.getPublisher()); }
        });
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {gotoCommentActivity(post.getPostid(),  post.getPublisher());    }
        });


        viewHolder.post_text.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                if (viewHolder.like.getTag().toString().equals("like")) {
                    like(post.getPublisher(), post.getPostid(), post.getPost_text(), mPost.get(i));
                }
                if (viewHolder.like.getTag().toString().equals("liked")) {
                    liked(post.getPublisher(), post.getPostid());
                }
            }
        },300));

        viewHolder.send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               insertCommentValue(post, viewHolder.comment_text.getText().toString());
                viewHolder.comment_text.setText("");

                if (!post.getPublisher().equals(firebaseUser.getUid())){
                }
                viewHolder.comment_text.setText("");
                viewHolder.comment_text.clearFocus();
            }
        });
        viewHolder.comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                comment_activity(editable.toString().toLowerCase());
            }

            private void comment_activity(String s) {
                if (!TextUtils.isEmpty(s)){
                    viewHolder.send_comment.setVisibility(View.VISIBLE);
                    viewHolder.comment.setVisibility(View.GONE);
                }else {
                    viewHolder.send_comment.setVisibility(View.GONE);
                    viewHolder.comment.setVisibility(View.VISIBLE);
                }
            }
        });
        //more
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
//                                      viewHolder.post_body.setVisibility(View.GONE);
                                Post post_ = mPost.get(i);
                                deletePostimage(post_.getPostid(), post.getImageUrl(), post.getType());

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
                                reportPost(viewHolder.post_body, mPost.get(i));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                final Post post_ = mPost.get(i);
                if (!post_.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);

                }else {
                    popupMenu.getMenu().findItem(R.id.save).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();

                //saves
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                        .child(mAuth.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(post_.getPostid()).exists()){
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


            }
        });



        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()){
                      final User user = dataSnapshot.getValue(User.class);
                      viewHolder.username.setText(user.getUsername());
                      //display userprofileimage
                      Glide.with(mContext).load(user.getImageURL()).listener(new RequestListener<Drawable>() {
                          @Override
                          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                              viewHolder.pb_profile.setVisibility(View.GONE);
                              return false;
                          }

                          @Override
                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                              viewHolder.pb_profile.setVisibility(View.GONE);

                              return false;
                          }
                      }).into(viewHolder.image_profile);
                      //go to user profile when username or image is clicked
                      viewHolder.username.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) { gotoUserprofile(user.getId()); }
                      });
                      viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {gotoUserprofile(user.getId());    }
                      });
                  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void insertCommentValue(Post post, String  msg) {

        final String postid = post.getPostid();
        final String publisherid = post.getPublisher();

        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(postid);
        String notificationid = reference.push().getKey();
        final String commentid = reference.push().getKey();

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());


        //add to
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("comment", msg);
        hashMap1.put("postid", postid);
        hashMap1.put("commentid", commentid);
        hashMap1.put("publisherid", firebaseUser.getUid());
        hashMap1.put("userid", publisherid);
        hashMap1.put("time", currentTime);
        hashMap1.put("date", currentDate);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        reference.child(commentid).setValue(hashMap1);

        addNotificationComment(notificationid, post, msg );
    }

    private void deletePostimage(final String postid, String imageUrl, String  postType) {
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("deleted", "true");
        reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "success deleted", Toast.LENGTH_SHORT).show();
            }
        });

        removeNotificationItem(postid, postType, imageUrl);
        //photo from storage
//        if (postType.equals("photo_post")){
//            Toast.makeText(mContext, "Deleting Post..", Toast.LENGTH_SHORT).show();
//
//            FirebaseDatabase.getInstance().getReference("Posts")
//                    .child(postid).removeValue()
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(mContext, "Post deleted!", Toast.LENGTH_SHORT).show();
//                                }
//                            },500);
//                        }
//                    });
//
//
//        }else if (postType.equals("text_post")){
//            FirebaseDatabase.getInstance().getReference("Posts")
//                    .child(postid).removeValue()
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(mContext, "Post deleted!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }

    }

    private void removeNotificationItem(final String postid, final String postType, final  String imageUrl) {

        comment_publisher = new ArrayList<>();
        notificationlist = new ArrayList<>();



        notificationlist = new ArrayList<>();
        //remove notification from logged in usere prospectie
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                   final String key = snapshot.getKey();
                    notificationlist.add(key);
                    DatabaseReference refa = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(key);
                    refa.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Notification n = dataSnapshot.getValue(Notification.class);
                                if (n.getPostid().equals(postid)){
                                    FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(key)
                                            .removeValue();
                                }
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
        //remove comment likes
        final DatabaseReference c = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid);
        c.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    c.removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void gotoCommentActivity(String postid, String publisher) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("postid", postid);
        intent.putExtra("publisherid", publisher);
        mContext.startActivity(intent);
    }
    private void gotoCommentActivityForComments(String postid, String publisher) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("postid", postid);
        intent.putExtra("publisherid", publisher);
        intent.putExtra("scrollDown", "true");
        mContext.startActivity(intent);

    }

    //show dialog to display userlist who have likd the post.
    private void gowholikespage(String postid) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_userlist);
        final TextView title = dialog.findViewById(R.id.title);
        final ProgressBar progress_bar = dialog.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycleview_users);

        dialog.show();


        final List<String> idList;
        idList = new ArrayList<>();
        final List<User> userlist = new ArrayList<>();
        final UserAdapter userAdapter;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        userAdapter = new UserAdapter(mContext, userlist);
        recyclerView.setAdapter(userAdapter);

        reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers(userlist);
            }

            private void showUsers(final List<User> userlist) {
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userlist.clear();
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            for (String id:idList){
                                if (user.getId().equals(id)){
                                    userlist.add(user);
                                }
                            }
                        }
                        title.setText("LIKES");
                        title.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gotoUserprofile(String id) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra("profileID", id);
        mContext.startActivity(intent);
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    checkback2
    private void like(final String publisher, String postid,  final String postext, Post post ) {
        String notificationId = reference.push().getKey();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("notificationid", notificationId);
        reference.setValue(hashMap1);
        addNotification( postid, publisher, notificationId, post);


    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
     TextView username,post_text,likes,comments,edit,delete,save,attribue_text, timego,caption;
     ImageView image_profile,like,dot_menu,feature_photo , more,comment;
     ProgressBar pb_profile, pb_photo;
     RelativeLayout post_body;
     EditText comment_text;
     ImageButton send_comment;
     RecyclerView recyclerView_comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_body = itemView.findViewById(R.id.post_body);
            attribue_text = itemView.findViewById(R.id.attribue_text);
            feature_photo = itemView.findViewById(R.id.feature_photo);
            comment_text = itemView.findViewById(R.id.comment_text);
            username = itemView.findViewById(R.id.username);
            post_text = itemView.findViewById(R.id.post_text);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            send_comment = itemView.findViewById(R.id.send_comment);
            dot_menu = itemView.findViewById(R.id.dot_menu);
            more = itemView.findViewById(R.id.dot_menu);
            timego = itemView.findViewById(R.id.timego);
            caption = itemView.findViewById(R.id.caption);
            comment = itemView.findViewById(R.id.comment);
            pb_profile = itemView.findViewById(R.id.progress_bar_profile);
            pb_photo = itemView.findViewById(R.id.progress_bar_photo);


        }
    }

    private void addNotification(String postid, final String userid, String notificationId , final Post post){


        if (!userid.equals(firebaseUser.getUid())){
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            final String currentTime =currentTimeFormat.format(calForTime.getTime());
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", "like your post");
            hashMap.put("postid", postid);
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
                        LIKE_PUSH_Notification( myusername, post);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


    }
    private void LIKE_PUSH_Notification( final String  myusername, final Post post) {

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
                                            Toast.makeText(mContext, "Failed like", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(mContext, "Failed ", Toast.LENGTH_SHORT).show();
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
                    Log.d("tag", imageView.getTag().toString());
                }else {
                    imageView.setImageResource(R.drawable.ic_fav_light);
                    imageView.setTag("like");
                    Log.d("tag", imageView.getTag().toString());

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

    private void addNotificationComment(String notificationId, final Post post, final String msg){

        final String postid = post.getPostid();
        final String publisherid = post.getPublisher();

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());


        if (!publisherid.equals(firebaseUser.getUid())){

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Commented: "+msg);
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("notificationid", notificationId);
        hashMap.put("pointerid", "null");
        hashMap.put("type", "comment_on_post");
        hashMap.put("time", currentTime);
        hashMap.put("date", currentDate);
        hashMap.put("isseen", "false");
        hashMap.put("timestamp", System.currentTimeMillis());
        reff.child(notificationId).setValue(hashMap);

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



    private  void getImage(final ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Picasso.get().load(user.getImageURL()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editPost(String postid){ }

    private void deletePost(String postid){

    }
    private void reportPost(final RelativeLayout pbody, final Post post){
        DatabaseReference re = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("posts_report").child(firebaseUser.getUid()).child(post.getPostid());
        re.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    showAleardyReportedDialog();
                }else {
                    showreportDialog(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }

    private void showAleardyReportedDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_simple_msg);
        TextView title = dialog.findViewById(R.id.textTitle);
        TextView msg = dialog.findViewById(R.id.textMsg);
        title.setText("Already Reported");
        msg.setText("You have already submit report againts this post, our team will check and take action accordingly.");
        dialog.show();
    }

    private void showreportDialog(final Post post) {
        final Dialog dialog1 = new Dialog(mContext);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_report_form);
        final Button btn = dialog1.findViewById(R.id.submitReportBtn);
        final EditText reportComment = dialog1.findViewById(R.id.reportComment);
        final RadioGroup categoryRadio = dialog1.findViewById(R.id.reportCategory_radio);
        dialog1.show();
        categoryRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btn.setEnabled(true);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  RadioButton radioButton = categoryRadio.findViewById(categoryRadio.getCheckedRadioButtonId());

                DatabaseReference r = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("posts_report").child(firebaseUser.getUid()).child(post.getPostid());
                r.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Post p = dataSnapshot.getValue(Post.class);
                                    int reportcount = p.getReported() + 1;

                                    HashMap<String, Object> h = new HashMap<>();
                                    h.put("reported", reportcount);
                                    reference.updateChildren(h).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ref = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("posts_report");
                                            HashMap<String, Object> h= new HashMap<>();
                                            h.put("post_id", post.getPostid());
                                            h.put("comment_id", "null");
                                            h.put("type", "posts_report");
                                            h.put("report_comment", reportComment.getText().toString());
                                            ref.child(firebaseUser.getUid()).child(post.getPostid()).setValue(h);



                                            ref = FirebaseDatabase.getInstance().getReference("Reports").child("posts_report");
                                            String key = ref.push().getKey();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "post_report");
                                            hashMap.put("report_category", radioButton.getTag().toString());
                                            hashMap.put("post_id", post.getPostid() );
                                            hashMap.put("comment_id", "null");
                                            hashMap.put("report_post", reportComment.getText().toString());
                                            hashMap.put("user_comment_text", "null");
                                            hashMap.put("report_publisher", firebaseUser.getUid());
                                            ref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog1.dismiss();

                                                    final Dialog dialog = new Dialog(mContext);
                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog.setContentView(R.layout.dialog_simple_msg);
                                                    TextView title = dialog.findViewById(R.id.textTitle);
                                                    TextView msg = dialog.findViewById(R.id.textMsg);
                                                    title.setText("Report Submitted");
                                                    msg.setText("Your report is being reviewed, our team will check and take appropirate action!");
                                                    dialog.show();

                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(mContext, "You have already reported this post.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        });

    }


}
