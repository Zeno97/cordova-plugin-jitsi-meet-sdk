package org.jitsi.meet.sdk;

import android.media.AudioManager;
import android.os.Build.VERSION;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@ReactModule(
   name = "AudioMode"
)
class AudioModeModule extends ReactContextBaseJavaModule {
   public static final String NAME = "AudioMode";
   static final int DEFAULT = 0;
   static final int AUDIO_CALL = 1;
   static final int VIDEO_CALL = 2;
   static final String TAG = "AudioMode";
   private static final boolean supportsConnectionService;
   private static boolean useConnectionService_;
   private AudioManager audioManager;
   private AudioDeviceHandlerInterface audioDeviceHandler;
   private static final ExecutorService executor;
   private int mode = -1;
   static final String DEVICE_BLUETOOTH = "BLUETOOTH";
   static final String DEVICE_EARPIECE = "EARPIECE";
   static final String DEVICE_HEADPHONES = "HEADPHONES";
   static final String DEVICE_SPEAKER = "SPEAKER";
   private static final String DEVICE_CHANGE_EVENT = "org.jitsi.meet:features/audio-mode#devices-update";
   private Set<String> availableDevices = new HashSet();
   private String selectedDevice;
   private String userSelectedDevice;

   static boolean useConnectionService() {
      return supportsConnectionService && useConnectionService_;
   }

   public AudioModeModule(ReactApplicationContext reactContext) {
      super(reactContext);
      this.audioManager = (AudioManager)reactContext.getSystemService("audio");
   }

   public Map<String, Object> getConstants() {
      Map<String, Object> constants = new HashMap();
      constants.put("DEVICE_CHANGE_EVENT", "org.jitsi.meet:features/audio-mode#devices-update");
      constants.put("AUDIO_CALL", 1);
      constants.put("DEFAULT", 0);
      constants.put("VIDEO_CALL", 2);
      return constants;
   }

   private void notifyDevicesChanged() {
      this.runInAudioThread(new Runnable() {
         public void run() {
            WritableArray data = Arguments.createArray();
            boolean hasHeadphones = AudioModeModule.this.availableDevices.contains("HEADPHONES");
            Iterator var3 = AudioModeModule.this.availableDevices.iterator();

            while(true) {
               String device;
               do {
                  if (!var3.hasNext()) {
                     ReactInstanceManagerHolder.emitEvent("org.jitsi.meet:features/audio-mode#devices-update", data);
                     JitsiMeetLogger.i("AudioMode Updating audio device list");
                     return;
                  }

                  device = (String)var3.next();
               } while(hasHeadphones && device.equals("EARPIECE"));

               WritableMap deviceInfo = Arguments.createMap();
               deviceInfo.putString("type", device);
               deviceInfo.putBoolean("selected", device.equals(AudioModeModule.this.selectedDevice));
               data.pushMap(deviceInfo);
            }
         }
      });
   }

   public String getName() {
      return "AudioMode";
   }

   public void initialize() {
      this.runInAudioThread(new Runnable() {
         public void run() {
            AudioModeModule.this.setAudioDeviceHandler();
         }
      });
   }

   private void setAudioDeviceHandler() {
      if (this.audioDeviceHandler != null) {
         this.audioDeviceHandler.stop();
      }

      if (useConnectionService()) {
         this.audioDeviceHandler = new AudioDeviceHandlerConnectionService(this.audioManager);
      } else {
         this.audioDeviceHandler = new AudioDeviceHandlerGeneric(this.audioManager);
      }

      this.audioDeviceHandler.start(this);
   }

   void runInAudioThread(Runnable runnable) {
      executor.execute(runnable);
   }

   @ReactMethod
   public void setAudioDevice(final String device) {
      this.runInAudioThread(new Runnable() {
         public void run() {
            if (!AudioModeModule.this.availableDevices.contains(device)) {
               JitsiMeetLogger.w("AudioMode Audio device not available: " + device);
               AudioModeModule.this.userSelectedDevice = null;
            } else {
               if (AudioModeModule.this.mode != -1) {
                  JitsiMeetLogger.i("AudioMode User selected device set to: " + device);
                  AudioModeModule.this.userSelectedDevice = device;
                  AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode);
               }

            }
         }
      });
   }

   @ReactMethod
   public void setMode(final int mode, final Promise promise) {
      if (mode != 0 && mode != 1 && mode != 2) {
         promise.reject("setMode", "Invalid audio mode " + mode);
      } else {
         this.runInAudioThread(new Runnable() {
            public void run() {
               boolean success;
               try {
                  success = AudioModeModule.this.updateAudioRoute(mode);
               } catch (Throwable var3) {
                  success = false;
                  JitsiMeetLogger.e(var3, "AudioMode Failed to update audio route for mode: " + mode);
               }

               if (success) {
                  AudioModeModule.this.mode = mode;
                  promise.resolve((Object)null);
               } else {
                  promise.reject("setMode", "Failed to set audio mode to " + mode);
               }

            }
         });
      }
   }

   @ReactMethod
   public void setUseConnectionService(final boolean use) {
      this.runInAudioThread(new Runnable() {
         public void run() {
            AudioModeModule.useConnectionService_ = use;
            AudioModeModule.this.setAudioDeviceHandler();
         }
      });
   }

   private boolean updateAudioRoute(int mode) {
      JitsiMeetLogger.i("AudioMode Update audio route for mode: " + mode);
      if (!this.audioDeviceHandler.setMode(mode)) {
         return false;
      } else if (mode == 0) {
         this.selectedDevice = null;
         this.userSelectedDevice = null;
         this.notifyDevicesChanged();
         return true;
      } else {
         boolean bluetoothAvailable = this.availableDevices.contains("BLUETOOTH");
         boolean headsetAvailable = this.availableDevices.contains("HEADPHONES");
         String audioDevice;
         if (bluetoothAvailable) {
            audioDevice = "BLUETOOTH";
         } else if (headsetAvailable) {
            audioDevice = "HEADPHONES";
         } else {
            audioDevice = "SPEAKER";
         }

         if (this.userSelectedDevice != null && this.availableDevices.contains(this.userSelectedDevice)) {
            audioDevice = this.userSelectedDevice;
         }

         if (this.selectedDevice != null && this.selectedDevice.equals(audioDevice)) {
            return true;
         } else {
            this.selectedDevice = audioDevice;
            JitsiMeetLogger.i("AudioMode Selected audio device: " + audioDevice);
            this.audioDeviceHandler.setAudioRoute(audioDevice);
            this.notifyDevicesChanged();
            return true;
         }
      }
   }

   String getSelectedDevice() {
      return this.selectedDevice;
   }

   void resetSelectedDevice() {
      this.selectedDevice = null;
      this.userSelectedDevice = null;
   }

   void addDevice(String device) {
      this.availableDevices.add(device);
      this.resetSelectedDevice();
   }

   void removeDevice(String device) {
      this.availableDevices.remove(device);
      this.resetSelectedDevice();
   }

   void replaceDevices(Set<String> devices) {
      this.availableDevices = devices;
      this.resetSelectedDevice();
   }

   void updateAudioRoute() {
      if (this.mode != -1) {
         this.updateAudioRoute(this.mode);
      }

   }

   static {
      supportsConnectionService = VERSION.SDK_INT >= 26;
      useConnectionService_ = supportsConnectionService;
      executor = Executors.newSingleThreadExecutor();
   }

   interface AudioDeviceHandlerInterface {
      void start(AudioModeModule var1);

      void stop();

      void setAudioRoute(String var1);

      boolean setMode(int var1);
   }
}
