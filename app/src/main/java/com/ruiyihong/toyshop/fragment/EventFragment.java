package com.ruiyihong.toyshop.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruiyihong.toyshop.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 81521 on 2017/7/16.
 * 活动页面
 */

public class EventFragment extends BaseFragment {
    @InjectView(R.id.rv_frag_event_youhui)
    RecyclerView mRvFragEventYouhui;
    @InjectView(R.id.rv_frag_event_benefit)
    RecyclerView mRvFragEventBenefit;
    @InjectView(R.id.rv_frag_event_out)
    RecyclerView mRvFragEventOut;
    @InjectView(R.id.rv_frag_event_qinzi)
    RecyclerView mRvFragEventQinzi;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_event, null);

        return view;
    }

    @Override
    protected void initData() {
        //优惠活动
        initYouhui();

        event();
    }

    private void event() {
/*
        //数据
        List<EventTestData> list = new ArrayList<>();
        EventTestData data1 = new EventTestData("1.9", R.mipmap.event_benefit_pic1, "孩子公益活动宣传环保绘画活动");
        EventTestData data2 = new EventTestData("2.9", R.mipmap.event_benefit_pic2, "孩子公益活动宣传环保绘画活动");
        EventTestData data3 = new EventTestData("4.9", R.mipmap.event_benefit_pic3, "孩子公益活动宣传环保绘画活动");
        list.add(data1);
        list.add(data2);
        list.add(data3);


        //公益活动
        initBenefit(mRvFragEventBenefit,list,R.drawable.event_bg_circle1);
        //户外活动
        initBenefit(mRvFragEventOut,list,R.drawable.event_bg_circle2);
        //亲子活动
        initBenefit(mRvFragEventQinzi,list,R.drawable.event_bg_circle1);*/
    }

    private void initBenefit(RecyclerView rv,List list,int drawable) {
/*

        //RecycleView设置

        rv.setLayoutManager(new FullyGridLayoutManager(mActivity, 3));
        rv.setAdapter(new EventRvAdapter(mActivity, list, drawable));
        rv.addItemDecoration(new DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL_LIST, 20, getResources().getColor(R.color.divider_line_tran)));
*/

    }

    private void initYouhui() {
        /*//数据
        List<EventTestData> list = new ArrayList<>();
        EventTestData data1 = new EventTestData("1.9", R.mipmap.event_youhui_pic1, "youhui1");
        EventTestData data2 = new EventTestData("2.9", R.mipmap.event_youhui_pic2, "youhui2");
        EventTestData data3 = new EventTestData("4.9", R.mipmap.event_youhui_pic3, "youhui3");
        list.add(data1);
        list.add(data2);
        list.add(data3);

        //RecycleView设置

        mRvFragEventYouhui.setLayoutManager(new FullyGridLayoutManager(mActivity, 3));
        mRvFragEventYouhui.setAdapter(new EventYouhuiRvAdapter(mActivity, list));*/
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
