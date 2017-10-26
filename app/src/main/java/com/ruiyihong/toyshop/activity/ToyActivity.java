package com.ruiyihong.toyshop.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.PopAgeBean;
import com.ruiyihong.toyshop.bean.PopBrandBean;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.fragment.ToyShopBookFragment;
import com.ruiyihong.toyshop.fragment.ToyShopToyFragment;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.view.CommonLoadingView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/9/28.
 * 精品玩具
 */

public class ToyActivity extends BaseActivity {


    @InjectView(R.id.toyshop_search)
    ImageButton mToyshopSearch;
    @InjectView(R.id.tv_toyshop_filter_allage)
    TextView mTvToyshopFilterAllage;
    @InjectView(R.id.tv_toyshop_filter_allbrand)
    TextView mTvToyshopFilterAllbrand;
    @InjectView(R.id.tv_toyshop_filter_fenlei)
    TextView mTvToyshopFilterFenlei;
    @InjectView(R.id.tv_toyshop_filter_pay)
    TextView mTvToyshopFilterPay;
    @InjectView(R.id.ll_toyshop_filter)
    LinearLayout mLlToyshopFilter;
    @InjectView(R.id.tv_toyShop_title)
    TextView tv_toyShop_title;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.fl_replace)
    FrameLayout flReplace;

    private static final String AGEBUFFER_KEY = "age";
    private static final String GN_JXFL_BUFFER_KEY = "brand";
    private static final String PAYBUFFER_KEY = "pay";
    private static final String BUFFER_KEY_ALL_AGE = "all_age_buffer_key";//年龄缓存key
    private static final String BUFFER_KEY_GONGNENG = "gongneng_buffer_key";//功能
    private static final String BUFFER_KEY_FENLEI = "feilei_buffer_key";//分类
    private static final int NetWorkError = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    public static final int DATA_TYPE_TOY = 11; //页面类型---玩具
    public static final int DATA_TYPE_BOOK = 12; //页面类型---图书
    private static final int MSG_SHOPPING_FINDALL = 13;
    private static final int MSG_SHOPPING_ADD = 14;
    private static final int INTENT_REQUEST_LOGIN = 15;

    private static final int TYPE_GET_AGE = 0;//导航栏类型 年龄
    private static final int TYPE_GET_GONGNENG = 1;//导航栏类型 功能（玩具汇）
    private static final int TYPE_GET_FENLEI = 2;//导航栏类型 精细分类（图书）


    private int count = -1;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                case MSG_SHOPPING_FINDALL://查询所有购物车信息
                    //shppingCartSetting();
                    break;
            }
        }
    };
    private int mType;
    private List<PopAgeBean.DataBean> mAgeList;
    private List<PopBrandBean.DataBean> mFeileiList;
    private List<PopBrandBean.DataBean> mGongnengList;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_toy;
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

        mType = getIntent().getIntExtra("type", -1);
        if (mType == DATA_TYPE_TOY) {
            //玩具
            initToy();
        } else if (mType == DATA_TYPE_BOOK) {
            //图书
            initBook();
        }
        /**图书和玩具相同的部分*/
        try {
            //年龄
            getAge();
            //排序

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图书汇
     */
    private void initBook() {
        //获取全部分类筛选数据
        getBookjingxifenleiData();

        tv_toyShop_title.setText("图书汇");
        mTvToyshopFilterAllage.setText("世界各地实用年龄");
        mTvToyshopFilterAllbrand.setText("精细分类");
        mTvToyshopFilterPay.setText("排序");

        replaceFragment(new ToyShopBookFragment());

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_replace,fragment);
        transaction.commit();

    }

    private void getBookjingxifenleiData() {
        //获取缓存数据
        String buffer = SPUtil.getString(this, BUFFER_KEY_FENLEI, "");
        if (!TextUtils.isEmpty(buffer)) {
            parseFenlei(buffer);
            CloseLoadingView();
        }
        try {
            getNet(AppConstants.BOOK_JXFL, TYPE_GET_FENLEI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 玩具汇
     */
    private void initToy() {
        //功能
        getGongneng();
        tv_toyShop_title.setText("玩具汇");
        mTvToyshopFilterAllage.setText("年龄");
        mTvToyshopFilterAllbrand.setText("功能");
        mTvToyshopFilterPay.setText("排序");

        replaceFragment(new ToyShopToyFragment());
    }

    private void getGongneng() {

        try {
            getNet(AppConstants.TOY_GONGNENG, TYPE_GET_GONGNENG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAge() throws IOException {
        //先获取缓存数据
        String buffer = SPUtil.getString(this, BUFFER_KEY_ALL_AGE, "");
        if (!TextUtils.isEmpty(buffer)) {
            parseAgeData(buffer);
            CloseLoadingView();
        }
        getNet(AppConstants.ALLAGE_URL, TYPE_GET_AGE);
    }

    /**
     * get请求
     *
     * @param url
     */
    private void getNet(String url, final int type) throws IOException {
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NetWorkError);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e(type + "===get====" + result);
                if (result != null) {
                    CloseLoadingView();
                    if (type == TYPE_GET_AGE) {
                        //年龄
                        parseAgeData(result);
                    } else if (type == TYPE_GET_FENLEI) {
                        //精细分类
                        parseFenlei(result);
                    } else if (type == TYPE_GET_GONGNENG) {
                        //功能
                        parseGongneng(result);
                    }
                }
            }
        });
    }

    /**
     * 功能
     *
     * @param result
     */
    private void parseGongneng(String result) {
        PopBrandBean popBrandBean = GsonUtil.parseJsonWithGson(result, PopBrandBean.class);
        mGongnengList = popBrandBean.getData();
        //缓存数据
        SPUtil.setString(this, result, BUFFER_KEY_GONGNENG);
    }

    /**
     * 精细分类
     * @param result
     */
    private void parseFenlei(String result) {
        try {
            PopBrandBean popBrandBean = GsonUtil.parseJsonWithGson(result, PopBrandBean.class);
            mFeileiList = popBrandBean.getData();
            //缓存数据
            SPUtil.setString(this, result, BUFFER_KEY_FENLEI);
        } catch (Exception e) {

        }
    }

    /**
     * 年龄
     *
     * @param json
     */
    private void parseAgeData(String json) {
        try {
            PopAgeBean popAgeBean = GsonUtil.parseJsonWithGson(json, PopAgeBean.class);
            mAgeList = popAgeBean.getData();
            //缓存
            SPUtil.setString(this, BUFFER_KEY_ALL_AGE, json);
        } catch (Exception e) {

        }
    }

    @Override
    protected void initEvent() {


    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
