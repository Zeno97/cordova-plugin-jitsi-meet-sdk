package org.jitsi.meet.sdk.log;

import android.util.Log;
import org.jetbrains.annotations.NotNull;

public class JitsiMeetDefaultLogHandler extends JitsiMeetBaseLogHandler {
   private static final String TAG = "JitsiMeetSDK";

   protected void doLog(int priority, @NotNull String tag, @NotNull String msg) {
      Log.println(priority, tag, msg);
   }

   protected String getDefaultTag() {
      return "JitsiMeetSDK";
   }
}
