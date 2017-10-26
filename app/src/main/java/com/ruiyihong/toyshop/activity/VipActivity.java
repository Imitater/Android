package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.VipRcAdapter;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.StatusBarUtil;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 李晓曼嘻嘻嘻 on 2017/7/18.
 * vip
 */

public class VipActivity extends BaseActivity {
    @InjectView(R.id.rv_vip)
    RecyclerView mRvVip;
    private final int[] images = {R.mipmap.vip_pt, R.mipmap.vip_bj, R.mipmap.vip_yz, R.mipmap.vip_jz, R.mipmap.vip_top};
    private final int[] bgImages = {R.drawable.event_bg_circle1, R.drawable.event_bg_circle4, R.drawable.event_bg_circle3, R.drawable.event_bg_circle1, R.drawable.event_bg_circle2};
    @InjectView(R.id.iv_pic)
    ImageView mIvPic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip;
    }

    @Override
    protected void initView() {
        mIvPic.setFocusableInTouchMode(true);
        mIvPic.requestFocus();
        initVip();
    }

    private void initVip() {

        mRvVip.setLayoutManager(new FullyLinearLayoutManager(this));
        VipRcAdapter rcAdapter = new VipRcAdapter(this, images, bgImages);
        mRvVip.setAdapter(rcAdapter);
        rcAdapter.setOnItemClickListener(new VipRcAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String user = SPUtil.getString(VipActivity.this, AppConstants.SP_LOGIN, "");
                String uname = "";
                if (!TextUtils.isEmpty(user)) {
                    try {
                        JSONObject object = new JSONObject(user);
                        uname = object.getString("uname");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(uname)) {
                    Intent intent = new Intent(VipActivity.this, LoginActivity.class);
                    intent.putExtra("type", position);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(VipActivity.this, VipQuanyiActivity.class);
                intent.putExtra("type", position);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) {

    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }
}
