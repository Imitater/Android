package com.ruiyihong.toyshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.LogUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by 81521 on 2017/7/5.
 */

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvAdapter.MyViewHolder> {
    private  Activity context;
    private  int[] images=null;
    private  String[] imgurls=null;
    private  String[] strs;
    private  String[] Prices;
    private  boolean flag;
    private onRecyclerViewItemClickListener itemClickListener;
    private int screenWidth;
    private int screenHeight;

    public HomeRvAdapter(Activity context, int[] images, String[] strs, boolean flag) {
        this.context = context;
        this.images = images;
        this.strs = strs;
        this.flag = flag;
        // 屏幕宽（像素，如：480px）
        screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        // 屏幕高（像素，如：800p
        screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
    }


    public HomeRvAdapter(Activity context, String[] imgurls, String[] strs,String[] Prices,boolean flag) {
        this.context = context;
        this.imgurls = imgurls;
        this.Prices = Prices;
        this.strs = strs;
        this.flag = flag;
        // 屏幕宽（像素，如：480px）
        screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        // 屏幕高（像素，如：800p
        screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_rv_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if(images!=null){
            holder.iv_module.setImageResource(images[position]);
        }else{
            Picasso.with(context).load(imgurls[position]).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_module);
        }
        holder.tv_module.setText(strs[position]);

        if(Prices!=null){
            holder.tv_pay.setText(Prices[position]+"元/天");
        }

        if (flag){
            holder.tv_jp.setVisibility(View.VISIBLE);
            holder.tv_pay.setVisibility(View.VISIBLE);
        }else{
            holder.tv_jp.setVisibility(View.INVISIBLE);
            holder.tv_pay.setVisibility(View.GONE);
            holder.tv_temp.setVisibility(View.GONE);
        }

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
        return  strs.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private  ImageView iv_module;
        private  TextView tv_module;
        private  TextView tv_jp;
        private  TextView tv_pay;
        private  TextView tv_temp;


        public MyViewHolder(View itemView) {
            super(itemView);
            iv_module = itemView.findViewById(R.id.iv_module);
            tv_module = itemView.findViewById(R.id.tv_module);
            tv_jp = itemView.findViewById(R.id.tv_jp);
            tv_pay = itemView.findViewById(R.id.tv_pay);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            View ll = itemView.findViewById(R.id.ll);

            RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) ll.getLayoutParams();
            pa.width = screenWidth/3;
            ll.setLayoutParams(pa);
            RelativeLayout.LayoutParams tv_pa = (RelativeLayout.LayoutParams) tv_pay.getLayoutParams();
            tv_pa.width = screenWidth/3;
            tv_pay.setLayoutParams(tv_pa);

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
