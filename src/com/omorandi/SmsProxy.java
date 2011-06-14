/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.omorandi;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.util.Log;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;



// This proxy can be created by calling Android.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule=SmsModule.class)
public class SmsProxy extends KrollProxy
{
	// Standard Debugging variables
	private static final String LCAT = "SmsProxy";
	private String messageBody = null;
	private String recipient = null;
	
	@Kroll.constant
	public static final int SENT = 0;
	@Kroll.constant
	public static final int CANCELLED = -1;
	@Kroll.constant
	public static final int FAILED = -2;
	
	// Constructor
	public SmsProxy(TiContext tiContext) {
		super(tiContext);
		Log.d(LCAT, "Sms Proxy created");
	}
	
	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict options) {
		super.handleCreationDict(options);
		
		if (options.containsKey("messageBody")) {
			messageBody = (String)options.get("messageBody");
			Log.d(LCAT, "messageBody: " + messageBody);
		}
		
		if (options.containsKey("recipient")) {
			recipient = (String)options.get("recipient");
			Log.d(LCAT, "recipient: " + recipient);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Activity currentActivity = context.getActivity();
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(currentActivity.getBaseContext(), "Result OK", 
						Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(currentActivity.getBaseContext(), "Result: " + resultCode, 
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	   
	public KrollDict createEventObject (boolean success, int result, String resultMessage) 
	{
		KrollDict event = new KrollDict();
		event.put("success", success);
		event.put("result", result);
		event.put("resultMessage", resultMessage);
		return event;
	}
	
	
	@Kroll.method   
	public void send()
	{       
		Activity currentActivity = context.getActivity();
		String MESSAGE_SENT = "SMS_SENT";
		String MESSAGE_DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(currentActivity, 0,
				new Intent(MESSAGE_SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(currentActivity, 0,
				new Intent(MESSAGE_DELIVERED), 0);

		//---when the SMS has been sent---
		currentActivity.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				KrollDict event;
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					event = createEventObject(true, SENT, "Message sent");
					fireEvent("complete", event);
					break;
				default: 
					event = createEventObject(false, FAILED, "Message delivery failed");
					fireEvent("complete", event);
					break;
				}
			}
		}, new IntentFilter(MESSAGE_SENT));

		//---when the SMS has been delivered---
		currentActivity.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				KrollDict event;
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					event = createEventObject(true, SENT, "Message sent");
					fireEvent("complete", event);
					break;
				case Activity.RESULT_CANCELED:
					event = createEventObject(false, FAILED, "Operation canceled");
					fireEvent("complete", event);
					break;                        
				}
			}
		}, new IntentFilter(MESSAGE_DELIVERED));        

		SmsManager sms = SmsManager.getDefault();
		KrollDict event;
		if (recipient == null) {
			event = createEventObject(false, FAILED, "recipient missing");
			this.fireEvent("complete", event);
			return;
		}
		if (messageBody == null) {
			event = createEventObject(false, FAILED, "messageBody missing");
			this.fireEvent("complete", event);
			return;
		}
		
		sms.sendTextMessage(recipient, null, messageBody, sentPI, deliveredPI);        
	}
	
}