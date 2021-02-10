package org.jitsi.meet.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceAllocation;
import com.dropbox.core.v2.users.SpaceUsage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;

@ReactModule(
   name = "Dropbox"
)
class DropboxModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
   public static final String NAME = "Dropbox";
   private String appKey;
   private String clientId;
   private final boolean isEnabled;
   private Promise promise;

   public DropboxModule(ReactApplicationContext reactContext) {
      super(reactContext);
      String pkg = reactContext.getApplicationContext().getPackageName();
      int resId = reactContext.getResources().getIdentifier("dropbox_app_key", "string", pkg);
      this.appKey = reactContext.getString(resId);
      this.isEnabled = !TextUtils.isEmpty(this.appKey);
      this.clientId = this.generateClientId();
      reactContext.addLifecycleEventListener(this);
   }

   @ReactMethod
   public void authorize(Promise promise) {
      if (this.isEnabled) {
         Auth.startOAuth2Authentication(this.getCurrentActivity(), this.appKey);
         this.promise = promise;
      } else {
         promise.reject(new Exception("Dropbox integration isn't configured."));
      }

   }

   private String generateClientId() {
      Context context = this.getReactApplicationContext();
      PackageManager packageManager = context.getPackageManager();
      ApplicationInfo applicationInfo = null;
      PackageInfo packageInfo = null;

      String applicationLabel;
      try {
         applicationLabel = context.getPackageName();
         applicationInfo = packageManager.getApplicationInfo(applicationLabel, 0);
         packageInfo = packageManager.getPackageInfo(applicationLabel, 0);
      } catch (NameNotFoundException var7) {
      }

      applicationLabel = applicationInfo == null ? "JitsiMeet" : packageManager.getApplicationLabel(applicationInfo).toString().replaceAll("\\s", "");
      String version = packageInfo == null ? "dev" : packageInfo.versionName;
      return applicationLabel + "/" + version;
   }

   public Map<String, Object> getConstants() {
      Map<String, Object> constants = new HashMap();
      constants.put("ENABLED", this.isEnabled);
      return constants;
   }

   @ReactMethod
   public void getDisplayName(String token, Promise promise) {
      DbxRequestConfig config = DbxRequestConfig.newBuilder(this.clientId).build();
      DbxClientV2 client = new DbxClientV2(config, token);

      try {
         FullAccount account = client.users().getCurrentAccount();
         promise.resolve(account.getName().getDisplayName());
      } catch (DbxException var6) {
         promise.reject(var6);
      }

   }

   public String getName() {
      return "Dropbox";
   }

   @ReactMethod
   public void getSpaceUsage(String token, Promise promise) {
      DbxRequestConfig config = DbxRequestConfig.newBuilder(this.clientId).build();
      DbxClientV2 client = new DbxClientV2(config, token);

      try {
         SpaceUsage spaceUsage = client.users().getSpaceUsage();
         WritableMap map = Arguments.createMap();
         map.putString("used", String.valueOf(spaceUsage.getUsed()));
         SpaceAllocation allocation = spaceUsage.getAllocation();
         long allocated = 0L;
         if (allocation.isIndividual()) {
            allocated += allocation.getIndividualValue().getAllocated();
         }

         if (allocation.isTeam()) {
            allocated += allocation.getTeamValue().getAllocated();
         }

         map.putString("allocated", String.valueOf(allocated));
         promise.resolve(map);
      } catch (DbxException var10) {
         promise.reject(var10);
      }

   }

   public void onHostDestroy() {
   }

   public void onHostPause() {
   }

   public void onHostResume() {
      String token = Auth.getOAuth2Token();
      if (token != null && this.promise != null) {
         this.promise.resolve(token);
         this.promise = null;
      }

   }
}
