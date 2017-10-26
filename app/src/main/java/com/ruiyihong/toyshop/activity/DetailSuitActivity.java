package com.ruiyihong.toyshop.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.ProductLoveRvAdapter;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.bean.ToyBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartBiz;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/7/21.
 * 强力推荐
 */

public class DetailSuitActivity extends BaseActivity {
    @InjectView(R.id.ib_detail_share)
    ImageButton mIbDetailShare;
    @InjectView(R.id.iv_detail_product)
    ImageView mIvDetailProduct;
    @InjectView(R.id.tv_detail_product_name)
    TextView mTvDetailProductName;
    @InjectView(R.id.tv_detail_product_rent_price)
    TextView mTvDetailProductRentPrice;
    @InjectView(R.id.tv_detail_product_price)
    TextView mTvDetailProductPrice;
    @InjectView(R.id.tv_detail_baby_age)
    TextView mTvDetailBabyAge;
    @InjectView(R.id.rv_detail_more_suit)
    RecyclerView mRvDetailMoreSuit;
    @InjectView(R.id.tv_suit_info)
    TextView mTvSuitInfo;
    @InjectView(R.id.btn_detail_shoppingcar)
    Button mBtnDetailShoppingcar;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    private String toyUrl;
    private static final int TOY_INFO = 0;
    private static final int RECOMMEND_INFO = 1;
    private static final int NetWorkError=2;
    private static final int CloseLoadingView=3;
    private static final int PageLoading=4;
    private static final int MSG_SHOPPING_FINDALL = 5;
    private static final int INTENT_REQUEST_LOGIN = 6;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOY_INFO:
                    initDetailData((String) msg.obj);
                    break;
                case RECOMMEND_INFO:
                    recommendData((String) msg.obj);
                    break;
                case NetWorkError :    //显示网络错误页面
                    if(loadingView!=null)
                        loadingView.loadError();
                    break;
                case CloseLoadingView:     //关闭Loading动画
                    if(loadingView!=null)
                        loadingView.loadSuccess(false);
                    break;
                case PageLoading:       //页面加载中动画
                    if(loadingView!=null)
                        loadingView.load();
                    break;
                case MSG_SHOPPING_FINDALL://查询所有购物车信息
                    shopAllList = (List<ShppingCarHttpBean.WjlistBean>) msg.obj;
                    shppingCartSetting();
                    break;
            }
        }
    };
    private ToyBean toyBean;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private int toyCount = -1;
    private int id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_suit;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)){
            handler.sendEmptyMessage(NetWorkError);
            return;
        }else{
            handler.sendEmptyMessage(PageLoading);
        }
        initIntentData();

    }

    private void initIntentData() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        Log.i("radish", "id------------------" + id);
        toyUrl = AppConstants.TOY_SUIT_DETAIL;
        String recommendUrl = AppConstants.TOY_RECOMMEND_SUIT_DETAIL;
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        netPost(toyUrl, params);
        params = new HashMap<>();
        netPost(recommendUrl, params);
        //购物车信息

        findAllGood();
    }

    private void netPost(final String url, HashMap<String, Object> params) {
        try {
            OkHttpUtil.postJson(url, params, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {

                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.isSuccessful()) {
                        final String body = response.body().string();
                        Log.i("radish", "body------------------" + body);
                        if (!TextUtils.isEmpty(body) && !body.endsWith("null")) {
                            CloseLoadingView();
                            Message msg = Message.obtain();
                            if (url.equals(toyUrl)) {
                                msg.what = TOY_INFO;
                                Log.i("radish", "bodyToy------------------" + body);

                            } else {
                                msg.what = RECOMMEND_INFO;
                                Log.i("radish", "bodyRecommend------------------" + body);
                            }
                            msg.obj = body;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }


    private void recommendData(String obj) {
        //   ArrayList list = GsonUtil.parseJsonWithGson(obj, ArrayList.class);
        List<ToyBean> RecommendList = (List<ToyBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ToyBean>>() {
        }.getType());

        //猜你喜欢
        initProductLove(RecommendList);
    }

    private void initDetailData(String body) {
        //产品基本描述
        toyBean = GsonUtil.parseJsonWithGson(body, ToyBean.class);
        Picasso.with(DetailSuitActivity.this).load(AppConstants.IMG_BASE_URL + toyBean.getShopimg()).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(mIvDetailProduct);

        mTvDetailProductName.setText(toyBean.getName() + " " + toyBean.getShoptext());
        mTvDetailProductRentPrice.setText(toyBean.getShopprice() + "元/天");
        mTvDetailProductPrice.setText("吊牌价：" + toyBean.getDpj() + "元");
        mTvDetailBabyAge.setText(toyBean.getSuitage());
        //套餐简介
        mTvSuitInfo.setText(toyBean.getShoptext());


    }

    private void initProductLove(final List<ToyBean> list) {
        mRvDetailMoreSuit.setLayoutManager(new FullyGridLayoutManager(this, 2));
        ProductLoveRvAdapter rvAdapter = new ProductLoveRvAdapter(this, list);
        mRvDetailMoreSuit.setAdapter(rvAdapter);


        rvAdapter.setOnItemClickListener(new ProductLoveRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(DetailSuitActivity.this, "跳转", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailSuitActivity.this, DetailSuitActivity.class);
                //携带玩具id
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initEvent() {
        mBtnDetailShoppingcar.setOnClickListener(this);
        mIbDetailShare.setOnClickListener(this);
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_shoppingcar:
                addShoppingCar();
                break;
            case R.id.ib_detail_share:
                showDialogShared();
                break;
        }
    }

    private void showDialogShared() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shared);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
    }
    private void addData(){
        if (toyBean == null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", id);
            netPost(toyUrl, params);
            Toast.makeText(DetailSuitActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
        }
        if (toyCount<0){
            findAllGood();
            Toast.makeText(DetailSuitActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
        }
    }
    private void addShoppingCar() {
        uid = SPUtil.getUid(this);
        if (uid == null){
            startActivityForResult(new Intent(this,LoginActivity.class),INTENT_REQUEST_LOGIN);
            return;
        }
        if (toyCount<0 || toyBean == null) {
            addData();
        }
        if (toyBean.getKcl() - toyCount > 0) {
            ShoppingCartHttpBiz.addGood(id,uid[0],1);
            ToastHelper.getInstance().displayToastShort("加入购物车成功");
        } else {
            ToastHelper.getInstance().displayToastShort("库存量不足");
        }
    }
    /**
     * 查找所有购物车产品
     */
    public void findAllGood(){
        uid = SPUtil.getUid(this);
        if(uid ==null){
         //   startActivity(new Intent(this,LoginActivity.class));
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
                            SPUtil.setString(DetailSuitActivity.this,ShoppingCartHttpBiz.BUFFER_SHOPPING_CART,result);
                        }else{
                            //0 购物车无数据
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


    private void shppingCartSetting() {
        if (shopAllList != null){
            toyCount = ShoppingCartHttpBiz.findCountById(DetailSuitActivity.this, shopAllList,id);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_LOGIN){
            addData();
        }
    }
}
