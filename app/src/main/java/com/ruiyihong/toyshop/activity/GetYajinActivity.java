package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hegeyang on 2017/8/10 0010 .
 */

public class GetYajinActivity extends BaseActivity {
	@InjectView(R.id.tv_get_yajin_money)
	TextView tvGetYajinMoney;
	@InjectView(R.id.tv_get_yajin_submit)
	TextView tvGetYajinSubmit;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_get_yajin;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {

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
