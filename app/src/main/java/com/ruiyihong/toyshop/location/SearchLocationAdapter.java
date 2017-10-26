/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.ruiyihong.toyshop.R;

import java.util.List;

/**
 * Created by shen on 2015/8/27.
 */
public class SearchLocationAdapter extends BaseAdapter {
    private Context context;
    private List<PoiInfo> list;
    private LayoutInflater inflater;
    public SearchLocationAdapter(Context context, List<PoiInfo> list){
        this.context=context;
        this.list=list;
        inflater= LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null) {
            convertView = inflater.inflate(R.layout.item_serach_info, null);
            viewHolder=new ViewHolder();
            viewHolder.tvName=(TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvAddress=(TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(list.get(position).name);
        viewHolder.tvAddress.setText(list.get(position).address);
        return convertView;
    }

    static class ViewHolder{
        TextView tvName;
        TextView tvAddress;
    }
}
