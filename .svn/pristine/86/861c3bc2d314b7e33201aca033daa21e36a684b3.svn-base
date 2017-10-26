package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.JifenDaijinquanBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/11.
 */

public class JifenDuihuanDjqRvAdapter extends RecyclerView.Adapter<JifenDuihuanDjqRvAdapter.MyViewHolder> {

    private final Context context;
    private List list;
    private OnItemClickListener itemListener;

    public JifenDuihuanDjqRvAdapter(Context context, List list){
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_duihuan_djq,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int i) {
        JifenDaijinquanBean.DataBean bean = (JifenDaijinquanBean.DataBean) list.get(i);
        Picasso.with(context).load(AppConstants.IMG_BASE_URL+bean.getDimg()).placeholder(R.mipmap.djq_5).into(viewHolder.iv_djp);
        viewHolder.tv_lbb.setText(bean.getDluo()+"");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener != null){
                    itemListener.itemClickListener(view,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_djp;
        private final TextView tv_lbb;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_djp = itemView.findViewById(R.id.iv_item_djp);
            tv_lbb = itemView.findViewById(R.id.tv_item_lbb);

        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemListener = listener;
    }

    public interface OnItemClickListener{
        void itemClickListener(View v,int position);
    }

    public void setList(List list){
        this.list = list;
    }
}
