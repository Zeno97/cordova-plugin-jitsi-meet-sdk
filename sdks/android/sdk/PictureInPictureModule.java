package org.jitsi.meet.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PictureInPictureParams.Builder;
import android.os.Build.VERSION;
import android.util.Rational;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "PictureInPicture"
)
class PictureInPictureModule extends ReactContextBaseJavaModule {
   public static final String NAME = "PictureInPicture";
   private static final String TAG = "PictureInPicture";
   private static boolean isSupported;
   private boolean isDisabled;

   public PictureInPictureModule(ReactApplicationContext reactContext) {
      super(reactContext);
      ActivityManager am = (ActivityManager)reactContext.getSystemService("activity");
      isSupported = VERSION.SDK_INT >= 26 && !am.isLowRamDevice();
   }

   public Map<String, Object> getConstants() {
      Map<String, Object> constants = new HashMap();
      constants.put("SUPPORTED", isSupported);
      return constants;
   }

   @TargetApi(26)
   public void enterPictureInPicture() {
      if (!this.isDisabled) {
         if (!isSupported) {
            throw new IllegalStateException("Picture-in-Picture not supported");
         } else {
            Activity currentActivity = this.getCurrentActivity();
            if (currentActivity == null) {
               throw new IllegalStateException("No current Activity!");
            } else {
               JitsiMeetLogger.i("PictureInPicture Entering Picture-in-Picture");
               Builder builder = (new Builder()).setAspectRatio(new Rational(1, 1));
               if (!currentActivity.enterPictureInPictureMode(builder.build())) {
                  throw new RuntimeException("Failed to enter Picture-in-Picture");
               }
            }
         }
      }
   }

   @ReactMethod
   public void enterPictureInPicture(Promise promise) {
      try {
         this.enterPictureInPicture();
         promise.resolve((Object)null);
      } catch (RuntimeException var3) {
         promise.reject(var3);
      }

   }

   @ReactMethod
   public void setPictureInPictureDisabled(Boolean disabled) {
      this.isDisabled = disabled;
   }

   public boolean isPictureInPictureSupported() {
      return isSupported;
   }

   public String getName() {
      return "PictureInPicture";
   }
}
