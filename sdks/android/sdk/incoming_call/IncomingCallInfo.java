package org.jitsi.meet.sdk.incoming_call;

import androidx.annotation.NonNull;

public class IncomingCallInfo {
   private final String callerAvatarURL;
   private final String callerName;
   private final boolean hasVideo;

   public IncomingCallInfo(@NonNull String callerName, @NonNull String callerAvatarURL, boolean hasVideo) {
      this.callerName = callerName;
      this.callerAvatarURL = callerAvatarURL;
      this.hasVideo = hasVideo;
   }

   public String getCallerAvatarURL() {
      return this.callerAvatarURL;
   }

   public String getCallerName() {
      return this.callerName;
   }

   public boolean hasVideo() {
      return this.hasVideo;
   }
}
