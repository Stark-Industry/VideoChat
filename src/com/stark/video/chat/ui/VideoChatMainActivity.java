package com.stark.video.chat.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.stark.video.chat.model.hot.VideoInfo;
import com.stark.video.chat.model.hot.XmppManager;
import com.stark.video.chat.ui.BaseActivity;
import com.stark.video.chat.R;
import com.stark.video.chat.ui.pagetab.HotVideoViewPageAdapter;
import com.stark.video.chat.ui.pinterest.HotVideoAdapter;
import com.stark.video.chat.ui.pinterest.MultiColumnListView.OnLoadMoreListener;
import com.stark.video.chat.ui.pinterest.MultiColumnPullToRefreshListView;
import com.stark.video.chat.ui.pinterest.MultiColumnPullToRefreshListView.OnRefreshListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class VideoChatMainActivity extends BaseActivity {

	private PagerTabStrip hotVideoTabs = null;
	private ViewPager hotVideoPager = null;
	private TextView captureTextView = null;
	private List<View> viewList = null;

	private MultiColumnPullToRefreshListView hotVideoListView = null;
	private HotVideoAdapter hotVideoAdapter = null;

	private HandlerThread handlerThread = null;
	private LoadHandler loadHandler = null;

	private List<VideoInfo> videoList = null;

	private static final int LOAD_DEEFAULT = 0;
	private static final int APP_LOGIN = 1;
	private static final int LOAD_DEFAULT_COMPLETE = 0;

	private static int currLoadPage = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_chat_main_activity);
		
		hotVideoTabs = (PagerTabStrip) this.findViewById(R.id.hot_video_pager_tab);
		hotVideoPager = (ViewPager) this.findViewById(R.id.hot_video_viewpager);
		captureTextView = (TextView) this.findViewById(R.id.hot_video_capture);
		captureTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
			
		});
		
		viewList = new ArrayList<View>();
		LayoutInflater Inflater=LayoutInflater.from(this);
		View hotSqureView = Inflater.inflate(R.layout.video_chat_hot_squre,null);
		viewList.add(hotSqureView);  
		View hotDiscoverView = Inflater.inflate(R.layout.video_chat_hot_discover, null);
		viewList.add(hotDiscoverView);  

		handlerThread = new HandlerThread("DataRefreshThread");
		handlerThread.start();
		loadHandler = new LoadHandler(handlerThread.getLooper());
		
		hotVideoListView = (MultiColumnPullToRefreshListView) hotSqureView.findViewById(R.id.hot_video_list_view);
		hotVideoAdapter = new HotVideoAdapter(this);
		hotVideoListView.setAdapter(hotVideoAdapter);
		hotVideoListView.setShowLastUpdatedText(true);
		//hotVideoListView.setLastUpdatedDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		hotVideoListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				currLoadPage = 1;
				videoList = XmppManager.getInstance().getVideoInfo(currLoadPage);
				hotVideoAdapter.setVideoList(videoList);
				hotVideoListView.onRefreshComplete();
			}
		});
		
		hotVideoListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				videoList = XmppManager.getInstance().getVideoInfo(++ currLoadPage);
				if (null == videoList || 0 == videoList.size()) {
					-- currLoadPage;
				}
				else {
					hotVideoAdapter.addVideoItemsOnBottom(videoList);
				}
				hotVideoListView.onLoadMoreComplete();
			}
		});
		
		hotVideoPager.setAdapter(new HotVideoViewPageAdapter(viewList));
		hotVideoPager.setCurrentItem(0);
		
		loadHandler.sendEmptyMessage(APP_LOGIN);
		loadHandler.sendEmptyMessage(LOAD_DEEFAULT);
	}

	private class LoadHandler extends Handler {
		public LoadHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_DEEFAULT:
				videoList = XmppManager.getInstance().getVideoInfo(currLoadPage);
				uiHandler.sendEmptyMessage(LOAD_DEFAULT_COMPLETE);
				break;
			case APP_LOGIN:
				XmppManager.getInstance().login("admin", "admin");
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	private Handler uiHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_DEFAULT_COMPLETE:
				hotVideoAdapter.setVideoList(videoList);
				break;
			default:
				break;
			}
		}
	};

	public void onDestroy() {
		if (loadHandler != null) {
			loadHandler.removeCallbacksAndMessages(null);
			loadHandler = null;
		}

		if (handlerThread != null) {
			handlerThread.getLooper().quit();
			handlerThread = null;
		}

		super.onDestroy();
	}
}
