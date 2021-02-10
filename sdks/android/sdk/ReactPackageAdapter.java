package org.jitsi.meet.sdk;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Collections;
import java.util.List;

class ReactPackageAdapter implements ReactPackage {
   public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      return Collections.emptyList();
   }

   public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Collections.emptyList();
   }
}
