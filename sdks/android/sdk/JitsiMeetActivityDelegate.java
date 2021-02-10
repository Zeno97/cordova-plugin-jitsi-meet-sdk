package org.jitsi.meet.sdk;

import android.app.Activity;
import android.content.Intent;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.PermissionListener;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class JitsiMeetActivityDelegate {
   private static PermissionListener permissionListener;
   private static Callback permissionsCallback;

   static boolean arePermissionsBeingRequested() {
      return permissionListener != null;
   }

   public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.onActivityResult(activity, requestCode, resultCode, data);
      }

   }

   public static void onBackPressed() {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.onBackPressed();
      }

   }

   public static void onHostDestroy(Activity activity) {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.onHostDestroy(activity);
      }

   }

   public static void onHostPause(Activity activity) {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
         if (reactContext != null && activity == reactContext.getCurrentActivity()) {
            reactInstanceManager.onHostPause(activity);
         }
      }

   }

   public static void onHostResume(Activity activity) {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.onHostResume(activity, new DefaultHardwareBackBtnHandlerImpl(activity));
      }

      if (permissionsCallback != null) {
         permissionsCallback.invoke(new Object[0]);
         permissionsCallback = null;
      }

   }

   public static void onNewIntent(Intent intent) {
      ReactInstanceManager reactInstanceManager = ReactInstanceManagerHolder.getReactInstanceManager();
      if (reactInstanceManager != null) {
         reactInstanceManager.onNewIntent(intent);
      }

   }

   public static void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
      permissionsCallback = new Callback() {
         public void invoke(Object... args) {
            if (JitsiMeetActivityDelegate.permissionListener != null && JitsiMeetActivityDelegate.permissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
               JitsiMeetActivityDelegate.permissionListener = null;
            }

         }
      };
   }

   public static void requestPermissions(Activity activity, String[] permissions, int requestCode, PermissionListener listener) {
      permissionListener = listener;

      try {
         activity.requestPermissions(permissions, requestCode);
      } catch (Exception var5) {
         JitsiMeetLogger.e(var5, "Error requesting permissions");
         onRequestPermissionsResult(requestCode, permissions, new int[0]);
      }

   }
}
