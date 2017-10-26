package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.view.MyImageView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hegeyang on 2017/8/6 0006 .
 */

public class VipQuanyiActivity extends BaseActivity {
	@InjectView(R.id.iv_vip_quanyi)
	ImageView ivVipQuanyi;
	private ArrayList<Integer> images;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_vipquanyi;
	}

	@Override
	protected void initView() {
		images = new ArrayList<>();
		images.add(R.mipmap.vip_quanyi_0);
		images.add(R.mipmap.vip_quanyi_1);
		images.add(R.mipmap.vip_quanyi_2);
		images.add(R.mipmap.vip_quanyi_3);
		images.add(R.mipmap.vip_quanyi_4);
	}

	@Override
	protected void initData() {
		int type = getIntent().getIntExtra("type", -1);
		ivVipQuanyi.setImageResource(images.get(type));
	}

	@Override
	protected void initEvent() {

	}

	@Override
	protected void processClick(View v) throws IOException {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.inject(this);
	}
}
