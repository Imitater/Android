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

public class DetailInfo3RvAdapter extends RecyclerView.Adapter<DetailInfo3RvAdapter.MyViewHolder> {
    private final Context context;
    private final int[][] images;

    public DetailInfo3RvAdapter(Context context, int[][] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_info3_rv_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv_item1.setImageResouce(images[position][0]);
        holder.iv_item2.setImageResouce(images[position][1]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final MyImageView iv_item1;
        private final MyImageView iv_item2;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_item1 = itemView.findViewById(R.id.iv_detail_info3_rv_item1);
            iv_item2 = itemView.findViewById(R.id.iv_detail_info3_rv_item2);

        }
    }
}
