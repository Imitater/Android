package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.EventYouhuiBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/7/17.
 */

public class EventYouhuiRvAdapter extends RecyclerView.Adapter<EventYouhuiRvAdapter.MyViewHolder  > {
    private final List<EventYouhuiBean.DataBean> list;
    private final Context context;
    private onRecyclerViewItemClickListener itemClickListener;

    public EventYouhuiRvAdapter(Context context, List<EventYouhuiBean.DataBean> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_event_youhui,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        EventYouhuiBean.DataBean bean = list.get(position);
        Picasso.with(context).load(bean.getShopimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).fit().into(holder.iv_item);
        holder.tv_item.setText(bean.getYhprice()+"元/天");

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
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_item;
        private final TextView tv_item;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_item = itemView.findViewById(R.id.iv_item_event_youhui);
            tv_item = itemView.findViewById(R.id.tv_item_event_youhui);
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
