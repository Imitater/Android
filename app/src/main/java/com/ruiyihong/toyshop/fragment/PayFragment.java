/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.VipMemberDetial;
import com.ruiyihong.toyshop.alipay.AplipayResultBean_tb;
import com.ruiyihong.toyshop.alipay.OrderInfoUtil2_0;
import com.ruiyihong.toyshop.alipay.PayResult;
import com.ruiyihong.toyshop.bean.AlipaySuccessBean;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.bean.VipDataBean;
import com.ruiyihong.toyshop.bean.mine.AlipayResultBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.util.UpdateVipUtil;
import com.ruiyihong.toyshop.view.swipemenu.SwipeMenu;
import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Burt on 2017/8/17 0017.
 */

public class PayFragment extends DialogFragment {

    @InjectView(R.id.tv_cancel_pay)
    TextView tvCancelPay;
    @InjectView(R.id.ck_yu_e_pay)
    RadioButton ckYuEPay;
    @InjectView(R.id.ck_wx_pay)
    RadioButton ckWxPay;
    @InjectView(R.id.ck_zfb_pay)
    RadioButton ckZfbPay;
    @InjectView(R.id.bt_now_pay)
    Button btNowPay;
    @InjectView(R.id.tv_total_Money)
    TextView tvTotalMoney;
    @InjectView(R.id.Rg_pay_category)
    RadioGroup RgPayCategory;

    private static int PAY_TYPE_ZFB = 0;
    private static int PAY_TYPE_WX = 1;
    private static final int GET_VIP_TYPE = 2;
    private static onPayStatusListener listener;
    private final int totalPrice;
    private final String orderNumber;
    private int payType = -1;
    boolean flag = true;
    private AlertDialog dialog;

    private static final int SDK_PAY_FLAG = 3;
    private static final int SDK_AUTH_FLAG = 4;
    private static final int MSG_ORDER_FAIL = 5;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            LogUtil.e("pay flag" + msg.what);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    payFlag((Map<String, String>) msg.obj);
                    break;
                }
                case MSG_ORDER_FAIL:
                    ToastHelper.getInstance().displayToastShort((String) msg.obj);
                    //销毁对话框
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;

                case GET_VIP_TYPE:
                    parseVipData((String) msg.obj);
                    break;
            }
        }
    };

    /**
     * 更新sp中存储的vip等级信息
     *
     * @param vipData
     */
    private void parseVipData(String vipData) {
        Gson gson = new Gson();
        VipDataBean vipDataBean = gson.fromJson(vipData, VipDataBean.class);
        String login = SPUtil.getString(getActivity(), AppConstants.SP_LOGIN, "");
        UserBean userBean = gson.fromJson(login, UserBean.class);
        userBean.uclass = vipDataBean.uclass;
        login = gson.toJson(userBean);
        SPUtil.setString(getActivity(), AppConstants.SP_LOGIN, login);
        //vipDataBean.uclass
    }

    private void payFlag(Map<String, String> obj) {
        @SuppressWarnings("unchecked")
        PayResult payResult = new PayResult(obj);
        /**
         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */

        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            //销毁支付的对话框
            if (listener != null) {
                listener.onUiChanged();
            }
            //等待支付结果验证的dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RandomDialog);
            View dialogview = View.inflate(getActivity(), R.layout.zhifubao_loading, null);
            final RotateLoading zhifubao_loading = dialogview.findViewById(R.id.zhifubao_loading);
            zhifubao_loading.start();
            builder.setView(dialogview);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            AlipayResultBean alipayResultBean = GsonUtil.parseJsonWithGson(resultInfo, AlipayResultBean.class);
            String out_trade_no = orderNumber;
            if (alipayResultBean!=null) {
                out_trade_no = alipayResultBean.getAlipay_trade_app_pay_response().getOut_trade_no();//订单号
            }
            final String finalOut_trade_no = out_trade_no;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ding", finalOut_trade_no);
                    try {
                        OkHttpUtil.postJson(AppConstants.SHOP_BUY_SUCCESS, map, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = OkHttpUtil.getResult(response);
                                if (!TextUtils.isEmpty(result)) {
                                    LogUtil.e("商品支付结果" + result);
                                    Message msg = Message.obtain();
                                    msg.what = MSG_ORDER_FAIL;
                                    try {
                                        AplipayResultBean_tb bean = GsonUtil.parseJsonWithGson(result, AplipayResultBean_tb.class);
                                        List<AplipayResultBean_tb.DataBean> data = bean.getData();
                                        // LogUtil.e("支付成功====" + data.get(0).getTai());
                                        if (data != null && data.size() != 0 && !"null".equals(data)) {

                                            int tai = data.get(0).getTai();
                                            if (tai == 1) {
                                                //已支付  改变卡状态
                                                msg.obj = "支付成功";
                                                // 累计积分
                                                UpdateVipUtil.upVip(data.get(0).getTotal(), getActivity());
                                                //更新会员等级
                                                getVipType();
                                                if (listener != null) {
                                                    listener.onSuccess(orderNumber);
                                                }

                                            } else {
                                                msg.obj = "支付失败";
                                            }
                                        } else {
                                            //未支付状态
                                            if (listener != null) {
                                                listener.onFailuer();
                                            }
                                        }
                                    } catch (Exception e) {
                                        if (listener != null) {
                                            listener.onFailuer();
                                        }
                                    } finally {
                                        mHandler.sendMessage(msg);
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
            Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新会员等级
     */
    private void getVipType() {
        String url = AppConstants.SERVE_URL + "index/vipclass/membinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", SPUtil.getUid(getActivity())[0]);
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
                        obtain.what = GET_VIP_TYPE;
                        obtain.obj = result;
                        mHandler.sendMessage(obtain);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String orderInfo;

    public PayFragment(String hao, int total) {
        orderNumber = hao;
        totalPrice = total;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);// todo 沙箱环境
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_pay, container, false);
        ButterKnife.inject(this, v);
        tvTotalMoney.setText("￥" + totalPrice);
        RgPayCategory.check(R.id.ck_yu_e_pay);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
    }

    private void initEvent() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.tv_cancel_pay, R.id.ck_yu_e_pay, R.id.ck_wx_pay, R.id.ck_zfb_pay, R.id.bt_now_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel_pay:
                if (listener != null)
                    listener.onCancel(orderNumber);
                break;
            case R.id.ck_yu_e_pay://余额支付

                break;
            case R.id.ck_wx_pay://微信支付

                break;
            case R.id.ck_zfb_pay://支付宝支付

                break;
            case R.id.bt_now_pay://确认支付
                int checkedRadioButtonId = RgPayCategory.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.ck_wx_pay:
                        payType = PAY_TYPE_WX;
                        LogUtil.e("微信支付");
                        break;
                    case R.id.ck_zfb_pay:
                        payType = PAY_TYPE_ZFB;
                        LogUtil.e("支付宝支付");
                        break;
                }
                pay();

                break;
        }
    }

    /**
     * 支付
     */
    private void pay() {
        if (payType == PAY_TYPE_WX) {
            //微信支付
            ToastHelper.getInstance().displayToastLong("暂不支持微信支付，请选择支付宝支付");
        } else if (payType == PAY_TYPE_ZFB) {
            //支付宝支付
            payByAlipay();
        }
    }

    //支付宝支付
    private void payByAlipay() {

        String url = "http://api.y91edu.com/alipay/pay/orderInfo.php";

        HashMap<String, String> map = new HashMap<>();
        map.put("hao", orderNumber);
        map.put("total", totalPrice + "");
        map.put("body", "玩具图书租借");
        try {
            OkHttpUtil.postString(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    orderInfo = OkHttpUtil.getResult(response);
//                    Log.e("huida", "订单信息" + orderInfo);
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(getActivity());
                            Map<String, String> result = alipay.payV2(orderInfo, true);
                            Log.e("msp======", result.toString());

                            Message msg = Message.obtain();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;

                            boolean b = mHandler.sendMessage(msg);

                            LogUtil.e("handler finish==="+b );
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

    public static void setOnPayStatusListener(onPayStatusListener listeners) {
        listener = listeners;
    }

    public interface onPayStatusListener {
        void onSuccess(String orderNum);

        void onCancel(String orderNumber);

        void onFailuer();

        void onUiChanged();
    }
}
