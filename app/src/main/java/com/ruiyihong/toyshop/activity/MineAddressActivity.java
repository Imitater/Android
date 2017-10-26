package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.AddressBean;

import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MineAddressActivity extends BaseActivity {


	@InjectView(R.id.ll_address_nomore)
	LinearLayout llAddressNomore;
	@InjectView(R.id.tv_address_add)
	TextView tvAddressAdd;
	@InjectView(R.id.lv_mine_address)
	ListView lvMineAddress;
	public static final String ADDRESS_ADD = "add";
	public static final String ADDRESS_CHANGE = "change";
	public static final String MINE_ADDRESS = "mine";
	public static final String JIESUAN_ADDRESS="jiesuan";
	private ArrayList<AddressBean> addressList;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				String result = (String) msg.obj;
				lvMineAddress.setVisibility(View.VISIBLE);
				llAddressNomore.setVisibility(View.GONE);
				parseAddressData(result);
			}else if(msg.what==1){
				lvMineAddress.setVisibility(View.GONE);
				llAddressNomore.setVisibility(View.VISIBLE);
			}
		}
	};
	private String type;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_mine_address;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		type = intent.getStringExtra("type");

		//getDataFromNet();

		if(type.equals(MINE_ADDRESS)){
			getDataFromNet();
		}else if(type.equals(JIESUAN_ADDRESS)){
			LogUtil.e("结算Type   "+type);
			String data = intent.getStringExtra("data");
			if (!TextUtils.isEmpty(data)) {
				lvMineAddress.setVisibility(View.VISIBLE);
				llAddressNomore.setVisibility(View.GONE);
				parseAddressData(data);
			}
		}

	}

	private void getDataFromNet() {
		String getAddress = AppConstants.SERVE_URL+"index/vip/allshinfo";
		String loginData = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
		HashMap<String, String> params = new HashMap<>();
		try {
			JSONObject jsonObject = new JSONObject(loginData);
			int id =jsonObject.getInt("uid");
			params.put("id",id+"");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			OkHttpUtil.postString(getAddress, params, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String result = OkHttpUtil.getResult(response);

					if (result!=null){
						if (result.contains("status")){
							handler.sendEmptyMessage(1);
						}else {
							Message obtain = Message.obtain();
							obtain.what=0;
							obtain.obj = result;
							handler.sendMessage(obtain);
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseAddressData(String result) {


		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<AddressBean>>() {
		}.getType();
		addressList = gson.fromJson(result, type);
		lvMineAddress.setAdapter(new AddressAdapter());

		lvMineAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				ToastHelper.getInstance().displayToastShort(i+"");
			}
		});
	}

	@Override
	protected void initEvent() {
		tvAddressAdd.setOnClickListener(this);
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()){
			case R.id.tv_address_add:
				Intent intent = new Intent(this,AddAdressActivity.class);
				intent.putExtra("type",ADDRESS_ADD);
				startActivityForResult(intent,0);
				break;
		}
	}


	class AddressAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if(view==null){
				holder= new ViewHolder();
				view = View.inflate(MineAddressActivity.this,R.layout.address_item,null);
				holder.tv_add_item_name = view.findViewById(R.id.tv_add_item_name);
				holder.tv_add_item_sex = view.findViewById(R.id.tv_add_item_sex);
				holder.tv_add_item_phone = view.findViewById(R.id.tv_add_item_phone);
				holder.tv_add_item_moren = view.findViewById(R.id.tv_add_item_moren);
				holder.tv_add_item_content = view.findViewById(R.id.tv_add_item_content);
				holder.ib_add_item_change = view.findViewById(R.id.ib_add_item_change);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			if (type.equals(JIESUAN_ADDRESS)){
				holder.ib_add_item_change.setVisibility(View.INVISIBLE);
			}
			final AddressBean addressBean = addressList.get(i);
			holder.tv_add_item_name.setText(addressBean.shname);
			holder.tv_add_item_phone.setText(addressBean.shphon);
			if(addressBean.shsex==1){
				holder.tv_add_item_sex.setText("女士");
			}else{
				holder.tv_add_item_sex.setText("先生");
			}
			if(addressBean.ismoren==1){
				holder.tv_add_item_moren.setVisibility(View.VISIBLE);
			}else{
				holder.tv_add_item_moren.setVisibility(View.GONE);
			}
			holder.tv_add_item_content.setText(addressBean.dwaddre+addressBean.xxaddre);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (type.equals(JIESUAN_ADDRESS)){
						AddressBean addressBean = addressList.get(i);
						Intent intent=new Intent();
						intent.putExtra(JIESUAN_ADDRESS,new Gson().toJson(addressBean));
						setResult(RESULT_OK,intent);
						finish();
					}else{
						Intent intent = new Intent(MineAddressActivity.this, AddAdressActivity.class);
						intent.putExtra("type",MineAddressActivity.ADDRESS_CHANGE);
						Bundle bundle = new Bundle();
						bundle.putSerializable("bean",addressBean);
						intent.putExtras(bundle);
						startActivityForResult(intent,0);
					}
				}
			});
			return view;
		}
		class ViewHolder{
			TextView tv_add_item_name;
			TextView tv_add_item_sex;
			TextView tv_add_item_phone;
			TextView tv_add_item_moren;
			TextView tv_add_item_content;
			ImageButton ib_add_item_change;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==0){
			getDataFromNet();
		}
	}

}
