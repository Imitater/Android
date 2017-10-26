package com.ruiyihong.toyshop.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.AddressBean;
import com.ruiyihong.toyshop.location.Location;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.REGutil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/7/25 0025 .
 */

public class AddAdressActivity extends BaseActivity {


	@InjectView(R.id.tv_add_title)
	TextView tvAddTitle;
	@InjectView(R.id.tv_add_save)
	TextView tvAddSave;
	@InjectView(R.id.et_add_name)
	EditText etAddName;
	@InjectView(R.id.rb_add_nv)
	RadioButton rbAddNv;
	@InjectView(R.id.rb_add_nan)
	RadioButton rbAddNan;
	@InjectView(R.id.rg_add_address)
	RadioGroup rgAddAddress;
	@InjectView(R.id.textView3)
	TextView textView3;
	@InjectView(R.id.rl_add_contact)
	LinearLayout rlAddContact;
	@InjectView(R.id.et_add_number)
	EditText etAddNumber;
	@InjectView(R.id.tv_add_location)
	TextView tvAddLocation;
	@InjectView(R.id.cb_add_moren)
	CheckBox cbAddMoren;
	@InjectView(R.id.Linear_address)
	LinearLayout Linear_address;
	@InjectView(R.id.et_add_address2)
	EditText etAddAddress2;
	@InjectView(R.id.tv_add_delete)
	TextView tvAddDelete;
	@InjectView(R.id.back_add_adres)
	ImageButton ibAddAdres;
	private int yhsex = 1;
	private int moren = 0;
	private String type;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if(msg.what==1){
				ToastHelper.getInstance().displayToastShort("添加成功");
				setResult(0);
				finish();
			}else if(msg.what==0){
				ToastHelper.getInstance().displayToastShort("添加失败，请您稍后重试");
			}else if(msg.what==2){
				ToastHelper.getInstance().displayToastShort("修改成功");
				setResult(0);
				finish();
			}else if(msg.what==3){
				ToastHelper.getInstance().displayToastShort("删除成功");
				setResult(0);
				finish();
			}else if(msg.what==4) {
				ToastHelper.getInstance().displayToastShort("删除失败，请您稍后重试");
			}
		}
	};
	private AddressBean bean;
	private ProgressDialog progressDialog;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_add_address;
	}

	@Override
	protected void initView() {
		type = getIntent().getStringExtra("type");
		Drawable nanDrawable = cbAddMoren.getCompoundDrawables()[0];
		int i = ScreenUtil.dp2px(this, 18);
		nanDrawable.setBounds(0, 0, i, i);
		cbAddMoren.setCompoundDrawables(nanDrawable, null, null, null);
	}

	@Override
	protected void initData() {
		if(type.equals(MineAddressActivity.ADDRESS_ADD)){
			tvAddDelete.setVisibility(View.INVISIBLE);
		}else{
			tvAddDelete.setVisibility(View.VISIBLE);
			bean = (AddressBean) getIntent().getExtras().getSerializable("bean");
			//LogUtil.e(bean.id+"地址ID");
			etAddName.setText(bean.shname);
			etAddName.setSelection(bean.shname.length());
			etAddNumber.setText(bean.shphon);
			etAddNumber.setSelection(bean.shphon.length());
			if (bean.shsex==0){
				rgAddAddress.check(R.id.rb_add_nan);
			}else{
				rgAddAddress.check(R.id.rb_add_nv);
			}
			if(bean.ismoren==1){
				cbAddMoren.setChecked(true);
			}else{
				cbAddMoren.setChecked(false);
			}
			tvAddLocation.setText(bean.dwaddre);
			etAddAddress2.setText(bean.xxaddre);
			etAddAddress2.setSelection(bean.xxaddre.length());
			tvAddDelete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					showIfDeleDialog();
				}
			});
		}
	}

	private void showIfDeleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定删除这条地址？");
		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				showWaitDialog();
				String dele = AppConstants.SERVE_URL+"index/vip/deladdre";
				HashMap<String, String> params = new HashMap<>();
				params.put("did",bean.id+"");
				LogUtil.e(bean.id+"");
				try {
					OkHttpUtil.postString(dele, params, new Callback() {
						@Override
						public void onFailure(Call call, IOException e) {
							handler.sendEmptyMessage(4);
						}

						@Override
						public void onResponse(Call call, Response response) throws IOException {
							String result = OkHttpUtil.getResult(response);
							if(result!=null){
								try {
									JSONObject jsonObject = new JSONObject(result);
									int status = jsonObject.getInt("status");
									if(status==1){
										handler.sendEmptyMessageDelayed(3,500);
									}else{
										handler.sendEmptyMessageDelayed(4,500);
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
		});
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			// requestCode即所声明的权限获取码，在checkSelfPermission时传入
			case 100:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获取到权限，作相应处理
					Intent intent = new Intent(AddAdressActivity.this, ReadContactActivity.class);
					startActivityForResult(intent,0);

				} else {
					// 没有获取到权限，做特殊处理
					ToastHelper.getInstance().displayToastShort("请手动添加联系人");
				}
				break;

		}
	}

	@Override
	protected void initEvent() {
		Linear_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AddAdressActivity.this, Location.class);
				startActivityForResult(intent,0);
			}
		});
		rlAddContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= 23) {
					int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
					int hasReadContactsCountPermission = checkSelfPermission( Manifest.permission.GET_ACCOUNTS);

					if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
						requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 100);
						return;
					}
					Intent intent = new Intent(AddAdressActivity.this, ReadContactActivity.class);
					startActivityForResult(intent,0);
				}else{
					Intent intent = new Intent(AddAdressActivity.this, ReadContactActivity.class);
					startActivityForResult(intent,0);
				}
			}
		});


		tvAddSave.setOnClickListener(this);
		rgAddAddress.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
				switch (i){
					case R.id.rb_add_nan:
						yhsex=0;
						break;
					case R.id.rb_add_nv:
						yhsex=1;
						break;
				}
			}
		});
		cbAddMoren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(b){
					moren =1;
				}else{
					moren =0;
				}
			}
		});
		ibAddAdres.setOnClickListener(this);
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()){
			case R.id.tv_add_save:
				save();
				break;
			case R.id.back_add_adres:
				setResult(1);
				finish();
				break;
		}
	}


	private void save() {
		String name = etAddName.getText().toString().trim();
		String phone = etAddNumber.getText().toString().trim();
		String addres1 = tvAddLocation.getText().toString().trim();
		String addres2 = etAddAddress2.getText().toString().trim();
		if(name.length()<1){
			ToastHelper.getInstance().displayToastShort("名字不能小于一个汉字");
			return;
		}
		if(!REGutil.checkCellphone(phone)){
			ToastHelper.getInstance().displayToastShort("手机号好像输错了，检查一下吧");
			return;
		}
		if(TextUtils.isEmpty(addres1) || addres1.equals("点击定位地址 或小区")){
			ToastHelper.getInstance().displayToastShort("请定位小区");
			return;
		}
		if(TextUtils.isEmpty(addres2)){
			ToastHelper.getInstance().displayToastShort("请填写详细的地址");
			return;
		}


		String loginData = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
		String id ="";
		try {
			JSONObject jsonObject = new JSONObject(loginData);
			id = jsonObject.getString("uid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String addAddress="";
		HashMap<String, String> params = new HashMap<>();
		params.put("shphon",phone);
		params.put("dwaddre",addres1);
		params.put("xxaddre",addres2);
		params.put("shname",name);
		if(rgAddAddress.getCheckedRadioButtonId()==R.id.rb_add_nan){
			yhsex = 0;
		}else {
			yhsex = 1;
		}
		if (cbAddMoren.isChecked()){
			moren =1;
		}else{
			moren = 0;
		}
		params.put("shsex",yhsex+"");
		params.put("ismoren",moren+"");
		if(type.equals(MineAddressActivity.ADDRESS_ADD)){
			addAddress= AppConstants.SERVE_URL+"index/vip/shadre";
			params.put("id",id);
		}else{
			LogUtil.e("修改地址==========");
			addAddress= AppConstants.SERVE_URL+"index/vip/editaddre";
			params.put("uid",id);
			params.put("did",bean.id+"");
		}
		showWaitDialog();
		//LogUtil.e(addAddress);
		try {
			OkHttpUtil.postString(addAddress, params, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					handler.sendEmptyMessage(0);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String result = OkHttpUtil.getResult(response);
					LogUtil.e("添加地址======"+result);
					if(result!=null){
						try {
							JSONObject jsonObject = new JSONObject(result);

							int state = jsonObject.getInt("status");
							if(state==1){
								if(type.equals(MineAddressActivity.ADDRESS_ADD)){
									handler.sendEmptyMessageDelayed(1,500);
								}else{
									handler.sendEmptyMessageDelayed(2,500);
								}

							}else{
								handler.sendEmptyMessageDelayed(0,500);
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

	private void showWaitDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(R.style.MaterialDialog);
		progressDialog.setMessage("正在提交..");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==0){
			String location = data.getStringExtra("location");
			if(!TextUtils.isEmpty(location)) {
				String[] split = location.split(":");
				if(split.length>2){
					tvAddLocation.setText(split[1]+split[0]);
				}else{
					tvAddLocation.setText(split[0]);
				}
			}
		}else if (resultCode==1){
			String phone = data.getStringExtra("phone");
			if(!TextUtils.isEmpty(phone))
				etAddNumber.setText(phone.split(":")[1]);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(1);
		finish();
	}
}
