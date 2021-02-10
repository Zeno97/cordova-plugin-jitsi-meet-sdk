package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.UiThreadUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class ListenerUtils {
   public static Map<String, Method> mapListenerMethods(Class listener) {
      Map<String, Method> methods = new HashMap();
      Pattern onPattern = Pattern.compile("^on[A-Z]+");
      Pattern camelcasePattern = Pattern.compile("([a-z0-9]+)([A-Z0-9]+)");
      Method[] var4 = listener.getDeclaredMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (Modifier.isPublic(method.getModifiers()) && Void.TYPE.equals(method.getReturnType())) {
            String name = method.getName();
            if (onPattern.matcher(name).find()) {
               Class<?>[] parameterTypes = method.getParameterTypes();
               if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(HashMap.class)) {
                  name = camelcasePattern.matcher(name.substring(2)).replaceAll("$1_$2").toUpperCase(Locale.ROOT);
                  methods.put(name, method);
               }
            }
         }
      }

      return methods;
   }

   public static void runListenerMethod(final Object listener, final Map<String, Method> listenerMethods, final String eventName, final ReadableMap eventData) {
      if (UiThreadUtil.isOnUiThread()) {
         runListenerMethodOnUiThread(listener, listenerMethods, eventName, eventData);
      } else {
         UiThreadUtil.runOnUiThread(new Runnable() {
            public void run() {
               ListenerUtils.runListenerMethodOnUiThread(listener, listenerMethods, eventName, eventData);
            }
         });
      }

   }

   private static void runListenerMethodOnUiThread(Object listener, Map<String, Method> listenerMethods, String eventName, ReadableMap eventData) {
      UiThreadUtil.assertOnUiThread();
      Method method = (Method)listenerMethods.get(eventName);
      if (method != null) {
         try {
            method.invoke(listener, toHashMap(eventData));
         } catch (IllegalAccessException var6) {
            throw new RuntimeException(var6);
         } catch (InvocationTargetException var7) {
            throw new RuntimeException(var7);
         }
      }

   }

   private static HashMap<String, Object> toHashMap(ReadableMap readableMap) {
      HashMap<String, Object> hashMap = new HashMap();
      ReadableMapKeySetIterator i = readableMap.keySetIterator();

      while(i.hasNextKey()) {
         String key = i.nextKey();
         hashMap.put(key, readableMap.getString(key));
      }

      return hashMap;
   }
}
