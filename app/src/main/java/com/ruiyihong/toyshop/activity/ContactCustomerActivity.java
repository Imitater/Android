package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.mine.CustomerBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/8/10 0010 .
 */

public class ContactCustomerActivity extends BaseActivity {
	private static final int MSG_GET_DATA = 0;
	@InjectView(R.id.rc_contact_customer)
	RecyclerView rcContactCustomer;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_GET_DATA:
					parseData((String)msg.obj);
					break;
			}
		}
	};
	private List<CustomerBean.DataBean> dataList;

	private void parseData(String json) {
		CustomerBean customerBean = GsonUtil.parseJsonWithGson(json, CustomerBean.class);
		dataList = customerBean.getData();

		rcContactCustomer.setLayoutManager(new LinearLayoutManager(this));
		rcContactCustomer.setAdapter(new ContactAdapter());


	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_contact_customer;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		try {
			getDataFromNet();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void getDataFromNet() throws IOException {
		String url = AppConstants.SERVE_URL+"index/kefu/link";
		OkHttpUtil.get(url, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String result = OkHttpUtil.getResult(response);
				LogUtil.e("联系客服===="+result);
				if (!TextUtils.isEmpty(result)){
					Message msg = Message.obtain();
					msg.what = MSG_GET_DATA;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		});
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
		ButterKnife.inject(this);
	}

	class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

		@Override
		public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(ContactCustomerActivity.this).inflate(R.layout.item_rc_contact_customer, parent, false);

			return new ContactViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ContactViewHolder holder, int position) {
			// 客服电话
			holder.phone1.setText(dataList.get(position).getPhone());
			holder.phone2.setVisibility(View.GONE);
			holder.address.setText(dataList.get(position).getAddr());
			holder.phone1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					call(holder.phone1.getText().toString().trim());
				}
			});
			holder.phone2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					call(holder.phone2.getText().toString().trim());
				}
			});
			//图片
			String bgimg =AppConstants.IMG_BASE_URL+ dataList.get(position).getBgimg();
			Picasso.with(ContactCustomerActivity.this).load(bgimg).placeholder(R.mipmap.lunbo_default).error(R.mipmap.lunbo_default).into(holder.iv_icon);

		}

		@Override
		public int getItemCount() {
			return dataList.size();
		}

		class ContactViewHolder extends RecyclerView.ViewHolder{
			TextView phone1;
			TextView phone2;
			TextView address;
			ImageView iv_icon;
			public ContactViewHolder(View itemView) {
				super(itemView);
				phone1 = itemView.findViewById(R.id.tv_contact_customer_phone1);
				phone2 = itemView.findViewById(R.id.tv_contact_customer_phone2);
				address = itemView.findViewById(R.id.tv_contact_customer_item_address);
				iv_icon = itemView.findViewById(R.id.iv_contact_customer_item_icon);

			}
		}
	}

	private void call(String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
//		Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
//		startActivity(intent);
	}
}
