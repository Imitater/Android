/**
 * 2017.
 * Huida.Burt
 * CopyRight
 * <p>
 * 结算页面
 */

package com.ruiyihong.toyshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.AddressBean;
import com.ruiyihong.toyshop.bean.ExpressBean;
import com.ruiyihong.toyshop.bean.OrderSuccessBean;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.bean.ZuQiBean;
import com.ruiyihong.toyshop.bean.mine.MyPreferentialBean;
import com.ruiyihong.toyshop.fragment.PayFragment;
import com.ruiyihong.toyshop.fragment.ShoppingCartFragment;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartBiz;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.DecimalUtil;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.ChoseVoucherDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.ruiyihong.toyshop.activity.MineAddressActivity.JIESUAN_ADDRESS;

public class SettleActivity extends BaseActivity implements PayFragment.onPayStatusListener {
    @InjectView(R.id.tv_receive_people)
    TextView tvReceivePeople;
    @InjectView(R.id.tv_receive_name)
    TextView tvReceiveName;
    @InjectView(R.id.tv_receive_phone)
    TextView tvReceivePhone;
    @InjectView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @InjectView(R.id.ln_address_edit)
    LinearLayout lnAddressEdit;
    @InjectView(R.id.rv_settle)
    RecyclerView rvSettle;
    @InjectView(R.id.tv_settle_count)
    TextView tvSettleCount;
    @InjectView(R.id.tv_settle_money)
    TextView tvSettleMoney;
    @InjectView(R.id.tv_settle_submit)
    TextView tvSettleSubmit;
    @InjectView(R.id.rv_setttle_bottom)
    RelativeLayout rvSetttleBottom;
    @InjectView(R.id.settle_title)
    RelativeLayout settle_title;
    @InjectView(R.id.tv_settle_peisongfs)
    TextView tvSettlePeisongfs;
    @InjectView(R.id.tv_djq)
    TextView tvDjq;
    @InjectView(R.id.tv_djq_yuan)
    TextView tvDjqYuan;
    @InjectView(R.id.tv_zuqi)
    TextView tvZuqi;
    @InjectView(R.id.tv_order_item_shopname)
    TextView tvOrderItemShopname;
    @InjectView(R.id.rl_peisong_click)
    RelativeLayout rlPeisongClick;
    @InjectView(R.id.rl_djq_click)
    RelativeLayout rlDjqClick;
    @InjectView(R.id.rl_zuqi_click)
    RelativeLayout rlZuqiClick;

    private static final int getDefaultAddress = 0;
    private static final int SelectDefaultAddress = 1;
    private static final int getEAY_All = 2;

    private static final int getExpress = 3;
    private static final int getVIP = 4;
    private static final int getDjq = 5;


    private int mHandlerCount = 0;
    private int mExpress = 0;
    private static String mVip = "";
    private int mDjq = -1;
    private int mDjqID = -1;


    private String result;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case getEAY_All:
                    setSettleMoney(mVip + "", mExpress + "", mDjq + "");
                    break;
                case getDefaultAddress:
                    result = (String) msg.obj;
                    parseAddressData(result);
                    break;
                case SelectDefaultAddress:
                    Intent intent = new Intent(SettleActivity.this, MineAddressActivity.class);
                    intent.putExtra("type", JIESUAN_ADDRESS);
                    intent.putExtra("data", result);
                    startActivityForResult(intent, 0);
                    break;
                case getDjq:
                    mDjq = (int) msg.obj;
                    mHandlerCount++;
                    LogUtil.e("获取代金券:  " + mDjq + "  HandlerCount:  " + mHandlerCount);
                    break;
                case getVIP:
                    mVip = (String) msg.obj;
                    mHandlerCount++;
                    LogUtil.e("获取Vip:  " + mVip + "  HandlerCount:  " + mHandlerCount);
                    break;
                case getExpress:
                    mExpress = (int) msg.obj;
                    mHandlerCount++;
                    LogUtil.e("获取邮费:  " + mExpress + "  HandlerCount:  " + mHandlerCount);
                    break;
            }

            if (mHandlerCount >= 3) {
                handler.sendEmptyMessage(getEAY_All);
                mHandlerCount = 0;
            }
        }
    };

    private ArrayList<ShppingCarHttpBean.WjlistBean> list;
    private String uid;
    private PopupWindow mPopWindowSelectDay;
    private PayFragment payFragment;
    private int zq = 1;
    private ChoseVoucherDialog choseVoucherDialog;
    private String vkind;//会员卡种类
    private ArrayList<Object> yhjList;
    private AlertDialog buyDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_settle;
    }

    @Override
    protected void initView() {
        settle_title.setFocusableInTouchMode(true);
        settle_title.requestFocus();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(ShoppingCartFragment.selectedList);
        list = (ArrayList<ShppingCarHttpBean.WjlistBean>) bundleExtra.get(ShoppingCartFragment.selectedList);
        rvSettle.setLayoutManager(new LinearLayoutManager(SettleActivity.this));
        rvSettle.setAdapter(new MyAdapter(list));
        tvSettleCount.setText(ShoppingCartBiz.getGoodNumber(list) + "");
        uid = SPUtil.getUid(SettleActivity.this)[0];

        tvSettleSubmit.setEnabled(false);
        getExpressFromNet();  //获取邮费
        getAddressFromeNet(); //获取地址
        getVIPFromeNet();     //租期
        getYHQFromeNet(); //代金券
    }

    //设置结算默认数据
    private void setSettleMoney(String zuqi, String express, String djq) {
        LogUtil.e("结算代金券============" + djq);
        zq = Integer.parseInt(zuqi);
        if (zq == 0) {
            //money = DecimalUtil.multiplyWithScale(ShoppingCartBiz.getSelectedPrice(list), "1", 2);
            tvZuqi.setText(zq + "天");
            rlZuqiClick.setEnabled(false);
            rlDjqClick.setEnabled(false);
            showBuyVipDialog();
            return;
        }
        String money;
        if (zq > 20) {
            //次卡会员
            tvZuqi.setText(1 + "天");
            money = DecimalUtil.multiplyWithScale(ShoppingCartBiz.getSelectedPrice(list), "1", 2);
            zq = 1;
        } else {
            tvZuqi.setText(zq + "天");
            //年卡或半年卡会员
            //money = DecimalUtil.multiplyWithScale(ShoppingCartBiz.getSelectedPrice(list), zuqi, 2);
            money = 0.00 + "";
            rlZuqiClick.setEnabled(false);//todo 年卡会员，代金券不可抵用邮费
            rlDjqClick.setEnabled(false);
        }
        money = DecimalUtil.add(money, express);
        money = DecimalUtil.subtract(money, 0.00 + "");
        tvSettlePeisongfs.setText(express + " 元");
        // tvDjqYuan.setText("-" + djq + "元");
        tvSettleMoney.setText("￥" + money);
        tvSettleSubmit.setEnabled(true);

    }

    /**
     * 弹出是否需要购买会员卡的对话框
     */
    private void showBuyVipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的会员卡等级不足，租赁玩具/图书需要购买会员卡，您是否需要购买会员卡");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //跳转办卡页面
                Intent intent = new Intent(SettleActivity.this, VipMemberBuyActivity.class);
                startActivity(intent);
                SettleActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //销毁当前页面
                SettleActivity.this.finish();
                if (buyDialog!=null){
                    buyDialog.dismiss();
                }
            }
        });
        buyDialog = builder.create();
        buyDialog.show();
    }

    //选择天数后进行计算
    private void ChangeDaysPrice(int position) {
        //一定是次卡
        zq = position + 1;
        LogUtil.e(position + "设置选择天数后数据=" + zq);
        String money = DecimalUtil.multiplyWithScale(ShoppingCartBiz.getSelectedPrice(list), zq + "", 2);
        tvZuqi.setText(zq + "天");
        money = DecimalUtil.add(money, mExpress + "");
        money = DecimalUtil.subtract(money,(mDjq == -1?0:mDjq ) + "");
        tvDjqYuan.setText("-" + (mDjq == -1?0:mDjq ) + "元");
        tvSettleMoney.setText("￥" + money);
    }

    //从网络获取代金券
    private void getYHQFromeNet() {
        Map<String, String> para = new HashMap<>();
        para.put("uid", uid);
        try {
            OkHttpUtil.postString(AppConstants.MY_DJQ, para, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("代金券返回失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (result != null) {
                        MyPreferentialBean djqBean = GsonUtil.parseJsonWithGson(result, MyPreferentialBean.class);
                        List<MyPreferentialBean.DataBean> djqList = djqBean.getData();//代金券
                        List<MyPreferentialBean.Data1Bean> hbList = djqBean.getData1();//红包
                        yhjList = new ArrayList<>();
                        yhjList.addAll(djqList);
                        yhjList.addAll(hbList);
                        if (yhjList.size() > 0) {
                            Message msg = Message.obtain();
                            //mDjqID = id;
                            msg.obj = yhjList.size();
                            msg.what = getDjq;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.obj = -1;
                            msg.what = getDjq;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从网络获取Vip信息
    private void getVIPFromeNet() {
        Map<String, Object> para = new HashMap<>();
        para.put("uid", uid);
        String url = AppConstants.SERVE_URL + "index/vipclass/vipkind";
        try {
            OkHttpUtil.postJson(url, para, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("VIP返回失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    //String result = response.body().string();
                    LogUtil.e(response.code() + "===租期=====" + result);
                    if (result != null) {
                        ZuQiBean zuQiBean = GsonUtil.parseJsonWithGson(result, ZuQiBean.class);
                        List<ZuQiBean.DataBean> data = zuQiBean.getData();
                        if (data.size() > 0) {
                            ZuQiBean.DataBean dataBean = data.get(0);
                            vkind = dataBean.getVkind();
                            String vzdzq = dataBean.getVzdzq();
                            Message msg = Message.obtain();
                            LogUtil.e("vip卡的种类:  " + vkind + " 最大租期： " + vzdzq);
                            msg.obj = vzdzq;
                            msg.what = getVIP;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.obj = 0 + "";
                            msg.what = getVIP;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //从网络获取运费
    private void getExpressFromNet() {
        String url = AppConstants.SERVE_URL + "index/expense/expense";
        try {
            OkHttpUtil.get(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("获取邮费失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (result != null) {
                        ExpressBean expressBean = GsonUtil.parseJsonWithGson(result, ExpressBean.class);
                        List<ExpressBean.DataBean> data = expressBean.getData();
                        ExpressBean.DataBean dataBean = data.get(0);
                        Message msg = Message.obtain();
                        msg.obj = dataBean.getExpense();
                        msg.what = getExpress;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设置代金券
    private void RefreshmDjq() {
        String money = "";
        //次卡
        money = DecimalUtil.multiplyWithScale(ShoppingCartBiz.getSelectedPrice(list), zq + "", 2);
        money = DecimalUtil.subtract(money, (mDjq == -1?0:mDjq ) + "");
        //tvZuqi.setText(zq + "天");
        LogUtil.e("设置选择代金券后数据");
        money = DecimalUtil.add(money, mExpress + "");

        tvDjqYuan.setText("-" + (mDjq == -1?0:mDjq )+ "元");
        tvSettleMoney.setText("￥" + money);

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) {

    }

    //窗口单击事件
    @OnClick({R.id.ln_address_edit, R.id.tv_settle_submit, R.id.rl_djq_click, R.id.rl_zuqi_click})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ln_address_edit:
                handler.sendEmptyMessage(SelectDefaultAddress);
                break;
            case R.id.tv_settle_submit:
                UpDingDan();
                break;
            case R.id.rl_peisong_click:
                break;
            case R.id.rl_djq_click:
                if (mDjq == -1) {
                    //没有代金券
                    ToastHelper.getInstance()._toast("您没有优惠券可以选择");
                } else {
                    showPopUpWindowDjq(view);
                }
                break;
            case R.id.rl_zuqi_click:
                showPopUpWindowDay(view);
                break;
        }
    }

    //选择代金券
    private void showPopUpWindowDjq(View view) {

        choseVoucherDialog = new ChoseVoucherDialog(this, R.style.Dialog_Find_Image);
        choseVoucherDialog.setData(yhjList, mDjqID);
        choseVoucherDialog.show();
        choseVoucherDialog.setOnInsureButtonClickListener(new ChoseVoucherDialog.OnInsureButtonClickListener() {
            @Override
            public void onInsureButtonClick(int djqId, int djqMian) {
                if (djqId != -1 && djqMian != -1) {
                    mDjq = djqMian;
                    mDjqID = djqId;
                    RefreshmDjq();
                    choseVoucherDialog.dismiss();
                    choseVoucherDialog = null;
                } else {
                    mDjq = 0;
                    mDjqID = -1;
                    RefreshmDjq();
                    choseVoucherDialog.dismiss();
                    choseVoucherDialog = null;
                }
            }
        });
    }

    //选择天数对话框
    private void showPopUpWindowDay(View view) {
        //设置contentView
        View contentView = LayoutInflater.from(SettleActivity.this).inflate(R.layout.popup_seleday_layout, null);
        mPopWindowSelectDay = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        RecyclerView rv = contentView.findViewById(R.id.rv_numberPicker);
        rv.setLayoutManager(new GridLayoutManager(SettleActivity.this, 5));
       /* rv.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));*/
        rv.setAdapter(new NumberPickerAdapter());
        mPopWindowSelectDay.setContentView(contentView);
        //设置各个控件的点击响应
        //显示PopupWindow
        mPopWindowSelectDay.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    //这里直接拉起支付页面
    private void UpDingDan() {

        String address = tvReceiveAddress.getText().toString();
        String name = tvReceiveName.getText().toString();
        String phone = tvReceivePhone.getText().toString();
        if (TextUtils.isEmpty(address)){
            //地址为空
            ToastHelper.getInstance().displayToastLong("请添加收货地址");
            return;
        }else if (TextUtils.isEmpty(name)){
            //收货人为空
            ToastHelper.getInstance().displayToastLong("请添加收货人姓名");
            return;
        }else if (TextUtils.isEmpty(phone)){
            //电话为空
            ToastHelper.getInstance().displayToastLong("请添加收货电话");
            return;
        }

        String testUri = AppConstants.SERVE_URL + "index/order/shengcheng";
        String s = "";
        String s1 = "";
        String s2 = "";
        for (ShppingCarHttpBean.WjlistBean bean : list) {
            s += bean.getId() + ",";
            s1 += bean.getWshu() + ",";
            s2 += bean.getShopprice() + ",";
        }
        LogUtil.e("Uid = " + uid);
        LogUtil.e("ID s = " + s.substring(0, s.length() - 1));
        LogUtil.e("Shopprice s1 = " + s1.substring(0, s1.length() - 1));
        LogUtil.e("Num s2 = " + s2.substring(0, s2.length() - 1));

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("wid", s.substring(0, s.length() - 1));//物品id
        params.put("wsl", s1.substring(0, s1.length() - 1));//物品数量
        params.put("wjg", s2.substring(0, s2.length() - 1));//物品价格

        LogUtil.e("提交的代金券id===" + mDjqID);
        if (mDjqID == -1) {
            params.put("yid", "");
        } else {
            params.put("yid", mDjqID + "");  //优惠券 id
        }
        params.put("ptime", "1"); //派送时间

        params.put("uaddr", address);
        params.put("unames", name);
        params.put("uphone", phone);
        params.put("zuqi", tvZuqi.getText().toString().trim());  // 租期


        try {
            final String finalS = s;
            OkHttpUtil.postString(testUri, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("上传订单错误");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = OkHttpUtil.getResult(response);
                    LogUtil.e("上传订单成功" + result);
                    if (result != null) {
                        OrderSuccessBean orderSuccessBean = GsonUtil.parseJsonWithGson(result, OrderSuccessBean.class);
                        if (1 == orderSuccessBean.getStatus()) {
                            //生成订单成功
                            final String hao = orderSuccessBean.getHao();//订单号
                            final int total = (int) orderSuccessBean.getTotal();//总金额
                            //获取本地界面金额，和服务器返回金额比对
                            String money = tvSettleMoney.getText().toString().trim();
                            money = money.substring(1, money.length() - 1);
                            // TODO: 2017/9/22
                            if (Double.parseDouble(money) == total) {
                                popupPay(hao, total, mVip,finalS);
                            }
                            LogUtil.e("订单结算数据总金额" + total);

                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //支付页面
    private void popupPay(final String hao, final int total, final String vip, final String finalS) throws IOException {
        //年卡半年卡会员验证是否还有租借的资格
        String url = AppConstants.SERVE_URL + "index/order/qxpay";
        final String[] uid = SPUtil.getUid(this);
        if (uid == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastHelper.getInstance().displayToastLong("您还未登录！");
                }
            });
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid[0]);
        map.put("ding",hao);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("验证租借权限=======" + result);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 4000) {
                            //可以租借
                            showPay(hao, total, vip);
                            //从购物车删除
                            String wid = finalS.substring(0, finalS.length() - 1);
                            LogUtil.e("购物车wid===" + wid);
                            String[] split = wid.split(",");
                            for (int i = 0; i < split.length; i++) {
                                ShoppingCartHttpBiz.delGood(split[i], uid[0]);
                            }
                        } else {
                            final String msg = jsonObject.getString("msg");
                            //不可以租借
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   // ToastHelper.getInstance().displayToastLong("你所能租借的图书/玩具已超出会员权益，请归还后再租借！");
                                    //弹出对话框
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(SettleActivity.this);
                                    //"您是"+"年卡/半年卡"+"会员，\r\n您的会员权益为：能够免费租借玩具3件，图书8本。\r\n你所租借的图书/玩具已超出会员权益，如您有未归还的玩具/图书，请归还后再租借！"
                                    builder.setMessage(msg);
                                    builder.setNegativeButton("我知道了",null);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            //关闭结算页面
                                            SettleActivity.this.finish();
                                        }
                                    });
                                    //删除该订单
                                    try {
                                        deleteOrder(hao);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        });


    }
    /**
     * 删除订单
     *
     * @param dId
     */
    private void deleteOrder(String dId) throws IOException {
        String url = AppConstants.SERVE_URL + "index/Order/delOrder";
        LogUtil.e("删除的订单号=====" + dId);
        HashMap<String, String> map = new HashMap<>();
        map.put("ding", dId);

        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.e("超出会员权益，无法支付，删除生成的订单=="+OkHttpUtil.getResult(response));
            }
        });
    }

    private void showPay(final String hao, final int total, final String vip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                payFragment = new PayFragment(hao, total);
                payFragment.show(getFragmentManager(), "pay_fragment");
                PayFragment.setOnPayStatusListener(SettleActivity.this);
            }
        });
    }

    /******************支付结果回调********************/
    @Override
    public void onSuccess(final String orderNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SettleActivity.this, SettleSuccessActivity.class);
                intent.putExtra("orderNum", orderNum);
                intent.putExtra("type", SettleSuccessActivity.TYPE_SUSECC);
                startActivity(intent);
                SettleActivity.this.finish();
            }
        });


    }

    @Override
    public void onCancel(String orderNumber) {
        ToastHelper.getInstance().displayToastShort("支付取消");
        onUiChanged();
        //销毁结算页面
        finish();
        //跳转详情页面
        Intent intent = new Intent(this, SettleSuccessActivity.class);
        intent.putExtra("orderNum", orderNumber);
        intent.putExtra("type", SettleSuccessActivity.TYPE_UN_PAY);
        startActivity(intent);
    }

    @Override
    public void onFailuer() {
        ToastHelper.getInstance().displayToastShort("支付失败");
        onUiChanged();
    }

    @Override
    public void onUiChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (payFragment != null) {
                    payFragment.dismiss();
                }
            }
        });
    }

    /******************支付回调********************/

    //从网络获取地址
    private void getAddressFromeNet() {
        String getAddress = AppConstants.SERVE_URL + "index/vip/allshinfo";
        String loginData = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
        HashMap<String, String> params = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            int id = jsonObject.getInt("uid");
            params.put("id", id + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OkHttpUtil.postString(getAddress, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //地址有问题
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastHelper.getInstance().displayToastShort("请添加默认地址");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);

                    if (result != null) {
                        if (result.contains("status")) {
                            //地址有问题
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastHelper.getInstance().displayToastShort("请添加默认地址");
                                }
                            });
                        } else {
                            //地址没问题
                            Message obtain = Message.obtain();
                            obtain.what = getDefaultAddress;
                            obtain.obj = result;
                            handler.sendMessage(obtain);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //解析地址
    private void parseAddressData(String result) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AddressBean>>() {
        }.getType();
        ArrayList<AddressBean> addressList = gson.fromJson(result, type);
        AddressBean addressBean = addressList.get(0);
        setAddressData(addressBean);
    }

    //设置地址到界面
    private void setAddressData(AddressBean bean) {
        int shsex = bean.shsex;
        if (shsex == 1) {
            tvReceiveName.setText(bean.shname + " 女士");
        } else {
            tvReceiveName.setText(bean.shname + " 先生");
        }
        tvReceivePhone.setText(bean.shphon);
        tvReceiveAddress.setText(bean.dwaddre + bean.xxaddre);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
//                String result = data.getStringExtra(JIESUAN_ADDRESS);
//                LogUtil.e("结算页面收到数据" + result);
//                AddressBean addressBean = new Gson().fromJson(result, AddressBean.class);
//                setAddressData(addressBean);
                getAddressFromeNet();
            }
        }

    }

    /*******************天数选择***************/
    class NumberPickerAdapter extends RecyclerView.Adapter<NumberPickerAdapter.mNumberPickerHolder> {
        @Override
        public mNumberPickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mNumberPickerHolder holder = new mNumberPickerHolder(LayoutInflater.from(SettleActivity.this).inflate(R.layout.number_layout, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(mNumberPickerHolder holder, final int position) {
            holder.tv.setText("" + (position + 1));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeDaysPrice(position);
                    mPopWindowSelectDay.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class mNumberPickerHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public mNumberPickerHolder(View v) {
                super(v);
                tv = v.findViewById(R.id.tv_number_item);
            }
        }
    }

    /*******************商品展示***************/
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.mHolder> {
        private List<ShppingCarHttpBean.WjlistBean> list;


        public MyAdapter(List<ShppingCarHttpBean.WjlistBean> list) {
            this.list = list;
        }

        @Override
        public mHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mHolder holder = new mHolder(LayoutInflater.from(
                    SettleActivity.this).inflate(R.layout.settle_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(mHolder holder, int position) {
            ShppingCarHttpBean.WjlistBean item = list.get(position);
            Picasso.with(SettleActivity.this).load(AppConstants.IMG_BASE_URL + item.getShopimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).fit().into(holder.icon);
            holder.name.setText(item.getName());
            holder.price.setText("￥" + item.getShopprice() + "/天");
            holder.settle_peisontime.setText("发货时间：承诺48小时发货");
            holder.tv_settle_count.setText("x" + item.getWshu());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class mHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView name;
            TextView price;
            TextView settle_peisontime;
            TextView settle_peisonfs;
            TextView tv_settle_count;

            public mHolder(View v) {
                super(v);
                icon = v.findViewById(R.id.settle_icon);
                name = v.findViewById(R.id.settle_name);
                price = v.findViewById(R.id.settle_price);
                tv_settle_count = v.findViewById(R.id.tv_settle_count);
                settle_peisontime = v.findViewById(R.id.settle_peisontime);
                settle_peisonfs = v.findViewById(R.id.tv_settle_peisongfs);
            }
        }
    }


}
