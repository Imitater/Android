package com.ruiyihong.toyshop.adapter;



import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;

/**
 * Created by 81521 on 2017/7/7.
 */

public class HomeRvMoudleAdpater extends RecyclerView.Adapter<HomeRvMoudleAdpater.MyViewHolder> {

    private HomeRvAdapter.onRecyclerViewItemClickListener itemClickListener;
    private final Context context;
    private final int[] images;
    private final String[] strs;

    public HomeRvMoudleAdpater(Context context, int[] images, String[] strs) {
        this.context = context;
        this.images = images;
        this.strs = strs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_rv_module_item, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.iv_module.setImageResource(images[position]);
        holder.tv_module.setText(strs[position]);

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
        return images.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_module;
        private final TextView tv_module;


        public MyViewHolder(View itemView) {
            super(itemView);
            iv_module = itemView.findViewById(R.id.iv_module);
            tv_module = itemView.findViewById(R.id.tv_module);


        }
    }

    public void setOnItemClickListener(HomeRvAdapter.onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;

    }

    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v,int position);
    }

}
