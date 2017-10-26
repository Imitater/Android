package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hegeyang on 2017/8/10 0010 .
 */

public class SendYajinActivity extends BaseActivity {
	@InjectView(R.id.tv_send_yajin_money)
	TextView tvSendYajinMoney;
	@InjectView(R.id.tv_send_yajin_submit)
	TextView tvSendYajinSubmit;
	@InjectView(R.id.cb_send_zfb)
	ImageView cbSendZfb;
	@InjectView(R.id.rl_send_zfb)
	RelativeLayout rlSendZfb;
	@InjectView(R.id.cb_send_wexin)
	ImageView cbSendWexin;
	@InjectView(R.id.rl_send_weixin)
	RelativeLayout rlSendWeixin;
	private int sendType = 0;
	@Override
	protected int getLayoutId() {
		return R.layout.activity_send_yajin;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initEvent() {
		rlSendZfb.setOnClickListener(this);
		rlSendWeixin.setOnClickListener(this);
	}

	@Override
	protected void processClick(View v) throws IOException {
		if(v==rlSendZfb){
			cbSendZfb.setVisibility(View.VISIBLE);
			cbSendWexin.setVisibility(View.INVISIBLE);
			sendType=0;
		}else if(v==rlSendWeixin){
			cbSendZfb.setVisibility(View.INVISIBLE);
			cbSendWexin.setVisibility(View.VISIBLE);
			sendType=1;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.inject(this);
	}
}
