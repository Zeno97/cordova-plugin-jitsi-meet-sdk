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

#import <Cordova/CDVPlugin.h>
@import JitsiMeetSDK;

@interface CDVJitsiMeet : CDVPlugin <JitsiMeetViewDelegate>

@property (nonatomic, strong) NSString *room;
@property (nonatomic, strong) JitsiMeetView *jitsiMeetView;
@property (nonatomic, strong) NSString *lastCallbackId;
@property (nonatomic, strong) CDVPluginResult* result;

// Start che jitsi meet conference
- (void)startConference:(CDVInvokedUrlCommand*)command;
// Exit the jitsi meet conference
- (void)disposeConference:(CDVInvokedUrlCommand*)command;

@end
