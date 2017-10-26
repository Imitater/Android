package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.PopBrandBean;

import java.util.ArrayList;

/**
 * Created by 81521 on 2017/7/10.
 */

public class ToyshopPopPayAdapter extends RecyclerView.Adapter<ToyshopPopPayAdapter.MyViewHolder> {

    private final Context context;
    private final String[] list;
    private onRecyclerViewItemClickListener itemClickListener;
    private int currentPosition = -1;

    public ToyshopPopPayAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_rv_pay_item, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv.setText(list[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_pop_item);

        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener =  listener;

    }

    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v,int position);
    }

    /**
     * 设置当前点击的条目position的方法
     */
    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
    }
}
