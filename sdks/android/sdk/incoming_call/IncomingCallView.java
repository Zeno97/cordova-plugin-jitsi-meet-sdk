package org.jitsi.meet.sdk.incoming_call;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReadableMap;
import java.lang.reflect.Method;
import java.util.Map;
import org.jitsi.meet.sdk.BaseReactView;
import org.jitsi.meet.sdk.ListenerUtils;

public class IncomingCallView extends BaseReactView<IncomingCallViewListener> {
   private static final Map<String, Method> LISTENER_METHODS = ListenerUtils.mapListenerMethods(IncomingCallViewListener.class);

   public IncomingCallView(@NonNull Context context) {
      super(context);
   }

   protected void onExternalAPIEvent(String name, ReadableMap data) {
      this.onExternalAPIEvent(LISTENER_METHODS, name, data);
   }

   public void setIncomingCallInfo(IncomingCallInfo callInfo) {
      Bundle props = new Bundle();
      props.putString("callerAvatarURL", callInfo.getCallerAvatarURL());
      props.putString("callerName", callInfo.getCallerName());
      props.putBoolean("hasVideo", callInfo.hasVideo());
      this.createReactRootView("IncomingCallApp", props);
   }
}
