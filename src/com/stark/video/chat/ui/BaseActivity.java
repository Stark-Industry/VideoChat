package com.stark.video.chat.ui;

import com.stark.video.chat.VideoChatApplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
	protected final Context appContext = VideoChatApplication.getApplication().getApplicationContext();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
}
