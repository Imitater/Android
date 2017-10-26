package com.ruiyihong.toyshop.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.DetailActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.activity.ShoppingCarActivity;
import com.ruiyihong.toyshop.adapter.ToyShopAdapter;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.bean.ToyShopToyListBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetUtli;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
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


public class ToyShopToyFragment extends BaseFragment {
    private static final String NO_FILTER_DATA_BUFFER_KEY_TOY = "no_filter_data_buffer_key_toy";
    private static final int PARSE_TOY_LIST_DATA = 30;
    private static final int PARSE_SORT_DATA = 31;
    private static final int PARSE_FILTER_DATA = 32;
    private static final int PARSE_MORE_DATA = 33;
    @InjectView(R.id.rv_toyshop)
    RecyclerView mRvToyshop;
    @InjectView(R.id.floatingActionButton)
    ImageButton mFloatingActionButton;
    @InjectView(R.id.rl_parent)
    RelativeLayout rlParent;
    @InjectView(R.id.tv_pop_shopping_number)
    TextView mTvPopShoppingNumber;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private String[] strs = {"综合排序", "人气最高", "最新上架", "价格最高", "价格最低"};
    private HashMap<String, String> loadNumMap = new HashMap<>();
    private ToyShopAdapter mAdapter;
    private List<ToyShopToyListBean.DataBeanX.DataBean> mToyList;
    private PathMeasure mPathMeasure;
    private int loadNum = 0;//加载更多的次数
    /*** 贝塞尔曲线中间过程的点的坐标*/
    private float[] mCurrentPosition = new float[2];
    private float mParentY = -1;
    private String filterAge;
    private String filterGongneng;
    private int refreshType = -2; //刷新的类型
    private static final int REFRESHTYPE_MOST_POPULER = 10;//人气最高
    private static final int REFRESHTYPE_NEWEST = 11;//最新上架
    private static final int REFRESHTYPE_HIGHEST_PRICE = 12;//价格最高
    private static final int REFRESHTYPE_LOWEST_PRICE = 13;//价格最低
    private static final int MSG_SHOPPING_ADD = 15;
    private static final int INTENT_REQUEST_LOGIN = 16;
    private int count = -1;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result;
            switch (msg.what){
                case PARSE_TOY_LIST_DATA:
                    try {
                        result = (String) msg.obj;
                        parseToyData(result);
                        //缓存数据
                        SPUtil.setString(mActivity, NO_FILTER_DATA_BUFFER_KEY_TOY, result);
                    }catch (Exception e){

                    }
                    break;
                case PARSE_SORT_DATA:
                    result = (String) msg.obj;
                    parseSortData(result);
                    break;
                case PARSE_FILTER_DATA:
                    result = (String) msg.obj;
                    parseFilterData(result);
                    break;
                case PARSE_MORE_DATA:
                    result = (String) msg.obj;
                    parseMoreData(result);
                    break;
                case MSG_SHOPPING_ADD://向购物车添加商品
                    String obj = (String) msg.obj;
                    Log.e("radish","obj------------------"+obj );
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

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.toyshop_vp_item, null);
        return view;
    }

    @Override
    protected void initData() {
        //回显购物车的数量
        initShoppingChart();
        //获取无过滤数据
        getNoFilterData(false);
        //初始化刷新加载
        initHeaderAndFooter();
    }

    private void addShoppingSetting() {
        count++;
        Log.e("radish", "addcount------------------" + count);
        mTvPopShoppingNumber.setVisibility(View.VISIBLE);
        mTvPopShoppingNumber.setText(count + "");
    }

    /**
     * 初始化购物车数量
     */
    private void initShoppingChart(){
        //回显购物车的数量
        if (count > 0) {
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");
        } else {
            mTvPopShoppingNumber.setVisibility(View.GONE);
        }
    }

    private void initHeaderAndFooter() {
        mSmartRefreshLayout.setOnMultiPurposeListener(new Refresh_Listener());


        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(mActivity));

    }

    @Override
    protected void initEvent() {
      //下拉刷新监听
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //重新获取数据
                if (filterAge == null && filterGongneng == null && refreshType==-2){
                    //没有筛选的刷新
                    getNoFilterData(true);
                }else {
                    if (refreshType == -2){
                        //不排序，只筛选
                        try {
                            getFilterData(filterAge,filterGongneng,true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //排序
                        String selection = "";
                        switch (refreshType){
                            case REFRESHTYPE_HIGHEST_PRICE:
                                //价格最高
                                selection = "价格最高";
                                break;
                            case REFRESHTYPE_LOWEST_PRICE:
                                selection = "价格最低";
                                break;
                            case REFRESHTYPE_NEWEST:
                                selection = "最新上架";
                                break;
                            case REFRESHTYPE_MOST_POPULER:
                                selection = "人气最高";
                                break;
                        }
                        sort(selection,strs,filterAge,filterGongneng,true);
                    }
                }
            }
        });
        //上拉加载监听
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //获取更多数据
                try {
                    getMoreData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //跳转购物车
        mFloatingActionButton.setOnClickListener(this);
    }

    /**
     * 获取加载更多的数据
     */
    private void getMoreData() throws IOException {
        LogUtil.e("页数==="+loadNum);

        loadNum++;//第几次加载更多
        loadNumMap.put("loadNum", "" + loadNum);
        OkHttpUtil.postString(AppConstants.TOYSHOP_TOY_LOADMORE_URL, loadNumMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = OkHttpUtil.getResult(response);

                if (!TextUtils.isEmpty(result)) {
                    Message msg = Message.obtain();
                    msg.what = PARSE_MORE_DATA;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void parseMoreData(String json) {
        LogUtil.e("more数据==="+json);
        //解析更多数据
        ToyShopToyListBean.DataBeanX beanX = GsonUtil.parseJsonWithGson(json, ToyShopToyListBean.DataBeanX.class);
        List<ToyShopToyListBean.DataBeanX.DataBean> moreDataList = beanX.getData();

        if (moreDataList!=null && moreDataList.size()>0) {
            //有更多数据
            mToyList.addAll(moreDataList);

            //刷新界面
            mAdapter.updataList(mToyList);
            mAdapter.notifyDataSetChanged();

            mSmartRefreshLayout.finishLoadmore(0);
        }else{
            //没有更多数据了
            ToastHelper.getInstance().displayToastShort("没有更多数据了");
            mSmartRefreshLayout.finishLoadmore(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatingActionButton:
                Intent intent = new Intent(mActivity, ShoppingCarActivity.class);
                startActivityForResult(intent,0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initShoppingChart();
    }

    /**
     * 获取玩具类表无筛选全部数据
     * isRefresh  是否是下拉刷新获取数据
     */
    private void getNoFilterData(final boolean isRefresh) {

        String buffer = SPUtil.getString(mActivity, NO_FILTER_DATA_BUFFER_KEY_TOY, "");
        if (!TextUtils.isEmpty(buffer)) {
            //有本地
            parseToyData(buffer);
        }
        try {
            OkHttpUtil.get(AppConstants.ALL_TOY_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response != null && response.code() == 200) {
                        final String result = response.body().string();
                        if (isRefresh){
                            //刷新结束
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSmartRefreshLayout.finishRefresh(0);
                                }
                            });
                        }
                        //解析数据
                        Message msg = Message.obtain();
                        msg.obj = result;
                        msg.what = PARSE_TOY_LIST_DATA;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseToyData(String json) {

        ToyShopToyListBean toyListBean = GsonUtil.parseJsonWithGson(json, ToyShopToyListBean.class);
        loadNum = toyListBean.getData().getCurrent_page();
        mToyList = toyListBean.getData().getData();
        mRvToyshop.setLayoutManager(new FullyGridLayoutManager(mActivity, 2));
        mAdapter = new ToyShopAdapter(mActivity, mToyList);
        mRvToyshop.setAdapter(mAdapter);

        //购物车按钮点击
        mAdapter.setOnShoppingcartClickListener(new MyonShoppingcartClickListener());

        //条目点击事件
        mAdapter.setOnItemClickListener(new MyonRecyclerViewItemClickListener());

    }

    /**
     * 按价格排序
     */
    public void sort(String selection, String[] strs, String age, String gongneng, boolean isRefresh) {
        mTvPopShoppingNumber.setText(count + "");

        this.filterAge = age;
        this.filterGongneng = gongneng;

        String url = "";

        //判断排序条件
        if (strs[0].equals(selection)) {
            refreshType=-2;
            //综合排序
            try {
                getFilterData(age,gongneng, isRefresh);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        } else if (strs[1].equals(selection)) {
            refreshType = REFRESHTYPE_MOST_POPULER;
            //人气排序
            url = AppConstants.TOYSHOP_URL + "/otherlist";
        } else if (strs[2].equals(selection)) {
            refreshType = REFRESHTYPE_NEWEST;
            //最新上架
            url = AppConstants.TOYSHOP_URL + "/xxid";
        } else if (strs[3].equals(selection)) {
            refreshType = REFRESHTYPE_HIGHEST_PRICE;
            //价格最高
            url = AppConstants.TOYSHOP_URL + "/prjia";
        } else if (strs[4].equals(selection)) {
            refreshType = REFRESHTYPE_LOWEST_PRICE;
            //价格最低
            url = AppConstants.TOYSHOP_URL + "/prsh";
        }
        try {
            getSortData(url, age, gongneng,isRefresh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSortData(String url, String age, String gongneng, final boolean isRefresh) throws IOException {

        LogUtil.e("排序url=="+url+"=======年龄="+age+"=======功能="+gongneng);
        if (TextUtils.isEmpty(age)){
            age="";
        }
        if (TextUtils.isEmpty(gongneng)){
            gongneng="";
        }
        if (!TextUtils.isEmpty(gongneng)){
            if (gongneng.contains("全部")) {
                gongneng = "";
            }else{
                String[] split = gongneng.split(",");
                if (split != null && split.length >= 2){
                    gongneng = split[0];
                }
            }
        }
        if (!TextUtils.isEmpty(age)){
            if (age.contains("全部")){
                age = "";
            }else{
                String[] split = age.split(",");
                if (split != null && split.length >= 2){
                    age = split[0];
                }
            }
        }
        LogUtil.e("排序url=="+url+"=======年龄="+age+"=======功能="+gongneng);
        //0(图书),1(玩具)
        HashMap<String, String> params = new HashMap<>();
        params.put("lei", "1");
        params.put("age",age);
        params.put("wjclass",gongneng);

        OkHttpUtil.postString(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isRefresh){
                    //刷新
                    mSmartRefreshLayout.finishRefresh(0);
                }
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("排序后的数据"+result);
                if (!TextUtils.isEmpty(result) && !"null".equals(result)){
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = PARSE_SORT_DATA;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void parseSortData(String result) {

        ToyShopToyListBean.DataBeanX dataBean = GsonUtil.parseJsonWithGson(result, ToyShopToyListBean.DataBeanX.class);
        mToyList = dataBean.getData();
        mAdapter.updataList(mToyList);
        mAdapter.notifyDataSetChanged();

    }

    public void getFilterData(String age, String gongNeng, final boolean isRefresh) throws IOException {
        this.filterAge = age;
        this.filterGongneng = gongNeng;

        String[] ageSplite = null;
        String[] gnSplite = null;
        if (!TextUtils.isEmpty(age)){
            if (age.contains(",")) {
                ageSplite = age.split(",");
                age = ageSplite[0];
            }
        }else {
            age="";
        }
        if (!TextUtils.isEmpty(gongNeng)){
            if (gongNeng.contains(",")) {
                gnSplite = gongNeng.split(",");
                gongNeng = gnSplite[0];
            }
        }else {
            gongNeng="";
        }
        if (TextUtils.equals("全部",age)){
            age="";
        }
        if (TextUtils.equals("全部",gongNeng)){
            gongNeng="";
        }
        LogUtil.e("玩图汇筛选=="+"=年龄="+age+"=功能="+gongNeng);
        //0(图书),1(玩具)
        String url = AppConstants.TOYSHOP_URL + "/search";
        HashMap<String, String> params = new HashMap<>();
        params.put("lei", "1");
        params.put("age", age);
        params.put("wjclass", gongNeng);
        OkHttpUtil.postString(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (isRefresh){
                    //刷新
                    mSmartRefreshLayout.finishRefresh(0);
                }
                Message msg = Message.obtain();
                msg.what = PARSE_FILTER_DATA;
                msg.obj = result;
                handler.sendMessage(msg);

            }
        });
    }

    /**
     * 解析筛选之后的数据
     *
     * @param json
     */
    private void parseFilterData(final String json) {
        ToyShopToyListBean.DataBeanX dataBeanX = GsonUtil.parseJsonWithGson(json, ToyShopToyListBean.DataBeanX.class);
        mToyList =dataBeanX.getData();
        mAdapter.updataList(mToyList);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 条目点击事件
     */
    private class MyonRecyclerViewItemClickListener implements ToyShopAdapter.onRecyclerViewItemClickListener {
        @Override
        public void onItemClick(View v, int position) {
            int kcl = mToyList.get(position).getKcl();
            if (kcl>0) {
                //跳转详情页面
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra("type", DetailActivity.TOY_TYPE);
                intent.putExtra("id", mToyList.get(position).getId());

                startActivityForResult(intent, 0);
            }else{
                ToastHelper.getInstance().displayToastShort("库存量不足");
            }
        }
    }

    /**
     * 购物车按钮点击事件
     */
    private class MyonShoppingcartClickListener implements ToyShopAdapter.onShoppingcartClickListener {

        @Override
        public void onShoppingcartClick(View v, int position) {
            ToyShopToyListBean.DataBeanX.DataBean bean = mToyList.get(position);
            uid = SPUtil.getUid(mActivity);
            if (uid == null) {
                //未登录
                startActivityForResult(new Intent(mActivity,LoginActivity.class),INTENT_REQUEST_LOGIN);
            } else {
                if (shopAllList == null || mToyList == null) {
                    addData();
                }
                int kcl = bean.getKcl();
                int toyCount = ShoppingCartHttpBiz.findCountById(mActivity,shopAllList, bean.getId());
                Log.e("radish","kcl+toycount------------------"+kcl+"---"+toyCount );
                if (kcl - toyCount > 0) {
                    //将商品添加到购物车动画
                    addToCartWithAnimation(v);
                    Log.e("radish","购物车动画完成------------------" );
                    //将商品添加到购物车
                    addGood(bean.getId(),uid[0],1);
                } else {
                    ToastHelper.getInstance().displayToastShort("库存量不足");
                }
            }
        }
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
                Log.e("radish","failuer------------------"+e.toString() );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                Log.e("radish","result------------------"+result );
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
        if (NetWorkUtil.isNetWorkAvailable(mActivity)) {
            if (mToyList == null) {
                getNoFilterData(false);
            }
            if (count<0){
                //findAllGood();
            }
        }else {
            ToastHelper.getInstance().displayToastShort("网络异常");
        }

    }

/*    private void addShoppingCar(int position) {
        ToyShopToyListBean.DataBeanX.DataBean dataBean = mToyList.get(position);
        int id = dataBean.getId();
        String name = dataBean.getName();
        //todo 添加购物车
        *//*
              商品插入购物车说明
              id, type, count, name, age, img, price, dpj, num
         *//*
        ShoppingCartBiz.addGood(mActivity,id+"","1",dataBean.getKcl()+"",name,"",dataBean.getShopimg(),dataBean.getShopprice()+"",dataBean.getDpj()+"","1");


    }*/


    private void addToCartWithAnimation(View v) {

        if (mParentY == -1) {
            //rv在屏幕上的位置
            int[] location1 = new int[2];
            rlParent.getLocationOnScreen(location1);
            mParentY = location1[1];
        }

        final ImageView goods = new ImageView(mActivity);
        goods.setImageResource(R.mipmap.head_icon_jifen);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
        rlParent.addView(goods, params);

        //动画（位移动画，从点击的位置，移动到浮动按钮的位置）
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        float startX = location[0]; //按钮在屏幕中的位置 x
        float startY = location[1] - mParentY; //按钮在屏幕中的位置-顶部的高度 y


        float toX = mFloatingActionButton.getX();  //目的x
        float toY = mFloatingActionButton.getY(); //目的y
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

    public void setCartNumberVisible(boolean flag,int count,List list){
        this.count = count;
        shopAllList = list;
        if(flag){
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(this.count + "");
        }else{
            mTvPopShoppingNumber.setVisibility(View.GONE);
        }
    }
}
