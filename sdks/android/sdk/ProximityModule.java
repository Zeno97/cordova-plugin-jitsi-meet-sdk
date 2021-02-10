package org.jitsi.meet.sdk;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(
   name = "Proximity"
)
class ProximityModule extends ReactContextBaseJavaModule {
   public static final String NAME = "Proximity";
   private final WakeLock wakeLock;

   public ProximityModule(ReactApplicationContext reactContext) {
      super(reactContext);
      PowerManager powerManager = (PowerManager)reactContext.getSystemService("power");

      WakeLock wakeLock;
      try {
         wakeLock = powerManager.newWakeLock(32, "jitsi:Proximity");
      } catch (Throwable var5) {
         wakeLock = null;
      }

      this.wakeLock = wakeLock;
   }

   public String getName() {
      return "Proximity";
   }

   @ReactMethod
   public void setEnabled(final boolean enabled) {
      if (this.wakeLock != null) {
         UiThreadUtil.runOnUiThread(new Runnable() {
            public void run() {
               if (enabled) {
                  if (!ProximityModule.this.wakeLock.isHeld()) {
                     ProximityModule.this.wakeLock.acquire();
                  }
               } else if (ProximityModule.this.wakeLock.isHeld()) {
                  ProximityModule.this.wakeLock.release();
               }

            }
         });
      }
   }
}
