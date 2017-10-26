package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.view.MyImageView;

import java.util.List;

/**
 * Created by 81521 on 2017/7/5.
 */

public class YouhuiRvAdapter extends RecyclerView.Adapter<YouhuiRvAdapter.MyViewHolder> {
    private final Context context;
    private final List list;

    public YouhuiRvAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lv_youhui, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_money;
        private final TextView tv_time;
        private final TextView tv_time_format;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_money = itemView.findViewById(R.id.tv_item_youhui_money);
            tv_time = itemView.findViewById(R.id.tv_item_youhui_time);
            tv_time_format = itemView.findViewById(R.id.tv_item_youhui_time_format);
        }
    }
}
