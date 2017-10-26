package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.LimitedTimeActivity;
import com.ruiyihong.toyshop.bean.EventYouhuiBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/8.
 */

public class LimitedTimeRvAdapter extends RecyclerView.Adapter<LimitedTimeRvAdapter.MyViewHolder> {
    private final Context context;
    private List list;
    private onRecyclerViewItemClickListener itemClickListener;
    private onRecyclerViewItemShopCartClickListener itemShopCartClickListener;

    public LimitedTimeRvAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event_xsyh,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int i) {
        EventYouhuiBean.DataBean dataBean = (EventYouhuiBean.DataBean) list.get(i);
        viewHolder.tv_name.setText(dataBean.getName());
        viewHolder.tv_dpj.setText(dataBean.getDpj()+"/天");
        viewHolder.tv_price.setText(dataBean.getYhprice()+"元");
        Picasso.with(context).load(AppConstants.IMG_BASE_URL+dataBean.getShopimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(viewHolder.iv_product);
        if (dataBean.getIsnew() == 0){
            //不是新品
            viewHolder.iv_new.setVisibility(View.GONE);
        }else{
            //是新品
            viewHolder.iv_new.setVisibility(View.VISIBLE);
        }
        //条目
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null){
                    itemClickListener.onItemClick(view,i);
                }
            }
        });

        //购物车
        viewHolder.iv_shopcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemShopCartClickListener.onItemClick(view,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_product;
        private final ImageView iv_new;
        private final ImageView iv_qiang;
        private final RelativeLayout rl_item;
        private final TextView tv_name;
        private final TextView tv_price;
        private final TextView tv_dpj;
        private final ImageView iv_shopcar;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product_pic);
            iv_new = itemView.findViewById(R.id.iv_new);
            iv_qiang = itemView.findViewById(R.id.iv_qiang);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_name = itemView.findViewById(R.id.tv_product_name);
            tv_price = itemView.findViewById(R.id.tv_product_price);
            tv_dpj = itemView.findViewById(R.id.tv_product_dpj);
            iv_shopcar = itemView.findViewById(R.id.iv_item_shopcar);


            int screenWidth = ScreenUtil.getScreenWidth(context);
            int width = screenWidth/2;
            int px = ScreenUtil.dp2px(context, 10);
            width -= px;
            //image
            rl_item.measure(0,0);
            ViewGroup.LayoutParams layoutParams = iv_product.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            iv_product.setLayoutParams(layoutParams);


        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;

    }


    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v,int position);
    }

    public void setOnItemShopCartClickListener(onRecyclerViewItemShopCartClickListener listener) {
        this.itemShopCartClickListener = listener;

    }


    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemShopCartClickListener {

        void onItemClick(View v,int position);
    }

    //改变list
    public void setList(List list){
        this.list = list;
    }
}
