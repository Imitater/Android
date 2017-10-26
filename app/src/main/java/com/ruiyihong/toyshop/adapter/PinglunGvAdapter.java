package com.ruiyihong.toyshop.adapter;

/**
 * Created by hegeyang on 2017/8/5 0005 .
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.LogUtil;
import com.squareup.picasso.Picasso;

public class PinglunGvAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> list = new ArrayList<String>();

    public PinglunGvAdapter() {
        super();
    }

    /**
     * 获取列表数据
     *
     * @param list
     */
    public void setList(List<String> list) {
        //Log.e(" 4444 ", list.size()+"");
        //this.list.clear();
        //this.list.addAll(list);
        this.list = list;
        this.notifyDataSetChanged();
    }

    public PinglunGvAdapter(Context mContext, List<String> list) {
        super();
        this.mContext = mContext;
        this.list = list;
        //Log.e(" 2222 ", list.size()+"");
    }

    @Override
    public int getCount() {
        //Log.e("  ", list.size()+"");
        if (list == null) {
            return 1;
        } else if (list.size() == 6) {
            return 6;
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (list != null
                && list.size() == 6) {
            return list.get(position);
        } else if (list == null || position - 1 < 0
                || position > list.size()) {
            return null;
        } else {
            return list.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_publish, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.item_grid_image);
            holder.delete = convertView.findViewById(R.id.item_grid_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (parent.getChildCount() == position) {
            if (isShowAddItem(position)) {
                holder.iv.setImageResource(R.mipmap.pinglun_choosepic);
                holder.delete.setVisibility(View.INVISIBLE);
            } else {
                Picasso.with(mContext).load(new File(list.get(position))).fit().into(holder.iv);
                //holder.iv.setImageBitmap(BitmapFactory.decodeFile(list.get(position)));
                holder.delete.setVisibility(View.VISIBLE);
            }
        } else {
            //这里就是多次加载的问题，可以不用理这里面的 代码，
        }
        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = list == null ? 0 : list.size();
        return position == size;
    }

    class ViewHolder {
        ImageView iv;
        ImageView delete;
    }

}
