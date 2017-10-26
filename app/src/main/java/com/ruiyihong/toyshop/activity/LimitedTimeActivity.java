package com.ruiyihong.toyshop.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.LimitedTimeRvAdapter;
import com.ruiyihong.toyshop.bean.EventYouhuiBean;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.StringUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 123
 * Created by 李晓曼 on 2017/8/8.
 */

public class LimitedTimeActivity extends BaseActivity {
    private static final int MSG_MORE_YOUHUI = 0;
    private static final int TIME_DAOJISHI = 1;
    private static final int MSG_LOAD_MORE = 2;
    private static final int MSG_REFRESH = 3;
    private static final int MSG_FAILURE = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    private static final int NetWorkError = 7;
    private static final int MSG_SHOPPING_FINDALL = 11;
    private static final int MSG_SHOPPING_ADD = 12;
    private static final int INTENT_REQUEST_LOGIN = 13;
    private int count = -1;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private static final String BUFFER_LIMITEDTIME_YOUHUI = "buffer_limiedtime_youhui";
    @InjectView(R.id.iv_pic)
    ImageView ivPic;
    @InjectView(R.id.iv_shop_cart)
    ImageView mIvShopCart;
    @InjectView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @InjectView(R.id.tv_dd)
    TextView mTvDd;
    @InjectView(R.id.tv_hh)
    TextView mTvHh;
    @InjectView(R.id.tv_mmss)
    TextView mTvMmss;
    @InjectView(R.id.rv_xsyh)
    RecyclerView mRvXsyh;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.tv_pop_shopping_number)
    TextView mTvPopShoppingNumber;
    @InjectView(R.id.rl_parent)
    RelativeLayout rlParent;
    private int page = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MORE_YOUHUI:
                    //限时优惠列表
                    SPUtil.setString(LimitedTimeActivity.this, BUFFER_LIMITEDTIME_YOUHUI, (String) msg.obj);
                    initRvXsyh((String) msg.obj);
                case TIME_DAOJISHI:
                    //倒计时
                    DaoJiShi();
                    if (dd == 0 && hh == 0 && mm == 0) {
                        isRun = false;
                        handler.removeMessages(TIME_DAOJISHI);
                    }
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
                case MSG_SHOPPING_FINDALL://查询所有购物车信息
                    shopAllList = (List<ShppingCarHttpBean.WjlistBean>) msg.obj;
                    shppingCartSetting();
                    break;
                case MSG_SHOPPING_ADD://向购物车添加商品
                    String obj = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(obj);
                        if (object.getInt("status") == 0){
                            ToastHelper.getInstance().displayToastShort("添加购物车失败");
                        }else if(object.getInt("status") == -2){
                            ToastHelper.getInstance().displayToastShort("库存不足");

                        }else{
                            ToastHelper.getInstance().displayToastShort("添加购物车成功");
                            addShoppingSetting();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private List<EventYouhuiBean.DataBean> dataList;
    private List<EventYouhuiBean.DataBean> refreshList;
    private List<EventYouhuiBean.DataBean> upLoadList;
    private LimitedTimeRvAdapter rvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_xianshi_youhui;
    }

    @Override
    protected void initView() {
        mRlTitle.setFocusableInTouchMode(true);
        mRlTitle.requestFocus();
    }


    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }
        //回显购物车的数量
        if (count > 0) {
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");
        } else {
            mTvPopShoppingNumber.setVisibility(View.INVISIBLE);
        }

        page = 1;
        String buffer = SPUtil.getString(this, BUFFER_LIMITEDTIME_YOUHUI, "");
        if (!TextUtils.isEmpty(buffer)) {
            CloseLoadingView();
            initRvXsyh(buffer);
        }
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            postNet(AppConstants.EVENT_MORE_YOUHUI, new HashMap<String, Object>(), MSG_MORE_YOUHUI);
        } else {
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }
        if (count > 0) {
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");
        } else {
            mTvPopShoppingNumber.setVisibility(View.GONE);
            mTvPopShoppingNumber.setText(count + "");
        }
        findAllGood();
    }
    private void shppingCartSetting() {
        Log.i("radish","shopAllList------------------" );
        if (shopAllList == null || shopAllList.size() == 0){
            count = 0;
            if (mTvPopShoppingNumber != null) {
                mTvPopShoppingNumber.setVisibility(View.INVISIBLE);
            }
        }else{
            Log.i("radish","shopAllList.size------------------"+shopAllList.size() );
            count = ShoppingCartHttpBiz.findAllCount(LimitedTimeActivity.this,shopAllList);
            if (mTvPopShoppingNumber != null) {
                mTvPopShoppingNumber.setVisibility(View.VISIBLE);
                mTvPopShoppingNumber.setText(count + "");
            }
        }
    }
    private void postNet(String url, Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("radish", "onResponse: 访问网络失败" );
                    Message msg = Message.obtain();
                    msg.what = MSG_FAILURE;
                    msg.obj = type;
                    handler.sendMessage(msg);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("radish", "onResponse: 访问网络成功" );
                    String result = OkHttpUtil.getResult(response);
                    CloseLoadingView();
                    if (!TextUtils.isEmpty(result)) {
                        Log.i("radish", "response------------------" + result);
                        Message msg = Message.obtain();
                        msg.what = type;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            Log.e("radish", "onResponse: 访问网络异常" );
            handler.sendEmptyMessage(NetWorkError);
            e.printStackTrace();
        }
    }
    private void addShoppingSetting() {
        count++;
        Log.i("radish", "addcount------------------" + count);
        mTvPopShoppingNumber.setVisibility(View.VISIBLE);
        mTvPopShoppingNumber.setText(count + "");
    }
    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    private void refreshMore(String obj) {
        SPUtil.setString(LimitedTimeActivity.this, BUFFER_LIMITEDTIME_YOUHUI, obj);
        EventYouhuiBean eventYouhuiBean = GsonUtil.parseJsonWithGson(obj, EventYouhuiBean.class);
        refreshList = eventYouhuiBean.getData();
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
        EventYouhuiBean eventYouhuiBean = GsonUtil.parseJsonWithGson(obj, EventYouhuiBean.class);
        upLoadList = eventYouhuiBean.getData();
        if (upLoadList != null && upLoadList.size() > 0 && rvAdapter != null) {
            dataList.addAll(upLoadList);
            rvAdapter.setList(dataList);
            rvAdapter.notifyDataSetChanged();
        } else {
            ToastHelper.getInstance().displayToastShort("暂无更多数据");
        }
        mSmartRefreshLayout.finishLoadmore(0);
    }

    private long dd;
    private long hh;
    private long mm;
    private long ss;
    private boolean isRun = true;

    private void initRvXsyh(String obj) {
        EventYouhuiBean bean = GsonUtil.parseJsonWithGson(obj, EventYouhuiBean.class);
        //倒计时
        youhuiTime(bean.getTime());
        refreshList = bean.getData();
        dataList = refreshList;
        if (dataList.size() > 0) {
            mRvXsyh.setLayoutManager(new FullyGridLayoutManager(this, 2));
            rvAdapter = new LimitedTimeRvAdapter(this, dataList);
            mRvXsyh.setAdapter(rvAdapter);
            //条目点击事件
            rvAdapter.setOnItemClickListener(new LimitedTimeRvAdapter.onRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(LimitedTimeActivity.this, DetailActivity.class);
                    if (dataList.get(position).getIsbw() == 0) {
                        //图书
                        intent.putExtra("type", DetailActivity.BOOK_TYPE);
                    } else {
                        //玩具
                        intent.putExtra("type", DetailActivity.TOY_TYPE);
                    }
                    intent.putExtra("id", dataList.get(position).getId());
                    startActivity(intent);
                }
            });
            //购物车点击事件
            rvAdapter.setOnItemShopCartClickListener(new LimitedTimeRvAdapter.onRecyclerViewItemShopCartClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    EventYouhuiBean.DataBean bean = dataList.get(position);
                    uid = SPUtil.getUid(LimitedTimeActivity.this);
                    if (uid == null) {
                        //未登录
                        startActivityForResult(new Intent(LimitedTimeActivity.this,LoginActivity.class),INTENT_REQUEST_LOGIN);
                    } else {
                        if (shopAllList == null || dataList == null) {
                            addData();
                        }
                        int kcl = bean.getKcl();
                        int toyCount = ShoppingCartHttpBiz.findCountById(LimitedTimeActivity.this,shopAllList, bean.getId());
                        if (kcl - toyCount > 0) {
                            //将商品添加到购物车动画
                            addToCartWithAnimation(v);
                            //将商品添加到购物车
                            addGood(bean.getId(),uid[0],1);
                        } else {
                            ToastHelper.getInstance().displayToastShort("库存量不足");
                        }
                    }
                }
            });
        }
    }

    private void youhuiTime(String time) {
        //2017-07-29 00:00:00
        long[] formatTime = StringUtil.formatDaojishi(time);
        if (formatTime != null) {
            dd = formatTime[0];
            hh = formatTime[1];
            mm = formatTime[2];
            ss = formatTime[3];
            mTvDd.setText(dd + "");
            mTvHh.setText(hh + "");
            mTvMmss.setText("小时" + mm + ":" + ss);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (isRun) {
                        try {
                            Thread.sleep(1000); // sleep 1000ms == 1秒
                            Message message = Message.obtain();
                            message.what = TIME_DAOJISHI;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            //已过期
            mTvDd.setText(0 + "");
            mTvHh.setText(0 + "");
            mTvMmss.setText("小时" + 0 + ":" + 0);
        }
    }


    private void DaoJiShi() {
        //剩余2天 12:50
        mTvDd.setText(dd + "");
        mTvHh.setText(hh + "");
        mTvMmss.setText("小时" + mm + ":" + ss);
        ss--;
        if (ss < 0) {
            ss = 59;
            mm--;
            if (mm < 0) {
                mm = 59;
                hh--;
                if (hh < 0) {
                    // 倒计时结束
                    hh = 23;
                    dd--;
                    mTvDd.setText(0 + "");
                    mTvHh.setText(0 + "");
                    mTvMmss.setText("小时" + 0 + ":" + 0);
                }
            }
        }

    }

    @Override
    protected void initEvent() {
        setView();
        mIvShopCart.setOnClickListener(this);
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.iv_shop_cart:
                Intent intent = new Intent(this, ShoppingCarActivity.class);
                startActivity(intent);
                break;
        }
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
                if (NetWorkUtil.isNetWorkAvailable(LimitedTimeActivity.this)) {
                    //下拉刷新
                    Map<String, Object> map = new HashMap();
                    postNet(AppConstants.EVENT_MORE_YOUHUI_UPLOAD, map, MSG_REFRESH);

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
                Log.i("radish", "onLoadmore------------------");
                if (NetWorkUtil.isNetWorkAvailable(LimitedTimeActivity.this)) {
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

    private float mParentY = -1;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];

    private void addToCartWithAnimation(View v) {

        if (mParentY == -1) {
            //rv在屏幕上的位置
            int[] location1 = new int[2];
            rlParent.getLocationOnScreen(location1);
            mParentY = location1[1];
        }

        final ImageView goods = new ImageView(LimitedTimeActivity.this);
        goods.setImageResource(R.mipmap.head_icon_jifen);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
        rlParent.addView(goods, params);

        //动画（位移动画，从点击的位置，移动到浮动按钮的位置）
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        float startX = location[0]; //按钮在屏幕中的位置 x
        float startY = location[1] - mParentY; //按钮在屏幕中的位置-顶部的高度 y


        float toX = mIvShopCart.getX();  //目的x
        float toY = mIvShopCart.getY(); //目的y
        LogUtil.e("tox==" + toX);
        LogUtil.e("toy==" + toY);

        float diffX = Math.abs(startX - toX);
        float diffY = Math.abs(startY - toY);

        float diff = diffX * diffX + diffY * diffY;
        diff = (float) Math.sqrt(diff);

        //四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration((long) (diff * 1));
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });
        //   五、 开始执行动画
        valueAnimator.start();

        //   六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // todo 购物车的数量加1,初始的数据要从购物车中获取
                rlParent.removeView(goods);

                /*String number = mTvPopShoppingNumber.getText().toString();
                int i = Integer.parseInt(number);

                i++;
                if (i == 1) {
                    mTvPopShoppingNumber.setVisibility(View.VISIBLE);
                }
                mTvPopShoppingNumber.setText(i + "");*/

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
    /**
     * 查找所有购物车产品
     */
    public void findAllGood(){
        uid = SPUtil.getUid(this);
        if(uid ==null){
            // startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        ShoppingCartHttpBiz.setOnResultCallBackListener(new ShoppingCartHttpBiz.OnResultCallbackListener() {
            @Override
            public void OnResultCallback(final String result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CloseLoadingView();
                        LogUtil.e("购物车接口回调结果： "+result);
                        ShppingCarHttpBean bean = GsonUtil.parseJsonWithGson(result, ShppingCarHttpBean.class);
                        int status = bean.getStatus();
                        List<ShppingCarHttpBean.WjlistBean> list = null;
                        if(status==1){
                            //1 购物车有数据
                            list = bean.getWjlist();
                            //本地保存缓存
                            SPUtil.setString(LimitedTimeActivity.this, ShoppingCartHttpBiz.BUFFER_SHOPPING_CART,result);
                        }else{
                            //0 购物车无数据
                            list = null;
                        }
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOPPING_FINDALL;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });
        ShoppingCartHttpBiz.getAll(uid[0]);
    }

    //添加购物车
    public void addGood(int wid,String uid,int num) {
        String url= AppConstants.AddShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("wid",wid);
        para.put("uid",uid);
        para.put("shu",num);

        ShoppingCartHttpBiz.Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result!=null&&result.length()>2){
                    Message msg = Message.obtain();
                    msg.what = MSG_SHOPPING_ADD;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }


    private void addData(){
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            if (dataList == null) {
                postNet(AppConstants.EVENT_MORE_YOUHUI, new HashMap<String, Object>(), MSG_MORE_YOUHUI);
            }
            if (count<0){
                findAllGood();
            }
        }else {
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }

    }





}
