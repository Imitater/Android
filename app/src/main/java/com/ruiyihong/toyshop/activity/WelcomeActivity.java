package com.ruiyihong.toyshop.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.UpdateVersionBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.UpdateService;

import java.io.IOException;
import java.util.HashMap;

import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by hegeyang on 2017/7/7 0007 .
 */

public class WelcomeActivity extends BaseActivity {
	@InjectView(R.id.iv_welcome)
	ImageView ivWelcome;
	private UpdateVersionBean versionBean;
	private Handler handler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 0:
					Intent intent;
					intent = new Intent(WelcomeActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1:
					showIfUpdate(versionBean);
					break;
			}
		}
	};
	private ValueAnimator valueAnimator;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_welcome;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		final Window window = getWindow();
		valueAnimator = ValueAnimator.ofFloat(0.2f, 1f);
		valueAnimator.setDuration(3000);
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				WindowManager.LayoutParams params = window.getAttributes();
				params.alpha= (float) animation.getAnimatedValue();
				window.setAttributes(params);
			}
		});
		valueAnimator.start();
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				overridePendingTransition(0,0);
				boolean aBoolean = SPUtil.getBoolean(AppConstants.IS_OPENMAIN, WelcomeActivity.this);
				Intent intent;
				if(aBoolean){
					getUpdateData();
				}else{
					intent = new Intent(WelcomeActivity.this,SplashActivity.class);
					startActivity(intent);
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
	}

	@Override
	protected void initEvent() {

	}

	@Override
	protected void processClick(View v) {

	}
	private void showIfUpdate(final UpdateVersionBean versionBean) {
		LogUtil.e("对话框进程");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("检测到有更新");
		builder.setMessage("是否更新？");
		builder.setNegativeButton("现在更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Intent service = new Intent(WelcomeActivity.this,UpdateService.class);
				service.putExtra("data",AppConstants.VERSION_UPDATE_HEAD+versionBean.url);
				startService(service);
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setPositiveButton("暂不更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}

	public void getUpdateData() {
		String update = AppConstants.SERVE_URL+"index/version/version";
		HashMap<String, String> params = new HashMap<>();
		try {
			OkHttpUtil.postString(update, params, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					handler.sendEmptyMessage(0);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String result = OkHttpUtil.getResult(response);
					if(result!=null){
						try {
							Gson gson = new Gson();
							versionBean = gson.fromJson(result, UpdateVersionBean.class);
							PackageManager pm = WelcomeActivity.this.getPackageManager();
							PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
							String versionName = pi.versionName;
							int i = versionName.compareTo(versionBean.vision);
							if(i==0){
								handler.sendEmptyMessage(0);
							}else if(i==-1){
								handler.sendEmptyMessage(1);
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(0);
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
