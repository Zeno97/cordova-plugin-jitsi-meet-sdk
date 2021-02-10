package org.jitsi.meet.sdk.net;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.net.UnknownHostException;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "NAT64AddrInfo"
)
public class NAT64AddrInfoModule extends ReactContextBaseJavaModule {
   public static final String NAME = "NAT64AddrInfo";
   private static final String HOST = "ipv4only.arpa";
   private static final long INFO_LIFETIME = 60000L;
   private static final String TAG = "NAT64AddrInfo";
   private NAT64AddrInfo info;
   private long infoTimestamp;

   public NAT64AddrInfoModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   @ReactMethod
   public void getIPv6Address(String ipv4Address, Promise promise) {
      if (System.currentTimeMillis() - this.infoTimestamp > 60000L) {
         this.info = null;
      }

      String result;
      if (this.info == null) {
         result = "ipv4only.arpa";

         try {
            this.info = NAT64AddrInfo.discover(result);
         } catch (UnknownHostException var6) {
            JitsiMeetLogger.e(var6, "NAT64AddrInfo NAT64AddrInfo.discover: " + result);
         }

         this.infoTimestamp = System.currentTimeMillis();
      }

      try {
         result = this.info == null ? null : this.info.getIPv6Address(ipv4Address);
      } catch (IllegalArgumentException var5) {
         JitsiMeetLogger.e(var5, "NAT64AddrInfo Failed to get IPv6 address for: " + ipv4Address);
         result = null;
      }

      promise.resolve(result);
   }

   public String getName() {
      return "NAT64AddrInfo";
   }
}
