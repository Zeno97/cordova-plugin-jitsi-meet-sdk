package org.jitsi.meet.sdk;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build.VERSION;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class JitsiMeetOngoingConferenceService extends Service implements OngoingConferenceTracker.OngoingConferenceListener {
   private static final String TAG = JitsiMeetOngoingConferenceService.class.getSimpleName();

   static void launch(Context context) {
      OngoingNotification.createOngoingConferenceNotificationChannel();
      Intent intent = new Intent(context, JitsiMeetOngoingConferenceService.class);
      intent.setAction(Actions.START);
      ComponentName componentName;
      if (VERSION.SDK_INT >= 26) {
         componentName = context.startForegroundService(intent);
      } else {
         componentName = context.startService(intent);
      }

      if (componentName == null) {
         JitsiMeetLogger.w(TAG + " Ongoing conference service not started");
      }

   }

   static void abort(Context context) {
      Intent intent = new Intent(context, JitsiMeetOngoingConferenceService.class);
      context.stopService(intent);
   }

   public void onCreate() {
      super.onCreate();
      OngoingConferenceTracker.getInstance().addListener(this);
   }

   public void onDestroy() {
      OngoingConferenceTracker.getInstance().removeListener(this);
      super.onDestroy();
   }

   public IBinder onBind(Intent intent) {
      return null;
   }

   public int onStartCommand(Intent intent, int flags, int startId) {
      String action = intent.getAction();
      if (Actions.START.equals(action)) {
         Notification notification = OngoingNotification.buildOngoingConferenceNotification();
         if (notification == null) {
            this.stopSelf();
            JitsiMeetLogger.w(TAG + " Couldn't start service, notification is null");
         } else {
            this.startForeground(OngoingNotification.NOTIFICATION_ID, notification);
            JitsiMeetLogger.i(TAG + " Service started");
         }
      } else if (Actions.HANGUP.equals(action)) {
         JitsiMeetLogger.i(TAG + " Hangup requested");
         if (AudioModeModule.useConnectionService()) {
            ConnectionService.abortConnections();
         }

         this.stopSelf();
      } else {
         JitsiMeetLogger.w(TAG + " Unknown action received: " + action);
         this.stopSelf();
      }

      return 2;
   }

   public void onCurrentConferenceChanged(String conferenceUrl) {
      if (conferenceUrl == null) {
         this.stopSelf();
         JitsiMeetLogger.i(TAG + "Service stopped");
      }

   }

   static final class Actions {
      static final String START;
      static final String HANGUP;

      static {
         START = JitsiMeetOngoingConferenceService.TAG + ":START";
         HANGUP = JitsiMeetOngoingConferenceService.TAG + ":HANGUP";
      }
   }
}
