package org.jitsi.meet.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;

@ReactModule(
   name = "AppInfo"
)
class AppInfoModule extends ReactContextBaseJavaModule {
   public static final String NAME = "AppInfo";

   public AppInfoModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public Map<String, Object> getConstants() {
      Context context = this.getReactApplicationContext();
      PackageManager packageManager = context.getPackageManager();

      ApplicationInfo applicationInfo;
      PackageInfo packageInfo;
      try {
         String packageName = context.getPackageName();
         applicationInfo = packageManager.getApplicationInfo(packageName, 0);
         packageInfo = packageManager.getPackageInfo(packageName, 0);
      } catch (NameNotFoundException var6) {
         applicationInfo = null;
         packageInfo = null;
      }

      Map<String, Object> constants = new HashMap();
      constants.put("buildNumber", packageInfo == null ? "" : String.valueOf(packageInfo.versionCode));
      constants.put("name", applicationInfo == null ? "" : packageManager.getApplicationLabel(applicationInfo));
      constants.put("version", packageInfo == null ? "" : packageInfo.versionName);
      constants.put("LIBRE_BUILD", false);
      constants.put("GOOGLE_SERVICES_ENABLED", false);
      return constants;
   }

   public String getName() {
      return "AppInfo";
   }
}
