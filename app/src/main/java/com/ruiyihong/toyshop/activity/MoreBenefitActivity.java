package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.EventMoreBenefitBean;
import com.ruiyihong.toyshop.adapter.MoreBenefitRvAdapter;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.DividerItemDecoration;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;
import com.ruiyihong.toyshop.view.MyImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/10/21 0021.
 */

public class MoreBenefitActivity extends BaseActivity {
    public static final int TYPE_BENEFIT = 0;
    public static final int TYPE_OUT = 1;
    public static final int TYPE_QINZI = 2;
    private static final String BENEFIT_BUFFER_MORE_LIST = "buffer_more_benefit_list";
    private static final String OUT_BUFFER_MORE_LIST = "buffer_more_out_list";
    private static final String QINZI_BUFFER_MORE_LIST = "buffer_more_qinzi_list";
    @InjectView(R.id.rv_more_benefit)
    RecyclerView mRvMoreBenefit;
    @InjectView(R.id.iv)
    MyImageView mIv;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    private static final int MSG_MORE_BENEFIT = 1;
    private static final int MSG_LOAD_MORE = 2;
    private static final int MSG_REFRESH = 3;
    private static final int MSG_FAILURE = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    private static final int NetWorkError = 7;
    private int page = 1;
    private List<EventMoreBenefitBean.DataBean> dataList;
    private List<EventMoreBenefitBean.DataBean> refreshList;
    private List<EventMoreBenefitBean.DataBean> upLoadList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MORE_BENEFIT:
                    //解析更多公益活动列表
                    switch (mClass){
                        case TYPE_BENEFIT:
                            SPUtil.setString(MoreBenefitActivity.this, BENEFIT_BUFFER_MORE_LIST, (String) msg.obj);
                            break;
                        case TYPE_OUT:
                            SPUtil.setString(MoreBenefitActivity.this, OUT_BUFFER_MORE_LIST, (String) msg.obj);
                            break;
                        case TYPE_QINZI:
                            SPUtil.setString(MoreBenefitActivity.this, QINZI_BUFFER_MORE_LIST, (String) msg.obj);
                            break;
                    }
                    parseBenefitData((String) msg.obj);
                    break;
                case MSG_LOAD_MORE:
                    //上拉加载
                    loadMore((String) msg.obj);
                    break;
                case MSG_REFRESH:
                    //下拉刷新
                    refreshMore((String) msg.obj);

                    break;
                case MSG_FAILURE:
                    //访问网络失败
                    ToastHelper.getInstance().displayToastShort("访问网络失败");
                    int type = (int) msg.obj;
                    if (type == MSG_LOAD_MORE || type == MSG_REFRESH) {
                        mSmartRefreshLayout.finishRefresh(0);
                    }
                    break;
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;
                case CloseLoadingView:     //关闭Loading动画
                    if (loadingView != null)
                        loadingView.loadSuccess(false);
                    break;
                case PageLoading:       //页面加载中动画
                    if (loadingView != null)
                        loadingView.load();
                    break;

            }
        }
    };
    private MoreBenefitRvAdapter rvAdapter;
    private int mClass;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_benefit;
    }

    @Override
    protected void initView() {
        mIv.setFocusableInTouchMode(true);
        mIv.requestFocus();
    }

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }

        page = 1;
        String buffer = "";
        switch (mClass){
            case TYPE_BENEFIT:
                buffer = SPUtil.getString(MoreBenefitActivity.this, BENEFIT_BUFFER_MORE_LIST, "");
                break;
            case TYPE_OUT:
                buffer = SPUtil.getString(MoreBenefitActivity.this, OUT_BUFFER_MORE_LIST, "");
                break;
            case TYPE_QINZI:
                buffer = SPUtil.getString(MoreBenefitActivity.this, QINZI_BUFFER_MORE_LIST, "");
                break;
        }
        if (!TextUtils.isEmpty(buffer)) {
            CloseLoadingView();
            parseBenefitData(buffer);
        }
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            mClass = getIntent().getIntExtra("class", -1);
            HashMap<String, Object> map = new HashMap<>();
            map.put("class",mClass);
            postNet(AppConstants.EVENT_MORE_BENEFIT, map, MSG_MORE_BENEFIT);
        } else {
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }


    }
    private void postNet(String url, Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg = Message.obtain();
                    msg.what = MSG_FAILURE;
                    msg.obj = type;
                    handler.sendMessage(msg);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(result)) {
                        Log.e("radish", "response------------------" + result);
                        CloseLoadingView();
                        Message msg = Message.obtain();
                        msg.what = type;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(NetWorkError);
            e.printStackTrace();
        }
    }
    private void parseBenefitData(String data){
        //todo 解析data
        final EventMoreBenefitBean dataList = GsonUtil.parseJsonWithGson(data, EventMoreBenefitBean.class);
        mRvMoreBenefit.setLayoutManager(new FullyLinearLayoutManager(this));
        rvAdapter = new MoreBenefitRvAdapter(this, dataList.getData());
        mRvMoreBenefit.setAdapter(rvAdapter);
        mRvMoreBenefit.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, 1, getResources().getColor(R.color.divider_line)));
        rvAdapter.setOnItemClickListener(new MoreBenefitRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MoreBenefitActivity.this, BenefitActivity.class);
                intent.putExtra("id",dataList.getData().get(position).getId());
                startActivity(intent);
            }
        });
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }
    private void refreshMore(String obj) {
        switch (mClass){
            case TYPE_BENEFIT:
                SPUtil.setString(MoreBenefitActivity.this, BENEFIT_BUFFER_MORE_LIST, obj);
                break;
            case TYPE_OUT:
                SPUtil.setString(MoreBenefitActivity.this, OUT_BUFFER_MORE_LIST, obj);
                break;
            case TYPE_QINZI:
                SPUtil.setString(MoreBenefitActivity.this, QINZI_BUFFER_MORE_LIST, obj);
                break;
        }
        EventMoreBenefitBean eventMoreBenefitBean = GsonUtil.parseJsonWithGson(obj, EventMoreBenefitBean.class);
        refreshList = eventMoreBenefitBean.getData();
        if (refreshList != null && refreshList.size() > 0 && rvAdapter != null) {
            dataList = refreshList;
            if (upLoadList != null && upLoadList.size() != 0) {
                dataList.addAll(upLoadList);
            }
            rvAdapter.setList(dataList);
            rvAdapter.notifyDataSetChanged();
        }
        mSmartRefreshLayout.finishRefresh(0);
    }

    private void loadMore(String obj) {
        EventMoreBenefitBean eventMoreBenefitBean = GsonUtil.parseJsonWithGson(obj, EventMoreBenefitBean.class);
        upLoadList = eventMoreBenefitBean.getData();
        if (upLoadList != null && upLoadList.size() > 0 && rvAdapter != null) {
            dataList.addAll(upLoadList);
            rvAdapter.setList(dataList);
            rvAdapter.notifyDataSetChanged();
        } else {
            ToastHelper.getInstance().displayToastShort("暂无更多数据");
        }
        mSmartRefreshLayout.finishLoadmore(0);
    }
    @Override
    protected void initEvent() {

        setView();
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
    }
    private void setView() {
        //上拉，下拉设置
        mSmartRefreshLayout.setOnMultiPurposeListener(new Refresh_Listener());
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this));
        //下拉刷新监听
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Log.i("radish", "onRefresh------------------");
                if (NetWorkUtil.isNetWorkAvailable(MoreBenefitActivity.this)) {
                    //下拉刷新
                    Map<String, Object> map = new HashMap();
                    map.put("class",mClass);
                    map.put("page",page++);
                    postNet(AppConstants.EVENT_MORE_BENEFIT_UPLOAD, map, MSG_REFRESH);

                } else {
                    mSmartRefreshLayout.finishRefresh(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }

            }
        });
        //上拉加载监听
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Log.e("radish", "onLoadmore------------------");
                if (NetWorkUtil.isNetWorkAvailable(MoreBenefitActivity.this)) {
                    //上拉加载
                    Map<String, Object> map = new HashMap();
                    map.put("page", page++);
                    postNet(AppConstants.EVENT_MORE_YOUHUI_UPLOAD, map, MSG_LOAD_MORE);

                } else {
                    mSmartRefreshLayout.finishLoadmore(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
