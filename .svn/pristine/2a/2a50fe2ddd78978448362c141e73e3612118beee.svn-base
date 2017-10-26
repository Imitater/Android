package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.view.MyImageView;

/**
 * Created by 81521 on 2017/7/5.
 */

public class DetailInfoRvAdapter extends RecyclerView.Adapter<DetailInfoRvAdapter.MyViewHolder> {
    private final Context context;
    private final int images;
    private final String[] strs;

    public DetailInfoRvAdapter(Context context, int images, String[] strs) {
        this.context = context;
        this.images = images;
        this.strs = strs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_info1_rv_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv_item.setBackgroundResource(images);
        holder.tv_item.setText(strs[position]);
    }

    @Override
    public int getItemCount() {
        return strs.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_item;
        private final TextView tv_item;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_item = itemView.findViewById(R.id.iv_detail_info1_rv_item);
            tv_item = itemView.findViewById(R.id.tv_detail_info1_rv_item);

        }
    }
}
