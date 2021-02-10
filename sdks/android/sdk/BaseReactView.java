package org.jitsi.meet.sdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReadableMap;
import com.rnimmersive.RNImmersiveModule;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public abstract class BaseReactView<ListenerT> extends FrameLayout {
   protected static int BACKGROUND_COLOR = -15658735;
   static final Set<BaseReactView> views = Collections.newSetFromMap(new WeakHashMap());
   protected final String externalAPIScope;
   private ListenerT listener;
   private ReactRootView reactRootView;

   public static BaseReactView findViewByExternalAPIScope(String externalAPIScope) {
      synchronized(views) {
         Iterator var2 = views.iterator();

         BaseReactView view;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            view = (BaseReactView)var2.next();
         } while(!view.externalAPIScope.equals(externalAPIScope));

         return view;
      }
   }

   static ArrayList<BaseReactView> getViews() {
      return new ArrayList(views);
   }

   public BaseReactView(@NonNull Context context) {
      super(context);
      this.setBackgroundColor(BACKGROUND_COLOR);
      ReactInstanceManagerHolder.initReactInstanceManager((Activity)context);
      this.externalAPIScope = UUID.randomUUID().toString();
      synchronized(views) {
         views.add(this);
      }
   }

   public void createReactRootView(String appName, @Nullable Bundle props) {
      if (props == null) {
         props = new Bundle();
      }

      props.putString("externalAPIScope", this.externalAPIScope);
      if (this.reactRootView == null) {
         this.reactRootView = new ReactRootView(this.getContext());
         this.reactRootView.startReactApplication(ReactInstanceManagerHolder.getReactInstanceManager(), appName, props);
         this.reactRootView.setBackgroundColor(BACKGROUND_COLOR);
         this.addView(this.reactRootView);
      } else {
         this.reactRootView.setAppProperties(props);
      }

   }

   public void dispose() {
      if (this.reactRootView != null) {
         this.removeView(this.reactRootView);
         this.reactRootView.unmountReactApplication();
         this.reactRootView = null;
      }

   }

   public ListenerT getListener() {
      return this.listener;
   }

   protected abstract void onExternalAPIEvent(String var1, ReadableMap var2);

   protected void onExternalAPIEvent(Map<String, Method> listenerMethods, String name, ReadableMap data) {
      ListenerT listener = this.getListener();
      if (listener != null) {
         ListenerUtils.runListenerMethod(listener, listenerMethods, name, data);
      }

   }

   public void onWindowFocusChanged(boolean hasFocus) {
      super.onWindowFocusChanged(hasFocus);
      RNImmersiveModule immersive = RNImmersiveModule.getInstance();
      if (hasFocus && immersive != null) {
         immersive.emitImmersiveStateChangeEvent();
      }

   }

   public void setListener(ListenerT listener) {
      this.listener = listener;
   }
}
