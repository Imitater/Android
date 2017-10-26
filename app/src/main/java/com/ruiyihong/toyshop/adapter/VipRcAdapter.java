package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;

/**
 * Created by 李晓曼 on 2017/7/26.
 */

public class VipRcAdapter extends RecyclerView.Adapter<VipRcAdapter.MyViewHolder> {

    private final int[] images;
    private final Context context;
    private final int[] bgImages;
    private onRecyclerViewItemClickListener itemClickListener;

    public VipRcAdapter(Context context, int[] images,int[] bgImages){
        this.context = context;
        this.images = images;
        this.bgImages = bgImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_vip,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int i) {
        viewHolder.iv_bg_vip.setImageResource(images[i]);
        viewHolder.tv_zhuce.setBackgroundResource(bgImages[i]);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener!=null){
                    itemClickListener.onItemClick(view,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_bg_vip;
        private final TextView tv_zhuce;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_bg_vip = itemView.findViewById(R.id.iv_bg_vip);
            tv_zhuce = itemView.findViewById(R.id.tv_zhuce);
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
