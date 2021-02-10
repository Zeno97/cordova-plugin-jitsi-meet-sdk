package org.jitsi.meet.sdk;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.squareup.duktape.Duktape;

@ReactModule(
   name = "JavaScriptSandbox"
)
class JavaScriptSandboxModule extends ReactContextBaseJavaModule {
   public static final String NAME = "JavaScriptSandbox";

   public JavaScriptSandboxModule(ReactApplicationContext reactContext) {
      super(reactContext);
   }

   @ReactMethod
   public void evaluate(String code, Promise promise) {
      Duktape vm = Duktape.create();

      try {
         Object res = vm.evaluate(code);
         promise.resolve(res.toString());
      } catch (Throwable var8) {
         promise.reject(var8);
      } finally {
         vm.close();
      }

   }

   public String getName() {
      return "JavaScriptSandbox";
   }
}
