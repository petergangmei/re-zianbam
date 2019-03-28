package com.zianbam.yourcommunity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.APIService;
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.Model.Notification;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder>{

    private Context mContext;
    private List<Notification> mNotification;
    private DatabaseReference reference;
    APIService apiService;
    private FirebaseUser firebaseUser;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,viewGroup,false);
        return new NotificationAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, int i) {
      final   Notification notification = mNotification.get(i);
      final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        isFollowing(notification.getUserid(), viewholder.followbackBtn);

        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(myid).child(notification.getNotificationid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Notification n = dataSnapshot.getValue(Notification.class);
                    //add isseen value true.

                    //show time ago
                    String timestamepraw = dataSnapshot.child("timestamp").getValue().toString();
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long time = Long.parseLong(timestamepraw);
                    String timeAgo = getTimeAgo.getTimeAgo(time, mContext);
                    viewholder.timeago.setText(timeAgo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


      getUserInfo(viewholder.image_profile, viewholder.username, notification.getUserid());



      if (notification.isIspost()){
          displayNotificationComments(viewholder.text , notification.getText(), viewholder.text, notification.getPostid(), notification.getNotificationid(), notification.getPointerid(), notification.getType());

          reference = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
       reference.keepSynced(true);
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   Post post = dataSnapshot.getValue(Post.class);
                   if (post.getType().equals("photo_post")){
                       viewholder.post_image.setVisibility(View.VISIBLE);
                       viewholder.post_text.setVisibility(View.GONE);
                       getPostImage(viewholder.post_image, notification.getPostid(), viewholder.username);
                   }else if (post.getType().equals("text_post")){
                       if(!notification.getText().equals("like your post")){
                           viewholder.post_text.setVisibility(View.GONE);
                       }else {
                           viewholder.post_text.setVisibility(View.VISIBLE);
                       }

                       viewholder.post_image.setVisibility(View.GONE);
                       reference = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
                       reference.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.exists()){
                                   final Post p= dataSnapshot.getValue(Post.class);
                                   if (p.getDeleted().equals("false")){
                                       viewholder.username.setTag("not_deleted");
                                   }else if (p.getDeleted().equals("true")){
                                       viewholder.username.setTag("not_deleted");
                                   }
                                    viewholder.post_text.setText(" [ "+ p.getPost_text()+" ] ");
                               }
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {
                           }
                       });
                   }else {
                       viewholder.post_text.setVisibility(View.GONE);
                       viewholder.post_image.setVisibility(View.GONE);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

      }else {
          viewholder.post_image.setVisibility(View.GONE);
          viewholder.followbackBtn.setVisibility(View.VISIBLE);
          final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
          viewholder.text.setText(notification.getText());
          viewholder.post_text.setVisibility(View.GONE);


          viewholder.followbackBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (viewholder.followbackBtn.getText().equals("Follow back")){
                      addNofication(notification.getUserid());
                      reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                      reference.keepSynced(true);
                      reference.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.exists()){
                                  User user = dataSnapshot.getValue(User.class);
//                                  sendNotification(notification.getUserid(), u.getUsername());
                                  FOLLOW_PUSH_Notification( notification.getUserid(), user.getUsername());
                              }
                          }
                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {
                          }
                      });
                      Toast.makeText(mContext, "Followed!", Toast.LENGTH_SHORT).show();
                  }else {
                      RemoveNotification(notification.getUserid());

                  }
              }
          });
      }


      onItemViewClicklistner(viewholder.itemView, viewholder.text, viewholder.username, notification);
      onItemViewLongClicklistner(viewholder.itemView, notification);



    }

    private void onItemViewLongClicklistner(View itemView, final Notification notification) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to delete?");
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                               final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(notification.getNotificationid());
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext, "Notification Deleted!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

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
                return false;
            }
        });
    }

    private void onItemViewClicklistner(View itemView,final TextView text, final TextView username, final  Notification notification) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uTag = text.getTag().toString();
                if (!notification.isIspost()) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("profileID", notification.getUserid());
                    mContext.startActivity(intent);
                } else {
                    reference = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Post p = dataSnapshot.getValue(Post.class);
                                if (p.getDeleted().equals("true")) {
                                    Dialog dialog = new Dialog(mContext);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_simple_msg);
                                    TextView title = dialog.findViewById(R.id.textTitle);
                                    TextView msg = dialog.findViewById(R.id.textMsg);
                                    title.setText("Post deleted!");
                                    msg.setText("The post which you are looking for was deleted.");
                                    dialog.show();

                                } else if (p.getDeleted().equals("false")) {
                                    if (uTag.equals("not_deleted_comment")) {
                                        //if the comment is not deleted directly send the user to comment activty
                                        if (notification.getType().equals("like_on_post")) {
                                            Intent intent = new Intent(mContext, CommentsActivity.class);
                                            intent.putExtra("postid", notification.getPostid());
                                            intent.putExtra("publisherid", notification.getUserid());
                                            mContext.startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(mContext, CommentsActivity.class);
                                            intent.putExtra("postid", notification.getPostid());
                                            intent.putExtra("publisherid", notification.getUserid());
                                            intent.putExtra("scrollDown", "true");
                                            mContext.startActivity(intent);
                                        }


                                    } else if (uTag.equals("deleted_comment")) {
                                        //if the comment is deleted , toast a message notifying the user that he/she have already deleted the message which the post pubhlisher liked earlier.
                                        if (text.getTag().toString().equals("deleted_comment")) {
                                            Toast.makeText(mContext, "Your comment is already deleted.", Toast.LENGTH_LONG).show();
                                        }
                                        if (notification.isIspost()) {

                                            if (notification.getType().equals("like_on_post")) {
                                                Intent intent = new Intent(mContext, CommentsActivity.class);
                                                intent.putExtra("postid", notification.getPostid());
                                                intent.putExtra("publisherid", notification.getUserid());
                                                mContext.startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(mContext, CommentsActivity.class);
                                                intent.putExtra("postid", notification.getPostid());
                                                intent.putExtra("publisherid", notification.getUserid());
                                                intent.putExtra("scrollDown", "true");
                                                mContext.startActivity(intent);
                                            }

                                        } else {
                                            Toast.makeText(mContext, "Hellow", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    private void displayNotificationComments(final TextView un, final String text, final TextView text1, final String postid, String notificationid, final String pointerid, String type) {
        if (type.equals("like_comment")){
            reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(pointerid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        text1.setText(text);
                        un.setTag("not_deleted_comment");
                    }else {
                        text1.setText(text+" (deleted)");
                        un.setTag("deleted_comment");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            text1.setText(text);
        }


    }


    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        ImageView image_profile, post_image;
        TextView username, text, post_text, timeago;
        Button followbackBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
            post_text = itemView.findViewById(R.id.post_text);
            timeago = itemView.findViewById(R.id.timeago);
            followbackBtn = itemView.findViewById(R.id.followbackBtn);
        }
    }

    private void FOLLOW_PUSH_Notification(final String publisher, final String myusername) {

        final String userid = firebaseUser.getUid();
        final String post_publisherid = publisher;
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
                                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
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
    private void isFollowing(final String userid, final Button button){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("Following");
                }else {
                    button.setText("Follow back");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private  void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, final String postid, final TextView  username){
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post.getDeleted().equals("false")){
                    Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.image_placeholder).into(imageView);
                    username.setTag("not_deleted");
                }else {
                    Picasso.get().load(R.drawable.deleted_file).into(imageView);
                    imageView.getLayoutParams().width=80;
                    username.setTag("deleted");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RemoveNotification(final String id) {
       final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers").child(firebaseUser.getUid()).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String notificationid = dataSnapshot.child("id").getValue().toString();

                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).child(notificationid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(id).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers")
                            .child(firebaseUser.getUid()).child(id).removeValue();

                    Toast.makeText(mContext, "Unfollowed!", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(mContext, "Data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addNofication(String userid){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
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
                .child(userid);
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("id", notificationId);
        ref.child(firebaseUser.getUid()).setValue(hashMap1);

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userid).child("followers")
                .child(firebaseUser.getUid());
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("id", notificationId);
        ref1.child(userid).setValue(hashMap2);

    }


}
