package org.jitsi.meet.sdk;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(
   name = "AndroidSettings"
)
class AndroidSettingsModule extends ReactContextBaseJavaModule {
   public static final String NAME = "AndroidSettings";

   public AndroidSettingsModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public String getName() {
      return "AndroidSettings";
   }

   @ReactMethod
   public void open(Promise promise) {
      Context context = this.getReactApplicationContext();
      Intent intent = new Intent();
      intent.addFlags(268435456);
      intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
      intent.setData(Uri.fromParts("package", context.getPackageName(), (String)null));

      try {
         context.startActivity(intent);
      } catch (ActivityNotFoundException var5) {
         promise.reject(var5);
         return;
      }

      promise.resolve((Object)null);
   }
}
