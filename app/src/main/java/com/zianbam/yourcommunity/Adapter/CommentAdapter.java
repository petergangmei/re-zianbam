package com.zianbam.yourcommunity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.APIService;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.Model.Comment;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.ProfileActivity;
import com.zianbam.yourcommunity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postid;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, ref;
    APIService apiService;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup ,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(i);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //set comment text in the comment field
        viewHolder.comment.setText(comment.getComment());


        //check if comment is liked or not
        isCommentLiked(viewHolder.like_comment, comment.getCommentid(), viewHolder.comment_likes);

        //check comments like count
        getCommentLikeCount(viewHolder.comment_likes, comment.getCommentid());

        //get comment publisher profile
        getcommenterprofile(comment.getPublisherid(), viewHolder.progressbar_propic, viewHolder.image_profile, viewHolder.username);

        //get time ago
        print_timeAgo(viewHolder.timego, comment.getPostid(),comment.getCommentid());


        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToUserProfile(comment.getPublisherid());}
        });
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToUserProfile(comment.getPublisherid());}
        });
        //like  a comment
//        ref = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(firebaseUser.getUid());
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    String notificationid  = dataSnapshot.child("notificationid").getValue().toString();
//                    Toast.makeText(mContext, "data sexist"+notificationid, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        likeComment(comment.getComment(), viewHolder.like_comment, comment.getCommentid(), viewHolder.comment_likes, comment.getPublisherid(), comment.getComment(),"1234");





        //delete comment in long press comment item
        viewHolder.comment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               if (comment.getPublisherid().equals(firebaseUser.getUid())){

                   AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                   alertDialog.setTitle("Would you like to");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                               }
                           });
                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid());
                                   ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()){

                                               FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid()).removeValue();
                                                if (!comment.getUserid().equals(firebaseUser.getUid())){
                                                    FirebaseDatabase.getInstance().getReference("Notifications").child(comment.getUserid()).child(comment.getCommentid()).removeValue();
                                                }
                                           }
                                       }
                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });

                                   dialogInterface.dismiss();;
                               }
                           });
                   alertDialog.show();
               }else{

                   ref = FirebaseDatabase.getInstance().getReference("Posts").child(comment.getPostid());
                   ref.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){
                              final Post post = dataSnapshot.getValue(Post.class);
                               if (post.getPublisher().equals(firebaseUser.getUid())){
                                   final  AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                                   alertDialog.setTitle("Would you like to");
                                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close",
                                           new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           });
                                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid()).removeValue();
                                           if (!comment.getUserid().equals(firebaseUser.getUid())){
                                               FirebaseDatabase.getInstance().getReference("Notifications").child(comment.getUserid()).child(comment.getCommentid()).removeValue();
                                           }
                                       }
                                   });
                                   alertDialog.show();
                               }else {
                                   final  AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                                   alertDialog.setTitle("Would you like to");
                                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close",
                                           new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           });
                                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Report", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           reportComment(comment, post.getPostid());
                                       }
                                   });
                                   alertDialog.show();
                               }
                           }
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {
                       }
                   });


               }
                return true;
            }
        });

    }

    private void goToUserProfile(String publisherid) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra("profileID", publisherid);
        mContext.startActivity(intent);
    }

    private void reportComment(final Comment comment, final String postid) {

        DatabaseReference re = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("comments_report").child(firebaseUser.getUid()).child(postid);
        re.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    showAleardyReportedDialog();
                }else {
                    showreportDialog(comment, postid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showreportDialog(final Comment comment, final String postid) {
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
                final RadioButton radioButton = categoryRadio.findViewById(categoryRadio.getCheckedRadioButtonId());

                DatabaseReference r = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("comments_report").child(firebaseUser.getUid()).child(postid).child(comment.getCommentid());
                r.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Comment p = dataSnapshot.getValue(Comment.class);
                                    int reportcount = p.getReported() + 1;

                                    HashMap<String, Object> h = new HashMap<>();
                                    h.put("reported", reportcount);
                                    reference.updateChildren(h).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ref = FirebaseDatabase.getInstance().getReference("Reports").child("Reporters").child("comments_report");
                                            HashMap<String, Object> h= new HashMap<>();
                                            h.put("post_id", postid);
                                            h.put("comment_id", comment.getCommentid());
                                            h.put("type", "posts_report");
                                            h.put("report_comment", reportComment.getText().toString());
                                            ref.child(firebaseUser.getUid()).child(postid).child(comment.getCommentid()).setValue(h);



                                            ref = FirebaseDatabase.getInstance().getReference("Reports").child("comments_report");
                                            String key = ref.push().getKey();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "comment_report");
                                            hashMap.put("report_category", radioButton.getTag().toString());
                                            hashMap.put("post_id", postid );
                                            hashMap.put("comment_id", comment.getCommentid());
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


    private void showAleardyReportedDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_simple_msg);
        TextView title = dialog.findViewById(R.id.textTitle);
        TextView msg = dialog.findViewById(R.id.textMsg);
        title.setText("Already Reported");
        msg.setText("You have already submit report againts this comment, our team will check and take action accordingly.");
        dialog.show();
    }



    private void getCommentLikeCount(final TextView comment_likes, final String comment_id) {

                ref = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(comment_id);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.getChildrenCount() == 0){
                                comment_likes.setText("");
                            }else if (dataSnapshot.getChildrenCount() == 1){
                                comment_likes.setText(" 1 like");

                            }else if (dataSnapshot.getChildrenCount()>1){
                                comment_likes.setText(dataSnapshot.getChildrenCount()+" likes");
                            }
                        }else {
                            comment_likes.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void isCommentLiked(final ImageView like_comment, String comment_id, TextView comment_likes) {

        ref = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(comment_id).child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    like_comment.setImageResource(R.drawable.ic_fav_red);
                    like_comment.setTag("liked");
                }else {
                    like_comment.setImageResource(R.drawable.ic_fav_light);
                    like_comment.setTag("like");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        getCommentLikeCount(comment_likes, comment_id);


    }

    private void likeComment(final String commentText, final ImageView like_comment, final String comment_id, final TextView comment_likes, final String publisher_id, final String  msg, final String notificationId) {
        like_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like_comment.getTag().toString().equals("like")){
                    insertLikeCommentValue(commentText, comment_id, publisher_id, msg);
                    isCommentLiked(like_comment,comment_id, comment_likes);
                    Toast.makeText(mContext, "publisher: "+publisher_id, Toast.LENGTH_SHORT).show();

                }else if (like_comment.getTag().toString().equals("liked")){

                    removeNotifyCommentPublisher_like(like_comment,comment_id, comment_likes, publisher_id, notificationId);
                    isCommentLiked(like_comment,comment_id, comment_likes);
                }
            }
        });
    }

    private void removeNotifyCommentPublisher_like(final ImageView like_comment, final String comment_id, final TextView comment_likes, final String publisher_id, final String notificationId) {
        ref = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(comment_id).child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String notificationID = dataSnapshot.child("notificationid").getValue().toString();
                    FirebaseDatabase.getInstance().getReference("Notifications").child(publisher_id).child(notificationID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(comment_id).child(firebaseUser.getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getCommentLikeCount(comment_likes, comment_id);
                                            if(!publisher_id.equals(firebaseUser.getUid())){

                                                isCommentLiked(like_comment,comment_id, comment_likes);
                                            }

                                        }


                                    });

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void insertLikeCommentValue(String commentText, String comment_id, final String publisher_id, final String msg) {

        ref = FirebaseDatabase.getInstance().getReference("Comments_like").child(postid).child(comment_id).child(firebaseUser.getUid());
        final String notificationId = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment_publisher", publisher_id);
        hashMap.put("liked_publisher", firebaseUser.getUid());
        hashMap.put("notificationid", notificationId);
        ref.setValue(hashMap);
        notifycommentpublisher_like(commentText, publisher_id, notificationId, postid, msg,comment_id);
    }

    private void notifycommentpublisher_like(final String  commentText, final String publisher_id, String notificationId, final String post_id, String msg, String comment_id) {
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
               hashMap.put("text", "liked your comment: "+msg);
               hashMap.put("postid", post_id);
               hashMap.put("notificationid",notificationId );
               hashMap.put("pointerid", comment_id);
               hashMap.put("type", "like_comment");
               hashMap.put("ispost", true);
               hashMap.put("time", currentTime);
               hashMap.put("date", currentDate);
               hashMap.put("isseen", "false");
               hashMap.put("timestamp", ServerValue.TIMESTAMP);
               reference.child(notificationId).setValue(hashMap);

               ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
               ref.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()) {
                           User user = dataSnapshot.getValue(User.class);
                           COMMENT_LIKE_PUSH_notificaation(commentText, user.getUsername(), post_id, publisher_id);
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
           }else {
               Toast.makeText(mContext, "You are the publisher of this comment", Toast.LENGTH_SHORT).show();

           }
    }

        private void COMMENT_LIKE_PUSH_notificaation(final String  commentText, final String  myusername, final String  post_id, String publisher_id) {


            final String userid = firebaseUser.getUid();
            final String post_publisherid = publisher_id;
            final String type = "like_comment";
//            final String post_id = postid1;
            final String title = "Notification";
            final String body = "["+myusername+"] liked your comment: "+commentText ;
            final String postURL = "null";

            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query query = tokens.orderByKey().equalTo(publisher_id);
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
                                                Toast.makeText(mContext, "sned push notification", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(mContext, "on fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, "ccaancelled", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void print_timeAgo(final TextView timego, String postid, String commentid) {
        ref =  FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(commentid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String time = dataSnapshot.child("timestamp").getValue().toString();
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long realtime = Long.parseLong(time);
                    String timeAgo = getTimeAgo.getTimeAgo(realtime, mContext);
                    timego.setText(timeAgo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getcommenterprofile(final String publisher, final ProgressBar progressbar_propic, final ImageView image_profile, final TextView username) {
        // get user name and image
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageURL()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressbar_propic.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressbar_propic.setVisibility(View.GONE);
                        return false;
                    }
                }).into(image_profile);

                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_profile,like_comment;
        TextView comment, username, timego, comment_likes;
        LinearLayout re1;
        RelativeLayout bottom;
        ScrollView scrollView;
        ProgressBar progressbar_propic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bottom = itemView.findViewById(R.id.bottom);
//            scrollView = itemView.findViewById(R.id.scrollview);
            re1 = itemView.findViewById(R.id.re1);
            image_profile = itemView.findViewById(R.id.image_profile);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username_);
            timego = itemView.findViewById(R.id.timeago);
            progressbar_propic = itemView.findViewById(R.id.progressbar_propic);
            like_comment = itemView.findViewById(R.id.like_comment);
            comment_likes = itemView.findViewById(R.id.comment_likes);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView comment, String publisherid){

    }


}
