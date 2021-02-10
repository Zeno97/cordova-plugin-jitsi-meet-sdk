# cordova-plugin-jitsi-meet-sdk
Cordova plugin for Jitsi Meet React Native SDK. Actually is Android Only.
All options, feature flags and listeners are available. 
All feature flags available are here: https://github.com/jitsi/jitsi-meet/blob/master/react/features/base/flags/constants.js

# Installation
`cordova plugin add cordova-plugin-jitsi-meet-sdk

# Usage
All paramenter are optional except for room. You need to specify at least the room name.
If serverURL is not specified by default is "https://meet.jit.si".
All feature flags not specified by default are false.
All boolean options by default are false.
All string options by default are empty.

This is the minimal setup

FirebasePlugin.startJitsiMeet(
{
    room: "MyAmazingRoom",
}, 
function(listener){
    // a listener has been fired!
    console.log(listener);
    
    switch(listener){
        case "onConferenceWillJoin":
            //you are going to join into conference
            break;
        case "onConferenceJoined":
            //you are full intered into conference
            break;
        case "onConferenceTerminated":
            //you left the conference
            break;
        case "onCreate":
            //JitsiMeetActivity is created
            break;
        case "onDestroy":
            //JitsiMeetActivity is destroyed
            break;
        case "onBackPressed":
            //has been pressed backbutton inside JitsiMeetActivity
            break;
        case "onNewIntent":
            //JitsiMeetActivity has fired onNewIntent event
            break;
        case "onUserLeaveHint":
            //JitsiMeetActivity is in Picture in Picture mode
            break;
    }
});


Instead this is a complete example.

FirebasePlugin.startJitsiMeet(
{
    serverURL: "https://meet.jit.si",
    room: "MyAmazingRoom",
    displayName: "Max!",
    email: "max@amazingmax.it",
    audioMuted: false,
    videoMuted: false,
    welcomePageEnabled: false,
    subject: "My amazing name",
    audioOnly: false,
    //token: "your jwt token, if you have one",
    flags: {
        "chat.enabled": true,
        "invite.enabled": true,
        "kick-out.enabled": true,
        "live-streaming.enabled": true,
        "pip.enabled": true,
        "raise-hand.enabled":true,
        "recording.enabled": true,
        "video-share.enabled": true,
        "add-people.enabled": true,
        "calendar.enabled": true,
        "meeting-name.enabled": true,
        "video-share.enabled": true,
        "meeting-password.enabled": true,
        "toolbox.alwaysVisible": true
    }
}, function(listener){
    // a listener has been fired!
    console.log(listener);
    
    switch(listener){
        case "onConferenceWillJoin":
            //you are going to join into conference
            break;
        case "onConferenceJoined":
            //you are full intered into conference
            break;
        case "onConferenceTerminated":
            //you left the conference
            break;
        case "onCreate":
            //JitsiMeetActivity is created
            break;
        case "onDestroy":
            //JitsiMeetActivity is destroyed
            break;
        case "onBackPressed":
            //has been pressed backbutton inside JitsiMeetActivity
            break;
        case "onNewIntent":
            //JitsiMeetActivity has fired onNewIntent event
            break;
        case "onUserLeaveHint":
            //JitsiMeetActivity is in Picture in Picture mode
            break;
    }
});
