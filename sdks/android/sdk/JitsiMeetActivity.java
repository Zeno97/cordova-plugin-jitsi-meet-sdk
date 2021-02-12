package org.jitsi.meet.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.facebook.react.modules.core.PermissionListener;
import java.util.Map;

import org.apache.cordova.PluginResult;
import org.jitsi.meet.JitsiMeet.JitsiMeet;
import org.jitsi.meet.sdk.R.id;
import org.jitsi.meet.sdk.R.layout;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class JitsiMeetActivity extends FragmentActivity implements JitsiMeetActivityInterface, JitsiMeetViewListener {
   protected static final String TAG = JitsiMeetActivity.class.getSimpleName();
   private static final String ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE";
   private static final String JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions";
   private static JitsiMeetActivity instance = null;
   
   public static JitsiMeetActivity getInstance(){
      return instance;
   }

   public static void launch(Context context, JitsiMeetConferenceOptions options) {
      Intent intent = new Intent(context, JitsiMeetActivity.class);
      intent.setAction("org.jitsi.meet.CONFERENCE");
      intent.putExtra("JitsiMeetConferenceOptions", options);
      context.startActivity(intent);
   }

   public static void launch(Context context, String url) {
      JitsiMeetConferenceOptions options = (new JitsiMeetConferenceOptions.Builder()).setRoom(url).build();
      launch(context, options);
   }

   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
	   instance = this;
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onCreate");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
      this.setContentView(layout.activity_jitsi_meet);
      this.getJitsiView().setListener(this);
      if (!this.extraInitialize()) {
         this.initialize();
      }

   }

   public void onDestroy() {
      this.leave();
	   instance = null;
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onDestroy");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
      if (AudioModeModule.useConnectionService()) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ConnectionService.abortConnections();
         }
      }

      JitsiMeetOngoingConferenceService.abort(this);
      super.onDestroy();
   }

   public void finish() {
      this.leave();
      super.finishAndRemoveTask();
      //super.finish();//Do you have issue? Uncomment this and comment the line above - By Max, the great Super Sayan!
   }

   protected JitsiMeetView getJitsiView() {
      JitsiMeetFragment fragment = (JitsiMeetFragment)this.getSupportFragmentManager().findFragmentById(id.jitsiFragment);
      return fragment.getJitsiView();
   }

   public void join(@Nullable String url) {
      JitsiMeetConferenceOptions options = (new JitsiMeetConferenceOptions.Builder()).setRoom(url).build();
      this.join(options);
   }

   public void join(JitsiMeetConferenceOptions options) {
      this.getJitsiView().join(options);
   }

   public void leave() {
      this.getJitsiView().leave();
   }

   @Nullable
   private JitsiMeetConferenceOptions getConferenceOptions(Intent intent) {
      String action = intent.getAction();
      if ("android.intent.action.VIEW".equals(action)) {
         Uri uri = intent.getData();
         if (uri != null) {
            return (new JitsiMeetConferenceOptions.Builder()).setRoom(uri.toString()).build();
         }
      } else if ("org.jitsi.meet.CONFERENCE".equals(action)) {
         return (JitsiMeetConferenceOptions)intent.getParcelableExtra("JitsiMeetConferenceOptions");
      }

      return null;
   }

   protected boolean extraInitialize() {
      return false;
   }

   protected void initialize() {
      this.join(this.getConferenceOptions(this.getIntent()));
   }

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
   }

   public void onBackPressed() {
      JitsiMeetActivityDelegate.onBackPressed();
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onBackPressed");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
   }

   public void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      JitsiMeetConferenceOptions options;
      if ((options = this.getConferenceOptions(intent)) != null) {
         this.join(options);
      } else {
         JitsiMeetActivityDelegate.onNewIntent(intent);
      }
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onNewIntent");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
   }

   protected void onUserLeaveHint() {
      this.getJitsiView().enterPictureInPicture();
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onUserLeaveHint");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
   }

   public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
      JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
   }

   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }

   public void onConferenceJoined(Map<String, Object> data) {
      JitsiMeetLogger.i("Conference joined: " + data);
      JitsiMeetOngoingConferenceService.launch(this);
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onConferenceJoined");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
   }

   public void onConferenceTerminated(Map<String, Object> data) {
      JitsiMeetLogger.i("Conference terminated: " + data);
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onConferenceTerminated");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
      this.finish();
   }

   public void onConferenceWillJoin(Map<String, Object> data) {
      JitsiMeetLogger.i("Conference will join: " + data);
      if(JitsiMeet.jitsiCallbackContext != null) {
         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"onConferenceWillJoin");
         pluginResult.setKeepCallback(true);
         JitsiMeet.jitsiCallbackContext.sendPluginResult(pluginResult);
      }
   }
}
