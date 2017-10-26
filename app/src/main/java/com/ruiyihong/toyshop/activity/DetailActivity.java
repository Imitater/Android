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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.ProductLoveRvAdapter;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.bean.ToyBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ShareUtils;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

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

public class DetailActivity extends BaseActivity {
    @InjectView(R.id.tv_title_detail)
    TextView mTvTitleDetail;
    @InjectView(R.id.ib_detail_share)
    ImageButton mIbDetailShare;
    @InjectView(R.id.wv)
    WebView mWv;
    @InjectView(R.id.tv_pop_shopping_number)
    TextView mTvPopShoppingNumber;
    @InjectView(R.id.iv_shopping_cart_icon)
    ImageView mIvShoppingCartIcon;
    @InjectView(R.id.fl_pop_shopping)
    FrameLayout mFlPopShopping;
    @InjectView(R.id.bt_detail_shopping)
    Button mBtDetailShopping;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.rv_detail_product_love)
    RecyclerView mRvDetailProductLove;

    private ToyBean toyBean;
    public static int TOY_TYPE = 0;
    public static int BOOK_TYPE = 1;
    private static final int TOY_INFO = 2;
    private static final int RECOMMEND_INFO = 3;
    private static final int NetWorkError = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    private static final int MSG_SHOPPING_FINDALL = 7;
    private static final int MSG_SHOPPING_ADD = 8;
    private static final int INTENT_REQUEST_LOGIN = 9;
    private static final int MSG_SHARED = 10;
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
                        if (object.getInt("status") == 0) {
                            ToastHelper.getInstance().displayToastShort("添加购物车失败");
                        } else {
                            ToastHelper.getInstance().displayToastShort("添加购物车成功");
                            addShoppingSetting();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_SHARED:
                    String web_urls = (String) msg.obj;
//                    Log.e("radish", "web_url0000---" + web_url);
                    String[] split = web_urls.split("\"");
                    if (split != null && split.length >= 3) {
                        web_url = split[1] + "?id=" + id;
//                        web_url = split[1];
                        Log.e("radish", "web_url---" + web_url);
                        initWeb();
                    }
                    break;
            }
        }
    };

    private void initWeb() {
        Log.e("radish", "initWeb:web_url----- " + web_url);
        mWv.loadUrl(web_url);
//        mWv.postUrl(web_url,new byte[]{id.byteValue()});

        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;

            }
        });

        WebSettings settings = mWv.getSettings();
        settings.setJavaScriptEnabled(true);
    }


    private void recommendData(String obj) {
        List<ToyBean> RecommendList = (List<ToyBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ToyBean>>() {
        }.getType());

        //猜你喜欢
        initProductLove(RecommendList);
    }


    private void initProductLove(final List<ToyBean> list) {
        mRvDetailProductLove.setLayoutManager(new FullyGridLayoutManager(this, 2));
        ProductLoveRvAdapter rvAdapter = new ProductLoveRvAdapter(this, list);
        mRvDetailProductLove.setAdapter(rvAdapter);

        rvAdapter.setOnItemClickListener(new ProductLoveRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                //携带玩具id
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", type);
                startActivity(intent);

            }
        });
    }
    private String web_url;

    private void addShoppingSetting() {
        count++;
        toyCount++;
        Log.i("radish", "addcount------------------" + count);
        if (!mIvShoppingCartIcon.isSelected()) {
            mIvShoppingCartIcon.setSelected(true);
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");

        } else {
            mTvPopShoppingNumber.setText(count + "");
        }
    }

    private void shppingCartSetting() {
        if (shopAllList == null) {
            count = 0;
            mIvShoppingCartIcon.setSelected(false);
            mTvPopShoppingNumber.setVisibility(View.GONE);
        } else {
            count = ShoppingCartHttpBiz.findAllCount(DetailActivity.this, shopAllList);
            if (mIvShoppingCartIcon != null)
                mIvShoppingCartIcon.setSelected(true);
            if (mTvPopShoppingNumber != null) {
                mTvPopShoppingNumber.setVisibility(View.VISIBLE);
                mTvPopShoppingNumber.setText(count + "");
            }
            toyCount = ShoppingCartHttpBiz.findCountById(DetailActivity.this, shopAllList, id);
        }
    }

    private String toyUrl;
    private int count = -1;
    private int toyCount = 0;
    private int type;
    private int mKcl;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private Integer id;
    private String[] uid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }
        //处理从上一个页面传过来的数据
        initIntentData();

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
        findAllGood();
        if (count > 0) {
            mIvShoppingCartIcon.setSelected(true);
            mTvPopShoppingNumber.setVisibility(View.VISIBLE);
            mTvPopShoppingNumber.setText(count + "");
        } else {
            mIvShoppingCartIcon.setSelected(false);
            mTvPopShoppingNumber.setVisibility(View.GONE);
            mTvPopShoppingNumber.setText(count + "");

        }

    }

    private String recommendUrl;
    //12,13,14,15
    private void initIntentData() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        id = intent.getIntExtra("id", -1);
        Log.e("radish", "type,id------------------" + type + "," + id);
        if (type == TOY_TYPE) {
            toyUrl = AppConstants.TOY_DETAIL;
            recommendUrl = AppConstants.TOY_RECOMMEND_DETAIL;
            mTvTitleDetail.setText("玩具详情");
        } else {
            toyUrl = AppConstants.BOOK_DETAIL;
            recommendUrl = AppConstants.BOOK_RECOMMEND_DETAIL;
            mTvTitleDetail.setText("图书详情");
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("wid", id);
        params.put("type", type);
        netPost(AppConstants.SHARED_URL, params);
        params = new HashMap<>();
        params.put("id", id);
        netPost(toyUrl, params);
        params = new HashMap<>();
        netPost(recommendUrl, params);
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
                        Log.e("radish", "body------------------" + body);
                        if (!TextUtils.isEmpty(body) && !body.endsWith("null")) {
                            CloseLoadingView();
                            //TODO
                            Message msg = Message.obtain();
                            if (url.equals(toyUrl)) {
                                //玩具详情
                                msg.what = TOY_INFO;

                            } else if (url.equals(AppConstants.SHARED_URL)) {
                                //分享
                                msg.what = MSG_SHARED;
                                Log.e("radish", "shared_url---" + body);
                            }else if (AppConstants.TOY_RECOMMEND_DETAIL.equals(url)){
                                msg.what = RECOMMEND_INFO;

                            }else if (AppConstants.BOOK_RECOMMEND_DETAIL.equals(url)){

                                msg.what = RECOMMEND_INFO;
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

    private void initDetailData(String body) {
        //产品基本描述
        toyBean = GsonUtil.parseJsonWithGson(body, ToyBean.class);
        Log.e("radish", "产品基本描述------------------" + toyBean);
        mKcl = toyBean.getKcl();
    }

    @Override
    protected void initEvent() {
        mBtDetailShopping.setOnClickListener(this);
        mFlPopShopping.setOnClickListener(this);
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
            case R.id.bt_detail_shopping:
                addShoppingCar();
                break;
            case R.id.fl_pop_shopping:
                Intent intent = new Intent(this, ShoppingCarActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_detail_share:
                showDialogShared();
                break;
        }
    }

    private void addData() {
        if (toyBean == null) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", id);
            netPost(toyUrl, params);
            Toast.makeText(DetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
        }
        if (count < 0) {
            findAllGood();
            Toast.makeText(DetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
        }
    }

    private void addShoppingCar() {
        uid = SPUtil.getUid(this);
        if (uid == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), INTENT_REQUEST_LOGIN);
            return;
        }
        if (count < 0 || toyBean == null) {
            addData();
        }
        if (toyBean != null && count >= 0) {
            Log.i("radish", "mKcl------------------" + mKcl);
            Log.i("radish", "toyCount------------------" + toyCount);
            if (mKcl <= toyCount) {
                Log.e("radish", "addShoppingCar: 库存量不足");
                ToastHelper.getInstance().displayToastShort("库存量不足");
            } else {
                Log.e("radish", "addShoppingCar: 库存量充足");
                addGood(toyBean.getId(), this.uid[0], 1);
            }
        } else {
            ToastHelper.getInstance().displayToastShort("网络异常");
        }

    }

    //添加购物车
    public void addGood(int wid, String uid, int num) {
        String url = AppConstants.AddShoppingCar;
        Map<String, Object> para = new HashMap<>();
        para.put("wid", wid);
        para.put("uid", uid);
        para.put("shu", num);

        ShoppingCartHttpBiz.Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result != null && result.length() > 2) {
                    Message msg = Message.obtain();
                    msg.what = MSG_SHOPPING_ADD;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 查找所有购物车产品
     */
    public void findAllGood() {
        uid = SPUtil.getUid(this);
        if (uid == null) {
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
                        LogUtil.e("购物车接口回调结果： " + result);
                        ShppingCarHttpBean bean = GsonUtil.parseJsonWithGson(result, ShppingCarHttpBean.class);
                        int status = bean.getStatus();
                        List<ShppingCarHttpBean.WjlistBean> list = null;
                        if (status == 1) {
                            //1 购物车有数据
                            list = bean.getWjlist();
                            //本地保存缓存
                            SPUtil.setString(DetailActivity.this, ShoppingCartHttpBiz.BUFFER_SHOPPING_CART, result);
                        } else {
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

    private void showDialogShared() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shared);
        dialog.findViewById(R.id.tv_share_moments).setOnClickListener(new ShareOnClickListener());//朋友圈
        dialog.findViewById(R.id.tv_share_wechat).setOnClickListener(new ShareOnClickListener());//微信
        dialog.findViewById(R.id.tv_share_qq).setOnClickListener(new ShareOnClickListener());//qq
        dialog.findViewById(R.id.tv_share_qq_kj).setOnClickListener(new ShareOnClickListener());//qq空间

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
    }

    // TODO: 2017/8/29 分享
    class ShareOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ShareUtils shareUtils = new ShareUtils();
            switch (view.getId()) {
                case R.id.tv_share_moments:
                    /*网页的url
     * @param title       显示分享网页的标题
     * @param description 对网页的描述
     * @param scene       分享方式：好友还是朋友圈 1朋友圈 0好友*/
                    ShareUtils.shareUrl(DetailActivity.this, web_url, mTvTitleDetail.getText().toString(), AppConstants.IMG_BASE_URL + toyBean.getShopimg(), null, 1);
                    break;
                case R.id.tv_share_wechat:
                    //分享至微信好友
                    ShareUtils.shareUrl(DetailActivity.this, web_url, toyBean.getName(), AppConstants.IMG_BASE_URL + toyBean.getShopimg(), null, 0);
                    break;
                case R.id.tv_share_qq:
                    //分享至qq好友
                    //分享至朋友圈
                    if (toyBean != null) {
                        shareUtils.shareToQq(DetailActivity.this, toyBean.getName(), null, web_url, AppConstants.IMG_BASE_URL + toyBean.getShopimg(), new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                //ToastHelper.getInstance().displayToastShort("分享成功");
                            }

                            @Override
                            public void onError(UiError uiError) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                    break;
                case R.id.tv_share_qq_kj:
                    //分享至qq空间
                    shareUtils.shareToQzone(DetailActivity.this, toyBean.getName(), null, web_url, AppConstants.IMG_BASE_URL + toyBean.getShopimg(), null);
                    break;
            }
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
        if (requestCode == INTENT_REQUEST_LOGIN) {
            addData();
        }
    }
}
