/*
 * Apache 2.0 License
 *
 * Copyright (c) Massimiliano Coppola 2021
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apache License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://opensource.org/licenses/Apache-2.0/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 */
package org.jitsi.meet.JitsiMeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.jitsi.meet.sdk.BroadcastEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.URL;
import java.util.Map;

public class JitsiMeet extends CordovaPlugin {
    public static final String TAG = "JitsiMeet";
    public static JitsiMeetActivity jitsiMeetActivity = null;
    private static JitsiMeet instance = null;
    public static CallbackContext jitsiCallbackContext;

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        instance = this;
        if (action.equals("startConference")) {
            jitsiCallbackContext = callbackContext;
            JitsiMeet.startConference(cordova.getContext(), args.getJSONObject(0));
        }
        else if(action.equals("disposeConference")){
            cordova.getActivity().runOnUiThread(() -> JitsiMeet.disposeConference(callbackContext));
        }
        else {
            return false;
        }
        return true;
    }

    public static JitsiMeet getInstance() { return instance; }

    public static boolean startConference(Context context, JSONObject JSONoptions) {
        try {
            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();

            if(JSONoptions.optString("room", "").isEmpty()){
                //va definita una stanza!
                return false;
            }

            // Configurazione UserInfo

            if(!JSONoptions.optString("avatar", "").isEmpty()) {
                userInfo.setAvatar(new URL(JSONoptions.getString("avatar")));
            }
            if(!JSONoptions.optString("displayName", "").isEmpty()) {
                userInfo.setDisplayName(JSONoptions.getString("displayName"));
            }
            if(!JSONoptions.optString("email", "").isEmpty()) {
                userInfo.setEmail(JSONoptions.getString("email"));
            }

            // Configurazione options

            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();

            builder.setRoom(JSONoptions.getString("room"));
            builder.setServerURL(new URL(JSONoptions.optString("serverURL", "https://meet.jit.si")));

            builder.setUserInfo(userInfo);

            if(!JSONoptions.optString("subject", "").isEmpty()) {
                builder.setSubject(JSONoptions.getString("subject"));
            }
            if(!JSONoptions.optString("token", "").isEmpty()) {
                builder.setToken(JSONoptions.getString("token"));
            }
            builder.setAudioMuted(JSONoptions.optBoolean("audioMuted",false));
            builder.setVideoMuted(JSONoptions.optBoolean("videoMuted",false));
            builder.setAudioOnly(JSONoptions.optBoolean("audioOnly",false));
            builder.setWelcomePageEnabled(JSONoptions.optBoolean("welcomePageEnabled",false));

            // Configurazione flags di options
            try {
                JSONObject flags = JSONoptions.getJSONObject("flags");
                JSONArray flagNames = flags.names();

                for (int i = 0; i < flagNames.length(); i++) {

                    String key = flags.names().get(i).toString();

                    if (flags.get(key) instanceof Integer) {
                        builder.setFeatureFlag(key, flags.getInt(key));
                    } else if (flags.get(key) instanceof String) {
                        builder.setFeatureFlag(key, flags.getString(key));
                    } else if (flags.get(key) instanceof Boolean) {
                        builder.setFeatureFlag(key, flags.getBoolean(key));
                    }

                }
            }
            catch (Exception e) {}

            JitsiMeetConferenceOptions options = builder.build();

            //TODO: Finire di sviluppare eventi broadcast
            BroadcastReceiver participantJoinedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"PARTICIPANT_JOINED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.PARTICIPANT_JOINED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(participantJoinedReceiver, intentFilter);

            //
            BroadcastReceiver participantLeftReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"PARTICIPANT_LEFT");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.PARTICIPANT_LEFT.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(participantLeftReceiver, intentFilter);

            //
            BroadcastReceiver conferenceJoinedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"CONFERENCE_JOINED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_JOINED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(conferenceJoinedReceiver, intentFilter);

            //
            BroadcastReceiver conferenceTerminatedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"CONFERENCE_TERMINATED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(conferenceTerminatedReceiver, intentFilter);

            //
            BroadcastReceiver conferenceWillJoinReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"CONFERENCE_WILL_JOIN");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_WILL_JOIN.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(conferenceWillJoinReceiver, intentFilter);

            //
            BroadcastReceiver audioMutedChangedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"AUDIO_MUTED_CHANGED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(audioMutedChangedReceiver, intentFilter);

            //
            BroadcastReceiver videoMutedChangedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"VIDEO_MUTED_CHANGED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.VIDEO_MUTED_CHANGED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(videoMutedChangedReceiver, intentFilter);

            //
            BroadcastReceiver endpointTextMessageReceivedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"ENDPOINT_TEXT_MESSAGE_RECEIVED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.ENDPOINT_TEXT_MESSAGE_RECEIVED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(endpointTextMessageReceivedReceiver, intentFilter);

            //
            BroadcastReceiver participantsInfoRetrievedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"PARTICIPANTS_INFO_RETRIEVED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.PARTICIPANTS_INFO_RETRIEVED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(participantsInfoRetrievedReceiver, intentFilter);

            //
            BroadcastReceiver chatMessageReceivedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"CHAT_MESSAGE_RECEIVED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.CHAT_MESSAGE_RECEIVED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(chatMessageReceivedReceiver, intentFilter);

            //
            BroadcastReceiver chatToggledReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"CHAT_TOGGLED");
                    pluginResult.setKeepCallback(true);
                    jitsiCallbackContext.sendPluginResult(pluginResult);
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BroadcastEvent.Type.CHAT_TOGGLED.getAction());
            LocalBroadcastManager.getInstance(getInstance().cordova.getActivity()).registerReceiver(chatToggledReceiver, intentFilter);



            jitsiMeetActivity.launch(context,options);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disposeConference(CallbackContext callbackContext) {
        if (jitsiMeetActivity != null){
            jitsiMeetActivity.finish();
            callbackContext.success("success");
            return true;
        }
        else {
            callbackContext.error("JitsiMeet not started");
            return false;
        }
    }

    public static JitsiMeetConferenceOptions convertJitsiMeetOptions(Map<String,String> JSONoptions) {
        try {
            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();

            // Configurazione UserInfo
                Log.d(TAG, "displayname");
                userInfo.setDisplayName(JSONoptions.get("displayName"));

                Log.d(TAG, "email");
                userInfo.setEmail(JSONoptions.get("email"));


            // Configurazione options

            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();

            builder.setRoom(JSONoptions.get("room"));
            builder.setServerURL(new URL(JSONoptions.get("serverURL")));

            builder.setUserInfo(userInfo);

            if(JSONoptions.get("callType") != null){
                switch(JSONoptions.get("callType")){
                    case "audio":
                        builder.setVideoMuted(true);
                        builder.setAudioOnly(true);
                        break;
                    case "video":
                        builder.setVideoMuted(false);
                        builder.setAudioOnly(false);
                        break;
                }
            }

            // Configurazione flags di options
            // non ancora, è problematico
            /*try {
                JSONObject flags = JSONoptions.getJSONObject("flags");
                JSONArray flagNames = flags.names();

                for (int i = 0; i < flagNames.length(); i++) {

                    String key = flags.names().get(i).toString();

                    if (flags.get(key) instanceof Integer) {
                        builder.setFeatureFlag(key, flags.getInt(key));
                    } else if (flags.get(key) instanceof String) {
                        builder.setFeatureFlag(key, flags.getString(key));
                    } else if (flags.get(key) instanceof Boolean) {
                        builder.setFeatureFlag(key, flags.getBoolean(key));
                    }

                }
            }
            catch (Exception e) {}*/

            JitsiMeetConferenceOptions options = builder.build();

            return options;

        } catch (Exception e) {
            Log.d(TAG, "Errore catch");
            return new JitsiMeetConferenceOptions.Builder().build();
        }
    }

}
