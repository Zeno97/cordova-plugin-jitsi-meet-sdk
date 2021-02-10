package org.jitsi.meet.sdk;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import org.json.JSONArray;
import org.json.JSONObject;

@ReactModule(
   name = "WiFiStats"
)
class WiFiStatsModule extends ReactContextBaseJavaModule {
   public static final String NAME = "WiFiStats";
   static final String TAG = "WiFiStats";
   public static final int SIGNAL_LEVEL_SCALE = 101;
   private static final ExecutorService executor = Executors.newSingleThreadExecutor();

   public WiFiStatsModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public String getName() {
      return "WiFiStats";
   }

   public static InetAddress toInetAddress(int value) throws UnknownHostException {
      return InetAddress.getByAddress(new byte[]{(byte)value, (byte)(value >> 8), (byte)(value >> 16), (byte)(value >> 24)});
   }

   @ReactMethod
   public void getWiFiStats(final Promise promise) {
      Runnable r = new Runnable() {
         public void run() {
            try {
               Context context = WiFiStatsModule.this.getReactApplicationContext().getApplicationContext();
               WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
               if (!wifiManager.isWifiEnabled()) {
                  promise.reject(new Exception("Wifi not enabled"));
                  return;
               }

               WifiInfo wifiInfo = wifiManager.getConnectionInfo();
               if (wifiInfo.getNetworkId() == -1) {
                  promise.reject(new Exception("Wifi not connected"));
                  return;
               }

               int rssi = wifiInfo.getRssi();
               int signalLevel = WifiManager.calculateSignalLevel(rssi, 101);
               JSONObject result = new JSONObject();
               result.put("rssi", rssi).put("signal", signalLevel).put("timestamp", System.currentTimeMillis());
               JSONArray addresses = new JSONArray();
               InetAddress wifiAddress = WiFiStatsModule.toInetAddress(wifiInfo.getIpAddress());

               try {
                  Enumeration e = NetworkInterface.getNetworkInterfaces();

                  label57:
                  while(true) {
                     NetworkInterface networkInterface;
                     boolean found;
                     Enumeration as;
                     InetAddress a;
                     do {
                        if (!e.hasMoreElements()) {
                           break label57;
                        }

                        networkInterface = (NetworkInterface)e.nextElement();
                        found = false;
                        as = networkInterface.getInetAddresses();

                        while(as.hasMoreElements()) {
                           a = (InetAddress)as.nextElement();
                           if (a.equals(wifiAddress)) {
                              found = true;
                              break;
                           }
                        }
                     } while(!found);

                     as = networkInterface.getInetAddresses();

                     while(as.hasMoreElements()) {
                        a = (InetAddress)as.nextElement();
                        if (!a.isLinkLocalAddress()) {
                           addresses.put(a.getHostAddress());
                        }
                     }
                  }
               } catch (SocketException var14) {
                  JitsiMeetLogger.e(var14, "WiFiStats Unable to NetworkInterface.getNetworkInterfaces()");
               }

               result.put("addresses", addresses);
               promise.resolve(result.toString());
               JitsiMeetLogger.d("WiFiStats WiFi stats: " + result.toString());
            } catch (Throwable var15) {
               JitsiMeetLogger.e(var15, "WiFiStats Failed to obtain wifi stats");
               promise.reject(new Exception("Failed to obtain wifi stats"));
            }

         }
      };
      executor.execute(r);
   }
}
