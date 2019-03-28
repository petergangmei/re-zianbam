package com.zianbam.yourcommunity.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.MainActivity;
import com.zianbam.yourcommunity.ProfileActivity;
import com.zianbam.yourcommunity.R;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                sendOreoNotification(remoteMessage);
            }else {
                sendNotification(remoteMessage);

            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String post_publisher = remoteMessage.getData().get("sented");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String postURL = remoteMessage.getData().get("postURL");
        String contentID = remoteMessage.getData().get("contentID");
        String type = remoteMessage.getData().get("type");


        if (type.equals("post")){

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

            int i = 0;
            if (j>0){
                i = j;
            }

            oreoNotification.getManager().notify(i, builder.build());

        }else if (type.equals("comment")){
            //ye
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            bundle.putString("scrollDown", "true");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

            int i = 0;
            if (j>0){
                i = j;
            }
            oreoNotification.getManager().notify(i, builder.build());

        }else if (type.equals("message")){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userid", post_publisher);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

            int i = 0;
            if (j>0){
                i = j;
            }
        }else if (type.equals("follow")){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("profileID", user);
            bundle.putString("postid", contentID);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

            int i = 0;
            if (j>0){
                i = j;
            }

            oreoNotification.getManager().notify(i, builder.build());

        } else if (type.equals("like_comment")) {
            //ye
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            bundle.putString("scrollDown", "true");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

            int i = 0;
            if (j>0){
                i = j;
            }
            oreoNotification.getManager().notify(i, builder.build());

        }else {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", post_publisher);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

                OreoNotification oreoNotification = new OreoNotification(this);
                Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon, postURL);

                int i = 0;
                if (j>0){
                    i = j;
                }
            }





    }


///sendnotificastion for version below android 8
    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String post_publisher = remoteMessage.getData().get("sented");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String postURL = remoteMessage.getData().get("postURL");
        String contentID = remoteMessage.getData().get("contentID");
        String type = remoteMessage.getData().get("type");



        if (type.equals("post")){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);

            try {
                URL url = new URL(postURL);
                final Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.zianbam_logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setLargeIcon(image)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);

            } catch(IOException e) {
                System.out.println(e);
            }

            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.zianbam_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j>0){
                i = j;
            }
            noti.notify(i,builder.build());

        }else if (type.equals("comment")){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);

            try {
                URL url = new URL(postURL);
                final Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.zianbam_logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setLargeIcon(image)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);

            } catch(IOException e) {
                System.out.println(e);
            }

            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.zianbam_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j>0){
                i = j;
            }
            noti.notify(i,builder.build());
        }else if (type.equals("follow")){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("profileID", user);
            bundle.putString("postid", contentID);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);

            try {
                URL url = new URL(postURL);
                final Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.zianbam_logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setLargeIcon(image)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);

            } catch(IOException e) {
                System.out.println(e);
            }

            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.zianbam_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j>0){
                i = j;
            }
            noti.notify(i,builder.build());

        }else if (type.equals("like_comment")) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", post_publisher);
            bundle.putString("postid", contentID);
            bundle.putString("scrollDown", "true");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);


            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.zianbam_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j>0){
                i = j;
            }
            noti.notify(i,builder.build());


        }else {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            long vibrate[] = {100,500,100,500};
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j>0){
                i = j;
            }
            noti.notify(i,builder.build());
        }



    }
}
