package com.developer.dinhduy.chatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.PublicKey;

public class MyMesagingService extends FirebaseMessagingService {
    public Context context;

    public MyMesagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String NotificationTitle = remoteMessage.getNotification().getTitle();
        String Notification_Messemger=remoteMessage.getNotification().getBody();
        SendNotification(Notification_Messemger,NotificationTitle);
    }
    private  void SendNotification (String messenger,String Title){
        Intent intent= new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent paPendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder  builder= new  NotificationCompat.Builder(this)
                        .setContentText(messenger)
                        .setContentTitle(Title)
                        .setSound(uri)
                        .setSmallIcon(R.drawable.avatar_default)
                        .setContentIntent(paPendingIntent);
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());


    }
}
