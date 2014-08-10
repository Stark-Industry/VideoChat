package com.stark.video.chat.ui.pinterest;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.stark.video.chat.R;
import com.stark.video.chat.model.hot.VideoInfo;
import com.stark.video.chat.util.DeviceUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HotVideoAdapter extends BaseAdapter {
	private Context currContext = null;
	private LayoutInflater currInflater = null;
	private List<VideoInfo> hotVideoData = new ArrayList<VideoInfo>();
	private int screenWidth = 0;
	private static final int VIDEO_ITEM_PADDING = 24;// pixel
	private DisplayImageOptions options = null;

	public HotVideoAdapter(Context context) {
		this.currContext = context;
		currInflater = LayoutInflater.from(currContext);
		screenWidth = DeviceUtil.getScreenWidth(currContext);
		options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .build();
	}

	public void setVideoList(List<VideoInfo> list) {
		if (hotVideoData.size() > 0) {
			hotVideoData.clear();
		}
		if (list != null) {
			hotVideoData.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void addVideoItemsOnBottom(List<VideoInfo> list) {
		if (list != null) {
			hotVideoData.addAll(list);
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {
		return hotVideoData.size();
	}

	@Override
	public Object getItem(int position) {
		return hotVideoData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final VideoInfo videoInfo = hotVideoData.get(position);
		
		if (null == convertView) {
			convertView = currInflater.inflate(R.layout.video_chat_main_item, null);
			viewHolder = new ViewHolder();
			viewHolder.avatarImg = (ImageView) convertView.findViewById(R.id.hot_author_avatar);
			viewHolder.authorName = (TextView) convertView.findViewById(R.id.hot_author_name);
			viewHolder.coverImg = (ImageView) convertView.findViewById(R.id.hot_video_cover);
			viewHolder.videoDescription = (TextView) convertView.findViewById(R.id.hot_video_description);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.authorName.setText(videoInfo.authorName);
		viewHolder.videoDescription.setText(videoInfo.title + "\n" + videoInfo.description);

		ImageLoader.getInstance().displayImage(videoInfo.avatarUrl, viewHolder.avatarImg, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				failDealing(imageUri, view, failReason);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}

		});

		ImageLoader.getInstance().displayImage(videoInfo.coverUrl, viewHolder.coverImg, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				LayoutParams lp = (LayoutParams) viewHolder.coverImg.getLayoutParams();
				lp.width = screenWidth / 2 - VIDEO_ITEM_PADDING ;
				lp.height = (int) (lp.width / videoInfo.coverImgRatio);
				viewHolder.coverImg.setLayoutParams(lp);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				failDealing(imageUri, view, failReason);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}

		});

		viewHolder.coverImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}

		});
		return convertView;
	}

	private void failDealing(String imageUri, View view, FailReason failReason) {
		String message = null;
		switch (failReason.getType()) {
		case IO_ERROR:
			message = "Input/Output error";
			break;
		case DECODING_ERROR:
			message = "can not be decoding";
			break;
		case NETWORK_DENIED:
			message = "Downloads are denied";
			break;
		case OUT_OF_MEMORY:
			message = "内存不足";
			Toast.makeText(currContext, message, Toast.LENGTH_SHORT).show();
			break;
		case UNKNOWN:
			message = "Unknown error";
			Toast.makeText(currContext, message, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private class ViewHolder {
		public ImageView avatarImg;
		public ImageView coverImg;
		public TextView authorName;
		public TextView videoDescription;
	}

}
