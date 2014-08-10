package com.stark.video.chat.ui.pagetab;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class HotVideoViewPageAdapter extends PagerAdapter {
	private List<View> viewList = null;
	private final String[] titles = {"广场", "动态"};
	
	public HotVideoViewPageAdapter(List<View> views) {
		super();
		this.viewList = views;
	}

	@Override
	public int getCount() {
		if (viewList != null) {
			return viewList.size();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeViewAt(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position), 0);
		return viewList.get(position);
	}
	
	

}
