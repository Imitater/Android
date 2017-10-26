package com.ruiyihong.toyshop.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.VipPowerRvAdapter;
import com.ruiyihong.toyshop.alipay.PayResult;
import com.ruiyihong.toyshop.bean.AlipaySuccessBean;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.bean.VipDataBean;
import com.ruiyihong.toyshop.bean.VipMemberDetialBean;
import com.ruiyihong.toyshop.bean.mine.AlipayResultBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.util.UpdateVipUtil;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/8/6 0006 .
 */

public class VipMemberDetial extends BaseActivity {


    private static final int ZFB = 0;
    private static final int WX = 1;
    private static final int SDK_PAY_FLAG = 2;
    private static final int MSG_MAKE_ORDER = 3;
    private static final int MSG_ORDER_FAIL = 4;
    private static final int GET_VIP_TYPE = 5;

    @InjectView(R.id.rv_vipmember_quanxian)
    RecyclerView rvVipmemberQuanxian;
    @InjectView(R.id.tv_vipmember_types)
    TextView tvVipmemberTypes;
    @InjectView(R.id.cb_vipmember_read)
    CheckBox cbVipmemberRead;
    @InjectView(R.id.cb_wexin)
    ImageView cbWexin;
    @InjectView(R.id.rl_vipmember_weixin)
    RelativeLayout rlVipmemberWeixin;
    @InjectView(R.id.cb_zfb)
    ImageView cbZfb;
    @InjectView(R.id.rl_vipmember_zfb)
    RelativeLayout rlVipmemberZfb;
    @InjectView(R.id.tv_vipmember_money)
    TextView tvVipmemberMoney;
    @InjectView(R.id.tv_vipmember_jieyue)
    TextView tvVipmemberJieyue;
    @InjectView(R.id.tv_vipmember_submit)
    TextView tvVipmemberSubmit;
    private int type;
    private int zfType = 0;
    private VipMemberDetialBean.DataBean dataBean;
    private static onPayStatusListener listener;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    paseData((String) msg.obj);
                    break;
                case MSG_MAKE_ORDER:
                    makeOrderHandler((String) msg.obj);
                    break;
                case SDK_PAY_FLAG: {
                    //处理支付宝支付的结果
                    payFlag((Map<String, String>) msg.obj);
                    break;
                }
                case MSG_ORDER_FAIL:
                    ToastHelper.getInstance().displayToastShort((String) msg.obj);
                    //销毁当前页面
                    finish();
                    break;
                case GET_VIP_TYPE://更新vip等级
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
        LogUtil.e("修改之后的登录信息=========="+login);
        SPUtil.setString(this,AppConstants.SP_LOGIN,login);
        //vipDataBean.uclass
    }

    /**
     * 生成订单信息
     * @param obj
     */
    private void makeOrderHandler(String obj) {
        LogUtil.e("会员卡订单=========="+obj);
        /**
         "status": 1,
         "msg": "会员卡订单生成成功",
         "ding": "5789676d22a556c2",-------订单号
         "price": 300  ---------多少元*/
        LogUtil.e("radish", "onResponse: result-"+obj );
        try {
            JSONObject jsonObject = new JSONObject(obj);
            int status = jsonObject.getInt("status");
            if (status == 1){
                //订单号
                String orderNumber = jsonObject.getString("ding");
                int price = jsonObject.getInt("price");
                LogUtil.e("radish", "handleMessage: 订单号"+ orderNumber);
                LogUtil.e("radish", "handleMessage: 价格"+price );
                //支付
                aliPay(orderNumber,price);
            }else if (status == -1){
                ToastHelper.getInstance().displayToastShort("不能购买多张卡");
            }else if (status == -2){
                ToastHelper.getInstance().displayToastShort("请先退压金，再购买");
            }else{
                ToastHelper.getInstance().displayToastShort("购买失败，请重新购买");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void payFlag(Map<String, String> obj) {
        @SuppressWarnings("unchecked")
        PayResult payResult = new PayResult(obj);
        /**
         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            String result = payResult.getResult();// 同步返回需要验证的信息
            AlipayResultBean alipayResultBean = GsonUtil.parseJsonWithGson(result, AlipayResultBean.class);
            //String total_amount = alipayResultBean.getAlipay_trade_app_pay_response().getTotal_amount();//支付金额
            final String out_trade_no = alipayResultBean.getAlipay_trade_app_pay_response().getOut_trade_no();//订单号

            //// TODO: 2017/9/28  等待支付结果对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(VipMemberDetial.this, R.style.RandomDialog);
            View dialogview = View.inflate(VipMemberDetial.this, R.layout.zhifubao_loading, null);
            final RotateLoading zhifubao_loading = dialogview.findViewById(R.id.zhifubao_loading);
            zhifubao_loading.start();
            builder.setView(dialogview);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ding",out_trade_no);
                    try {
                        OkHttpUtil.postJson(AppConstants.VIP_BUY_SUCCESS, map, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                VipMemberDetial.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (zhifubao_loading!=null && dialog!=null) {
                                            zhifubao_loading.stop();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = OkHttpUtil.getResult(response);
                                if (!TextUtils.isEmpty(result)){
                                    LogUtil.e( "会员卡办理onResponse: "+result );
                                    Message msg = Message.obtain();
                                    msg.what = MSG_ORDER_FAIL;
                                    try {
                                        AlipaySuccessBean bean = GsonUtil.parseJsonWithGson(result, AlipaySuccessBean.class);
                                        AlipaySuccessBean.DataBean data = bean.getData();
                                        if (data != null && !"null".equals(data)){
                                            //已支付  改变卡状态
                                            int tai = data.getTai();
                                            if (tai==1){//（0未支付1已支付）
                                                //支付成功
                                                msg.obj = "支付成功！";
                                                //累计积分
                                                UpdateVipUtil.upVip(data.getPrice(),VipMemberDetial.this);
                                                //更新会员等级
                                                getVipType();
                                            }
                                        }else{
                                            //未支付状态
                                            msg.obj = "支付失败，请重新支付";
                                        }
                                        handler.sendMessage(msg);
                                    }catch (Exception e){
                                        msg.obj = "支付失败，请重新支付";
                                        handler.sendMessage(msg);
                                    } finally {
                                        VipMemberDetial.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (zhifubao_loading!=null && dialog!=null) {
                                                    zhifubao_loading.stop();
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
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
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                Toast.makeText(this, "支付结果确认中",
                        Toast.LENGTH_SHORT).show();

            } else {
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(this, "支付失败",
                        Toast.LENGTH_SHORT).show();
            }
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
                    LogUtil.e("个人信息======vip type==="+result);
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

    private void makeOrder() {
        Map<String, Object> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
        if (TextUtils.isEmpty(uid[0])) {
            return;
        }
        LogUtil.e("radish", "buyVipCard: type:"+type );
        map.put("uid", uid[0]);
        map.put("kid",type);//（id   1：次卡，2：半年卡，3：年卡）
        try {
            OkHttpUtil.postJson(AppConstants.VIP_BUY, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(result)){
                        Message msg = Message.obtain();
                        msg.what = MSG_MAKE_ORDER;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void paseData(String data) {
        Gson gson = new Gson();
        VipMemberDetialBean vipMemberDetialBean = gson.fromJson(data, VipMemberDetialBean.class);
        rvVipmemberQuanxian.setLayoutManager(new FullyGridLayoutManager(this, 1));
        dataBean = vipMemberDetialBean.data.get(0);
        rvVipmemberQuanxian.setAdapter(new VipPowerRvAdapter(this, dataBean.vquan.split("；")));
        String[] split = dataBean.vjieyu.split(";");
        StringBuilder builder = new StringBuilder();
        builder.append("借阅周期及逾费用\n");
        for (int i = 0; i < split.length; i++) {
            builder.append(split[i] + "\n");
        }
        tvVipmemberJieyue.setText(builder);
        tvVipmemberTypes.setText(dataBean.vperiod);
        tvVipmemberMoney.setText(dataBean.vprice + "元");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vipmember_detail;
    }

    @Override
    protected void initView() {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//todo 沙箱环境
    }

    @Override
    protected void initData() {
        type = getIntent().getIntExtra("type", -1);
        getDataFromNet(type);
    }

    private void getDataFromNet(int type) {
        String url = AppConstants.SERVE_URL + "index/vipclass/vipxq";
        HashMap<String, String> params = new HashMap<>();
        LogUtil.e("type===" + type);
        params.put("id", type + "");
        try {
            OkHttpUtil.postString(url, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (result != null) {
                        Message obtain = Message.obtain();
                        obtain.what = 10;
                        obtain.obj = result;
                        handler.sendMessage(obtain);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initEvent() {
        rlVipmemberWeixin.setOnClickListener(this);
        rlVipmemberZfb.setOnClickListener(this);
        cbVipmemberRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvVipmemberSubmit.setBackgroundResource(R.color.tab_selected);
                    tvVipmemberSubmit.setEnabled(true);
                } else {
                    tvVipmemberSubmit.setBackgroundResource(R.color.unclick_button);
                    tvVipmemberSubmit.setEnabled(false);
                }
            }
        });
        tvVipmemberSubmit.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        if (v == rlVipmemberWeixin) {
            cbWexin.setVisibility(View.VISIBLE);
            cbZfb.setVisibility(View.INVISIBLE);
            zfType = WX;
        } else if (v == rlVipmemberZfb) {
            cbWexin.setVisibility(View.INVISIBLE);
            cbZfb.setVisibility(View.VISIBLE);
            zfType = ZFB;
        }else if (v.getId() == R.id.tv_vipmember_submit){
            //立即支付
            if (zfType ==ZFB ){
                //创建订单
                makeOrder();
            }else if (zfType == WX){
                //微信支付
                ToastHelper.getInstance().displayToastShort("暂不支持微信支付，请使用支付宝支付");
            }
        }
    }

    //支付宝支付
    private void aliPay(String orderNumber, int price) {
        if (price!=dataBean.vprice){
            return;
        }
        String url = "http://api.y91edu.com/alipay/kapay/orderInfo.php";
        HashMap<String, String> map = new HashMap<>();
        map.put("hao", orderNumber);
        map.put("total",dataBean.vprice+"");
        map.put("body","会员办理");

        try {
            OkHttpUtil.postString(url,map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                   final String orderInfo = OkHttpUtil.getResult(response);
                    LogUtil.e("huida","订单信息   "+ orderInfo);
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(VipMemberDetial.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
    public static void setOnPayStatusListener(onPayStatusListener listeners) {
        listener = listeners;
    }

    public interface onPayStatusListener {
        void onSuccess(String orderNum);

        void onCancel(String orderNumber);

        void onFailuer();
    }
}
