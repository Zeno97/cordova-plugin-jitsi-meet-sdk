package org.jitsi.meet.sdk;

import android.app.Activity;
import androidx.annotation.Nullable;
import com.BV.LinearGradient.LinearGradientPackage;
import com.calendarevents.CalendarEventsPackage;
import com.corbt.keepawake.KCKeepAwakePackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.devsupport.DevInternalSettings;
import com.facebook.react.jscexecutor.JSCExecutorFactory;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.soloader.SoLoader;
import com.horcrux.svg.SvgPackage;
import com.kevinresol.react_native_default_preference.RNDefaultPreferencePackage;
import com.ocetnik.timer.BackgroundTimerPackage;
import com.oney.WebRTCModule.RTCVideoViewManager;
import com.oney.WebRTCModule.WebRTCModule;
import com.oney.WebRTCModule.WebRTCModule.Options;
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
import com.reactnativecommunity.netinfo.NetInfoPackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
import com.rnimmersive.RNImmersivePackage;
import com.zmxv.RNSound.RNSoundPackage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.devio.rn.splashscreen.SplashScreenModule;
import org.jitsi.meet.sdk.net.NAT64AddrInfoModule;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

class ReactInstanceManagerHolder {
   private static ReactInstanceManager reactInstanceManager;

   private static List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      List<NativeModule> nativeModules = new ArrayList(Arrays.asList(new AndroidSettingsModule(reactContext), new AppInfoModule(reactContext), new AudioModeModule(reactContext), new DropboxModule(reactContext), new ExternalAPIModule(reactContext), new JavaScriptSandboxModule(reactContext), new LocaleDetector(reactContext), new LogBridgeModule(reactContext), new SplashScreenModule(reactContext), new PictureInPictureModule(reactContext), new ProximityModule(reactContext), new WiFiStatsModule(reactContext), new NAT64AddrInfoModule(reactContext)));
      if (AudioModeModule.useConnectionService()) {
         nativeModules.add(new RNConnectionService(reactContext));
      }

      Options options = new Options();
      AudioDeviceModule adm = JavaAudioDeviceModule.builder(reactContext).createAudioDeviceModule();
      options.setAudioDeviceModule(adm);
      options.setVideoDecoderFactory(new SoftwareVideoDecoderFactory());
      options.setVideoEncoderFactory(new SoftwareVideoEncoderFactory());
      nativeModules.add(new WebRTCModule(reactContext, options));

      try {
         Class<?> amplitudeModuleClass = Class.forName("org.jitsi.meet.sdk.AmplitudeModule");
         Constructor constructor = amplitudeModuleClass.getConstructor(ReactApplicationContext.class);
         nativeModules.add((NativeModule)constructor.newInstance(reactContext));
      } catch (Exception var6) {
      }

      return nativeModules;
   }

   private static List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Arrays.asList(new RTCVideoViewManager());
   }

   static void emitEvent(String eventName, @Nullable Object data) {
      ReactInstanceManager reactInstanceManager = getReactInstanceManager();
      if (reactInstanceManager != null) {
         ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
         if (reactContext != null) {
            ((RCTDeviceEventEmitter)reactContext.getJSModule(RCTDeviceEventEmitter.class)).emit(eventName, data);
         }
      }

   }

   static <T extends NativeModule> T getNativeModule(Class<T> nativeModuleClass) {
      ReactContext reactContext = reactInstanceManager != null ? reactInstanceManager.getCurrentReactContext() : null;
      return reactContext != null ? reactContext.getNativeModule(nativeModuleClass) : null;
   }

   static Activity getCurrentActivity() {
      ReactContext reactContext = reactInstanceManager != null ? reactInstanceManager.getCurrentReactContext() : null;
      return reactContext != null ? reactContext.getCurrentActivity() : null;
   }

   static ReactInstanceManager getReactInstanceManager() {
      return reactInstanceManager;
   }

   static void initReactInstanceManager(Activity activity) {
      if (reactInstanceManager == null) {
         SoLoader.init(activity, false);
         ArrayList packages = new ArrayList(Arrays.asList(new LinearGradientPackage(), new CalendarEventsPackage(), new KCKeepAwakePackage(), new MainReactPackage(), new SvgPackage(), new RNDefaultPreferencePackage(), new BackgroundTimerPackage(), new AsyncStoragePackage(), new NetInfoPackage(), new RNCWebViewPackage(), new RNImmersivePackage(), new RNSoundPackage(), new ReactPackageAdapter() {
            public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
               return ReactInstanceManagerHolder.createNativeModules(reactContext);
            }

            public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
               return ReactInstanceManagerHolder.createViewManagers(reactContext);
            }
         }));

         try {
            Class<?> googlePackageClass = Class.forName("co.apptailor.googlesignin.RNGoogleSigninPackage");
            Constructor constructor = googlePackageClass.getConstructor();
            packages.add((ReactPackage)constructor.newInstance());
         } catch (Exception var4) {
         }

         JSCExecutorFactory jsFactory = new JSCExecutorFactory("", "");
         reactInstanceManager = ReactInstanceManager.builder().setApplication(activity.getApplication()).setCurrentActivity(activity).setBundleAssetName("index.android.bundle").setJSMainModulePath("index.android").setJavaScriptExecutorFactory(jsFactory).addPackages(packages).setUseDeveloperSupport(false).setInitialLifecycleState(LifecycleState.RESUMED).build();
         DevInternalSettings devSettings = (DevInternalSettings)reactInstanceManager.getDevSupportManager().getDevSettings();
         if (devSettings != null) {
            devSettings.setBundleDeltasEnabled(false);
         }

         JitsiMeetUncaughtExceptionHandler.register();
      }
   }
}
