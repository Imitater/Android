package com.ruiyihong.toyshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.EnterVideoBean;
import com.ruiyihong.toyshop.util.StringUtil;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/6.
 */

public class VideoListRvAdapter extends RecyclerView.Adapter<VideoListRvAdapter.MyViewHolder> {

    private final Activity context;
    private final List<EnterVideoBean> list;
    private OnItemClickListener listener;
    private final int screenWidth;
    private final int screenHeight;

    public VideoListRvAdapter(Activity context, List list){
        this.context = context;
        this.list = list;

        // 屏幕宽（像素，如：480px）
        screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        // 屏幕高（像素，如：800p）
        screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_video,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String thumbUri = list.get(position).getThumbPath();
        Log.i("radish","thumbUri------------------"+thumbUri );
        if (!TextUtils.isEmpty(thumbUri)) {
            holder.iv_bg.setImageURI(Uri.parse(thumbUri));
        }else{
            holder.iv_bg.setImageResource(R.mipmap.video_player);
        }
        Log.i("radish","duration------------------"+list.get(position).getDuration() );
        String time = StringUtil.formatDuration(list.get(position).getDuration());
        holder.tv_time.setText(time+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_bg;
        private final TextView tv_time;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_bg = itemView.findViewById(R.id.iv_bg);
            tv_time = itemView.findViewById(R.id.tv_time);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_bg.getLayoutParams();
            layoutParams.height = screenWidth/3;
            iv_bg.setLayoutParams(layoutParams);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
}
