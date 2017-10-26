package com.ruiyihong.toyshop.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.JifenDuihuanBookActivity;
import com.ruiyihong.toyshop.activity.JifenDuihuanDjqActivity;
import com.ruiyihong.toyshop.activity.JifenDuihuanToyActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.adapter.JiFenRvAdapter;
import com.ruiyihong.toyshop.bean.JifenHotDjqBean;
import com.ruiyihong.toyshop.bean.JifenHotProductBean;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.view.CircleImageView;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.DividerItemDecoration;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;
import com.ruiyihong.toyshop.view.MyScrollView;
import com.ruiyihong.toyshop.view.jfsc.ChoujiangPager;
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
 * Created by hegeyang on 2017/8/3 0003 .
 */

public class JifenShopFragment extends BaseFragment {
    private static final int MSG_HOT_DJQ = 0;
    private static final int MSG_HOT_PRODUCT = 1;
    private static final int NetWorkError = 2;
    private static final int CloseLoadingView = 3;
    private static final int PageLoading = 4;
    private static final String BUFFER_FRAG_JIFEN_DUIHUAN_DJQ = "buffer_frag_jifen_duihuan_djq";
    private static final String BUFFER_FRAG_JIFEN_DUIHUAN_PRODUCT = "buffer_frag_jifen_duihuan_product";
    private static final int PARSE_JIFEN_DATA = 5;
    private static final String BUFFER_USER_JF = "buffer_user_jf";
    @InjectView(R.id.civ_head)
    CircleImageView mCivHead;
    @InjectView(R.id.tv_username)
    TextView mTvUsername;
    @InjectView(R.id.rv_jifen)
    RecyclerView mRvJifen;
    @InjectView(R.id.sv)
    MyScrollView mSv;
    @InjectView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @InjectView(R.id.tv_duihuan_toy)
    TextView mTvDuihuanToy;
    @InjectView(R.id.tv_duihuan_book)
    TextView mTvDuihuanBook;
    @InjectView(R.id.tv_duihuan_jinquan)
    TextView mTvDuihuanJinquan;
    @InjectView(R.id.rv_jifen_product)
    RecyclerView mRvJifenProduct;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.fl_jifen_choujiang)
    FrameLayout flJifenChoujiang;
    @InjectView(R.id.bt_mine_login)
    Button btMineLogin;
    @InjectView(R.id.textView8)
    TextView textView8;
    @InjectView(R.id.ll_user_info)
    RelativeLayout llUserInfo;
    @InjectView(R.id.tv_uclass)
    TextView tvUclass;
    @InjectView(R.id.tv_jifen)
    TextView tvJifen;
    @InjectView(R.id.iv_userinfo)
    ImageView ivUserinfo;

    private Intent intent;
    private List<JifenHotDjqBean.DataBean> dataDjqList;
    private List<JifenHotProductBean.DataBean> dataProductList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HOT_DJQ:
                    initRvJiFen(mRvJifen, dataDjqList, JiFenRvAdapter.TYPE_DJQ);
                    break;
                case MSG_HOT_PRODUCT:
                    initRvJiFen(mRvJifenProduct, dataProductList, JiFenRvAdapter.TYPE_PRODUCT);
                    break;
                case PARSE_JIFEN_DATA:
                    parseCredit((String) msg.obj);
                    //缓存
                    SPUtil.setString(mActivity,BUFFER_USER_JF,(String) msg.obj);
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
            }
        }
    };


    @Override
    protected View initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_jifen, null);

        return view;
    }

    @Override
    protected void initData() {

        boolean networkAvailable = NetWorkUtil.isNetWorkAvailable(mActivity);
        if (!networkAvailable){
            handler.sendEmptyMessage(NetWorkError);
            return;
        }else{
            handler.sendEmptyMessage(PageLoading);
        }
        mRlTitle.setFocusableInTouchMode(true);
        mRlTitle.requestFocus();
        //初始化用户个人信息
        try {

            initUserInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
        //初始化积分兑大奖
        initJF_DuiJiang();

        try {
            String buffer = SPUtil.getString(mActivity, BUFFER_FRAG_JIFEN_DUIHUAN_PRODUCT, "");
            LogUtil.e("buffer------------------" + buffer);
            if (!TextUtils.isEmpty(buffer)) {
                CloseLoadingView();
                JifenHotProductBean hotBean = GsonUtil.parseJsonWithGson(buffer, JifenHotProductBean.class);
                dataProductList = hotBean.getData();
                initRvJiFen(mRvJifenProduct, dataProductList, JiFenRvAdapter.TYPE_PRODUCT);
            }
            String buffer1 = SPUtil.getString(mActivity, BUFFER_FRAG_JIFEN_DUIHUAN_DJQ, "");
            if (!TextUtils.isEmpty(buffer1)) {
                CloseLoadingView();
                JifenHotDjqBean hotBean = GsonUtil.parseJsonWithGson(buffer1, JifenHotDjqBean.class);
                dataDjqList = hotBean.getData();
                initRvJiFen(mRvJifen, dataDjqList, JiFenRvAdapter.TYPE_DJQ);
            }
            postNet(AppConstants.JIFEN_DUIHUAN_HOT_DJQ, new HashMap<String, Object>(), MSG_HOT_DJQ);
            postNet(AppConstants.JIFEN_DUIHUAN_HOT_PRODUCT, new HashMap<String, Object>(), MSG_HOT_PRODUCT);

        }catch (Exception e){
            e.printStackTrace();
        }
         }

    public void initUserInfo() {
        //用户头像：
        String login = SPUtil.getString(mActivity, AppConstants.SP_LOGIN, "");

        if (btMineLogin!=null && llUserInfo!=null) {
            if (TextUtils.isEmpty(login)) {
                //未登录
                btMineLogin.setVisibility(View.VISIBLE);
                llUserInfo.setVisibility(View.GONE);
                btMineLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                //已登录
                btMineLogin.setVisibility(View.GONE);
                llUserInfo.setVisibility(View.VISIBLE);
                try {
                    UserBean userBean = GsonUtil.parseJsonWithGson(login, UserBean.class);

                    //头像
                    String yhimg = userBean.yhimg;
                    if (!TextUtils.isEmpty(yhimg)) {
                        //有头像，判断是否是三方的
                        if (yhimg.startsWith("http")) {
                            yhimg = yhimg.replace("\\", "");
                        }else {
                            yhimg = AppConstants.IMG_BASE_URL +yhimg;
                        }
                        Picasso.with(mActivity).load(yhimg).placeholder(R.mipmap.personinfo_head_icon).error(R.mipmap.personinfo_head_icon).fit().into(mCivHead);
                    }else{
                        //没有头像，是用默认头像
                        Picasso.with(mActivity).load(R.mipmap.personinfo_head_icon).into(mCivHead);
                    }
                    //用户名
                    mTvUsername.setText(userBean.yhniche);
                    //会员等级
                    String uclass = userBean.uclass;
                    tvUclass.setText(uclass + "会员");
                    int uclassIcon = 0;
                    if (uclass!=null) {
                        if (uclass.equals("普通")) {
                            uclassIcon = R.mipmap.vip_icon_putong;
                        } else if (uclass.equals("铂金")) {
                            uclassIcon = R.mipmap.vip_icon_bojin;
                        } else if (uclass.equals("银钻")) {
                            uclassIcon = R.mipmap.vip_icon_yinzuan;
                        } else if (uclass.equals("金钻")) {
                            uclassIcon = R.mipmap.vip_icon_jinzuan;
                        } else if (uclass.equals("top")) {
                            uclassIcon = R.mipmap.vip_icon_top;
                        }
                    }
                    if (uclassIcon != 0) {
                        ivUserinfo.setImageResource(uclassIcon);
                    }
                    String jf_buffer = SPUtil.getString(mActivity, BUFFER_USER_JF, "");
                    if (!TextUtils.isEmpty(jf_buffer)){
                        parseCredit(jf_buffer);
                    }
                    //积分
                    getCredit();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    private void getCredit() throws IOException {
        final HashMap<String, String> map = new HashMap<>();
        String[] uid = SPUtil.getUid(mActivity);
        if (uid != null) {
            map.put("uid", uid[0]);
            OkHttpUtil.postString(AppConstants.JIFEN_DUIHUAN_GET_CREDIT, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("用户积分===="+result);
                    if (result != null) {
                        Message msg = Message.obtain();
                        msg.what = PARSE_JIFEN_DATA;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        }

    }

    private void parseCredit(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            String zjf = data.getString("zjf");
            LogUtil.e("积分商城===我的萝卜币"+zjf);
            if (!TextUtils.isEmpty(zjf)) {
                tvJifen.setText(zjf + " 萝卜币");
            }else {
                tvJifen.setText("0萝卜币");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NetWorkError);
        }
    }

    /**
     * 积分抽大奖
     */
    private void initJF_DuiJiang() {
        flJifenChoujiang.removeAllViews();
        final ChoujiangPager choujiangPager = new ChoujiangPager(mActivity);
        choujiangPager.setOnDataGotListener(new ChoujiangPager.OnDataGotListener() {
            @Override
            public void onDataGot() {
                flJifenChoujiang.addView(choujiangPager.rootView);
            }
        });


    }

    private void postNet(String url, final Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    //LogUtil.e(type+"==积分商城======="+result);
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            CloseLoadingView();
                            switch (type) {
                                case MSG_HOT_DJQ:
                                    try {
                                        JifenHotDjqBean bean = GsonUtil.parseJsonWithGson(result, JifenHotDjqBean.class);
                                        dataDjqList = bean.getData();
                                        SPUtil.setString(mActivity, BUFFER_FRAG_JIFEN_DUIHUAN_DJQ, result);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        handler.sendEmptyMessage(NetWorkError);
                                    }
                                    break;
                                case MSG_HOT_PRODUCT:
                                    try {
                                        JifenHotProductBean p_bean = GsonUtil.parseJsonWithGson(result, JifenHotProductBean.class);
                                        dataProductList = p_bean.getData();
                                        SPUtil.setString(mActivity, BUFFER_FRAG_JIFEN_DUIHUAN_PRODUCT, result);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        handler.sendEmptyMessage(NetWorkError);
                                    }
                                    break;
                            }
                            handler.sendEmptyMessage(type);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NetWorkError);

        }
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    private void initRvJiFen(RecyclerView rv, final List list, final int type) {
        rv.setLayoutManager(new FullyLinearLayoutManager(mActivity));
        rv.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.BOTH_SET, 1, getResources().getColor(R.color.divider_rv_line)));
        JiFenRvAdapter mJifenAdapter = new JiFenRvAdapter(mActivity, list, type);
        rv.setAdapter(mJifenAdapter);

        mJifenAdapter.setOnItemClickListener(new JiFenRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //条目点击事件,兑换代金券
                showDialog(position, list, type);
            }
        });
    }

    private void showDialog(int position, List list, int type) {
        DuihuanDialogFragment dialog = new DuihuanDialogFragment();

        if (type == JiFenRvAdapter.TYPE_DJQ) {
            //代金券
            JifenHotDjqBean.DataBean djqBean = (JifenHotDjqBean.DataBean) list.get(position);
            int id = djqBean.getId();
            dialog.setData(id);

        } else if (type == JiFenRvAdapter.TYPE_PRODUCT) {
            //// TODO: 2017/8/20 兑换图书玩具
        }

        dialog.show(getFragmentManager(), "DuihuanDialog");

        dialog.setExchangeFinishListener(new DuihuanDialogFragment.ExChangeFinishListener() {
            @Override
            public void onExChangeSucess() {
                //兑换成功  消耗积分 刷新界面
                try {

                    initUserInfo();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onExChangeFailure() {

            }
        });
    }

    @Override
    protected void initEvent() {
        mTvDuihuanToy.setOnClickListener(this);
        mTvDuihuanBook.setOnClickListener(this);
        mTvDuihuanJinquan.setOnClickListener(this);
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_duihuan_toy:
                intent = new Intent(mActivity, JifenDuihuanToyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_duihuan_book:
                this.intent = new Intent(mActivity, JifenDuihuanBookActivity.class);
                startActivity(this.intent);
                break;
            case R.id.tv_duihuan_jinquan:
                this.intent = new Intent(mActivity, JifenDuihuanDjqActivity.class);
                startActivity(this.intent);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            initUserInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
