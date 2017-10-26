package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.ToyShopVpAdapter;
import com.ruiyihong.toyshop.fragment.MyCollectionFragment;
import com.ruiyihong.toyshop.fragment.ToyShopBookFragment;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 李晓曼 on 2017/8/15.
 * 我的收藏
 */

public class CollectionActivity extends BaseActivity {
    @InjectView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @InjectView(R.id.iv_refresh)
    ImageView mIvRefresh;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @InjectView(R.id.vp_my_collection)
    ViewPager mVpMyCollection;
    @InjectView(R.id.rb_mycoll_kc)
    RadioButton rbMycollKc;
    @InjectView(R.id.rb_mycoll_rmtj)
    RadioButton rbMycollRmtj;
    @InjectView(R.id.rb_mycoll_rmht)
    RadioButton rbMycollRmht;
    @InjectView(R.id.rg_title)
    RadioGroup rgTitle;
    @InjectView(R.id.line1)
    View line1;
    @InjectView(R.id.line2)
    View line2;
    @InjectView(R.id.line3)
    View line3;

    private ArrayList<Fragment> fragments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        //初始化
        rgTitle.check(0);
        line1.setVisibility(View.VISIBLE);

        initViewPager();

    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MyCollectionFragment fragment = new MyCollectionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type",i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        mVpMyCollection.setAdapter(new ToyShopVpAdapter(getSupportFragmentManager(), fragments) );

        mVpMyCollection.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当页面选中的时候，切换顶部的页签的选中状态
                switch (position){
                    case 0:
                        rgTitle.check(R.id.rb_mycoll_kc);
                        line1.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.INVISIBLE);
                        line3.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        rgTitle.check(R.id.rb_mycoll_rmtj);
                        line1.setVisibility(View.INVISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        rgTitle.check(R.id.rb_mycoll_rmht);
                        line1.setVisibility(View.INVISIBLE);
                        line2.setVisibility(View.INVISIBLE);
                        line3.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_mycoll_kc://课程
                        mVpMyCollection.setCurrentItem(0);
                        break;
                    case R.id.rb_mycoll_rmtj://推荐
                        mVpMyCollection.setCurrentItem(1);
                        break;
                    case R.id.rb_mycoll_rmht://话题
                        mVpMyCollection.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

}
