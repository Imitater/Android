package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimatorRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.SPUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hegeyang on 2017/7/7 0007 .
 */

public class SplashActivity extends BaseActivity {

	@InjectView(R.id.vp_splash)
	ViewPager vpSplash;
	@InjectView(R.id.btn_guide)
	TextView btnGuide;
	@InjectView(R.id.ll_splash)
	LinearLayout llSplash;
	@InjectView(R.id.iv_blue_point)
	ImageView ivBluePoint;
	private int[] images;
	private ArrayList<ImageView> imageList;
	private int distance;


	@Override
	protected int getLayoutId() {
		return R.layout.activity_splash;
	}

	@Override
	protected void initView() {


	}

	@Override
	protected void initData() {
		//存储已经跳转过mainactivity的标记
		SPUtil.putBoolean(AppConstants.IS_OPENMAIN,true,SplashActivity.this);
		images = new int[]{R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3};
		imageList = new ArrayList<>();
		for (int i = 0; i < images.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setBackgroundResource(images[i]);

			imageList.add(iv);

			//初始化LinearLayout中默认的点
			ImageView iv_point = new ImageView(this);
			iv_point.setBackgroundResource(R.drawable.nomarl_point);
			//获取手机的密度比
			float density = this.getResources().getDisplayMetrics().density;
			LinearLayout.LayoutParams params =
					new LinearLayout.LayoutParams((int) (13 * density), (int) (13 * density));
			if (i != 0) {
				//左边距
				params.leftMargin = (int) (32 * density);
			}
			iv_point.setLayoutParams(params);

			//将点添加到线性布局中
			llSplash.addView(iv_point);

		}
		initBluePoint();
		vpSplash.setAdapter(new SplashAdapter());

		vpSplash.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				int leftMargin= (int) (distance*positionOffset+distance*position);
				//获取红点的参数信息
				RelativeLayout.LayoutParams
						params = (RelativeLayout.LayoutParams) ivBluePoint.getLayoutParams();
				params.leftMargin=leftMargin;

				ivBluePoint.setLayoutParams(params);
			}

			@Override
			public void onPageSelected(int position) {
				AlphaAnimation animation;
				if (position==imageList.size()-1){
					//当前选中的是最后一个条目，button可见
					btnGuide.setVisibility(View.VISIBLE);
					//渐变动画
					animation = new AlphaAnimation(0, 1);
					animation.setDuration(2000);
					animation.setRepeatCount(0);
					btnGuide.setAnimation(animation);
					//button的点击事件
					btnGuide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//跳转到主页面
							Intent intent = new Intent(SplashActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
							overridePendingTransition(0,0);
						}
					});
				}else {
					//其他条目，button不可见
					btnGuide.setAnimation(null);
					btnGuide.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
	}

	private void initBluePoint() {
		ivBluePoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			//当全局开始布局的时候回调
			@Override
			public void onGlobalLayout() {
				//获取两个普通点的坐标
				distance = llSplash.getChildAt(1).getLeft() - llSplash.getChildAt(0).getLeft();
				//只需要获取距离，不需要多次监听
				//移除监听
				ivBluePoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);

			}
		});
	}

	@Override
	protected void initEvent() {

	}

	@Override
	protected void processClick(View v) {

	}



	class SplashAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view==o;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = imageList.get(position);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
