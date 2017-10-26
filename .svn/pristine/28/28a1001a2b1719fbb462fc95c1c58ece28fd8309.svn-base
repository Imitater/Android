package com.ruiyihong.toyshop.videoshootActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.StatusBarUtil;

/**
 * Created by hegeyang on 2017/8/8 0008 .
 */

public class ShootBaseActivity extends Activity {
	private AlertDialog progressDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public TextView showProgressDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		View view = View.inflate(this, R.layout.dialog_loading, null);
		builder.setView(view);
		ProgressBar pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
		TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			pb_loading.setIndeterminateTintList(ContextCompat.getColorStateList(this, R.color.dialog_pro_color));
		}
		tv_hint.setText("视频编译中");
		progressDialog = builder.create();
		progressDialog.show();

		return tv_hint;
	}

	public void closeProgressDialog() {
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
