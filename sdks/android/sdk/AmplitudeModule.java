package org.jitsi.meet.sdk;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.amplitude.api.Amplitude;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import org.json.JSONException;
import org.json.JSONObject;

@ReactModule(
   name = "Amplitude"
)
class AmplitudeModule extends ReactContextBaseJavaModule {
   public static final String NAME = "Amplitude";
   public static final String JITSI_PREFERENCES = "jitsi-preferences";
   public static final String AMPLITUDE_DEVICE_ID_KEY = "amplitudeDeviceId";

   public AmplitudeModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   @ReactMethod
   @SuppressLint({"HardwareIds"})
   public void init(String instanceName, String apiKey) {
      Amplitude.getInstance(instanceName).initialize(this.getCurrentActivity(), apiKey);
      SharedPreferences sharedPreferences = this.getReactApplicationContext().getSharedPreferences("jitsi-preferences", 0);
      String android_id = sharedPreferences.getString("amplitudeDeviceId", "");
      if (!TextUtils.isEmpty(android_id)) {
         Amplitude.getInstance(instanceName).setDeviceId(android_id);
      } else {
         String amplitudeId = Amplitude.getInstance(instanceName).getDeviceId();
         Editor editor = sharedPreferences.edit();
         editor.putString("jitsi-preferences", amplitudeId).apply();
      }

   }

   @ReactMethod
   public void setUserId(String instanceName, String userId) {
      Amplitude.getInstance(instanceName).setUserId(userId);
   }

   @ReactMethod
   public void setUserProperties(String instanceName, ReadableMap userProps) {
      if (userProps != null) {
         Amplitude.getInstance(instanceName).setUserProperties(new JSONObject(userProps.toHashMap()));
      }

   }

   @ReactMethod
   public void logEvent(String instanceName, String eventType, String eventPropsString) {
      try {
         JSONObject eventProps = new JSONObject(eventPropsString);
         Amplitude.getInstance(instanceName).logEvent(eventType, eventProps);
      } catch (JSONException var5) {
         JitsiMeetLogger.e(var5, "Error logging event");
      }

   }

   public String getName() {
      return "Amplitude";
   }
}
