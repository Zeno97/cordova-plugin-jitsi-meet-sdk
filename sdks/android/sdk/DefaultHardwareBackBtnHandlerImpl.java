package org.jitsi.meet.sdk;

import android.app.Activity;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

class DefaultHardwareBackBtnHandlerImpl implements DefaultHardwareBackBtnHandler {
   private final Activity activity;

   public DefaultHardwareBackBtnHandlerImpl(Activity activity) {
      this.activity = activity;
   }

   public void invokeDefaultOnBackPressed() {
      this.activity.finish();
   }
}
