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

#import "CDVJitsiMeet.h"

@implementation CDVJitsiMeet

- (void)startConference:(CDVInvokedUrlCommand*)command
{
    NSDictionary* meetingOptions = [command.arguments objectAtIndex:0];
    
    if([meetingOptions isKindOfClass:[NSDictionary class]] == false){
        //input not valid
        return;
    }
    
    NSString *room = [meetingOptions objectForKey:@"room"];
    if([room isEqual:nil]){
        //va definita una stanza!
        return;
    }
    
    NSString *serverURL = [meetingOptions objectForKey:@"serverURL"];
    if([serverURL isEqual:nil]){
        serverURL = @"https://meet.jit.si";
    }
    
    self.lastCallbackId = command.callbackId;
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"ok"];
    [self.result setKeepCallbackAsBool:YES];
    
    self.jitsiMeetView = [[JitsiMeetView alloc] initWithFrame:self.viewController.view.frame];
    self.jitsiMeetView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.jitsiMeetView.delegate = self;

    JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
        builder.serverURL = [NSURL URLWithString:serverURL];
        builder.room = room;
        
        NSString *subject = [meetingOptions objectForKey:@"subject"];
        if([subject isEqual:nil] == false){
            builder.subject = subject;
        }
        NSString *token = [meetingOptions objectForKey:@"token"];
        if([token isEqual:nil] == false){
            builder.token = token;
        }
        
        BOOL audioOnly = [[meetingOptions objectForKey:@"audioOnly"] boolValue];
        [builder setAudioOnly:audioOnly];
        
        BOOL audioMuted = [[meetingOptions objectForKey:@"audioMuted"] boolValue];
        [builder setAudioMuted:audioMuted];
        
        BOOL videoMuted = [[meetingOptions objectForKey:@"audioMuted"] boolValue];
        [builder setVideoMuted:videoMuted];
        
        BOOL welcomePageEnabled = [[meetingOptions objectForKey:@"welcomePageEnabled"] boolValue];
        [builder setWelcomePageEnabled:welcomePageEnabled];
        
        
        //flags
        NSDictionary *flags = [meetingOptions objectForKey:@"flags"];
        if([meetingOptions isKindOfClass:[NSDictionary class]]){
            NSArray *keys = [flags allKeys];
            
            for (int i = 0; i < keys.count; i++) {

                NSObject *value = [flags valueForKey:keys[i]];

                if ([value isKindOfClass:[NSNumber class]]) {
                    [builder setFeatureFlag:keys[i] withValue:value];
                } else {
                    [builder setFeatureFlag:keys[i] withBoolean:value];
                }

            }
        }
    }];

    [self.jitsiMeetView join:options];
    
    [self.viewController.view addSubview:self.jitsiMeetView];
}

- (void)disposeConference:(CDVInvokedUrlCommand*)command
{
    self.lastCallbackId = nil;
    self.result = nil;
    if(self.jitsiMeetView){
        [self.jitsiMeetView removeFromSuperview];
        self.jitsiMeetView = nil;
    }
}

- (void)conferenceWillJoin:(NSDictionary *)data {
    NSLog(@"About to join conference %@", self.room);
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"CONFERENCE_WILL_JOIN"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)conferenceJoined:(NSDictionary *)data {
    NSLog(@"Conference %@ joined", self.room);
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"CONFERENCE_JOINED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)conferenceTerminated:(NSDictionary *)data {
    NSLog(@"Conference %@ terminated", self.room);
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"CONFERENCE_TERMINATED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
    
    if(self.jitsiMeetView){
        [self.jitsiMeetView removeFromSuperview];
        self.jitsiMeetView = nil;
    }
}

- (void)participantJoined:(NSDictionary *)data {
    NSLog(@"Participant joined");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"PARTICIPANT_JOINED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)participantLeft:(NSDictionary *)data {
    NSLog(@"Participant left");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"PARTICIPANT_LEFT"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)audioMutedChanged:(NSDictionary *)data {
    NSLog(@"Audio muted changed");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"AUDIO_MUTED_CHANGED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)videoMutedChanged:(NSDictionary *)data {
    NSLog(@"Video muted changed");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"VIDEO_MUTED_CHANGED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)endpointTextMessageReceived:(NSDictionary *)data {
    NSLog(@"Endpoint text message received");
    
    self.result= [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"ENDPOINT_TEXT_MESSAGE_RECEIVED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)chatToggled:(NSDictionary *)data {
    NSLog(@"Chat toggled");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"CHAT_TOGGLED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)chatMessageReceived:(NSDictionary *)data {
    NSLog(@"Chat message received");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"CHAT_MESSAGE_RECEIVED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}

- (void)screenShareToggled:(NSDictionary *)data {
    NSLog(@"Screen share toggled");
    
    self.result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                 messageAsString:@"SCREEN_SHARE_TOGGLED"];
    [self.result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.result
                                callbackId:self.lastCallbackId];
}
@end
