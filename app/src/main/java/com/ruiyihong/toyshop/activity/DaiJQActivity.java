/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DaiJQActivity extends BaseActivity {

    @InjectView(R.id.title_iv)
    ImageView titleIv;
    @InjectView(R.id.recycle_view)
    RecyclerView recycleView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dai_jq;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        recycleView.setLayoutManager(new FullyGridLayoutManager(DaiJQActivity.this,3));
        recycleView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                        DaiJQActivity.this).inflate(R.layout.djq_item_layout, parent,
                        false));
                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 6;
            }

            class MyViewHolder extends RecyclerView.ViewHolder
            {

                ImageView iv;
                TextView tv;

                public MyViewHolder(View view)
                {
                    super(view);
                    iv = view.findViewById(R.id.iv_djq_img);
                    tv = view.findViewById(R.id.tv_lbb_num);
                }
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {

    }


}
