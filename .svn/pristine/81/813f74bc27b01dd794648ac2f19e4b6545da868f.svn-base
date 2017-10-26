package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.REGutil;
import com.ruiyihong.toyshop.util.SendSmsTimerUtils;
import com.ruiyihong.toyshop.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/7/29 0029 .
 */

public class FogetPasActivity extends BaseActivity {
	@InjectView(R.id.et_foget_phone)
	EditText etFogetPhone;
	@InjectView(R.id.et_foget_pas)
	EditText etFogetPas;
	@InjectView(R.id.et_foget_yzm)
	EditText etFogetYzm;
	@InjectView(R.id.tv_foget_submit)
	TextView tvFogetSubmit;
	@InjectView(R.id.tv_foget_sendyzm)
	TextView tvFogetSendyzm;
	private SendSmsTimerUtils sendSmsTimerUtils;
	private String PhoneCode="";
	private long sendTime;
	private String selePhone;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 0:
					ToastHelper.getInstance().displayToastShort("该手机还没有注册，请您先注册");
					sendSmsTimerUtils.onFinish();
					finish();
					break;
				case 1:
					String phone = (String) msg.obj;
					try {
						send(phone);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					ToastHelper.getInstance().displayToastShort("重置密码失败，请您稍后再试");
					break;
				case 3:
					ToastHelper.getInstance().displayToastShort("重置密码成功");
					finish();
					break;
			}
		}
	};

	private void send(String phone) throws IOException {
		final String sendMsg = AppConstants.SERVE_URL+"index/xin/forgetMsg";
		final String number = phone;
		HashMap<String, String> params = new HashMap<>();
		params.put("mobilet",phone);
		final int numcode = (int) ((Math.random() * 9 + 1) * 10000);
		params.put("checkma",numcode+"");
		OkHttpUtil.postString(sendMsg, params, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String result = OkHttpUtil.getResult(response);
				if(result!=null){
					try {
						JSONObject jsonObject = new JSONObject(result);
						int state = jsonObject.getInt("state");
						if(state==-1){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ToastHelper.getInstance().displayToastShort("一天只能发送5条验证信息，请明天再试");
									sendSmsTimerUtils.onFinish();
									return;
								}
							});
						}else if(state==1){
							sendTime = System.currentTimeMillis();
							PhoneCode = number+"-"+numcode;
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_fogetpas;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		sendSmsTimerUtils = new SendSmsTimerUtils(tvFogetSendyzm, 60000, 1000);
	}

	@Override
	protected void initEvent() {
		tvFogetSendyzm.setOnClickListener(this );
		tvFogetSubmit.setOnClickListener(this);
	}

	@Override
	protected void processClick(View v) throws IOException {
		switch (v.getId()){
			case R.id.tv_foget_submit:
				forgetSubmit();
				break;
			case R.id.tv_foget_sendyzm:
				sendYzm();
				break;
		}
	}

	private void sendYzm() {
		String phone = etFogetPhone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastHelper.getInstance().displayToastShort("请输入手机号");
			return;
		}else if(!TextUtils.isEmpty(phone)){
			boolean b = REGutil.checkCellphone(phone);
			if(!b){
				ToastHelper.getInstance().displayToastShort("手机号好像输错了，检查一下吧");
				return;
			}
		}
		try {
			sendSmsTimerUtils.start();
			sendSms(phone);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void sendSms(String phone) throws IOException {
		selePhone = phone;
		String regPhone = AppConstants.SERVE_URL+"index/Xin/regPhone";
		HashMap<String, String> params = new HashMap<>();
		params.put("phone", phone);
		OkHttpUtil.postString(regPhone, params, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastHelper.getInstance().displayToastShort("发送短信失败");
						sendSmsTimerUtils.onFinish();
					}
				});
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String result = OkHttpUtil.getResult(response);
				LogUtil.e("fasongjieguo"+result);
				if(result!=null){
					try {
						JSONObject jsonObject = new JSONObject(result);
						int status = jsonObject.getInt("status");
						switch (status){
							case 1:
								handler.sendEmptyMessage(0);
								break;
							case 0:
								Message obtain = Message.obtain();
								obtain.what=1;
								obtain.obj= selePhone;
								handler.sendMessage(obtain);
								break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void forgetSubmit() {
		String phone = etFogetPhone.getText().toString().trim();
		String pass = etFogetPas.getText().toString().trim();
		String yzm = etFogetYzm.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastHelper.getInstance().displayToastShort("还没有输入手机号哦");
			return;
		}
		if(TextUtils.isEmpty(pass)){
			ToastHelper.getInstance().displayToastShort("密码不能为空");
			return;
		}
		long nowTime = System.currentTimeMillis();

		if(!PhoneCode.contains(phone)){
			ToastHelper.getInstance().displayToastShort("手机号还没发过验证码");
			return;
		}
		if(!PhoneCode.contains(yzm)){
			ToastHelper.getInstance().displayToastShort("验证码不正确");
			return;
		}
		if((nowTime-sendTime)/6000>20){
			ToastHelper.getInstance().displayToastShort("验证码过期");
			return;
		}
		String change = AppConstants.SERVE_URL+"index/xin/forgetpass";
		HashMap<String, String> params = new HashMap<>();
		params.put("uname",phone);
		params.put("upass",pass);
		try {
			OkHttpUtil.postString(change, params, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					handler.sendEmptyMessage(2);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String result = OkHttpUtil.getResult(response);
					if (result!=null){
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.getInt("state");
							if(state==1){
								handler.sendEmptyMessage(3);
							}
							else{
								handler.sendEmptyMessage(2);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
