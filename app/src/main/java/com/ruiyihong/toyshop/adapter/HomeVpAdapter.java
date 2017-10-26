package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ruiyihong.toyshop.R;
import com.squareup.picasso.Picasso;

/**
 * Created by 81521 on 2017/7/5.
 * 轮播图的适配器
 */

public class HomeVpAdapter extends PagerAdapter {

    private final String[] datas;
    private final Context context;
    private View.OnTouchListener listener;

    public HomeVpAdapter(String[] datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % datas.length;
        ImageView view = (ImageView) View.inflate(context, R.layout.vp_item, null);

        //Picasso 在Holder中的使用方法
        Picasso.with(context).load(datas[position]).placeholder(R.mipmap.lunbo_default).error(R.mipmap.lunbo_default).into(view);

        container.addView(view);
        if(listener!=null){
            view.setOnTouchListener(listener);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        position = position % datas.length;
        container.removeView((View) object);
    }


    public void setOnItemClickListener(View.OnTouchListener listener){
        this.listener = listener;
    }
}



/****************************废弃代码*************************************/

// private final ArrayList<HomeLunboBean> datas;

//return datas.size();


//    public HomeVpAdapter(ArrayList<HomeLunboBean> datas , Context context) {
//        this.datas = datas;
//        this.context = context;
//    }


//    view.setImageResource(datas[position]);
//        String image_url = datas.get(position).getAdimg();
//        try {
//            OkHttpUtil.get(image_url, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    InputStream is = response.body().byteStream();
//                    BitmapFactory.decodeStream(is);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }