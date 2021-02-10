package org.jitsi.meet.sdk;

import java.util.Map;

public interface JitsiMeetViewListener {
   void onConferenceJoined(Map<String, Object> var1);

   void onConferenceTerminated(Map<String, Object> var1);

   void onConferenceWillJoin(Map<String, Object> var1);
}
