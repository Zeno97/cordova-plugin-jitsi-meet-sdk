package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import javax.annotation.Nonnull;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "LogBridge"
)
class LogBridgeModule extends ReactContextBaseJavaModule {
   public static final String NAME = "LogBridge";

   public LogBridgeModule(@Nonnull ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public String getName() {
      return "LogBridge";
   }

   @ReactMethod
   public void trace(String message) {
      JitsiMeetLogger.v(message);
   }

   @ReactMethod
   public void debug(String message) {
      JitsiMeetLogger.d(message);
   }

   @ReactMethod
   public void info(String message) {
      JitsiMeetLogger.i(message);
   }

   @ReactMethod
   public void log(String message) {
      JitsiMeetLogger.i(message);
   }

   @ReactMethod
   public void warn(String message) {
      JitsiMeetLogger.w(message);
   }

   @ReactMethod
   public void error(String message) {
      JitsiMeetLogger.e(message);
   }
}
