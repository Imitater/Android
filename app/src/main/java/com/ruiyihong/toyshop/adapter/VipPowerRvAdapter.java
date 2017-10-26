package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.VipMemberDetialBean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/7/30.
 */

public class VipPowerRvAdapter extends RecyclerView.Adapter<VipPowerRvAdapter.MyViewHolder> {

    private final Context context;
    private final String[] strs;

    public VipPowerRvAdapter(Context context, String[]strs){
        this.context = context;
        this.strs = strs;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_vip_quanxian,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_item.setText(strs[position]);
    }

    @Override
    public int getItemCount() {
        return strs.length;
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_item = itemView.findViewById(R.id.tv_item_quanxian);
        }
    }
}
