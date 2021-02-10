package org.jitsi.meet.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import java.util.Random;
import org.jitsi.meet.sdk.R.string;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

class OngoingNotification {
   private static final String TAG = OngoingNotification.class.getSimpleName();
   private static final String CHANNEL_ID = "JitsiNotificationChannel";
   private static final String CHANNEL_NAME = "Ongoing Conference Notifications";
   static final int NOTIFICATION_ID = (new Random()).nextInt(99999) + 10000;

   static void createOngoingConferenceNotificationChannel() {
      if (VERSION.SDK_INT >= 26) {
         Context context = ReactInstanceManagerHolder.getCurrentActivity();
         if (context == null) {
            JitsiMeetLogger.w(TAG + " Cannot create notification channel: no current context");
         } else {
            NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
            NotificationChannel channel = notificationManager.getNotificationChannel("JitsiNotificationChannel");
            if (channel == null) {
               channel = new NotificationChannel("JitsiNotificationChannel", "Ongoing Conference Notifications", 3);
               channel.enableLights(false);
               channel.enableVibration(false);
               channel.setShowBadge(false);
               notificationManager.createNotificationChannel(channel);
            }
         }
      }
   }

   static Notification buildOngoingConferenceNotification() {
      Context context = ReactInstanceManagerHolder.getCurrentActivity();
      if (context == null) {
         JitsiMeetLogger.w(TAG + " Cannot create notification: no current context");
         return null;
      } else {
         Intent notificationIntent = new Intent(context, context.getClass());
         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
         Builder builder;
         if (VERSION.SDK_INT >= 26) {
            builder = new Builder(context, "JitsiNotificationChannel");
         } else {
            builder = new Builder(context);
         }

         builder.setCategory("call").setContentTitle(context.getString(string.ongoing_notification_title)).setContentText(context.getString(string.ongoing_notification_text)).setPriority(0).setContentIntent(pendingIntent).setOngoing(true).setAutoCancel(false).setVisibility(1).setUsesChronometer(true).setOnlyAlertOnce(true).setSmallIcon(context.getResources().getIdentifier("ic_notification", "drawable", context.getPackageName()));
         if (AudioModeModule.useConnectionService()) {
            Intent hangupIntent = new Intent(context, JitsiMeetOngoingConferenceService.class);
            hangupIntent.setAction(JitsiMeetOngoingConferenceService.Actions.HANGUP);
            PendingIntent hangupPendingIntent = PendingIntent.getService(context, 0, hangupIntent, 134217728);
            Action hangupAction = new Action(0, "Hang up", hangupPendingIntent);
            builder.addAction(hangupAction);
         }

         return builder.build();
      }
   }
}
