package org.jitsi.meet.sdk;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import androidx.annotation.RequiresApi;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.util.Iterator;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "ConnectionService"
)
@RequiresApi(
   api = 26
)
class RNConnectionService extends ReactContextBaseJavaModule {
   public static final String NAME = "ConnectionService";
   private static final String TAG = "JitsiConnectionService";
   private CallAudioStateListener callAudioStateListener;

   @RequiresApi(
      api = 26
   )
   static void setAudioRoute(int audioRoute) {
      Iterator var1 = ConnectionService.getConnections().iterator();

      while(var1.hasNext()) {
         ConnectionService.ConnectionImpl c = (ConnectionService.ConnectionImpl)var1.next();
         c.setAudioRoute(audioRoute);
      }

   }

   RNConnectionService(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   @ReactMethod
   @SuppressLint({"MissingPermission"})
   public void startCall(String callUUID, String handle, boolean hasVideo, Promise promise) {
      JitsiMeetLogger.d("%s startCall UUID=%s, h=%s, v=%s", "JitsiConnectionService", callUUID, handle, hasVideo);
      ReactApplicationContext ctx = this.getReactApplicationContext();
      Uri address = Uri.fromParts("sip", handle, (String)null);

      PhoneAccountHandle accountHandle;
      try {
         accountHandle = ConnectionService.registerPhoneAccount(this.getReactApplicationContext(), address, callUUID);
      } catch (Throwable var13) {
         JitsiMeetLogger.e(var13, "JitsiConnectionService error in startCall");
         promise.reject(var13);
         return;
      }

      Bundle extras = new Bundle();
      extras.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", accountHandle);
      extras.putInt("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", hasVideo ? 3 : 0);
      ConnectionService.registerStartCallPromise(callUUID, promise);
      TelecomManager tm = null;

      try {
         tm = (TelecomManager)ctx.getSystemService("telecom");
         tm.placeCall(address, extras);
      } catch (Throwable var14) {
         JitsiMeetLogger.e(var14, "JitsiConnectionService error in startCall");
         if (tm != null) {
            try {
               tm.unregisterPhoneAccount(accountHandle);
            } catch (Throwable var12) {
            }
         }

         ConnectionService.unregisterStartCallPromise(callUUID);
         promise.reject(var14);
      }

   }

   @ReactMethod
   public void reportCallFailed(String callUUID) {
      JitsiMeetLogger.d("JitsiConnectionService reportCallFailed " + callUUID);
      ConnectionService.setConnectionDisconnected(callUUID, new DisconnectCause(1));
   }

   @ReactMethod
   public void endCall(String callUUID) {
      JitsiMeetLogger.d("JitsiConnectionService endCall " + callUUID);
      ConnectionService.setConnectionDisconnected(callUUID, new DisconnectCause(2));
   }

   @ReactMethod
   public void reportConnectedOutgoingCall(String callUUID, Promise promise) {
      JitsiMeetLogger.d("JitsiConnectionService reportConnectedOutgoingCall " + callUUID);
      if (ConnectionService.setConnectionActive(callUUID)) {
         promise.resolve((Object)null);
      } else {
         promise.reject("CONNECTION_NOT_FOUND_ERROR", "Connection wasn't found.");
      }

   }

   public String getName() {
      return "ConnectionService";
   }

   @ReactMethod
   public void updateCall(String callUUID, ReadableMap callState) {
      ConnectionService.updateCall(callUUID, callState);
   }

   public CallAudioStateListener getCallAudioStateListener() {
      return this.callAudioStateListener;
   }

   public void setCallAudioStateListener(CallAudioStateListener callAudioStateListener) {
      this.callAudioStateListener = callAudioStateListener;
   }

   void onCallAudioStateChange(CallAudioState callAudioState) {
      if (this.callAudioStateListener != null) {
         this.callAudioStateListener.onCallAudioStateChange(callAudioState);
      }

   }

   interface CallAudioStateListener {
      void onCallAudioStateChange(CallAudioState var1);
   }
}
