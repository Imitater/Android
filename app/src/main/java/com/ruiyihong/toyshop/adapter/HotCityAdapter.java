package com.ruiyihong.toyshop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;


/**
 * Created by 81521 on 2017/7/3.
 */

public class HotCityAdapter  extends BaseAdapter{

    private final String[] citys;
    private final Context context;

    public HotCityAdapter(String[] citys, Context context) {
        this.citys = citys;
        this.context = context;
    }

    @Override
    public int getCount() {
        return citys.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if (view==null){
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.city_list_item,null);
            holder.tv_city = view.findViewById(R.id.tv_city);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_city.setText(citys[i]);
        return view;
    }
    class ViewHolder{
        TextView tv_city;

    }
}
