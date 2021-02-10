package org.jitsi.meet.sdk;

import android.media.AudioManager;
import android.telecom.CallAudioState;
import androidx.annotation.RequiresApi;
import java.util.HashSet;
import java.util.Set;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

@RequiresApi(26)
class AudioDeviceHandlerConnectionService implements AudioModeModule.AudioDeviceHandlerInterface, RNConnectionService.CallAudioStateListener {
   private static final String TAG = AudioDeviceHandlerConnectionService.class.getSimpleName();
   private AudioManager audioManager;
   private AudioModeModule module;
   private int supportedRouteMask = -1;

   private static int audioDeviceToRouteInt(String audioDevice) {
      if (audioDevice == null) {
         return 8;
      } else {
         byte var2 = -1;
         switch(audioDevice.hashCode()) {
         case -1525857627:
            if (audioDevice.equals("HEADPHONES")) {
               var2 = 2;
            }
            break;
         case -1290540065:
            if (audioDevice.equals("SPEAKER")) {
               var2 = 3;
            }
            break;
         case -23258792:
            if (audioDevice.equals("EARPIECE")) {
               var2 = 1;
            }
            break;
         case 460509838:
            if (audioDevice.equals("BLUETOOTH")) {
               var2 = 0;
            }
         }

         switch(var2) {
         case 0:
            return 2;
         case 1:
            return 1;
         case 2:
            return 4;
         case 3:
            return 8;
         default:
            JitsiMeetLogger.e(TAG + " Unsupported device name: " + audioDevice);
            return 8;
         }
      }
   }

   private static Set<String> routesToDeviceNames(int supportedRouteMask) {
      Set<String> devices = new HashSet();
      if ((supportedRouteMask & 1) == 1) {
         devices.add("EARPIECE");
      }

      if ((supportedRouteMask & 2) == 2) {
         devices.add("BLUETOOTH");
      }

      if ((supportedRouteMask & 8) == 8) {
         devices.add("SPEAKER");
      }

      if ((supportedRouteMask & 4) == 4) {
         devices.add("HEADPHONES");
      }

      return devices;
   }

   public AudioDeviceHandlerConnectionService(AudioManager audioManager) {
      this.audioManager = audioManager;
   }

   public void onCallAudioStateChange(final CallAudioState state) {
      this.module.runInAudioThread(new Runnable() {
         public void run() {
            boolean audioRouteChanged = AudioDeviceHandlerConnectionService.audioDeviceToRouteInt(AudioDeviceHandlerConnectionService.this.module.getSelectedDevice()) != state.getRoute();
            int newSupportedRoutes = state.getSupportedRouteMask();
            boolean audioDevicesChanged = AudioDeviceHandlerConnectionService.this.supportedRouteMask != newSupportedRoutes;
            if (audioDevicesChanged) {
               AudioDeviceHandlerConnectionService.this.supportedRouteMask = newSupportedRoutes;
               Set<String> devices = AudioDeviceHandlerConnectionService.routesToDeviceNames(AudioDeviceHandlerConnectionService.this.supportedRouteMask);
               AudioDeviceHandlerConnectionService.this.module.replaceDevices(devices);
               JitsiMeetLogger.i(AudioDeviceHandlerConnectionService.TAG + " Available audio devices: " + devices.toString());
            }

            if (audioRouteChanged || audioDevicesChanged) {
               AudioDeviceHandlerConnectionService.this.module.resetSelectedDevice();
               AudioDeviceHandlerConnectionService.this.module.updateAudioRoute();
            }

         }
      });
   }

   public void start(AudioModeModule audioModeModule) {
      JitsiMeetLogger.i("Using " + TAG + " as the audio device handler");
      this.module = audioModeModule;
      RNConnectionService rcs = (RNConnectionService)ReactInstanceManagerHolder.getNativeModule(RNConnectionService.class);
      if (rcs != null) {
         rcs.setCallAudioStateListener(this);
      } else {
         JitsiMeetLogger.w(TAG + " Couldn't set call audio state listener, module is null");
      }

   }

   public void stop() {
      RNConnectionService rcs = (RNConnectionService)ReactInstanceManagerHolder.getNativeModule(RNConnectionService.class);
      if (rcs != null) {
         rcs.setCallAudioStateListener((RNConnectionService.CallAudioStateListener)null);
      } else {
         JitsiMeetLogger.w(TAG + " Couldn't set call audio state listener, module is null");
      }

   }

   public void setAudioRoute(String audioDevice) {
      int newAudioRoute = audioDeviceToRouteInt(audioDevice);
      RNConnectionService.setAudioRoute(newAudioRoute);
   }

   public boolean setMode(int mode) {
      if (mode != 0) {
         try {
            this.audioManager.setMicrophoneMute(false);
         } catch (Throwable var3) {
            JitsiMeetLogger.w(var3, TAG + " Failed to unmute the microphone");
         }
      }

      return true;
   }
}
