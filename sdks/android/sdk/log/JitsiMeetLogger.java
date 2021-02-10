package org.jitsi.meet.sdk.log;

import timber.log.Timber;

public class JitsiMeetLogger {
   public static void addHandler(JitsiMeetBaseLogHandler handler) {
      if (!Timber.forest().contains(handler)) {
         try {
            Timber.plant(handler);
         } catch (Throwable var2) {
            Timber.w(var2, "Couldn't add log handler", new Object[0]);
         }
      }

   }

   public static void removeHandler(JitsiMeetBaseLogHandler handler) {
      if (Timber.forest().contains(handler)) {
         try {
            Timber.uproot(handler);
         } catch (Throwable var2) {
            Timber.w(var2, "Couldn't remove log handler", new Object[0]);
         }
      }

   }

   public static void v(String message, Object... args) {
      Timber.v(message, args);
   }

   public static void v(Throwable t, String message, Object... args) {
      Timber.v(t, message, args);
   }

   public static void v(Throwable t) {
      Timber.v(t);
   }

   public static void d(String message, Object... args) {
      Timber.d(message, args);
   }

   public static void d(Throwable t, String message, Object... args) {
      Timber.d(t, message, args);
   }

   public static void d(Throwable t) {
      Timber.d(t);
   }

   public static void i(String message, Object... args) {
      Timber.i(message, args);
   }

   public static void i(Throwable t, String message, Object... args) {
      Timber.i(t, message, args);
   }

   public static void i(Throwable t) {
      Timber.i(t);
   }

   public static void w(String message, Object... args) {
      Timber.w(message, args);
   }

   public static void w(Throwable t, String message, Object... args) {
      Timber.w(t, message, args);
   }

   public static void w(Throwable t) {
      Timber.w(t);
   }

   public static void e(String message, Object... args) {
      Timber.e(message, args);
   }

   public static void e(Throwable t, String message, Object... args) {
      Timber.e(t, message, args);
   }

   public static void e(Throwable t) {
      Timber.e(t);
   }

   static {
      addHandler(new JitsiMeetDefaultLogHandler());
   }
}
