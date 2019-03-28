package com.zianbam.yourcommunity.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.zianbam.yourcommunity.R;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

//import com.zianbam.hangouts.Model.Notification;

public class OreoNotification extends ContextWrapper {
    private static final String CHANEL_ID = "com.zianbam.socialhangout";
    private static final String CHANEL_NAME = "zianbam";
    private NotificationManager notificationMaager;
    Context mContext;
    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel();
        }

    }
@TargetApi((Build.VERSION_CODES.O))
    private void createChannel() {
    NotificationChannel channel = new NotificationChannel(CHANEL_ID, CHANEL_NAME,
            NotificationManager.IMPORTANCE_HIGH);

    channel.enableLights(true);
    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    getManager().createNotificationChannel(channel);

    }
    public NotificationManager getManager(){
        if (notificationMaager == null){
            notificationMaager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationMaager;
    }
    @TargetApi((Build.VERSION_CODES.O))
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon, String postURL){

        try {
            URL url = new URL(postURL);
      final      Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Uri sound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
            return  new Notification.Builder(getApplicationContext(), CHANEL_ID)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(image)
                    .setVibrate(new long[]{0, 0, 00})
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.zianbam_logo)
                    .setAutoCancel(true);
        } catch(IOException e) {
            System.out.println(e);
        }
        long vibrate[] = {100,500,100,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();


        Uri sound = Uri.parse("android.resource://com.zianbam.yourcommunity/" + R.raw.notification_sound_one);
        return  new Notification.Builder(getApplicationContext(), CHANEL_ID)
                .setVibrate(new long[]{0, 0, 00})
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.zianbam_logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

    }

    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}
