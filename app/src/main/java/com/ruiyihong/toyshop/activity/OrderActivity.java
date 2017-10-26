package com.ruiyihong.toyshop.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.fragment.OrderContentFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by hegeyang on 2017/7/27 0027 .
 */

public class OrderActivity extends BaseActivity {

	@InjectView(R.id.ll_order_unpay)
	TextView llOrderUnpay;
	@InjectView(R.id.line_order_unpay)
	View lineOrderUnpay;
	@InjectView(R.id.ll_order_unfahuo)
	TextView llOrderUnfahuo;
	@InjectView(R.id.line_order_unfahuo)
	View lineOrderUnfahuo;
	@InjectView(R.id.ll_order_untalk)
	TextView llOrderUntalk;
	@InjectView(R.id.line_order_untalk)
	View lineOrderUntalk;
	@InjectView(R.id.ll_order_backed)
	TextView llOrderBacked;
	@InjectView(R.id.line_order_backed)
	View lineOrderBacked;
	@InjectView(R.id.ll_order_get)
	TextView llOrderGet;
	@InjectView(R.id.line_order_get)
	View lineOrderGet;
	@InjectView(R.id.vp_order_content)
	ViewPager vpOrderContent;
	private ArrayList<OrderContentFragment> pagerList;
	private List<View> lineList;
	private ArrayList<TextView> tvList;
	private VpOrderAdapter orderAdapter;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_order;
	}

	@Override
	protected void initView() {
		lineList = new ArrayList<View>();
		lineList.add(lineOrderUnpay);
		lineList.add(lineOrderUnfahuo);
		lineList.add(lineOrderBacked);
		lineList.add(lineOrderGet);
		lineList.add(lineOrderUntalk);

		tvList = new ArrayList<TextView>();
		tvList.add(llOrderUnpay);
		tvList.add(llOrderUnfahuo);
		tvList.add(llOrderBacked);
		tvList.add(llOrderGet);
		tvList.add(llOrderUntalk);
	}

	@Override
	protected void initData() {
		pagerList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			OrderContentFragment fr = new OrderContentFragment(i);
			pagerList.add(fr);
		}
		orderAdapter = new VpOrderAdapter(getSupportFragmentManager());
		vpOrderContent.setAdapter(orderAdapter);
	}

	@Override
	protected void initEvent() {
		llOrderUnpay.setOnClickListener(this);
		llOrderUnfahuo.setOnClickListener(this);
		llOrderBacked.setOnClickListener(this);
		llOrderGet.setOnClickListener(this);
		llOrderUntalk.setOnClickListener(this);
		vpOrderContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				switchLine(position);

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	@Override
	protected void processClick(View v) throws IOException {
		switch (v.getId()) {
			case R.id.ll_order_unpay:
				switchLine(0);
				vpOrderContent.setCurrentItem(0);
				break;
			case R.id.ll_order_unfahuo:
				switchLine(1);
				vpOrderContent.setCurrentItem(1);
				break;
			case R.id.ll_order_backed:
				switchLine(2);
				vpOrderContent.setCurrentItem(2);
				break;
			case R.id.ll_order_get:
				switchLine(3);
				vpOrderContent.setCurrentItem(3);
				break;
			case R.id.ll_order_untalk:
				switchLine(4);
				vpOrderContent.setCurrentItem(4);
				break;
		}
	}

	public void switchLine(int position) {
		for (int i = 0; i < tvList.size(); i++) {
			if (position == i) {
				tvList.get(i).setTextColor(getResources().getColor(R.color.tab_selected));
				lineList.get(i).setVisibility(View.VISIBLE);
			} else {
				tvList.get(i).setTextColor(getResources().getColor(R.color.grad_dark));
				lineList.get(i).setVisibility(View.INVISIBLE);
			}
		}
	}



	class VpOrderAdapter extends FragmentPagerAdapter {

		public VpOrderAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return pagerList.get(position);
		}

		@Override
		public int getCount() {
			return pagerList.size();
		}
	}

	public void notifyOrder(){
		//刷新
		if (orderAdapter!=null) {
			orderAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int currentItem = vpOrderContent.getCurrentItem();
		initData();
		vpOrderContent.setCurrentItem(currentItem);

	}
}
