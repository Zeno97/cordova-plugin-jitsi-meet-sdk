package org.jitsi.meet.sdk;

import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import java.util.HashMap;
import java.util.Map;

class LocaleDetector extends ReactContextBaseJavaModule {
   public LocaleDetector(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   public Map<String, Object> getConstants() {
      Context context = this.getReactApplicationContext();
      HashMap<String, Object> constants = new HashMap();
      constants.put("locale", context.getResources().getConfiguration().locale.toString());
      return constants;
   }

   public String getName() {
      return "LocaleDetector";
   }
}
