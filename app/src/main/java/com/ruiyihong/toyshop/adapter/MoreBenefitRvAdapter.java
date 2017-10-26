package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21 0021.
 */

public class MoreBenefitRvAdapter extends RecyclerView.Adapter<MoreBenefitRvAdapter.MyViewHolder> {

    private final Context context;
    private List<EventMoreBenefitBean.DataBean> list;
    private onRecyclerViewItemClickListener itemClickListener;

    public MoreBenefitRvAdapter(Context context, List<EventMoreBenefitBean.DataBean> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_more_benefit,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        EventMoreBenefitBean.DataBean dataBean = list.get(position);
        Picasso.with(context).load(AppConstants.IMG_BASE_URL+dataBean.getAcimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_pic);
        holder.tv_name.setText(dataBean.getTitle()+"");
        holder.tv_number.setText(dataBean.getLimit()+"");
        holder.tv_time.setText(dataBean.getStarttime()+"");
        holder.tv_person.setText(dataBean.getMain()+"");
        holder.tv_address.setText(dataBean.getWhere()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null){
                    itemClickListener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_name;
        private final TextView tv_number;
        private final TextView tv_time;
        private final TextView tv_person;
        private final TextView tv_address;
        private final ImageView iv_pic;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_benefit_name);
            tv_number = itemView.findViewById(R.id.tv_benefit_person_number);
            tv_time = itemView.findViewById(R.id.tv_benefit_time);
            tv_person = itemView.findViewById(R.id.tv_benefit_person);
            tv_address = itemView.findViewById(R.id.tv_benefit_address);
            iv_pic = itemView.findViewById(R.id.iv_benefit_pic);
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;

    }


    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v,int position);
    }

    //更新数据列表
    public void setList(List list){
        this.list = list;
    }
}
