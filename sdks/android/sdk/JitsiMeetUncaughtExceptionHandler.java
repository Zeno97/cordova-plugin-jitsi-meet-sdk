package org.jitsi.meet.sdk;

import java.lang.Thread.UncaughtExceptionHandler;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

class JitsiMeetUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final UncaughtExceptionHandler defaultUncaughtExceptionHandler;

   public static void register() {
      UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      JitsiMeetUncaughtExceptionHandler uncaughtExceptionHandler = new JitsiMeetUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
      Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
   }

   private JitsiMeetUncaughtExceptionHandler(UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
      this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
   }

   public void uncaughtException(Thread t, Throwable e) {
      JitsiMeetLogger.e(e, this.getClass().getSimpleName() + " FATAL ERROR");
      if (AudioModeModule.useConnectionService()) {
         ConnectionService.abortConnections();
      }

      if (this.defaultUncaughtExceptionHandler != null) {
         this.defaultUncaughtExceptionHandler.uncaughtException(t, e);
      }

   }
}
