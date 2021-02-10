/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.jitsi.meet.JitsiMeet;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
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
        if (action.equals("startJitsiMeet")) {
            jitsiCallbackContext = callbackContext;
            this.startJitsiMeet(cordova.getContext(), args.getJSONObject(0));
        }
        else {
            return false;
        }
        return true;
    }

    public static boolean startJitsiMeet(Context context, JSONObject JSONoptions) {
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

            JitsiMeetActivity.launch(context,options);

            return true;

        } catch (Exception e) {
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
