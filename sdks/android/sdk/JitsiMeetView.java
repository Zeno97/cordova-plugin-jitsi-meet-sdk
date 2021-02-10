package org.jitsi.meet.sdk;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableMap;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class JitsiMeetView extends BaseReactView<JitsiMeetViewListener> implements OngoingConferenceTracker.OngoingConferenceListener {
   private static final Map<String, Method> LISTENER_METHODS = ListenerUtils.mapListenerMethods(JitsiMeetViewListener.class);
   private volatile String url;

   private static Bundle mergeProps(@Nullable Bundle a, @Nullable Bundle b) {
      Bundle result = new Bundle();
      if (a == null) {
         if (b != null) {
            result.putAll(b);
         }

         return result;
      } else if (b == null) {
         result.putAll(a);
         return result;
      } else {
         result.putAll(a);
         Iterator var3 = b.keySet().iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            Object bValue = b.get(key);
            Object aValue = a.get(key);
            String valueType = bValue.getClass().getSimpleName();
            if (valueType.contentEquals("Boolean")) {
               result.putBoolean(key, (Boolean)bValue);
            } else if (valueType.contentEquals("String")) {
               result.putString(key, (String)bValue);
            } else {
               if (!valueType.contentEquals("Bundle")) {
                  throw new RuntimeException("Unsupported type: " + valueType);
               }

               result.putBundle(key, mergeProps((Bundle)aValue, (Bundle)bValue));
            }
         }

         return result;
      }
   }

   public JitsiMeetView(@NonNull Context context) {
      super(context);
      if (!(context instanceof JitsiMeetActivityInterface)) {
         throw new RuntimeException("Enclosing Activity must implement JitsiMeetActivityInterface");
      } else {
         OngoingConferenceTracker.getInstance().addListener(this);
      }
   }

   public void dispose() {
      OngoingConferenceTracker.getInstance().removeListener(this);
      super.dispose();
   }

   public void enterPictureInPicture() {
      PictureInPictureModule pipModule = (PictureInPictureModule)ReactInstanceManagerHolder.getNativeModule(PictureInPictureModule.class);
      if (pipModule != null && pipModule.isPictureInPictureSupported() && !JitsiMeetActivityDelegate.arePermissionsBeingRequested() && this.url != null) {
         try {
            pipModule.enterPictureInPicture();
         } catch (RuntimeException var3) {
            JitsiMeetLogger.e(var3, "Failed to enter PiP mode");
         }
      }

   }

   public void join(@Nullable JitsiMeetConferenceOptions options) {
      this.setProps(options != null ? options.asProps() : new Bundle());
   }

   public void leave() {
      this.setProps(new Bundle());
   }

   private void setProps(@NonNull Bundle newProps) {
      Bundle props = mergeProps(JitsiMeet.getDefaultProps(), newProps);
      props.putLong("timestamp", System.currentTimeMillis());
      this.createReactRootView("App", props);
   }

   public void onCurrentConferenceChanged(String conferenceUrl) {
      this.url = conferenceUrl;
   }

   protected void onExternalAPIEvent(String name, ReadableMap data) {
      this.onExternalAPIEvent(LISTENER_METHODS, name, data);
   }

   protected void onDetachedFromWindow() {
      this.dispose();
      super.onDetachedFromWindow();
   }
}
