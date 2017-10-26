package com.ruiyihong.toyshop.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.ChooseCityActivity;
import com.ruiyihong.toyshop.activity.DetailActivity;
import com.ruiyihong.toyshop.activity.DetailSuitActivity;
import com.ruiyihong.toyshop.activity.EventActivity;
import com.ruiyihong.toyshop.activity.JoinActivity;
import com.ruiyihong.toyshop.activity.LessonActivity;
import com.ruiyihong.toyshop.activity.LunboDetailActivity;
import com.ruiyihong.toyshop.activity.MoreToyActivity;
import com.ruiyihong.toyshop.activity.MoreTuijianActivity;
import com.ruiyihong.toyshop.activity.SearchActivity;
import com.ruiyihong.toyshop.activity.ToyShopActivity;
import com.ruiyihong.toyshop.activity.VipActivity;
import com.ruiyihong.toyshop.activity.VipMemberBuyActivity;
import com.ruiyihong.toyshop.adapter.HomeRvAdapter;
import com.ruiyihong.toyshop.adapter.HomeRvMoudleAdpater;
import com.ruiyihong.toyshop.adapter.HomeVpAdapter;
import com.ruiyihong.toyshop.bean.HomeJXTC;
import com.ruiyihong.toyshop.bean.HomeJXTCdown;
import com.ruiyihong.toyshop.bean.HomeLunboBean;
import com.ruiyihong.toyshop.bean.HomeNewBook;
import com.ruiyihong.toyshop.bean.HomeNewToy;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetUtli;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.DividerItemDecoration;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;
import com.ruiyihong.toyshop.view.MyImageView;
import com.ruiyihong.toyshop.view.MyScrollView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.squareup.picasso.Picasso;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * 首页 123
 */
public class HomeFragment extends BaseFragment {
    public static final int CHOOSE_CITY = 10;
    public static final int CHOOSE_CITY_RESULT = 11;
    private static final String HOME_VPDATA_BUFFER_KEY = "home_vpdata_buffer_key";//轮播图缓存key
    private static final String HOME_NEWTOY_BUFFER_KEY = "home_newtoy_buffer_key";//最新玩具缓存key
    private static final String HOME_NEWBOOK_BUFFER_KEY = "home_newbook_buffer_key";//最新玩具缓存key
    private static final String HOME_JXTC_BUFFER_KEY = "home_jxtc_buffer_key"; //精选套餐缓存key
    private static final String HOME_JXTC_LIST_BUFFER_KEY = "home_jxtc_list_buffer_key";//精选套餐列表缓存key
    @InjectView(R.id.tv_home_local_city)
    TextView mTvHomeLocalCity;
    @InjectView(R.id.et_home_search)
    EditText mEtHomeSearch;
    @InjectView(R.id.ib_erweima)
    ImageButton mIbErweima;
    @InjectView(R.id.vp_home)
    ViewPager mVpHome;
    @InjectView(R.id.rv_module)
    RecyclerView mRvModule;
    @InjectView(R.id.tv_home_moretoy)
    TextView mTvHomeMoretoy;
    @InjectView(R.id.rv_newest_toy)
    RecyclerView mRvNewestToy;
    @InjectView(R.id.tv_home_morebook)
    TextView mTvHomeMorebook;
    @InjectView(R.id.rv_newest_book)
    RecyclerView mRvNewestBook;
    @InjectView(R.id.tv_home_set)
    TextView mTvHomeSet;
    @InjectView(R.id.iv_home_jxtc)
    ImageView mIvHomeJxtc;
    @InjectView(R.id.rv_jxtc)
    RecyclerView mRvJxtc;
    @InjectView(R.id.iv_home_alltoy)
    MyImageView mIvHomeAlltoy;
    @InjectView(R.id.iv_home_allbook)
    MyImageView mIvHomeAllbook;
    @InjectView(R.id.ll_choose_city)
    LinearLayout mLlChooseCity;
    @InjectView(R.id.tv_home_set_pay)
    TextView mTvHomeSetPay; //精选套餐透明层  价格
    @InjectView(R.id.tv_home_set_name)
    TextView mTvHomeSetName; //精选套餐透明层  玩具名称
    @InjectView(R.id.tv_home_set_age)
    TextView mTvHomeSetAge; //精选套餐透明层  适用年龄
    @InjectView(R.id.ll_jxtc)
    LinearLayout mLlJxtc; //精选套餐 半透明布局
    @InjectView(R.id.ll_alltoy)
    LinearLayout mLlAlltoy; //全部玩具 半透明布局
    @InjectView(R.id.ll_allbook)
    LinearLayout mLlAllbook; //全部图书 半透明布局
    @InjectView(R.id.ll_home_vp_point)
    LinearLayout mLlHomeVpPoint;
    @InjectView(R.id.TopTitleBar)
    LinearLayout TopTitleBar;
    @InjectView(R.id.MyScrollView)
    MyScrollView MyScrollView;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @InjectView(R.id.iv_home_zsjm)
    MyImageView mIvHomeZsjm;
    @InjectView(R.id.tv_home_zsjm)
    TextView mTvHomeZsjm;


    private HomeRvAdapter mToyAdapter;
    private HomeRvAdapter mBookAdapter;

    private HomeRvAdapter mJXTCAdapter;

    private LocationClient mLocationClient;
    private MyBDLocationListener bdLocationListener;
    private static final String[] PERMISSIONS_CONTACT = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private static final int REQUEST_CONTACTS = 100;
    private static final int REQUEST_CEMERA = 120;
    private int mDividerLineColor;
    private String[] lunboUrls;
    private String[] newtoyUrls;
    private String[] newtoyInfo;
    private String[] newtoyPrice;
    private String[] newtoyUrls1;
    private String[] newtoyInfo1;
    private String[] newtoyPrice1;
    private String[] newtoyUrls2;
    private String[] newtoyInfo2;
    private String[] newtoyPrice2;
    private float density;
    private int preSelectedPoint = 0;
    private HomeLunboBean mlunbo;
    private HomeJXTC homeJXTC;

    private static final int NetWorkError = 1;
    private static final int PageLoading = 2;
    private static final int CloseLoadingView = 3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:    //轮播图
                    int i = mVpHome.getCurrentItem();
                    mVpHome.setCurrentItem(i + 1);
                    handler.sendEmptyMessageDelayed(0, 3000);
                    break;
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;
                case PageLoading:       //页面加载中动画
                    if (loadingView != null)
                        loadingView.load();
                    break;
                case CloseLoadingView:     //关闭Loading动画
                    if (loadingView != null)
                        loadingView.loadSuccess(false);
                    break;

            }
        }
    };

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_home, null);
        return view;
    }

    @Override
    protected void initData() {
        boolean networkAvailable = NetWorkUtil.isNetWorkAvailable(mActivity);
        if (!networkAvailable) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }

        mDividerLineColor = getResources().getColor(R.color.divider);
        density = mActivity.getResources().getDisplayMetrics().density;
        ButterKnife.inject(mActivity);
        //定位
        initLocation();
        //模块
        initModules();

        //修改透明图层的宽高
        initCoverage();

        new Thread(new Runnable() {
            @Override
            public void run() {


                //轮播图
                initViewPager();

                try {
                    //最新玩具
                    initToy();
                    //最新图书
                    initBook();
                    //精选套餐
                    //JXTC();
                    //精选套餐下面的三个图片
                    initJxtc();
                } catch (Exception e) {
                    LogUtil.e("initdata==exception==="+e.getMessage());
                    handler.sendEmptyMessage(NetWorkError);
                }
            }
        }).start();

    }

    private void initLocation() {
        String lastLocation = SPUtil.getString(mActivity, AppConstants.LAST_LOCATION, "");

        if (!TextUtils.isEmpty(lastLocation)) {
            mTvHomeLocalCity.setText(lastLocation);
        }
        mLocationClient = new LocationClient(mActivity.getApplicationContext());
        //声明LocationClient类
        bdLocationListener = new MyBDLocationListener();
        mLocationClient.registerLocationListener(bdLocationListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        int span = 1000;
        option.setIsNeedAddress(true);
        option.setScanSpan(span);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        if (Build.VERSION.SDK_INT >= 23) {
            showSetPermission();
        } else {
            mLocationClient.start();
        }
    }

    private void showSetPermission() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //ToastHelper.getInstance()._toast("没有权限,请手动开启定位权限");
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            this.requestPermissions(PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        } else {
            mLocationClient.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case REQUEST_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    mLocationClient.start();
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastHelper.getInstance().displayToastShort("获取位置权限失败，请手动开启");
                }
                break;
            case REQUEST_CEMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    startActivityForResult(new Intent(mActivity, CaptureActivity.class), 0);
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    ToastHelper.getInstance().displayToastShort("请手动打开相机权限");
                }
                break;
            default:
                break;
        }
    }


    /**
     * 透明图层相关初始化
     */
    private void initCoverage() {
        //精选套餐宽高
        mIvHomeJxtc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int tc_w = mIvHomeJxtc.getWidth();
                int tc_h = mIvHomeJxtc.getHeight();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlJxtc.getLayoutParams();
                params.width = tc_w / 2;
                params.height = tc_h / 2;
                mLlJxtc.requestLayout();

                //全部玩具
                mIvHomeAlltoy.measure(0, 0);
                int toy_w = mIvHomeAlltoy.getMeasuredWidth();
                int toy_h = mIvHomeAlltoy.getMeasuredHeight();

                params = (RelativeLayout.LayoutParams) mLlAlltoy.getLayoutParams();
                params.width = toy_w / 2;
                params.height = toy_h / 2;
                mLlAlltoy.requestLayout();

                //全部图书
                mIvHomeAllbook.measure(0, 0);
                int book_w = mIvHomeAllbook.getMeasuredWidth();
                int book_h = mIvHomeAllbook.getMeasuredHeight();

                params = (RelativeLayout.LayoutParams) mLlAllbook.getLayoutParams();
                params.width = book_w / 2;
                params.height = book_h / 2;
                mLlAllbook.requestLayout();

                //移除监听
                mIvHomeJxtc.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

    }

    public void JXTC() throws IOException {
        //加载缓存数据
        String buffer = SPUtil.getString(mActivity, HOME_JXTC_BUFFER_KEY, "");
        if (NetUtli.isNetworkAvailable(mActivity)) {
            //网络可用
            if (!TextUtils.isEmpty(buffer)) {
                //有缓存数据，先解析缓存数据
                try {

                    parseJXTCD(buffer);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            //网络不可用
            if (!TextUtils.isEmpty(buffer)) {
                //有缓存数据，先解析缓存数据
                try {

                    parseJXTCD(buffer);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                handler.sendEmptyMessage(NetWorkError);
            }
            return;
        }

        //精选套餐没有参数
        String url = AppConstants.SERVE_URL + "index/index/showimg";
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NetWorkError);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);

                if (!TextUtils.isEmpty(result)) {
                    if (result.length() > 2) {
                        try {
                            parseJXTCD(result);
                            //缓存数据
                            SPUtil.setString(mActivity, HOME_JXTC_BUFFER_KEY, result);
                        }catch (Exception e){
                            e.printStackTrace();
                            handler.sendEmptyMessage(NetWorkError);
                        }
                        CloseLoadingView();
                    } else {
                        LogUtil.e("当前城市没有精选套餐");
                    }
                }
            }
        });
    }

    private void parseJXTCD(String result) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HomeJXTC>>() {
        }.getType();

        ArrayList<HomeJXTC> jxtc = gson.fromJson(result, type);
        if (jxtc.size() > 0) {
            homeJXTC = jxtc.get(0);
            final String url = AppConstants.IMG_BASE_URL + homeJXTC.getShopimg();
            // TODO: 2017/8/29 图片有问题
            // OkHttpUtil.getPic(url, mIvHomeJxtc, mActivity);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(mActivity).load(url).placeholder(R.mipmap.lunbo_default).into(mIvHomeJxtc);
                    if (mTvHomeSetPay != null) {
                        mTvHomeSetPay.setText(homeJXTC.getShopprice());
                        mTvHomeSetAge.setText(homeJXTC.getSuitage());
                        mTvHomeSetName.setText(homeJXTC.getName());
                    }
                }
            });
        }
    }

    /**
     * 精选套餐下面的三个
     */
    private void initJxtc(){
        Log.e("radish_huida", "initJxtc: 强力推荐init" );
        //加载缓存数据
        String buffer = SPUtil.getString(mActivity, HOME_JXTC_LIST_BUFFER_KEY, "");
        if (!TextUtils.isEmpty(buffer)) {
            //有缓存数据，先解析缓存数据
            try {
                parseJXTCData(buffer);

            }catch (Exception e){
//                net_jxtc();
            }finally {
                net_jxtc();
            }
        }else{
            net_jxtc();
        }
    }

    private void net_jxtc() {

        LogUtil.e("具体推荐下面的 init====");
        // String url = AppConstants.SERVE_URL + "index/index/jttjwj";
        String url = AppConstants.SERVE_URL + "/index/index/sytj";
        //Map para = new HashMap<String, Object>();
        //TODO 精选套餐
        // 再此填写获取城市，由于后台现在只有上海的数据，所以写固定值
        // para.put("cname", "秦皇岛");
        try {
            OkHttpUtil.get(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("具体推荐下面的三个图片Error");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("首页精选套餐============" + result);
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            parseJXTCData(result);
                            //缓存数据
                            SPUtil.setString(mActivity, HOME_JXTC_LIST_BUFFER_KEY, result);
                        }catch (Exception e){
                            e.printStackTrace();
                            handler.sendEmptyMessage(NetWorkError);
                        }
                        CloseLoadingView();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJXTCData(String result) {
        Log.e("radish_huida", "parseJXTCData: 强力推荐parse" );
        HomeJXTCdown jxtcBean = GsonUtil.parseJsonWithGson(result, HomeJXTCdown.class);
        final List<HomeJXTCdown.DataBean> homenewtoys = jxtcBean.getData();
        int l = homenewtoys.size();
        newtoyUrls2 = new String[l];
        newtoyInfo2 = new String[l];
        newtoyPrice2 = new String[l];

        for (int i = 0; i < l; i++) {
            String ImgUrl = AppConstants.IMG_BASE_URL + homenewtoys.get(i).getShopimg();
            newtoyUrls2[i] = ImgUrl;
            newtoyInfo2[i] = homenewtoys.get(i).getName();
            newtoyPrice[i]=homenewtoys.get(i).getShopprice()+"";
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRvJxtc != null) {
                    mRvJxtc.setLayoutManager(new FullyLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
                    mRvJxtc.addItemDecoration(new DividerItemDecoration(
                            mActivity, DividerItemDecoration.VERTICAL_LIST, 1, mDividerLineColor));
                    mJXTCAdapter = new HomeRvAdapter(mActivity, newtoyUrls2, newtoyInfo2, newtoyPrice, true);
                    mRvJxtc.setAdapter(mJXTCAdapter);
                    //最新玩具条目点击，跳转玩具详情页面
                    mJXTCAdapter.setOnItemClickListener(new HomeRvAdapter.onRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Intent intent = new Intent(mActivity, DetailActivity.class);
                            //携带玩具id
                            intent.putExtra("id", homenewtoys.get(position).getId());
                            if (homenewtoys.get(position).getType() ==0){
                                intent.putExtra("type",DetailActivity.BOOK_TYPE);
                            }else{
                                intent.putExtra("type",DetailActivity.TOY_TYPE);
                            }
                            startActivityForResult(intent, 0);
                        }
                    });
                }
            }
        });
    }
    /**
     * 最新玩具
     */
    private void initToy() {
        //加载缓存数据
        String buffer = SPUtil.getString(mActivity, HOME_NEWTOY_BUFFER_KEY, "");
        Log.e("radish", "initBook: buffer_tushu---"+buffer );
        if (!TextUtils.isEmpty(buffer)) {
            //有缓存数据，先解析缓存数据
            try {
                parseToyData(buffer);

            }catch (Exception e){
                Log.e("radish_huida", "initBook: buffer错误" );
            }finally {
                net_toy();
            }
        }else{
            net_toy();
        }

    }

    private void net_toy() {
        Map para = new HashMap<String, Object>();
        //TODO 替换成详细城市
        //para.put("cname", "北京");
        // String url = AppConstants.SERVE_URL + "/index/index/jttjwj";
        String url = AppConstants.SERVE_URL + "/index/index/zxwj";
        try {
            OkHttpUtil.postString(url, para, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                    LogUtil.e("首页最新玩具==error==" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("首页最新玩具===" + result);
                    if (result != null) {
                        if (result.length() > 2) {
                            //缓存数据
                            try {
                                parseToyData(result);
                                SPUtil.setString(mActivity, HOME_NEWTOY_BUFFER_KEY, result);

                            }catch (Exception e){
                                e.printStackTrace();
                                handler.sendEmptyMessage(NetWorkError);
                            }
                            CloseLoadingView();
                        } else {
                            LogUtil.e("当前城市没有最新玩具");
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO 最新图书
     */
    private void initBook(){
        //加载缓存数据
        String buffer = SPUtil.getString(mActivity, HOME_NEWBOOK_BUFFER_KEY, "");
        Log.e("radish", "initBook: buffer_tushu---"+buffer );
        if (!TextUtils.isEmpty(buffer)) {
            //有缓存数据，先解析缓存数据
            try {
                parseBookData(buffer);

            }catch (Exception e){
                Log.e("radish_huida", "initBook: buffer错误" );
            }finally {
                net_book();
            }
        }else{
            net_book();
        }
    }

    private void net_book() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRvNewestBook.setLayoutManager(new FullyLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
                mRvNewestBook.addItemDecoration(new DividerItemDecoration(
                        mActivity, DividerItemDecoration.VERTICAL_LIST, 1, mDividerLineColor));
            }
        });
        // String url = AppConstants.SERVE_URL + "index/index/jtzxts";
        String url = AppConstants.SERVE_URL + "index/index/zxts";
        Log.e("radish_huida", "initBook: 最新图书"+url );
//        Map para = new HashMap<String, Object>();
        // todo 再此填写获取城市信息
        //para.put("cname", "北京");
        try {
            OkHttpUtil.get(url,  new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                    Log.e("radish_huida", "initBook: 最新图书访问失败" );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("radish_huida", "initBook: 最新图书访问成功   " );
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("首页最新图书===" + result);
                    if (result != null) {
                        //缓存数据
                        if (result.length() > 2) {
                            try{
                                parseBookData(result);
                                SPUtil.setString(mActivity, HOME_NEWBOOK_BUFFER_KEY, result);
                            }catch (Exception e){
                                e.printStackTrace();
                                handler.sendEmptyMessage(NetWorkError);
                            }
                            CloseLoadingView();
                        } else {
                            LogUtil.e("当前城市没有最新图书");
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //图书json数据解析方法
    private void parseBookData(String result) {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HomeNewBook>>() {
        }.getType();
        final ArrayList<HomeNewBook> homenewtoys = gson.fromJson(result, type);
        int l = homenewtoys.size();
        newtoyUrls = new String[l];
        newtoyInfo = new String[l];
        newtoyPrice = new String[l];

        for (int i = 0; i < l; i++) {
            String ImgUrl = AppConstants.IMG_BASE_URL + homenewtoys.get(i).getShopimg();
            newtoyUrls[i] = ImgUrl;
            newtoyInfo[i] = homenewtoys.get(i).getNname();
            newtoyPrice[i] = homenewtoys.get(i).getShopprice() + "";
            LogUtil.e("首页最新图书name=="+homenewtoys.get(i).getNname());
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRvNewestBook != null) {
                    mRvNewestBook.setLayoutManager(new FullyLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
                    mRvNewestBook.addItemDecoration(new DividerItemDecoration(
                            mActivity, DividerItemDecoration.VERTICAL_LIST, 1, mDividerLineColor));
                    mBookAdapter = new HomeRvAdapter(mActivity, newtoyUrls, newtoyInfo, newtoyPrice, true);
                    mRvNewestBook.setAdapter(mBookAdapter);
                    //最新图书条目点击，跳转图书详情页
                    mBookAdapter.setOnItemClickListener(new HomeRvAdapter.onRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Intent intent = new Intent(mActivity, DetailActivity.class);
                            //携带玩具id
                            intent.putExtra("type", DetailActivity.BOOK_TYPE);
                            intent.putExtra("id", homenewtoys.get(position).getId());
                            startActivityForResult(intent, 0);
                        }
                    });
                }

            }
        });
    }

    //玩具json数据解析方法
    private void parseToyData(String result) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<HomeNewToy>>() {
            }.getType();

            final ArrayList<HomeNewToy> homenewtoys = gson.fromJson(result, type);

            int l = homenewtoys.size();
            newtoyUrls1 = new String[l];
            newtoyInfo1 = new String[l];
            newtoyPrice1 = new String[l];

            for (int i = 0; i < l; i++) {
                String ImgUrl = AppConstants.IMG_BASE_URL + homenewtoys.get(i).getShopimg();
                newtoyUrls1[i] = ImgUrl;
                newtoyInfo1[i] = homenewtoys.get(i).getName();
                newtoyPrice1[i] = homenewtoys.get(i).getShopprice() + "";
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRvNewestToy != null) {
                        mRvNewestToy.setLayoutManager(new FullyGridLayoutManager(mActivity, 1, LinearLayoutManager.HORIZONTAL, false));
                        mRvNewestToy.addItemDecoration(new DividerItemDecoration(
                                mActivity, DividerItemDecoration.VERTICAL_LIST, 1, mDividerLineColor));

                        mToyAdapter = new HomeRvAdapter(mActivity, newtoyUrls1, newtoyInfo1, newtoyPrice1, true);
                        mRvNewestToy.setAdapter(mToyAdapter);

                        //最新玩具条目点击，跳转玩具详情页面
                        mToyAdapter.setOnItemClickListener(new HomeRvAdapter.onRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Intent intent = new Intent(mActivity, DetailActivity.class);
                                //携带玩具id
                                intent.putExtra("type", DetailActivity.TOY_TYPE);
                                intent.putExtra("id", homenewtoys.get(position).getId());
                                startActivityForResult(intent, 0);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            handler.sendEmptyMessage(NetWorkError);
        }
    }
    /**
     * 轮播图数据
     */
    private void initViewPager() {
        //加载缓存数据
        String buffer = SPUtil.getString(mActivity, HOME_VPDATA_BUFFER_KEY, "");
        if (!TextUtils.isEmpty(buffer)) {
            //有缓存数据，先解析缓存数据
            try {
                parseLunboData(buffer);

            }catch (Exception e){
                e.printStackTrace();
            }
            //关闭LoadingView
            CloseLoadingView();
        }
        try {
            OkHttpUtil.get(AppConstants.HOME_LUNBOTU_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && !"null".equals(response) && response.code() == 200) {
                        final String result = response.body().string();
                        //解析数据
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    parseLunboData(result);
                                    //缓存数据
                                    SPUtil.setString(mActivity, HOME_VPDATA_BUFFER_KEY, result);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(NetWorkError);
                                }
                                CloseLoadingView(); //关闭LoadingView
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析轮播图数据
     *
     * @param result json数据
     */
    private void parseLunboData(String result) {
        try {
            mlunbo = GsonUtil.parseJsonWithGson(result, HomeLunboBean.class);
        } catch (Exception e) {
            handler.sendEmptyMessage(NetWorkError);
        }
        if (mlunbo == null) {
            return;
        }
        final List<HomeLunboBean.DataBean> lunboList = mlunbo.getData();
        int l = lunboList.size();
        lunboUrls = new String[l];
        if (mLlHomeVpPoint != null && mLlHomeVpPoint.getChildCount() != 0) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLlHomeVpPoint.removeAllViews();
                }
            });

        }
        for (int i = 0; i < l; i++) {
            String ImgUrl = AppConstants.IMG_BASE_URL + lunboList.get(i).getAdimg();
            lunboUrls[i] = ImgUrl;
            //初始化点
            final ImageView point = new ImageView(mActivity);
            point.setImageResource(R.drawable.nomarl_point);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (5 * density), (int) (5 * density));
            if (i != 0) {
                params.leftMargin = (int) (10 * density);
            }
            point.setLayoutParams(params);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLlHomeVpPoint != null)
                        mLlHomeVpPoint.addView(point);
                }
            });

        }
        //　欢迎使用使用Picasso
        if (mVpHome != null) {
            HomeVpAdapter homeVpAdapter = new HomeVpAdapter(lunboUrls, mActivity);
            mVpHome.setAdapter(homeVpAdapter);
            homeVpAdapter.setOnItemClickListener(new View.OnTouchListener() {
                int start = 0;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            start = (int) System.currentTimeMillis();
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            int end = (int) System.currentTimeMillis();
                            handler.sendEmptyMessageDelayed(0, 3000);
                            if (end - start < 500) {
                                //点击，跳转详情页
                                String url = lunboList.get(mVpHome.getCurrentItem() % lunboList.size()).getUrl();
                                Intent intent = new Intent(mActivity, LunboDetailActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            handler.sendEmptyMessageDelayed(0, 3000);
                            break;
                    }
                    return true;
                }
            });
        }

        if (mVpHome != null)
           /* mVpHome.setOnTouchListener(new View.OnTouchListener() {
                int start = 0;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            start = (int) System.currentTimeMillis();
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            int end = (int) System.currentTimeMillis();
                            handler.sendEmptyMessageDelayed(0, 3000);
                            *//*if (end-start<500){
                                //点击，跳转详情页
                                String url = lunboList.get(mVpHome.getCurrentItem() % lunboList.size()).getUrl();
                                Intent intent = new Intent(mActivity, LunboDetailActivity.class);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            }*//*
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            handler.sendEmptyMessageDelayed(0, 3000);
                            break;
                    }
                    return false;
                }
            });*/
            if (mVpHome != null) {
                preSelectedPoint = mVpHome.getCurrentItem();
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0, 3000);
                ImageView point = (ImageView) mLlHomeVpPoint.getChildAt(preSelectedPoint);
                if (point != null)
                    point.setImageResource(R.drawable.blue_point);
            }
    }

    /**
     * 六大模块
     */
    private void initModules() {
        final int[] module_images = {R.mipmap.home_jxwj, R.mipmap.home_jxts, R.mipmap.home_txhd, R.mipmap.home_kcgm, R.mipmap.li_jfsc, R.mipmap.home_hytx};
        final String[] module_strs = {"精选玩具", "精选图书", "特色活动", "课程购买", "会员办理", "会员体系"};

        mRvModule.setLayoutManager(new GridLayoutManager(mActivity, 3));
        HomeRvMoudleAdpater moudleAdpater = new HomeRvMoudleAdpater(mActivity, module_images, module_strs);
        mRvModule.setAdapter(moudleAdpater);
        moudleAdpater.setOnItemClickListener(new HomeRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 六大模块
                LogUtil.e(position + "" + module_strs[position]);
                Intent intent = null;
                switch (position) {
                    case 0://玩具城
                        intent = new Intent(mActivity, ToyShopActivity.class);
                        intent.putExtra("type", ToyShopActivity.DATA_TYPE_TOY);
                        break;
                    case 1://图书城
                        intent = new Intent(mActivity, ToyShopActivity.class);
                        intent.putExtra("type", ToyShopActivity.DATA_TYPE_BOOK);
                        break;
                    case 2://特色活动
                        intent = new Intent(mActivity, EventActivity.class);
                        break;
                    case 3://课程购买
                        intent = new Intent(mActivity, LessonActivity.class);
                        break;
                    case 4:
                        //会员办理
                        intent = new Intent(mActivity, VipMemberBuyActivity.class);
                        break;
                    case 5:
                        intent = new Intent(mActivity, VipActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(bdLocationListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(bdLocationListener);
        }
    }


    @Override
    protected void initEvent() {
        //定位城市，点击跳转选择城市页面
        mTvHomeLocalCity.setOnClickListener(this);
        //viewpager的选中监听
        mVpHome.setOnPageChangeListener(new MyOnPageChangeListener());
        //更多玩具
        mTvHomeMoretoy.setOnClickListener(this);
        //更多图书
        mTvHomeMorebook.setOnClickListener(this);
        //搜索，跳转到搜索页面
        mEtHomeSearch.setOnClickListener(this);
        //下拉取消
        // MyScrollView.setScrollListener(new myScrollListener());
        //强力推荐
        mIvHomeJxtc.setOnClickListener(this);
        mTvHomeSet.setOnClickListener(this);
        // 网络错误的刷新按钮
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                handler.sendEmptyMessage(PageLoading);
                try {
                    initViewPager();//轮播图
                    initToy();  //最新玩具
                    initModules();//六大模块
                    initBook();//最新图书
                   // JXTC(); //精选套餐
                    initJxtc();//精选套餐下面的三个图片
                } catch (Exception e) {
                    handler.sendEmptyMessage(NetWorkError);
                }
            }
        });
        mIbErweima.setOnClickListener(this);


        smartRefreshLayout.setEnableLoadmore(false);

        smartRefreshLayout.setOnMultiPurposeListener(new Refresh_Listener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                super.onRefresh(refreshlayout);

            }
        });

        //招商加盟——点击加入
        mTvHomeZsjm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_home_local_city:
                String city = SPUtil.getString(mActivity, AppConstants.LAST_LOCATION, "定位失败");
                Intent intent = new Intent(mActivity, ChooseCityActivity.class);
                intent.putExtra("local_city",city);
                startActivityForResult(intent, CHOOSE_CITY);
                break;
            case R.id.et_home_search:
                Intent intent1 = new Intent(mActivity, SearchActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_home_moretoy://最新玩具，更多玩具
                Intent toy_intent = new Intent(mActivity, MoreToyActivity.class);
                toy_intent.putExtra("type", MoreToyActivity.DATA_TYPE_TOY);
                startActivity(toy_intent);
                break;
            case R.id.tv_home_morebook://最新图书，更多图书
                Intent book_intent = new Intent(mActivity, MoreToyActivity.class);
                book_intent.putExtra("type", MoreToyActivity.DATA_TYPE_BOOK);
                startActivity(book_intent);
                break;
            case R.id.iv_home_jxtc://强力推荐详情页
                if (homeJXTC != null) {
                    Intent suit_intent = new Intent(mActivity, DetailSuitActivity.class);
                    suit_intent.putExtra("id", homeJXTC.getId());
                    startActivity(suit_intent);
                }
                break;
            case R.id.tv_home_set:
                //更多强力推荐
                Intent tuijian_intent = new Intent(mActivity, MoreTuijianActivity.class);
                startActivity(tuijian_intent);
                break;
            case R.id.ib_erweima:
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //先判断有没有权限 ，没有就在这里进行权限的申请
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CEMERA);
                    } else {
                        startActivityForResult(new Intent(mActivity, CaptureActivity.class), 0);
                    }
                } else {
                    startActivityForResult(new Intent(mActivity, CaptureActivity.class), 0);
                }
                break;
            case R.id.tv_home_zsjm:
                //招商加盟——点击加入
                startActivity(new Intent(mActivity, JoinActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                ToastHelper.getInstance().displayToastShort(bundle.getString("result"));
            }
        }else if (requestCode == CHOOSE_CITY  && resultCode == CHOOSE_CITY_RESULT){
            //从选择城市页面跳转回来
            Bundle bundle = data.getExtras();
            if(bundle!=null) {
               String city = bundle.getString("city");
                mTvHomeLocalCity.setText(city);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(bdLocationListener);
        }
    }


    private class MyBDLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int locType = bdLocation.getLocType();
                    String error = "";
                    switch (locType) {
                        case 62:
                        case 63:
                            error = "定位失败，请检查网络";
                            break;
                        case 167:
                            error = "定位失败，请检查权限";
                            break;
                    }
                    if (!TextUtils.isEmpty(error)) {
                        if (mLocationClient != null)
                            mLocationClient.stop();
                        return;
                    }
                    if (!TextUtils.isEmpty(bdLocation.getCity()) && mTvHomeLocalCity != null) {
                        //判断本地定位和上次定位的不同，切换不同的城市
                        String city = bdLocation.getCity();
                        city = city.substring(0, city.length() - 1);
                        mTvHomeLocalCity.setText(city);
                        SPUtil.setString(mActivity, AppConstants.LAST_LOCATION, city);
                    }
                }
            });
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //点的选中切换
            position = position % mlunbo.getData().size();
            ImageView point = (ImageView) mLlHomeVpPoint.getChildAt(position);
            if (point != null) {
                point.setImageResource(R.drawable.blue_point);
            }
            ImageView prePoint = (ImageView) mLlHomeVpPoint.getChildAt(preSelectedPoint);
            if (prePoint != null) {
                prePoint.setImageResource(R.drawable.nomarl_point);
            }
            preSelectedPoint = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private class myScrollListener implements com.ruiyihong.toyshop.view.MyScrollView.ScrollListener {
        @Override
        public void scrollOritention(int l, int vertical, int oldl, int oldvertical) {
            /**
             *l当前水平滚动的开始位置
             *t当前的垂直滚动的开始位置
             *oldl上一次水平滚动的位置。
             *oldt上一次垂直滚动的位置。
             **/
            int i = ScreenUtil.dp2px(mActivity, 50);
            if (vertical < i) {
                TopTitleBar.setVisibility(View.VISIBLE);
                //  TopTitleBar
                if ((vertical - oldvertical) > 0) {
                    //上
                    float v = 1.0f / i;
                    float alpha = TopTitleBar.getAlpha();
                    // LogUtil.e(alpha+"");
                    if (alpha > 0.0f)
                        TopTitleBar.setAlpha(alpha - v * (vertical - oldvertical));
                } else {
                    //下
                    if (vertical - oldvertical < 0) {
                        float v = 1.0f / i;
                        float alpha = TopTitleBar.getAlpha();
                        if (alpha < 1.0f)
                            TopTitleBar.setAlpha(alpha + v * (oldvertical - vertical));
                    }
                }
            } else {
                TopTitleBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }


}
