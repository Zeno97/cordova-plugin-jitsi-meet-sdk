package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReadableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

class OngoingConferenceTracker {
   private static final OngoingConferenceTracker instance = new OngoingConferenceTracker();
   private static final String CONFERENCE_WILL_JOIN = "CONFERENCE_WILL_JOIN";
   private static final String CONFERENCE_TERMINATED = "CONFERENCE_TERMINATED";
   private final Collection<OngoingConferenceListener> listeners = Collections.synchronizedSet(new HashSet());
   private String currentConference;

   public OngoingConferenceTracker() {
   }

   public static OngoingConferenceTracker getInstance() {
      return instance;
   }

   synchronized String getCurrentConference() {
      return this.currentConference;
   }

   synchronized void onExternalAPIEvent(String name, ReadableMap data) {
      if (data.hasKey("url")) {
         String url = data.getString("url");
         if (url != null) {
            byte var5 = -1;
            switch(name.hashCode()) {
            case -940468954:
               if (name.equals("CONFERENCE_TERMINATED")) {
                  var5 = 1;
               }
               break;
            case 895709716:
               if (name.equals("CONFERENCE_WILL_JOIN")) {
                  var5 = 0;
               }
            }

            switch(var5) {
            case 0:
               this.currentConference = url;
               this.updateListeners();
               break;
            case 1:
               if (url.equals(this.currentConference)) {
                  this.currentConference = null;
                  this.updateListeners();
               }
            }

         }
      }
   }

   void addListener(OngoingConferenceListener listener) {
      this.listeners.add(listener);
   }

   void removeListener(OngoingConferenceListener listener) {
      this.listeners.remove(listener);
   }

   private void updateListeners() {
      synchronized(this.listeners) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            OngoingConferenceListener listener = (OngoingConferenceListener)var2.next();
            listener.onCurrentConferenceChanged(this.currentConference);
         }

      }
   }

   public interface OngoingConferenceListener {
      void onCurrentConferenceChanged(String var1);
   }
}
