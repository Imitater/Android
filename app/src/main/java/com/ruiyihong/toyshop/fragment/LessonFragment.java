package com.ruiyihong.toyshop.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.LessionPager;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 81521 on 2017/7/16.
 * 课程页面
 */

public class LessonFragment extends BaseFragment {
	@InjectView(R.id.rb_lession_audio)
	TextView rbLessionAudio;
	@InjectView(R.id.rb_lession_video)
	TextView rbLessionVideo;
	@InjectView(R.id.rg_lession)
	LinearLayout rgLession;
	@InjectView(R.id.iv_lession_line_audio)
	ImageView ivLessionLineAudio;
	@InjectView(R.id.iv_lession_line_video)
	ImageView ivLessionLineVideo;
	@InjectView(R.id.vp_lession)
	ViewPager vpLession;
	private ArrayList<LessionPager> pagers;


	@Override
	protected View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_lession, null);
		return view;
	}

	@Override
	protected void initData() {
		initDrawable();
		pagers = new ArrayList<>();
		pagers.add(new LessionPager(mActivity,0));
		pagers.add(new LessionPager(mActivity,1));
		vpLession.setAdapter(new LessionAdapter());
		pagers.get(0).initData();
		switchTab(0);

	}
	class LessionAdapter extends PagerAdapter{
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(pagers.get(position).rootView);
			return pagers.get(position).rootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return pagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view==o;
		}
	}

	private void initDrawable() {
		Drawable drawable = rbLessionAudio.getCompoundDrawables()[0];
		int i = ScreenUtil.dp2px(mActivity, 20);
		int j = ScreenUtil.dp2px(mActivity, 25);
		drawable.setBounds(0,0,j,i);
		rbLessionAudio.setCompoundDrawables(drawable,null,null,null);
		Drawable drawable1 = rbLessionVideo.getCompoundDrawables()[0];
		drawable1.setBounds(0,0,j,i);
		rbLessionVideo.setCompoundDrawables(drawable1,null,null,null);
	}

	@Override
	protected void initEvent() {
		rbLessionAudio.setOnClickListener(this);
		rbLessionVideo.setOnClickListener(this);
		vpLession.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {

			}

			@Override
			public void onPageSelected(int position) {
				switchTab(position);
				pagers.get(position).initData();
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
	}

	private void switchTab(int position) {
		if(position==0){
			rbLessionAudio.setSelected(true);
			rbLessionVideo.setSelected(false);
			ivLessionLineAudio.setVisibility(View.VISIBLE);
			ivLessionLineVideo.setVisibility(View.INVISIBLE);
		}else{
			rbLessionAudio.setSelected(false);
			rbLessionVideo.setSelected(true);
			ivLessionLineAudio.setVisibility(View.INVISIBLE);
			ivLessionLineVideo.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.rb_lession_audio:
				switchTab(0);
				vpLession.setCurrentItem(0);
				break;
			case R.id.rb_lession_video:
				switchTab(1);
				vpLession.setCurrentItem(1);
				break;
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

}
