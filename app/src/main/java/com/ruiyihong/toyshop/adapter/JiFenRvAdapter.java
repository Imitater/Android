package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.JifenHotDjqBean;
import com.ruiyihong.toyshop.bean.JifenHotProductBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/3.
 */

public class JiFenRvAdapter extends RecyclerView.Adapter<JiFenRvAdapter.MyViewHolder> {

    private final Context context;
    private final List list;
    public static final int TYPE_DJQ = 0;
    public static final int TYPE_PRODUCT = 1;
    private final int type;
    private OnItemClickListener mListener;

    public JiFenRvAdapter(Context context, List list ,int type){
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_jifen,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (type == TYPE_DJQ){
            JifenHotDjqBean.DataBean data1Bean = (JifenHotDjqBean.DataBean) list.get(position);
            Picasso.with(context).load(AppConstants.IMG_BASE_URL+data1Bean.getDimg()).placeholder(R.mipmap.djq_5).into(holder.iv_hot);
            holder.tv_info.setVisibility(View.GONE);
            holder.tv_money.setText(data1Bean.getDluo()+"萝卜币");
        }else{
            JifenHotProductBean.DataBean data2Bean = (JifenHotProductBean.DataBean) list.get(position);
            Picasso.with(context).load(AppConstants.IMG_BASE_URL+data2Bean.getShopimg()).placeholder(R.mipmap.good_default).fit().into(holder.iv_hot);
            holder.tv_info.setVisibility(View.VISIBLE);
            holder.tv_info.setText("免费租赁"+data2Bean.getLongtime());
            holder.tv_money.setText(data2Bean.getLbb()+"萝卜币");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null){
                    mListener.onItemClick(view,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (list!=null) {
            return list.size();
        }else{
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_hot;
        private final TextView tv_info;
        private final TextView tv_money;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_hot = itemView.findViewById(R.id.iv_hot);
            tv_info = itemView.findViewById(R.id.tv_info);
            tv_money = itemView.findViewById(R.id.tv_money);

            if (type == TYPE_PRODUCT) {
                int width = ScreenUtil.getScreenWidth(context) / 3;
                ViewGroup.LayoutParams layoutParams = iv_hot.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = width;
                iv_hot.setLayoutParams(layoutParams);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}
