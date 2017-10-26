package com.ruiyihong.toyshop.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.bean.ToyShopToyListBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.squareup.picasso.Picasso;

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
 * 首页最新玩具-->更多玩具
 */
public class MoreToyActivity extends BaseActivity {
    private static final String MORE_TYPE_TOY = "more_type_toy";
    private static final String MORE_TYPE_BOOK = "more_type_book";
    public static final int DATA_TYPE_TOY = 0;//最新玩具
    public static final int DATA_TYPE_BOOK = 1;//最新图书
    private static final int NetWorkError = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    private static final int MSG_LOAD_MORE = 7;
    private static final int MSG_REFRESH = 8;
    private static final int MSG_FAILURE = 9;
    private static final int MSG_DATA_NULL = 10;
    private static final int MSG_SHOPPING_FINDALL = 11;
    private static final int INTENT_REQUEST_LOGIN = 12;
    private static final int MSG_SHOPPING_ADD = 13;
    @InjectView(R.id.back)
    ImageButton back;
    @InjectView(R.id.rv_more_toy)
    RecyclerView mRvMoreToy;
    @InjectView(R.id.tv_more_title)
    TextView mTvTitle;


    private boolean isResfrsh = false;//是否是刷新状态
    @InjectView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.iv_shop_cart)
    ImageView mIvShopCart;
    @InjectView(R.id.rl_parent)
    RelativeLayout rlParent;
    @InjectView(R.id.tv_pop_shopping_number)
    TextView mTvPopShoppingNumber;
    private int currentType = DATA_TYPE_BOOK;//当前页面类型
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_TYPE_TOY:
                    moreToy((String) msg.obj);
                    break;
                case DATA_TYPE_BOOK:
                    moreBook((String) msg.obj);
                    break;
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;
                case CloseLoadingView: //关闭Loading动画
                    if (loadingView != null)
                        loadingView.loadSuccess(false);
                    break;
                case PageLoading:  //页面加载中动画
                    if (loadingView != null)
                        loadingView.load();
                    break;
                case MSG_LOAD_MORE:
                    //上拉加载
                    Log.i("radish","handler------------------上拉加载" );
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
                case MSG_DATA_NULL:
                    ToastHelper.getInstance().displayToastShort("暂无更多数据");
                    mSmartRefreshLayout.finishRefresh(0);
                    mSmartRefreshLayout.finishLoadmore(0);
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
    private List<ToyShopToyListBean.DataBeanX.DataBean> list;
    private List<ToyShopToyListBean.DataBeanX.DataBean> dataList;
    private List<ToyShopToyListBean.DataBeanX.DataBean> upLoadList;
    private int type;
    private MoreToyAdapter rvAdapter;
    private int page = 1;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private int count = -1;

    private void shppingCartSetting() {
        if (shopAllList == null || shopAllList.size() == 0){
            count = 0;
            if (mTvPopShoppingNumber != null) {
                mTvPopShoppingNumber.setVisibility(View.INVISIBLE);
            }
        }else{
            count = ShoppingCartHttpBiz.findAllCount(MoreToyActivity.this,shopAllList);
            if (mTvPopShoppingNumber != null) {
                mTvPopShoppingNumber.setVisibility(View.VISIBLE);
                mTvPopShoppingNumber.setText(count + "");
            }
        }
    }

    private void refreshMore(String obj) {
        if (type == DATA_TYPE_TOY) {
            SPUtil.setString(MoreToyActivity.this, MORE_TYPE_TOY, obj);
        }else{
            SPUtil.setString(MoreToyActivity.this,MORE_TYPE_BOOK,obj);
        }
        ToyShopToyListBean.DataBeanX toy = GsonUtil.parseJsonWithGson(obj, ToyShopToyListBean.DataBeanX.class);
        dataList = toy.getData();
        if (dataList != null && dataList.size() > 0 && rvAdapter != null) {
            list = dataList;
            if (upLoadList != null && upLoadList.size() != 0) {
                dataList.addAll(upLoadList);
            }
            rvAdapter.notifyDataSetChanged();
        }
        mSmartRefreshLayout.finishRefresh(0);
    }

    private void loadMore(String obj) {
        ToyShopToyListBean.DataBeanX toy = GsonUtil.parseJsonWithGson(obj, ToyShopToyListBean.DataBeanX.class);
        upLoadList = toy.getData();
        if (upLoadList != null && upLoadList.size() > 0 && rvAdapter != null) {
            list.addAll(upLoadList);
            rvAdapter.notifyDataSetChanged();
        } else {
            ToastHelper.getInstance().displayToastShort("暂无更多数据");
        }
        mSmartRefreshLayout.finishLoadmore(0);
    }


    private void addShoppingSetting() {
        count++;
        Log.i("radish", "addcount------------------" + count);
        mTvPopShoppingNumber.setVisibility(View.VISIBLE);
        mTvPopShoppingNumber.setText(count + "");
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_moretoy;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("radish","onresume------------------" );
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

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }
        page = 1;
        //回显购物车的数量
        if (count > 0) {
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");
        } else {
            mTvPopShoppingNumber.setVisibility(View.INVISIBLE);
        }
        //获取跳转携带数据
        type = getIntent().getIntExtra("type", -1);
        switch (type) {
            case DATA_TYPE_TOY:
                //玩具
                currentType = DATA_TYPE_TOY;
                mTvTitle.setText("最新玩具");
                String toy = SPUtil.getString(MoreToyActivity.this, MORE_TYPE_TOY, "");
                if (!TextUtils.isEmpty(toy)) {
                    CloseLoadingView();
                    moreToy(toy);
                }
                if (NetWorkUtil.isNetWorkAvailable(this)) {
                    initToyData();
                } else {
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
                break;
            case DATA_TYPE_BOOK:
                //图书
                currentType = DATA_TYPE_BOOK;
                mTvTitle.setText("最新图书");
                String book = SPUtil.getString(MoreToyActivity.this, MORE_TYPE_BOOK, "");
                Log.i("radish", "book------------------" + book);
                if (!TextUtils.isEmpty(book)) {
                    CloseLoadingView();
                    moreBook(book);
                }
                if (NetWorkUtil.isNetWorkAvailable(this)) {
                    initBookData();
                } else {
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
                break;
        }
        LogUtil.e("currentType==" + currentType);
    }

    /**
     * 加载图书数据
     */
    private void initBookData() {
        //获取最新图书数据
        HashMap<String, String> map = new HashMap<>();
        String cname = SPUtil.getString(this, AppConstants.LAST_LOCATION, "北京");
        map.put("cname", cname);
        netPost(AppConstants.HOME_MORE_BOOK, map, DATA_TYPE_BOOK);
    }

    /**
     * 加载玩具数据
     */
    private void initToyData() {
        //获取最新玩具数据
        HashMap<String, String> map = new HashMap<>();
        String cname = SPUtil.getString(this, AppConstants.LAST_LOCATION, "北京");
        map.put("cname", cname);
        netPost(AppConstants.HOME_MORE_TOY, map, DATA_TYPE_TOY);

    }

    private void moreToy(String obj) {

        ToyShopToyListBean.DataBeanX toy = GsonUtil.parseJsonWithGson(obj, ToyShopToyListBean.DataBeanX.class);
        dataList = toy.getData();
        list = dataList;
        mRvMoreToy.setLayoutManager(new GridLayoutManager(this, 2));
        if (list != null) {
            rvAdapter = new MoreToyAdapter();
            mRvMoreToy.setAdapter(rvAdapter);
        }
        rvAdapter.setOnItemClickListener(new onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MoreToyActivity.this, DetailActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", type);
                startActivity(intent);

            }
        });
    }

    private void moreBook(String obj) {

        ToyShopToyListBean.DataBeanX toy = GsonUtil.parseJsonWithGson(obj, ToyShopToyListBean.DataBeanX.class);
        list = toy.getData();


        mRvMoreToy.setLayoutManager(new GridLayoutManager(this, 2));
        rvAdapter = new MoreToyAdapter();
        mRvMoreToy.setAdapter(rvAdapter);
        rvAdapter.setOnItemClickListener(new onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MoreToyActivity.this, DetailActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", type);
                LogUtil.e("===详情页type====="+type);
                startActivity(intent);
            }
        });
    }

    private void netPost(String url, Map<String, String> map, final int type) {
        try {
            OkHttpUtil.postString(url, map, new Callback() {
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
                        LogUtil.e(type+"====更多玩具/图书===" + result);
                        CloseLoadingView();
                        switch (type) {
                            case DATA_TYPE_TOY:
                                SPUtil.setString(MoreToyActivity.this, MORE_TYPE_TOY, result);
                                break;
                            case DATA_TYPE_BOOK:
                                SPUtil.setString(MoreToyActivity.this, MORE_TYPE_BOOK, result);
                                break;
                        }
                        Message msg = Message.obtain();
                        msg.what = type;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        Log.i("radish","暂无更多数据------------------" );
                   //     ToastHelper.getInstance().displayToastShort("暂无更多数据");
                        handler.sendEmptyMessage(MSG_DATA_NULL);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(NetWorkError);
            e.printStackTrace();
        }
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    @Override
    protected void initEvent() {
        mSmartRefreshLayout.setOnMultiPurposeListener(new Refresh_Listener());
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this));

        mIvShopCart.setOnClickListener(this);
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
        mSmartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

                if (NetWorkUtil.isNetWorkAvailable(MoreToyActivity.this)) {
                    //上拉加载
                    Map<String, String> map = new HashMap();
                    String cname = SPUtil.getString(MoreToyActivity.this, AppConstants.LAST_LOCATION, "北京");
                    map.put("cname", cname);
                    page++;
                    LogUtil.e( "onLoadmore------------------"+page);
                    map.put("page",""+page);
                    if (type == DATA_TYPE_TOY){
                        netPost(AppConstants.HOME_MORE_TOY_UPLOAD, map, MSG_LOAD_MORE);
                    }else {
                        netPost(AppConstants.HOME_MORE_BOOK_UPLOAD, map, MSG_LOAD_MORE);
                    }
                } else {
                    mSmartRefreshLayout.finishLoadmore(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (NetWorkUtil.isNetWorkAvailable(MoreToyActivity.this)) {
                    //下拉刷新
                    Map<String, String> map = new HashMap();
                    String cname = SPUtil.getString(MoreToyActivity.this, AppConstants.LAST_LOCATION, "北京");
                    map.put("cname", cname);
                    if (type == DATA_TYPE_TOY){
                        netPost(AppConstants.HOME_MORE_TOY, map, MSG_REFRESH);
                    }else {
                        netPost(AppConstants.HOME_MORE_BOOK, map, MSG_REFRESH);
                    }
                } else {
                    mSmartRefreshLayout.finishRefresh(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {

        switch (v.getId()) {
            case R.id.iv_shop_cart:
                Intent intent = new Intent(this, ShoppingCarActivity.class);
                startActivity(intent);
                break;
        }
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

        final ImageView goods = new ImageView(MoreToyActivity.this);
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
                            SPUtil.setString(MoreToyActivity.this,ShoppingCartHttpBiz.BUFFER_SHOPPING_CART,result);
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

    /**
     * 更多玩具列表适配器
     */
    class MoreToyAdapter extends RecyclerView.Adapter<MoreToyAdapter.MyViewHolder> {

        private onRecyclerViewItemClickListener itemClickListener;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MoreToyActivity.this).inflate(R.layout.item_more_toy_book, parent,false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            final ToyShopToyListBean.DataBeanX.DataBean bean = list.get(position);
            if (bean == null) {
                return;
            }
            Picasso.with(MoreToyActivity.this).load(AppConstants.IMG_BASE_URL + bean.getShopimg()).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_image);
            holder.tv_toy_name.setText(bean.getName());
            holder.tv_toy_daypay.setText(bean.getShopprice() + "元/天");
            holder.tv_toy_diaopaijia.setText("吊牌价:" + bean.getDpj() + "元");
            holder.ib_shopping_cart_orange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uid = SPUtil.getUid(MoreToyActivity.this);
                    if (uid == null) {
                        //未登录
                        startActivityForResult(new Intent(MoreToyActivity.this,LoginActivity.class),INTENT_REQUEST_LOGIN);
                    } else {
                        addData();
                        int kcl = bean.getKcl();
                        int toyCount = ShoppingCartHttpBiz.findCountById(MoreToyActivity.this,shopAllList, bean.getId());
                        if (kcl - toyCount > 0) {
                            //将商品添加到购物车动画
                            addToCartWithAnimation(view);
                            //将商品添加到购物车
                            addGood(bean.getId(),uid[0],1);
                        } else {
                            ToastHelper.getInstance().displayToastShort("库存量不足");
                        }
                    }

                }
            });

            holder.ib_shopping_cart_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] uid = SPUtil.getUid(MoreToyActivity.this);
                    if (uid == null) {
                        //未登录
                        startActivityForResult(new Intent(MoreToyActivity.this,LoginActivity.class),INTENT_REQUEST_LOGIN);
                    } else {
                        if (shopAllList == null || list == null) {
                            addData();
                        }
                        int kcl = bean.getKcl();
                        int toyCount = ShoppingCartHttpBiz.findCountById(MoreToyActivity.this,shopAllList, bean.getId());
                        if (kcl - toyCount > 0) {

                            //将商品添加到购物车动画
                            addToCartWithAnimation(view);
                            //将商品添加到购物车
                            addGood(bean.getId(),uid[0],1);
                        } else {
                            ToastHelper.getInstance().displayToastShort("库存量不足");
                        }
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                return list.size();
            }else {
                return 0;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {


            private ImageView iv_image;//商品图片
            private TextView tv_toy_name;//商品名称
            private TextView tv_toy_daypay;//商品每日单价
            private TextView tv_toy_diaopaijia;//商品吊牌价
            private ImageButton ib_shopping_cart_yellow;//添加购物车-玩具页面
            private LinearLayout ib_shopping_cart_orange;//添加购物车-图书页面


            public MyViewHolder(View itemView) {
                super(itemView);
                iv_image = itemView.findViewById(R.id.iv_toy_image);
                tv_toy_name = itemView.findViewById(R.id.tv_toyshop_name);
                tv_toy_daypay = itemView.findViewById(R.id.tv_toyshop_daypay);
                tv_toy_diaopaijia = itemView.findViewById(R.id.tv_diaopaijia);
                ib_shopping_cart_yellow = itemView.findViewById(R.id.ib_shopping_cart_yellow);
                ib_shopping_cart_orange = itemView.findViewById(R.id.ib_shopping_cart_orange);

                //如果是玩具，显示玩具页面的购物车图片和单价样式
                if (currentType == DATA_TYPE_TOY) {
                    tv_toy_daypay.setBackgroundResource(R.drawable.shap_round_rectangle_orange);
                    tv_toy_daypay.setTextColor(Color.WHITE);
                    ib_shopping_cart_orange.setVisibility(View.GONE);
                    ib_shopping_cart_yellow.setVisibility(View.VISIBLE);
                    ib_shopping_cart_yellow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                } else if (currentType == DATA_TYPE_BOOK) {
                    tv_toy_daypay.setBackgroundResource(android.R.color.transparent);
                    tv_toy_daypay.setTextColor(Color.RED);
                    ib_shopping_cart_orange.setVisibility(View.VISIBLE);
                    ib_shopping_cart_yellow.setVisibility(View.GONE);
                }


            }
        }

        public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
            this.itemClickListener = listener;

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
            if (list == null) {
                switch (currentType) {
                    case DATA_TYPE_TOY:
                        //玩具
                        initToyData();
                        break;
                    case DATA_TYPE_BOOK:
                        //图书
                        initBookData();
                        break;
                }
            }
            if (count<0){
                findAllGood();
            }
        }else {
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }

    }
    /**
     * 条目点击事件的监听器
     */
    public interface onRecyclerViewItemClickListener {
        void onItemClick(View v, int position);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_LOGIN){
            addData();
        }
    }
}
