package org.jitsi.meet.sdk.log;

import android.util.Log;
import java.text.MessageFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber.Tree;

public abstract class JitsiMeetBaseLogHandler extends Tree {
   protected void log(int priority, @Nullable String tag, @NotNull String msg, @Nullable Throwable t) {
      String errmsg = Log.getStackTraceString(t);
      if (errmsg.isEmpty()) {
         this.doLog(priority, this.getDefaultTag(), msg);
      } else {
         this.doLog(priority, this.getDefaultTag(), MessageFormat.format("{0}\n{1}", msg, errmsg));
      }

   }

   protected abstract void doLog(int var1, @NotNull String var2, @NotNull String var3);

   protected abstract String getDefaultTag();
}
