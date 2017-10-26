package com.ruiyihong.toyshop.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.VideoRvAdapter;
import com.ruiyihong.toyshop.alipay.PayResult;
import com.ruiyihong.toyshop.bean.AlipaySuccessBean;
import com.ruiyihong.toyshop.bean.ClassBean;
import com.ruiyihong.toyshop.bean.ClassDetailBean;
import com.ruiyihong.toyshop.bean.CollectBean;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.bean.VipDataBean;
import com.ruiyihong.toyshop.bean.mine.AlipayResultBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GetOrderNumUtil;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ShareUtils;
import com.ruiyihong.toyshop.util.StringUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.util.UpdateVipUtil;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.ruiyihong.toyshop.view.MoreTextView;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/7/18.
 */

public class VideoActivity extends BaseActivity {
    @InjectView(R.id.vitamio)
    VideoView mVitamio;
    @InjectView(R.id.ib_shared)
    ImageButton mIbShared;
    @InjectView(R.id.video_top)
    RelativeLayout mVideoTop;
    @InjectView(R.id.iv_play_bottom)
    ImageView mIvPlayBottom;
    @InjectView(R.id.mediacontroller_time_current)
    TextView mMediacontrollerTimeCurrent;
    @InjectView(R.id.mediacontroller_progress)
    SeekBar mMediacontrollerProgress;
    @InjectView(R.id.mediacontroller_time_total)
    TextView mMediacontrollerTimeTotal;
    @InjectView(R.id.iv_bottom_screen_default)
    ImageView mIvBottomScreenDefault;
    @InjectView(R.id.video_bottom)
    LinearLayout mVideoBottom;
    @InjectView(R.id.pb_buffer)
    ProgressBar mPbBuffer;
    @InjectView(R.id.rl_video)
    RelativeLayout mRlVideo;
    @InjectView(R.id.mtv_video)
    MoreTextView mMtvVideo;
    @InjectView(R.id.rv_video_relative)
    RecyclerView mRvVideoRelative;
    @InjectView(R.id.rv_video_tuijian)
    RecyclerView mRvVideoTuijian;
    @InjectView(R.id.tv_video_title)
    TextView mTvVideoTitle;
    @InjectView(R.id.ib_shared_shu)
    ImageButton mIbSharedShu;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.tv_relative_class)
    TextView mTvRelativeClass;
    @InjectView(R.id.ib_collect)
    ImageButton mIbCollect;
    @InjectView(R.id.iv_audio)
    ImageView mIvAudio;
    private int screenHeight;
    private int screenWidth;
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_BCAC = 1;
    public static final int TYPE_TOY = 2;
    public static final int TYPE_VIDEO = 3;
    private static final int MSG_HIDE_CONTROLLER = 4;
    private static final int MSG_UPDATE_POSITION = 5;
    private static final int MSG_DETAIL_INFO = 6;
    private static final int MSG_RECOMMEND_TUIJIAN = 7;
    private static final int MSG_RECOMMEND_VIDEO = 8;
    private static final int MSG_VIDEO_PAY = 9;
    private static final int NetWorkError = 10;
    private static final int CloseLoadingView = 11;
    private static final int PageLoading = 12;
    private static final int MSG_CLASS_COLLECT = 13;
    private static final int MSG_SHARED = 14;
    private static final int SDK_PAY_FLAG = 15;
    private static final int MSG_MAKE_ORDER = 16;
    private static final int MSG_ORDER_FAIL = 17;
    private static final int GET_VIP_TYPE = 18;
    private boolean isControllerShowing = false;
    private GestureDetector detector;
    private boolean isPay = false;
    boolean flag = true;
    private static onPayStatusListener listener;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_POSITION:
                    //获取当前播放时长
                    updatePlayPosition();
                    break;
                case MSG_HIDE_CONTROLLER:
                    if (isControllerShowing) {
                        switchController();
                    }
                    break;
                case MSG_DETAIL_INFO:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        Log.e("radish", "handleMessage: progressDialog.dismiss()");
                        progressDialog.dismiss();
                    }
                    detailInfo((String) msg.obj);
                    break;
                case MSG_RECOMMEND_TUIJIAN:
                    initRvAudio(mRvVideoTuijian, (String) msg.obj);
                    break;
                case MSG_RECOMMEND_VIDEO:
                    initRvAudio(mRvVideoRelative, (String) msg.obj);
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
                case MSG_CLASS_COLLECT://收藏回显
                    classCollect((String) msg.obj);
                    break;
                case MSG_VIDEO_PAY:
                    if (dialog1 != null) {
                        dialog1.dismiss();
                    }
                    vitamioBoFang((String) msg.obj);
                    break;
                case MSG_SHARED:
                    String web_urls = (String) msg.obj;
                    intWebUrl(web_urls);
                    break;
                case SDK_PAY_FLAG: {
                    payFlag((Map<String, String>) msg.obj);
                    break;
                }
                case MSG_MAKE_ORDER:
                    String orderNumber = (String) msg.obj;
                    int money = msg.arg1;
                    payByAlipay(orderNumber, money);
                    break;
                case MSG_ORDER_FAIL:
                    ToastHelper.getInstance().displayToastShort((String) msg.obj);
                    break;
                case GET_VIP_TYPE:
                    parseVipData((String)msg.obj);
                    break;
            }
        }
    };
    /**
     * 更新sp中存储的vip等级信息
     * @param vipData
     */
    private void parseVipData(String vipData) {
        Gson gson = new Gson();
        VipDataBean vipDataBean = gson.fromJson(vipData, VipDataBean.class);
        String login = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
        UserBean userBean = gson.fromJson(login, UserBean.class);
        userBean.uclass = vipDataBean.uclass;
        login= gson.toJson(userBean);
        LogUtil.e("修改之后的登录信息====商品购买======"+login);
        SPUtil.setString(this,AppConstants.SP_LOGIN,login);
    }

    private float money;
    private AlertDialog dialog1;

    private void payFlag(Map<String, String> obj) {
        @SuppressWarnings("unchecked")
        PayResult payResult = new PayResult(obj);
        /**
         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            String result = payResult.getResult();
            AlipayResultBean alipayResultBean = GsonUtil.parseJsonWithGson(result, AlipayResultBean.class);
            //String total_amount = alipayResultBean.getAlipay_trade_app_pay_response().getTotal_amount();//支付金额
            final String out_trade_no = alipayResultBean.getAlipay_trade_app_pay_response().getOut_trade_no();//订单号

            // 等待支付结果对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this, R.style.RandomDialog);
            View dialogview = View.inflate(VideoActivity.this, R.layout.zhifubao_loading, null);
            final RotateLoading zhifubao_loading = dialogview.findViewById(R.id.zhifubao_loading);
            zhifubao_loading.start();
            builder.setView(dialogview);
            dialog1 = builder.create();
            dialog1.setCancelable(false);
            dialog1.show();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ding", out_trade_no);
                    try {
                        OkHttpUtil.postJson(AppConstants.CLASS_BUY_SUCCESS, map, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = OkHttpUtil.getResult(response);
                                if (!TextUtils.isEmpty(result)) {
                                    LogUtil.e("radish", "onResponse: " + result);
                                    Message msg = Message.obtain();
                                    msg.what = MSG_ORDER_FAIL;
                                    try {
                                        AlipaySuccessBean bean = GsonUtil.parseJsonWithGson(result, AlipaySuccessBean.class);
                                        AlipaySuccessBean.DataBean data = bean.getData();
                                        if (data != null && !"null".equals(data)) {
                                            //已支付  改变视频支付状态
                                            if (!isPay) {
                                                isPay = true;
                                                dialog_pay2.dismiss();
                                                updateData();
                                                //累计积分
                                                UpdateVipUtil.upVip((int)money,VideoActivity.this);
                                                //更新会员等级信息
                                                getVipType();
                                            }
                                        } else {
                                            //未支付状态
                                            msg.obj = "支付失败，请重新支付";
                                            handler.sendMessage(msg);
                                        }
                                    } catch (Exception e) {
                                        msg.obj = "支付失败，请重新支付";
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 3000);
        } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            Toast.makeText(VideoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 更新会员等级
     */
    private void getVipType() {
        String url = AppConstants.SERVE_URL+"index/vipclass/membinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("uid",SPUtil.getUid(this)[0]);
        try {
            OkHttpUtil.postString(url, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (result!=null){
                        Message obtain = Message.obtain();
                        obtain.what = GET_VIP_TYPE;
                        obtain.obj = result;
                        handler.sendMessage(obtain);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateData() {
        String payUrl = AppConstants.CLASS_PAY;
        HashMap<String, Object> map = new HashMap<>();
        map.put("kid", id);
        map.put("price", money);
        map.put("uname", username);
        netPost(payUrl, map, MSG_VIDEO_PAY);
    }

    private Dialog dialog_pay2;

    private void intWebUrl(String web_urls) {
        String[] split = web_urls.split("\"");
        if (split.length == 3) {
            //web_url = split[1] + "#" + id;
            web_urls = split[1] + "?id=" + id+"&uname="+username;
        }
        LogUtil.e("radish", "web_url---" +web_urls );
        switch (key) {
            case 0:
                ShareUtils.shareUrl(VideoActivity.this, web_urls, bean.getTitle(), AppConstants.IMG_BASE_URL + bean.getBgimg(), bean.getBrief(), 1);
                break;
            case 1:
                ShareUtils.shareUrl(VideoActivity.this, web_urls, bean.getTitle(), AppConstants.IMG_BASE_URL + bean.getBgimg(), bean.getBrief(), 0);
                break;
            case 2:
                ShareUtils.shareToQq(VideoActivity.this, bean.getTitle(), bean.getBrief(), web_urls, AppConstants.IMG_BASE_URL + bean.getBgimg(), new MyUiListener());
                break;
            case 3:
                ShareUtils.shareToQzone(VideoActivity.this, bean.getTitle(), bean.getBrief(), web_urls, AppConstants.IMG_BASE_URL + bean.getBgimg(), new MyUiListener());
                break;
        }
    }

    private PopupWindow popShared;
    private Dialog dialog;
//    private String web_url;

    private void classCollect(String obj) {
        CollectBean collectBean = GsonUtil.parseJsonWithGson(obj, CollectBean.class);
        if (collectBean.getData() != null && collectBean.getData().size() > 0) {
            CollectBean.DataBean dataBean = collectBean.getData().get(0);
            int issc = dataBean.getIssc();
            if (issc != 0) {
                //2收藏
                mIbCollect.setSelected(true);
            }
        } else {
            //0未收藏
            mIbCollect.setSelected(false);
        }
    }

    private ClassDetailBean.DataBean bean;
    private String username;
    private int id;
    private int type;

    private void detailInfo(String body) {
        ClassDetailBean detailBean = GsonUtil.parseJsonWithGson(body, ClassDetailBean.class);
        if (detailBean != null && detailBean.getData() != null && detailBean.getData().size() > 0) {
            bean = detailBean.getData().get(0);
            Log.e("radish", "classBean: " + bean);
            if (bean != null) {
                //相关视频
                initRelativityVideo();

                //设置标题
                mTvVideoTitle.setText(bean.getTitle());

                //设置查看更多
                mMtvVideo.setText(bean.getBrief());
                mMtvVideo.refreshText();

                switch (detailBean.getStatus()) {
                    case 0:
                        //请付费观看
                        dialogForPay();
                        break;
                    case 1:
                        //已付费视频
                        initVitamio(bean.getMedia());
                        break;
                    case 2:
                        //免费视频
                        initVitamio(bean.getMedia());
                        break;

                }


                //设置视频播放
                //initVitamio(bean.getMedia());

            }
        }
    }


    private void vitamioBoFang(String body) {
        Log.e("radish", "1vitamioBoFang: " + body);
        ClassDetailBean bean = GsonUtil.parseJsonWithGson(body, ClassDetailBean.class);
        Log.e("radish", "2vitamioBoFang: " + bean);
        //设置视频播放
        initVitamio(bean.getData().get(0).getMedia());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {

        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//todo 沙箱环境

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        type = intent.getIntExtra("type", -1);
    }


    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }
        netSetting();
    }

    private void init() {
        String sp_login = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
        try {
            JSONObject object = new JSONObject(sp_login);
            username = object.getString("uname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIbShared.setVisibility(View.GONE);
        //开始加载数据
        Log.e("radish", "init: 开始加载数据");
        initProgress();
        //初始化信息
        initInfo();
        //相关推荐
        initRelativityTuijian();

        //收藏回显
        Map<String, Object> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
        if (TextUtils.isEmpty(uid[0])) {
            return;
        }
        map.put("uid", uid[0]);
        map.put("id", id);
        netPost(AppConstants.CLASS_COLLECT, map, MSG_CLASS_COLLECT);


    }

    private void initRelativityVideo() {
        Log.e("radish", "initRelativityVideo: " + bean.getFname());
        String recommVideoUrl = AppConstants.CLASS_RECOMMEND_VIDEO;
        HashMap<String, Object> map = new HashMap<>();
        map.put("fname", bean.getFname());
        netPost(recommVideoUrl, map, MSG_RECOMMEND_VIDEO);
    }

    private void initRelativityTuijian() {
        Log.e("radish", "initRelativityTuijian: " + type);
        String recommendUrl = AppConstants.CLASS_RECOMMEND;
        HashMap<String, Object> map = new HashMap<>();
        map.put("kcclass", type);
        netPost(recommendUrl, map, MSG_RECOMMEND_TUIJIAN);
    }

    private void initInfo() {
        Vitamio.isInitialized(this);
        mIvBottomScreenDefault.setVisibility(View.VISIBLE);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        String detailUrl = AppConstants.CLASS_DETAIL;
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("uname", username);
        netPost(detailUrl, map, MSG_DETAIL_INFO);
    }

    private void netPost(final String url, Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.isSuccessful()) {
                        String body = response.body().string();
                        // TODO: 2017/9/4 课程收藏
                        LogUtil.e(type + "====视频课程=======" + body);
                        if (!TextUtils.isEmpty(body)) {
                            CloseLoadingView();
                            Message msg = Message.obtain();
                            msg.what = type;
                            msg.obj = body;
                            handler.sendMessage(msg);
                        }
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

    /**
     * 视频支付情况
     */
    private void dialogForPay() {
        money = bean.getPay();
        if (money > 0) {
            final Dialog dialogPay1 = new Dialog(this, R.style.Dialog_Fullscreen);
            dialogPay1.setCancelable(false);
            dialogPay1.getWindow().setGravity(Gravity.CENTER);
            dialogPay1.setContentView(R.layout.dialog_class_pay);
            TextView tv_money = dialogPay1.findViewById(R.id.tv_pay1_money);
            TextView tv_pay = dialogPay1.findViewById(R.id.tv_pay1);
            ImageView iv_cancel = dialogPay1.findViewById(R.id.imageView8);
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPay1.dismiss();
                    finish();
                }
            });
            tv_money.setText(money + "");

            tv_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPay1.dismiss();
                    dialog_pay2 = new Dialog(VideoActivity.this, R.style.Dialog_Fullscreen);
                    dialog_pay2.setCancelable(false);
                    dialog_pay2.getWindow().setGravity(Gravity.CENTER);
                    dialog_pay2.setContentView(R.layout.dialog_class_pay2);
                    final ImageView iv_weixin = dialog_pay2.findViewById(R.id.iv_weixin);
                    final ImageView iv_zhifubao = dialog_pay2.findViewById(R.id.iv_zhifubao);
                    dialog_pay2.findViewById(R.id.iv_close_pay2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_pay2.dismiss();
                            finish();
                        }
                    });
                    iv_weixin.setSelected(true);
                    iv_zhifubao.setSelected(false);
                    iv_weixin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            iv_weixin.setSelected(true);
                            iv_zhifubao.setSelected(false);
                        }
                    });

                    iv_zhifubao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            iv_weixin.setSelected(false);
                            iv_zhifubao.setSelected(true);
                        }
                    });

                    dialog_pay2.findViewById(R.id.tv_pay2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (iv_weixin.isSelected()) {
                                //微信支付
                                ToastHelper.getInstance().displayToastShort("暂不提供，请使用支付宝支付");
                            } else if (iv_zhifubao.isSelected()) {
                                //生成订单
                                makeOrder();


                                /*//支付宝支付
                                payByAlipay(money);*/
                            }

                        }
                    });
                    dialog_pay2.show();
                }
            });
            dialogPay1.show();
        } else {
            initVitamio(bean.getMedia());
        }
    }

    private void makeOrder() {
        Map<String, Object> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
        if (uid == null) {
            ToastHelper.getInstance().displayToastShort("请登录后操作！");
            return;
        }
        map.put("uid", uid[0]);
        map.put("kid", id);
        try {
            OkHttpUtil.postJson(AppConstants.ALIPAY_MAKE_ORDER, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NetWorkError);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("radish", "生成订单：" + response);
                    String result = OkHttpUtil.getResult(response);
                    Log.e("radish", "生成订单：" + result);
                    if (!TextUtils.isEmpty(result)) {
                        //{"status":1,"msg":"生成订单成功","ding":"f5e87d9295733ca6","money":0}
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int status = jsonObject.getInt("status");
                            if (status == 1) {
                                Message msg = Message.obtain();
                                msg.what = MSG_MAKE_ORDER;
                                msg.arg1 = jsonObject.getInt("money");
                                msg.obj = jsonObject.getString("ding");
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogUtil.e("课程订单生成异常123");
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("课程订单生成异常");
        }
    }

    //支付宝支付
    private void payByAlipay(String orderNumber, int money) {
//        if (money != this.money) {
//            return;
//        }
        String url = "http://api.y91edu.com/alipay/kepay/orderInfo.php";
        HashMap<String, String> map = new HashMap<>();
        map.put("hao", orderNumber);
        map.put("total", this.money + "");
        map.put("body", "课程购买");

        try {
            OkHttpUtil.postString(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String orderInfo = OkHttpUtil.getResult(response);
                    Log.e("huida", "订单信息   " + orderInfo);
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(VideoActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);
                            Log.e("msp", result.toString());

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    };

                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 视频设置
     *
     * @param path
     */
    private void initVitamio(String path) {
        //当音频时（.mp3）
        Log.e("radish", "media路径: " + path);
        if (path != null && path.length()>4  && ".mp3".equals(path.substring(path.length()-4))) {
            String bgimg = bean.getBgimg();
            mIvAudio.setVisibility(View.VISIBLE);
            Picasso.with(VideoActivity.this).load(AppConstants.IMG_BASE_URL+bgimg).fit().into(mIvAudio);
        }else{
            mIvAudio.setVisibility(View.GONE);
        }
        Log.e("radish", "initVitamio: " + AppConstants.VIDEO_BASE_URL + path);
        //     String path = "http://121.22.11.84:8008/12703/V/20160110/2016011001051152027.mp4";
        mVitamio.setVideoPath(AppConstants.VIDEO_BASE_URL + path);
        mVitamio.setBufferSize(10240);
        mVitamio.requestFocus();

        mVitamio.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 1.8f);
        //头尾布局默认不显示
        mVideoTop.setVisibility(View.GONE);
        mVideoBottom.setVisibility(View.GONE);
    }

    private void netSetting() {
        if (NetWorkUtil.is3gConnected(this)) {
            //流量
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("网络判断");
            builder.setMessage("目前是流量访问，是否继续？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    init();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        } else {
            init();
        }
    }

    private void initRvAudio(RecyclerView rv, String body) {

        final List<ClassBean> list = (List<ClassBean>) GsonUtil.parseJsonToList(body, new TypeToken<List<ClassBean>>() {
        }.getType());
        rv.setLayoutManager(new FullyGridLayoutManager(this, 3));
        VideoRvAdapter rvAdapter = new VideoRvAdapter(this, list);
        rv.setAdapter(rvAdapter);


        rvAdapter.setOnItemClickListener(new VideoRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                /*Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
                Log.e("radish", "onItemClick id: "+list.get(position).getId() );
                Log.e("radish", "onItemClick type: "+type );
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", type);
                startActivity(intent);
           //     finish();*/

                if (mVitamio != null && mVitamio.isPlaying()) {
                    playOrPauseVideo();
                }
                id = list.get(position).getId();
                initData();


            }
        });
    }

    @Override
    protected void initEvent() {
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
        mIvPlayBottom.setOnClickListener(this);
        mIvBottomScreenDefault.setOnClickListener(this);
        mIbShared.setOnClickListener(this);
        mIbSharedShu.setOnClickListener(this);
        mIbCollect.setOnClickListener(this);

        MyOnSeekBarChangeListener myOnSeekBarChangeListener = new MyOnSeekBarChangeListener();

        mMediacontrollerProgress.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        //手势识别事件监听
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //视频准备好监听
        mVitamio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediacontrollerTimeTotal.setText(StringUtil.formatDuration(mp.getDuration()));

                mMediacontrollerProgress.setMax((int) mp.getDuration());
                updatePlayPosition();
                mp.start();
            }
        });


        //视频播放错误监听
        mVitamio.setOnErrorListener(new MyOnErrorListener());

        //视频播放完成监听
        mVitamio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //规避系统解析的错误

                //视频完成后，移除自动更新进度
                handler.removeMessages(MSG_UPDATE_POSITION);

                mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(mp.getDuration()));

                mMediacontrollerProgress.setProgress((int) mp.getDuration());

                //切换播放暂停按钮
                mIvPlayBottom.setBackgroundResource(R.drawable.video_stop);

                //TODO 播放下一曲

            }
        });

    }

    private ProgressDialog progressDialog;

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(R.style.MaterialDialog);
        progressDialog.setMessage("正在处理");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bottom:
                playOrPauseVideo();
                break;
            case R.id.iv_bottom_screen_default:

                switchScreen();
                break;
            case R.id.ib_shared:
                showPopShared();

                break;
            case R.id.ib_shared_shu:
                showDialogShared();
                break;
            case R.id.ib_collect://收藏
                try {
                    postCollect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void postCollect() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
        if (uid == null) {
            ToastHelper.getInstance().displayToastShort("请登录后操作！");
            return;
        }
        map.put("uid", uid[0]);
        map.put("wid", id + "");//视频id
        map.put("sign", "2");//标记  2 是课程收藏
        OkHttpUtil.postString(AppConstants.COLLECT_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("课程收藏===" + result);
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        final String msg = object.getString("msg");
                        if (!TextUtils.isEmpty(msg) && msg.contains("成功")) {
                            VideoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int id = bean.getId();
                                    if (msg.contains("取消")) {
                                        //取消点赞
                                        mIbCollect.setSelected(false);

                                    } else {
                                        //点赞
                                        mIbCollect.setSelected(true);
                                    }
                                }
                            });
                        }
                        if (!TextUtils.isEmpty(msg) && msg.contains("失败")) {
                            ToastHelper.getInstance().displayToastShort("操作失败，请稍后再试");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showPopShared() {
        View sharedView = View.inflate(this, R.layout.pop_shared, null);
        popShared = new PopupWindow(sharedView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, false);
        sharedView.findViewById(R.id.tv_pop_shared_moment).setOnClickListener(new SharePopClickListener());
        sharedView.findViewById(R.id.tv_pop_shared_weichat).setOnClickListener(new SharePopClickListener());
        sharedView.findViewById(R.id.tv_pop_shared_qq).setOnClickListener(new SharePopClickListener());
        sharedView.findViewById(R.id.tv_pop_shared_qq_kj).setOnClickListener(new SharePopClickListener());
        popShared.setTouchable(true);
        popShared.setOutsideTouchable(true);
        popShared.setBackgroundDrawable(new ColorDrawable());
        popShared.showAsDropDown(mIbSharedShu);
    }


    private void showDialogShared() {
        dialog = new Dialog(this);
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

    private View sharedViews;

    class SharePopClickListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            VideoActivity.this.sharedViews = view;
            HashMap<String, Object> params = new HashMap<>();
            params.put("type", 2);
            params.put("wid", id);
            try {
                OkHttpUtil.postJson(AppConstants.SHARED_URL, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.isSuccessful()) {
                            final String body = response.body().string();
                            Log.i("radish", "body------------------" + body);
                            if (!TextUtils.isEmpty(body) && !body.endsWith("null")) {
                                //分享
                                Message msg = Message.obtain();
                                msg.what = MSG_SHARED;
                                msg.obj = body;
                                Log.e("radish", "shared_url---" + body);
                                handler.sendMessage(msg);
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (view.getId()) {
                case R.id.tv_pop_shared_moment:
                    key = 0;
                    //   ShareUtils.shareUrl(VideoActivity.this,web_url,bean.getTitle(),AppConstants.IMG_BASE_URL+bean.getBgimg(),bean.getBrief(),1);
                    break;
                case R.id.tv_pop_shared_weichat:
                    key = 1;
                    // ShareUtils.shareUrl(VideoActivity.this,web_url,bean.getTitle(),AppConstants.IMG_BASE_URL+bean.getBgimg(),bean.getBrief(),0);
                    break;
                case R.id.tv_pop_shared_qq:
                    key = 2;
                    //  ShareUtils.shareToQq(VideoActivity.this,bean.getTitle(), bean.getBrief(), web_url,AppConstants.IMG_BASE_URL+ bean.getBgimg(), new MyUiListener());
                    break;
                case R.id.tv_pop_shared_qq_kj:
                    key = 3;
                    //  ShareUtils.shareToQzone(VideoActivity.this,bean.getTitle(), bean.getBrief(), web_url,AppConstants.IMG_BASE_URL+ bean.getBgimg(),new MyUiListener());
                    break;
            }
        }
    }

    class MyUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastHelper.getInstance().displayToastShort("分享成功");
        }

        @Override
        public void onError(UiError uiError) {
            ToastHelper.getInstance().displayToastShort("分享失败+");
        }

        @Override
        public void onCancel() {
            ToastHelper.getInstance().displayToastShort("分享取消");
        }
    }

    int key = -1;

    // TODO: 2017/8/29 分享
    class ShareOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            VideoActivity.this.sharedViews = view;
            HashMap<String, Object> params = new HashMap<>();
            params.put("type", 2);
            params.put("wid", id);
            try {
                OkHttpUtil.postJson(AppConstants.SHARED_URL, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.isSuccessful()) {
                            final String body = response.body().string();
                            Log.i("radish", "body------------------" + body);
                            if (!TextUtils.isEmpty(body) && !body.endsWith("null")) {
                                //分享
                                Message msg = Message.obtain();
                                msg.what = MSG_SHARED;
                                msg.obj = body;
                                Log.e("radish", "shared_url---" + body);
                                handler.sendMessage(msg);
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (view.getId()) {
                case R.id.tv_share_moments:
                    key = 0;
                    //   ShareUtils.shareUrl(VideoActivity.this,web_url,bean.getTitle(),AppConstants.IMG_BASE_URL+bean.getBgimg(),bean.getBrief(),1);
                    break;
                case R.id.tv_share_wechat:
                    key = 1;
                    // ShareUtils.shareUrl(VideoActivity.this,web_url,bean.getTitle(),AppConstants.IMG_BASE_URL+bean.getBgimg(),bean.getBrief(),0);
                    break;
                case R.id.tv_share_qq:
                    key = 2;
                    //  ShareUtils.shareToQq(VideoActivity.this,bean.getTitle(), bean.getBrief(), web_url,AppConstants.IMG_BASE_URL+ bean.getBgimg(), new MyUiListener());
                    break;
                case R.id.tv_share_qq_kj:
                    key = 3;
                    //  ShareUtils.shareToQzone(VideoActivity.this,bean.getTitle(), bean.getBrief(), web_url,AppConstants.IMG_BASE_URL+ bean.getBgimg(),new MyUiListener());
                    break;
            }
        }
    }

    /**
     * 更新播放时长
     */
    private void updatePlayPosition() {
        //获取当前播放时长
        long currentPosition = mVitamio.getCurrentPosition();
        mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(currentPosition));
        mMediacontrollerProgress.setProgress((int) currentPosition);
        handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //切换横竖屏报错：onConfigurationChanged方法需要super.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreen();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setShuScreen();
        }
    }


    private void setShuScreen() {
        //变为默认屏幕
        mIbShared.setVisibility(View.GONE);
        float density = getResources().getDisplayMetrics().density;
        mVitamio.getLayoutParams().width = screenWidth;
        mVitamio.getLayoutParams().height = (int) (density * 200);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mVitamio.requestLayout();
    }

    private void setFullScreen() {
        //变为全屏
        mIbShared.setVisibility(View.VISIBLE);
        mVitamio.getLayoutParams().width = screenHeight;
        mVitamio.getLayoutParams().height = screenWidth;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mVitamio.requestLayout();
    }

    /**
     * 在全屏和默认屏幕之间切换
     */
    public void switchScreen() {
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //隐藏状态栏
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mVitamio.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                //显示状态栏
                WindowManager.LayoutParams attr = getWindow().getAttributes();
                attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().setAttributes(attr);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
    }

    /**
     * 播放视频
     */
    private void playOrPauseVideo() {
        if (mVitamio.isPlaying()) {
            //播放-->暂停
            mVitamio.pause();
            mIvPlayBottom.setBackgroundResource(R.drawable.video_stop);
            handler.removeMessages(MSG_UPDATE_POSITION);
        } else {
            //暂停-->播放
            mVitamio.start();
            mIvPlayBottom.setBackgroundResource(R.drawable.video_play);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
        }
    }

    /**
     * 切换控制面板状态方法
     */
    private void switchController() {
        if (isControllerShowing) {
            //显示---->隐藏
            mVideoTop.setVisibility(View.GONE);
            mVideoBottom.setVisibility(View.GONE);
            isControllerShowing = false;
        } else {
            //隐藏---->显示
            mVideoTop.setVisibility(View.VISIBLE);
            mVideoBottom.setVisibility(View.VISIBLE);
            isControllerShowing = true;
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 3000);
        }
    }

    /**
     * 触摸事件监听
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detector.onTouchEvent(event)) return true;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势识别
     */
    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        /**
         * 确认是单击时触发，仅单击
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //切换控制面板的状态
            switchController();
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPauseVideo();
            return super.onDoubleTap(e);
        }
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度条变化时
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
              /*  case R.id.sb_volume:
                    *//**
                 * 1.音频流类型
                 * 2.音量
                 * 3.标志 0:不显示系统音量弹窗
                 *        1:显示系统音量弹窗
                 *//*
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                    break;*/
                case R.id.mediacontroller_progress:
                    if (!fromUser) {
                        return;
                    }
                    mVitamio.seekTo(seekBar.getProgress());
                    mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(progress));

                    mIvPlayBottom.setBackgroundResource(R.drawable.video_play);
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
                    break;
            }
        }

        /**
         * 当手指触摸seekbar时调用
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //不需要自动隐藏
            handler.removeMessages(MSG_HIDE_CONTROLLER);
        }

        /**
         * 当手指离开seekbar时调用
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //自动隐藏
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 3000);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Activity被覆盖到下面或者锁屏时被调用
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mVitamio != null && mVitamio.isPlaying()) {
            playOrPauseVideo();
        }
    }

    /**
     * Activity创建或者从被覆盖、后台重新回到前台时被调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mVitamio != null && !mVitamio.isPlaying()) {
            playOrPauseVideo();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }


    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //给用户提示信息
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
            builder.setTitle("提示");
            builder.setMessage("当前视频不可播放");
            builder.setNegativeButton("退出播放", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent tencent = ShareUtils.getTencent(this);
        tencent.onActivityResult(requestCode, resultCode, data);
    }



    public interface onPayStatusListener {
        void onSuccess(String orderNum);

        void onCancel(String orderNumber);

        void onFailuer();
    }
}
