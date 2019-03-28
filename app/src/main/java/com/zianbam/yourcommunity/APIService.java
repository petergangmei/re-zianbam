package com.zianbam.yourcommunity;

import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
  @Headers(
          {
                  "Content-Type:application/json",
                  "Authorization:key=AAAA8F0rMlQ:APA91bH8-KtTDBxA-OO7GCf4yvAcKOdsEdmIqDbYPPBO0t0DsSQzXuFbCYefwYYkRf7IuSIjG89Hrt6tHmaTT3pQbtJKxalH3dJw4ON3fcNUPOpAWDoDCKAugkHLfwcGgXZU3wUWo4to"
          }
  )
  @POST("fcm/send")
  Call<MyResponse> sendNotification(@Body Sender body);
}
