package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.VipMemberBuyBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hegeyang on 2017/8/6 0006 .
 */

public class VipBuyAdapter extends RecyclerView.Adapter<VipBuyAdapter.MyViewHolder>{

	private final Context context;
	private final List<VipMemberBuyBean.DataBean> data;
	private onRecyclerViewItemClickListener itemClickListener;

	public VipBuyAdapter(Context context, List<VipMemberBuyBean.DataBean> data) {
		this.context = context;
		this.data = data;
	}


	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vip_buy,null));
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		VipMemberBuyBean.DataBean dataBean = data.get(position);
		holder.tv_vip_buy_money.setText(dataBean.vprice+"");
		holder.tv_vip_buy_types.setText(dataBean.vkind);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (itemClickListener!=null){
					itemClickListener.onItemClick(view,position);
				}
			}
		});
	}


	@Override
	public int getItemCount() {
		return data.size();
	}
	class MyViewHolder extends RecyclerView.ViewHolder{

		private final TextView tv_vip_buy_money;
		private final TextView tv_vip_buy_types;

		public MyViewHolder(View itemView) {
			super(itemView);
			tv_vip_buy_money = itemView.findViewById(R.id.tv_vip_buy_money);
			tv_vip_buy_types = itemView.findViewById(R.id.tv_vip_buy_types);
		}
	}
	public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
		this.itemClickListener = listener;

	}


	/**条目点击事件的监听器*/
	public  interface onRecyclerViewItemClickListener {

		void onItemClick(View v,int position);
	}
}
