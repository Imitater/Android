package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.JifenDuihuanBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/9.
 */

public class JifenDuihuanBookRvAdapter extends RecyclerView.Adapter<JifenDuihuanBookRvAdapter.MyViewHolder> {
    private final Context context;
    private List list;
    private OnItemClickListener itemListener;

    public JifenDuihuanBookRvAdapter(Context context, List list){
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_jifen_duihuan,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int i) {
        JifenDuihuanBean.DataBean bean = (JifenDuihuanBean.DataBean) list.get(i);
        Picasso.with(context).load(AppConstants.IMG_BASE_URL+bean.getShopimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(viewHolder.iv_product);
        viewHolder.tv_name.setText(bean.getShoptext());
        viewHolder.tv_price.setText("租赁价："+bean.getShopprice()+"/天");
        viewHolder.tv_time.setText("时长："+bean.getLongtime());
        viewHolder.tv_jifen.setText(bean.getLbb()+"");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener != null){
                    itemListener.itemClick(view,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_product;
        private final TextView tv_name;
        private final TextView tv_price;
        private final TextView tv_time;
        private final TextView tv_jifen;
        private final LinearLayout ll_duihuan;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_name = itemView.findViewById(R.id.tv_jifen_product_name);
            tv_price = itemView.findViewById(R.id.tv_jifen_product_price);
            tv_time = itemView.findViewById(R.id.tv_jifen_product_time);
            tv_jifen = itemView.findViewById(R.id.tv_jifen_product_jifen);
            ll_duihuan = itemView.findViewById(R.id.ll_jifen_product_duihuan);


            int width = ScreenUtil.getScreenWidth(context) / 3;
            ViewGroup.LayoutParams layoutParams = iv_product.getLayoutParams();
        //    layoutParams.width = width;
            layoutParams.height = width;
            iv_product.setLayoutParams(layoutParams);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemListener = listener;
    }

    public interface OnItemClickListener{
        void itemClick(View v,int position);
    }

    public void setList(List list){
        this.list = list;
    }
}
