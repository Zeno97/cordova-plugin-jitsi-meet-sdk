package org.jitsi.meet.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.facebook.react.ReactInstanceManager;
import org.devio.rn.splashscreen.SplashScreen;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class JitsiMeet {
   private static JitsiMeetConferenceOptions defaultConferenceOptions;

   public static JitsiMeetConferenceOptions getDefaultConferenceOptions() {
      return defaultConferenceOptions;
   }

   public static void setDefaultConferenceOptions(JitsiMeetConferenceOptions options) {
      if (options != null && options.getRoom() != null) {
         throw new RuntimeException("'room' must be null in the default conference options");
      } else {
         defaultConferenceOptions = options;
      }
   }

   public static String getCurrentConference() {
      return OngoingConferenceTracker.getInstance().getCurrentConference();
   }

   static Bundle getDefaultProps() {
      return defaultConferenceOptions != null ? defaultConferenceOptions.asProps() : new Bundle();
   }

   public static void showDevOptions() {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.showDevOptionsDialog();
      }

   }

   public static boolean isCrashReportingDisabled(Context context) {
      SharedPreferences preferences = context.getSharedPreferences("jitsi-default-preferences", 0);
      String value = preferences.getString("isCrashReportingDisabled", "");
      return Boolean.parseBoolean(value);
   }

   public static void showSplashScreen(Activity activity) {
      try {
         SplashScreen.show(activity);
      } catch (Exception var2) {
         JitsiMeetLogger.e(var2, "Failed to show splash screen");
      }

   }
}
