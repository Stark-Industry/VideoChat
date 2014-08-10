package com.stark.video.chat;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.stark.video.chat.model.hot.XmppManager;

import android.app.Application;
import android.content.Context;

public class VideoChatApplication extends Application {
	private static volatile VideoChatApplication videoChatAppInstance = null;
	
	public static VideoChatApplication getApplication() {
		return videoChatAppInstance;
	}

	public void onCreate() {
		super.onCreate();
		videoChatAppInstance = this;
		initUniversalImageLoader(getApplicationContext());
	}
	
	private static void initUniversalImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY)
		.denyCacheImageMultipleSizesInMemory()
		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.build();
		ImageLoader.getInstance().init(config);
	}
}
