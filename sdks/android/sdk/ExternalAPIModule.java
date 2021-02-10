package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "ExternalAPI"
)
class ExternalAPIModule extends ReactContextBaseJavaModule {
   public static final String NAME = "ExternalAPI";
   private static final String TAG = "ExternalAPI";

   public ExternalAPIModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public String getName() {
      return "ExternalAPI";
   }

   @ReactMethod
   public void sendEvent(String name, ReadableMap data, String scope) {
      OngoingConferenceTracker.getInstance().onExternalAPIEvent(name, data);
      BaseReactView view = BaseReactView.findViewByExternalAPIScope(scope);
      if (view != null) {
         JitsiMeetLogger.d("ExternalAPI Sending event: " + name + " with data: " + data);

         try {
            view.onExternalAPIEvent(name, data);
         } catch (Exception var6) {
            JitsiMeetLogger.e(var6, "ExternalAPI onExternalAPIEvent: error sending event");
         }
      }

   }
}
