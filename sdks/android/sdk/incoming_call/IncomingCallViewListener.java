package org.jitsi.meet.sdk.incoming_call;

import java.util.Map;

public interface IncomingCallViewListener {
   void onIncomingCallAnswered(Map<String, Object> var1);

   void onIncomingCallDeclined(Map<String, Object> var1);
}
