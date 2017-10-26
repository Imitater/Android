package com.ruiyihong.toyshop.activity;

/**
 * 订单结算成功页面
 */

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.ExpressBean;
import com.ruiyihong.toyshop.bean.mine.OrderDataBean;
import com.ruiyihong.toyshop.fragment.PayFragment;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.DividerItemDecoration;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettleSuccessActivity extends BaseActivity {



    @InjectView(R.id.iv_location)
    ImageView ivLocation;
    @InjectView(R.id.tv_orderfinish_shouhuoren)
    TextView tvOrderfinishShouhuoren;
    @InjectView(R.id.tv_orderfinish_address)
    TextView tvOrderfinishAddress;
    @InjectView(R.id.tv_orderfinish_phone)
    TextView tvOrderfinishPhone;
    @InjectView(R.id.textView10)
    TextView textView10;
    @InjectView(R.id.rv_order_finish)
    RecyclerView rvOrderFinish;
    @InjectView(R.id.tv_order_finishi_yunfei)
    TextView tvOrderFinishiYunfei;
    @InjectView(R.id.tv_order_finishi_total_pay)
    TextView tvOrderFinishiTotalPay;
    @InjectView(R.id.tv_order_finishi_count)
    TextView tvOrderFinishiCount;
    @InjectView(R.id.tv_state)
    TextView tvState;
    @InjectView(R.id.tv_order_item_take1)
    TextView tvOrderItemTake1;

    private List<OrderDataBean.DataBean> dataList;
    private static final int MSG_GET_EXPRESS = 1;
    private static final int MSG_ORDER_DATA = 0;
    public static final String TYPE_SUSECC = "TYPE_SUSECC";//支付成功
    public static final String TYPE_UN_PAY = "TYPE_UN_PAY";//未付款
    public static final String TYPE_UN_SEND = "TYPE_UN_SEND";//待发货
    public static final String TYPE_UN_COMMENT = "TYPE_UN_COMMENT";//待评价
    public static final String TYPE_UN_RETURN = "TYPE_UN_RETURN";//等待归还
    public static final String TYPE_FINISH = "TYPE_FINISH";//已归还，订单完成
    private static final int MSG_GIVE_BACK =2 ;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_DATA:
                    parseData((String) msg.obj);
                    break;
                case MSG_GET_EXPRESS:
                    //邮费
                    tvOrderFinishiYunfei.setText("(含运费" + msg.arg1 + "元)");
                    break;
                case MSG_GIVE_BACK:
                    parseGiveBack((String) msg.obj);
                    break;
            }
        }
    };
    private String type;

    private void parseData(String result) {
        OrderDataBean orderDataBean = GsonUtil.parseJsonWithGson(result, OrderDataBean.class);
        if (orderDataBean != null) {
            dataList = orderDataBean.getData();
            if (dataList != null && dataList.size() > 0) {
                rvOrderFinish.setLayoutManager(new FullyLinearLayoutManager(SettleSuccessActivity.this));
                rvOrderFinish.addItemDecoration(new DividerItemDecoration(
                        SettleSuccessActivity.this, DividerItemDecoration.HORIZONTAL_LIST, DensityUtil.dp2px(8), getResources().getColor(R.color.bg_grad_light)));

                SettleAdapter adapter = new SettleAdapter();
                rvOrderFinish.setAdapter(adapter);

                String addrass = dataList.get(0).getUaddr();
                String unames = dataList.get(0).getUnames();
                String uphone = dataList.get(0).getUphone();
                int spnum = dataList.get(0).getSpnum();

                tvOrderfinishShouhuoren.setText("收货人：" + unames);
                tvOrderfinishAddress.setText("收货地址：" + addrass);
                tvOrderfinishPhone.setText(uphone);
                tvOrderFinishiCount.setText("共" + spnum + "件商品");
                tvOrderFinishiTotalPay.setText("合计：￥" + dataList.get(0).getTotal() + "元");
                //tvOrderFinishiCount.setText(""+dataList.get(0).get); 商品数量
            }
        }
    }

    /**
     * 获取运费
     *
     * @return
     */
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
                        msg.arg1 = dataBean.getExpense();
                        msg.what = MSG_GET_EXPRESS;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settle_success;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String orderNum = getIntent().getStringExtra("orderNum");
        type = getIntent().getStringExtra("type");
        if (TYPE_SUSECC.equals(type)) {
            tvState.setText("支付成功");
            tvOrderItemTake1.setVisibility(View.GONE);
            //tvOrderItemTake1.setText("提醒发货");
        } else if (TYPE_UN_PAY.equals(type)) {
            tvState.setText("等待付款");
            tvOrderItemTake1.setVisibility(View.VISIBLE);
            tvOrderItemTake1.setText("现在付款");
        } else if (TYPE_UN_SEND.equals(type)) {
            tvState.setText("等待发货");
            tvOrderItemTake1.setVisibility(View.VISIBLE);
            tvOrderItemTake1.setText("提醒发货");
        } else if (TYPE_UN_COMMENT.equals(type)) {
            tvState.setText("等待评价");
            tvOrderItemTake1.setVisibility(View.VISIBLE);
            tvOrderItemTake1.setText(" 去评价 " );
        } else if (TYPE_UN_RETURN.equals(type)) {
            tvState.setText("等待归还");
            tvOrderItemTake1.setVisibility(View.VISIBLE);
            tvOrderItemTake1.setText(" 去归还 " );
        } else if (TYPE_FINISH.equals(type)) {
            tvState.setText("已归还，交易完成");
            tvOrderItemTake1.setVisibility(View.VISIBLE);
            tvOrderItemTake1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(orderNum)) {
            //通过订单号 获取订单信息
            try {
                getOrderInfo(orderNum);
                getExpressFromNet();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getOrderInfo(String orderNum) throws IOException {
        //String url = AppConstants.SERVE_URL+"Order/selOrder";//查询订单接口
        String url = AppConstants.MY_ORDER_SEL;
        HashMap<String, String> map = new HashMap<>();
        String[] uids = SPUtil.getUid(this);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        map.put("uid", uid);
        map.put("xuhao", orderNum);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //String result = OkHttpUtil.getResult(response);
                String result = response.body().string();
                LogUtil.e(response.code() + "===支付成功页面======" + result);
                if (result != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_ORDER_DATA;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        tvOrderItemTake1.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
         switch (v.getId()){
             case R.id.tv_order_item_take1:
                if (TYPE_UN_PAY.equals(type)) {
                     //现在付款
                    if (dataList!=null && dataList.size()>0) {
//                        payNow(dataList.get(0).getDid(), dataList.get(0).getTotal());
                        checkCanPay(dataList.get(0).getDid(), dataList.get(0).getTotal());
                    }

                 } else if (TYPE_UN_SEND.equals(type)) {
                     //提醒发货
                    ToastHelper.getInstance().displayToastShort("已提醒发货");

                 } else if (TYPE_UN_COMMENT.equals(type)) {
                    //去评价
                    if (dataList!=null && dataList.size()>0) {
                        Intent intent = new Intent(SettleSuccessActivity.this, PinglunActivity.class);
                        intent.putExtra("did", dataList.get(0).getDid());
                        startActivity(intent);
                    }

                 } else if (TYPE_UN_RETURN.equals(type)) {
                     //去归还
                    if (dataList!=null && dataList.size()>0) {
                        showReturnDialog(dataList.get(0).getDid());
                    }

                 }
                 break;
         }
    }
    private void checkCanPay(final String id, final int total) throws IOException {
        //年卡半年卡会员验证是否还有租借的资格
        String url = AppConstants.SERVE_URL + "index/order/qxpay";
        final String[] uid = SPUtil.getUid(this);
        if (uid == null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastHelper.getInstance().displayToastLong("您还未登录！");
                }
            });
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid[0]);
        map.put("ding",id);
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
                            SettleSuccessActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    payNow(id, total);
                                }
                            });
                        } else {
                            //不可以租借
                            SettleSuccessActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastHelper.getInstance().displayToastLong("你所能租借的图书/玩具已超出会员权益，请归还后再租借！");

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
    private void showReturnDialog(final String id) {
        final Dialog dialog = new Dialog(SettleSuccessActivity.this, R.style.Dialog_Fullscreen);
        View view = LayoutInflater.from(SettleSuccessActivity.this).inflate(R.layout.dialog_give_back, null);
        dialog.setContentView(view);
        dialog.show();

        Button ok = view.findViewById(R.id.btn_ok);
        Button cancel = view.findViewById(R.id.btn_cancel);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    giveBack(id);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }
    /**
     * 归还
     *
     * @param id
     */
    private void giveBack(String id) throws IOException {
        String url = AppConstants.SERVE_URL + "index/order/revert";
        HashMap<String, String> map = new HashMap<>();
        map.put("did",id);
       OkHttpUtil.postString(url, map, new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {

           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               String result = OkHttpUtil.getResult(response);
               if (result != null) {
                   Message message = Message.obtain();
                   message.what = MSG_GIVE_BACK;
                   message.obj = result;
                   handler.sendMessage(message);

               }
           }
       });
    }
    private void parseGiveBack(String result) {
        if ("失败".equals(result)){
            ToastHelper.getInstance().displayToastShort("归还失败，请稍后再试");
            return;
        }
        LogUtil.e("归还========="+result);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);;
            String msg1 = jsonObject.getString("msg");
            if (msg1 != null && msg1.contains("成功")) {
                //归还成功。
                ToastHelper.getInstance().displayToastShort("归还成功，我们的工作人员会尽快上门取件");
            } else if (result != null && result.contains("失败")) {
                //删除失败
                ToastHelper.getInstance().displayToastShort("您已经提交过归还申请，请等待工作人员上门取件");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 付款
     *
     * @param id
     * @param total
     */
    private void payNow(String id, int total) {
        final PayFragment payFragment = new PayFragment(id, total);
        payFragment.show(SettleSuccessActivity.this.getFragmentManager(), "pay_fragment");
        PayFragment.setOnPayStatusListener(new PayFragment.onPayStatusListener() {
            @Override
            public void onSuccess(String orderNum) {
                //跳转到支付成功页面
                Intent intent = new Intent(SettleSuccessActivity.this, SettleSuccessActivity.class);
                intent.putExtra("orderNum", orderNum);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancel(String orderNumber) {
                //取消支付
               onUiChanged();
                ToastHelper.getInstance().displayToastShort("支付取消");
            }

            @Override
            public void onFailuer() {
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
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    class SettleAdapter extends RecyclerView.Adapter<SettleAdapter.SellteViewHolder> {
        @Override
        public SellteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SettleSuccessActivity.this).inflate(R.layout.order_shop_item, parent, false);
            return new SellteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SellteViewHolder holder, int position) {
            final OrderDataBean.DataBean dataBean = dataList.get(position);
            holder.tv_name.setText(dataBean.getName());
            holder.tv_age.setText("适合年龄：" + dataBean.getSuitage());
            holder.tv_chandi.setText(dataBean.getBrandplace());
            holder.tv_count.setText("x" + dataBean.getShu()); //数量
            holder.tv_danjia.setText(dataBean.getShopprice() + "/天");
            holder.tv_diaopaijia.setText("吊牌价：" + dataBean.getDpj());
            // 图片
            Picasso.with(SettleSuccessActivity.this).load(AppConstants.IMG_BASE_URL + dataBean.getShopimg()).placeholder(R.mipmap.good_default).into(holder.iv_icon);

            //条目点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // todo 跳转到玩具详情页
                    Intent intent = new Intent(SettleSuccessActivity.this, DetailActivity.class);
                    intent.putExtra("id",dataBean.getId());
                    intent.putExtra("type",dataBean.getIsbw());
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class SellteViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_chandi;
            TextView tv_age;
            TextView tv_danjia;
            TextView tv_diaopaijia;
            TextView tv_count;

            public SellteViewHolder(View itemView) {
                super(itemView);
                iv_icon = itemView.findViewById(R.id.iv_order_shop_icon);
                tv_name = itemView.findViewById(R.id.tv_order_shop_name);
                tv_chandi = itemView.findViewById(R.id.tv_order_shop_chandi);
                tv_age = itemView.findViewById(R.id.tv_order_shop_age);
                tv_danjia = itemView.findViewById(R.id.tv_order_shop_danjia);
                tv_diaopaijia = itemView.findViewById(R.id.tv_order_shop_diaopai);
                tv_count = itemView.findViewById(R.id.tv_order_shop_geshu);
            }
        }
    }

}
