package com.ruiyihong.toyshop.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.OrderActivity;
import com.ruiyihong.toyshop.activity.PinglunActivity;
import com.ruiyihong.toyshop.activity.SettleActivity;
import com.ruiyihong.toyshop.activity.SettleSuccessActivity;
import com.ruiyihong.toyshop.activity.ToyShopActivity;
import com.ruiyihong.toyshop.bean.mine.MyOrderBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.DividerItemDecoration;
import com.ruiyihong.toyshop.view.MyListView;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/7/27 0027 .
 */

public class OrderContentFragment extends BaseFragment {
    private static final int MSG_DELETE_ORDER = 10;
    private static final int MSG_GIVE_BACK = 11;
    private static final int SETTLE_SUCCESS = 12;
    private int type = -1;
    @InjectView(R.id.tv_order_con_null)
    TextView tvOrderConNull;
    @InjectView(R.id.tv_order_con_cho0se)
    TextView tvOrderConCho0se;//去选宝贝
    @InjectView(R.id.rl_order_con_empty)
    RelativeLayout rlOrderConEmpty;
    @InjectView(R.id.rcy_order_con)
    RecyclerView rcyOrderCon;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://待付款
                    parseOrder((String) msg.obj);
                    break;
                case 1://待发货
                    parseOrder((String) msg.obj);
                    break;
                case 2://去归还
                    parseOrder((String) msg.obj);
                    break;
                case 3://已归还
                    parseOrder((String) msg.obj);
                    break;
                case 4://待评价
                    parseOrder((String) msg.obj);
                    break;
                case MSG_DELETE_ORDER:
                    //删除订单
                    parseDeleteOrder((String)msg.obj,msg.arg1);
                    break;
                case MSG_GIVE_BACK:
                    parseGiveBack((String)msg.obj);
                    break;
            }
        }
    };

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
                ToastHelper.getInstance().displayToastShort("归还失败，请稍后再试");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseDeleteOrder(String result, int position) {
        if ("失败".equals(result)){
            ToastHelper.getInstance().displayToastShort("删除订单失败，请稍后再试");
            return;
        }
        LogUtil.e("删除订单========="+result);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            String msg1 = data.getString("msg");

            if (msg1 != null && msg1.contains("成功")) {
                //删除成功。刷新界面
                if (dataList != null && orderAdapter != null) {
                    dataList.remove(position);
                    orderAdapter.notifyDataSetChanged();
                }
            } else if (result != null && result.contains("失败")) {
                //删除失败
                ToastHelper.getInstance().displayToastShort("删除订单失败，请稍后再试");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private OrderAdapter orderAdapter;
    private List<MyOrderBean.DataBeanX> dataList;

    /**
     * 订单
     *
     * @param result
     */
    private void parseOrder(String result) {
        MyOrderBean bean = GsonUtil.parseJsonWithGson(result, MyOrderBean.class);
        if (bean != null) {
            dataList = bean.getData();
            if (dataList != null && dataList.size() > 0) {
                if (rlOrderConEmpty!=null && rcyOrderCon!=null) {
                    rlOrderConEmpty.setVisibility(View.GONE);
                    rcyOrderCon.setVisibility(View.VISIBLE);
                    orderAdapter = new OrderAdapter();
                    rcyOrderCon.setLayoutManager(new LinearLayoutManager(mActivity));
                    rcyOrderCon.addItemDecoration(new DividerItemDecoration(
                            mActivity, DividerItemDecoration.HORIZONTAL_LIST, DensityUtil.dp2px(8), getResources().getColor(R.color.bg_grad_light)));
                    rcyOrderCon.setAdapter(orderAdapter);
                }
            } else {
                if(rlOrderConEmpty!=null && rcyOrderCon!=null){
                    rlOrderConEmpty.setVisibility(View.VISIBLE);
                    rcyOrderCon.setVisibility(View.GONE);
                }
            }
        }
    }
    @SuppressLint("ValidFragment")
    public OrderContentFragment(int type) {
        this.type = type;
    }

    @Override
    protected View initView() {
        View inflate = View.inflate(mActivity, R.layout.fragment_order_content, null);
        return inflate;
    }

    /**
     * tpey：页面类型
     * 0：待付款
     * 1：待发货
     * 2：去归还
     * 3：已归还
     * 4：待评价
     */
    @Override
    public void initData() {
        switch (type) {
            case 0://待付款
                initUnpaidOrder();
                break;
            case 1://待发货
                initUndeliveredOrder();
                break;
            case 2://去归还
                initGiveBack();
                break;
            case 3://已归还
                initAlreadyRetruned();
                break;
            case 4://待评价
                initUnPinjia();
                break;

        }
    }

    /**
     * 已归还
     */
    private void initAlreadyRetruned() {
        String url = AppConstants.SERVE_URL + "index/order/yigui";

        String[] uids = SPUtil.getUid(mActivity);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);

        try {
            postData(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 去归还
     */
    private void initGiveBack() {
        String url = AppConstants.SERVE_URL + "index/order/qugui";

        String[] uids = SPUtil.getUid(mActivity);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);

        try {
            postData(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 未评价
     */
    private void initUnPinjia() {
        String url = AppConstants.SERVE_URL + "index/order/daipj";
        String[] uids = SPUtil.getUid(mActivity);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);

        try {
            postData(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 待发货
     */
    private void initUndeliveredOrder() {
        String url = AppConstants.SERVE_URL + "index/order/daihuo";
        String[] uids = SPUtil.getUid(mActivity);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        try {
            postData(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 未付款
     */
    private void initUnpaidOrder() {
        String url = AppConstants.SERVE_URL + "index/order/dfk";
        String[] uids = SPUtil.getUid(mActivity);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        try {
            postData(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postData(String url, HashMap<String, String> map) throws IOException {

        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
               // String result = response.body().string();
                LogUtil.e(type + "=我的订单=" + result);
                Message msg = Message.obtain();
                msg.what = type;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void initEvent() {
        tvOrderConCho0se.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_order_con_cho0se:
                //跳转到玩具汇
                Intent intent = new Intent(mActivity, ToyShopActivity.class);
                intent.putExtra("type", ToyShopActivity.DATA_TYPE_TOY);
                startActivity(intent);
                break;
        }
    }

    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.order_item, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, final int position) {
            switch (type) {
                case 0://待付款
                    holder.tv_order_item_take1.setVisibility(View.GONE);

                    holder.tv_order_item_take2.setText("现在付款");
                    holder.tv_order_item_take2.setTextColor(getResources().getColor(R.color.tab_selected));

                    holder.tv_order_item_take3.setText("删除订单");
                    holder.tv_order_item_take3.setTextColor(getResources().getColor(R.color.text_color));

                    holder.tv_order_item_status.setText("待付款");

                    break;
                case 1://待发货
                    holder.tv_order_item_take1.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take2.setText("买家已付款");
                    holder.tv_order_item_take2.setTextColor(getResources().getColor(R.color.blue_dark));
                    holder.tv_order_item_take2.setBackgroundColor(Color.TRANSPARENT);

                    holder.tv_order_item_take3.setText("提醒发货");
                    holder.tv_order_item_take3.setTextColor(getResources().getColor(R.color.text_color));

                    holder.tv_order_item_status.setText("已付款");

                    break;
                case 2://去归还
                    holder.tv_order_item_take1.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take2.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take3.setText("  归还  ");
                    holder.tv_order_item_take3.setTextColor(getResources().getColor(R.color.tab_selected));
                    holder.tv_order_item_take3.setBackgroundResource(R.drawable.seares_normal);

                    holder.tv_order_item_status.setText("交易成功");

                    break;
                case 3://已归还
                    holder.tv_order_item_take1.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take2.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take3.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_status.setText("已归还");

                    break;
                case 4://待评价
                    holder.tv_order_item_take1.setVisibility(View.INVISIBLE);

                    holder.tv_order_item_take2.setVisibility(View.GONE);
//                    holder.tv_order_item_take2.setText("查看物流");
//                    holder.tv_order_item_take2.setTextColor(getResources().getColor(R.color.tab_selected));

                    holder.tv_order_item_take3.setText("  评价  ");
                    holder.tv_order_item_take3.setTextColor(getResources().getColor(R.color.red_light));
                    holder.tv_order_item_take3.setBackgroundResource(R.drawable.seares_red);

                    holder.tv_order_item_status.setText("交易成功");
                    break;
            }
            List<MyOrderBean.DataBeanX.DataBean> itemList = dataList.get(position).getData();
            holder.lv_order_item.setAdapter(new OrderItemAdapter(itemList));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String settle_type = "";
                    if (type==0){
                        //待付款
                        settle_type = SettleSuccessActivity.TYPE_UN_PAY;
                        LogUtil.e("settle_type==="+0);
                    }else if (type==1){
                        //待发货
                        settle_type = SettleSuccessActivity.TYPE_UN_SEND;
                        LogUtil.e("settle_type==="+1);
                    }else if (type==2){
                        //去归还
                        settle_type = SettleSuccessActivity.TYPE_UN_RETURN;
                        LogUtil.e("settle_type==="+2);
                    }else if (type==3){
                        //已归还
                        settle_type = SettleSuccessActivity.TYPE_FINISH;
                        LogUtil.e("settle_type==="+3);
                    }else if (type==4){
                        //待评价
                        settle_type = SettleSuccessActivity.TYPE_UN_COMMENT;
                        LogUtil.e("settle_type==="+4);
                    }
                    //条目点击事件，跳转到订单详情页
                    Intent intent = new Intent(mActivity, SettleSuccessActivity.class);
                    LogUtil.e("settle_type==="+settle_type);
                    intent.putExtra("type",settle_type);
                    intent.putExtra("orderNum",dataList.get(position).getId());
                    startActivityForResult(intent,SETTLE_SUCCESS);
                }
            });

            holder.tv_order_item_take3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type == 0) {
                        //删除订单
                        ShowdeleteDialog(dataList.get(position).getId(), position);
                    }
                    if (type == 1) {
                        //todo 提醒发货
                        ToastHelper.getInstance().displayToastShort("已提醒发货");
                    }
                    if (type == 2) {
                        //归还
                        showReturnDialog(dataList.get(position).getId());
                    }
                    if (type == 4) {
                        //评价
                        Intent intent = new Intent(mActivity, PinglunActivity.class);
                        intent.putExtra("did", dataList.get(position).getId());
                        startActivity(intent);
                    }
                }
            });


            MyOrderBean.DataBeanX dataBeanX = dataList.get(position);
            List<MyOrderBean.DataBeanX.DataBean> list = dataBeanX.getData();
            int shu = 0;
            for (int i = 0; i < list.size(); i++) {
                MyOrderBean.DataBeanX.DataBean dataBean = list.get(i);
                shu = dataBean.getShu()+shu;
            }

            holder.tv_order_item_num.setText(shu+"");
            if (dataBeanX.getData()!=null &&dataBeanX.getData().size()>0) {
               final int total = dataList.get(position).getData().get(0).getTotal();
                holder.tv_order_item_money.setText(total + "");//合计价格
                holder.tv_order_item_take2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (type == 0) {
                            //检查是否有租借的权限
                            try {
                                checkCanPay(dataList.get(position).getId(), total);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        if (type == 4) {
                            //todo 查看物流
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tv_order_item_status;
            TextView tv_order_item_num;
            TextView tv_order_item_money;
            TextView tv_order_item_yunfei;
            TextView tv_order_item_take1;
            TextView tv_order_item_take2;
            TextView tv_order_item_take3;
            MyListView lv_order_item;

            public OrderViewHolder(View itemView) {
                super(itemView);
                tv_order_item_status = itemView.findViewById(R.id.tv_order_item_status);
                lv_order_item = itemView.findViewById(R.id.lv_order_item);
                tv_order_item_num = itemView.findViewById(R.id.tv_order_item_num);
                tv_order_item_money = itemView.findViewById(R.id.tv_order_item_money);
                tv_order_item_yunfei = itemView.findViewById(R.id.tv_order_item_yunfei);
                tv_order_item_take1 = itemView.findViewById(R.id.tv_order_item_take1);
                tv_order_item_take2 = itemView.findViewById(R.id.tv_order_item_take2);
                tv_order_item_take3 = itemView.findViewById(R.id.tv_order_item_take3);

            }
        }
    }

    private void checkCanPay(final String id, final int total) throws IOException {
        //年卡半年卡会员验证是否还有租借的资格
        String url = AppConstants.SERVE_URL + "index/order/qxpay";
        final String[] uid = SPUtil.getUid(mActivity);
        if (uid == null) {
            mActivity.runOnUiThread(new Runnable() {
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    payNow(id, total);
                                }
                            });
                        } else {
                            String msg = jsonObject.getString("msg");
                            //不可以租借
                            //弹出对话框
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                            //"您是"+"年卡/半年卡"+"会员，\r\n您的会员权益为：能够免费租借玩具3件，图书8本。\r\n你所租借的图书/玩具已超出会员权益，如您有未归还的玩具/图书，请归还后再租借！"
                            builder.setMessage(msg);
                            builder.setNegativeButton("我知道了",null);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        });
    }

    private void showReturnDialog(final String id) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_give_back, null);
        dialog.setContentView(view);
        dialog.show();

        Button ok = view.findViewById(R.id.btn_ok);
        Button cancel = view.findViewById(R.id.btn_cancel);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveBack(id);
                if (dialog != null) {
                    dialog.dismiss();
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
     * @param id
     */
    private void giveBack(String id) {
        String url = AppConstants.SERVE_URL + "index/order/revert";
        HashMap<String, String> map = new HashMap<>();
        map.put("did",id);

        try {
            postNet(url,map,MSG_GIVE_BACK,-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ShowdeleteDialog(final String id, final int position) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_give_back, null);
        dialog.setContentView(view);
        dialog.show();

        Button ok = view.findViewById(R.id.btn_ok);
        Button cancel = view.findViewById(R.id.btn_cancel);
        TextView title = view.findViewById(R.id.tv_dialog_title);
        TextView info = view.findViewById(R.id.tv_dialog_info);

        title.setText("是否删除该订单");
        info.setVisibility(View.INVISIBLE);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteOrder(id, position);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
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
     * 删除订单
     *
     * @param dId
     * @param position
     */
    private void deleteOrder(String dId, final int position) throws IOException {
        String url = AppConstants.SERVE_URL + "index/Order/delOrder";
        LogUtil.e("删除的订单号=====" + dId);
        HashMap<String, String> map = new HashMap<>();
        map.put("ding", dId);

        postNet(url, map, MSG_DELETE_ORDER, position);
    }

    /**
     * 请求网络
     */
    private void postNet(String url, Map<String, String> map, final int type, final int position) throws IOException {
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                Message obtain = Message.obtain();
                obtain.what = type;
                if (result != null) {
                    obtain.obj = result;
                    obtain.arg1 = position;
                    handler.sendMessage(obtain);
                } else {
                    obtain.obj = "失败";
                    handler.sendMessage(obtain);
                }
            }
        });
    }

    /**
     * 付款
     *
     * @param id
     * @param total
     */
    private void payNow(String id, int total) {

        final PayFragment payFragment = new PayFragment(id, total);
        payFragment.show(mActivity.getFragmentManager(), "pay_fragment");
        PayFragment.setOnPayStatusListener(new PayFragment.onPayStatusListener() {
            @Override
            public void onSuccess(String orderNum) {
                //跳转到支付成功页面
                Intent intent = new Intent(mActivity, SettleSuccessActivity.class);
                intent.putExtra("orderNum", orderNum);
                startActivity(intent);
                //更新数据
//                initUndeliveredOrder();
//                initUnpaidOrder();
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
               mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (payFragment != null) {
                            LogUtil.e("payFrament dismiss");
                            payFragment.dismiss();
                        }
                    }
                });
            }
        });

    }

    private class OrderItemAdapter extends BaseAdapter {

        private final List<MyOrderBean.DataBeanX.DataBean> itemList;

        public OrderItemAdapter(List<MyOrderBean.DataBeanX.DataBean> itemList) {
            this.itemList = itemList;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(mActivity, R.layout.order_shop_item, null);
                holder.iv_icon = view.findViewById(R.id.iv_order_shop_icon);
                holder.tv_name = view.findViewById(R.id.tv_order_shop_name);
                holder.tv_chandi = view.findViewById(R.id.tv_order_shop_chandi);
                holder.tv_age = view.findViewById(R.id.tv_order_shop_age);
                holder.tv_danjia = view.findViewById(R.id.tv_order_shop_danjia);
                holder.tv_diaopaijia = view.findViewById(R.id.tv_order_shop_diaopai);
                holder.tv_count = view.findViewById(R.id.tv_order_shop_geshu);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            MyOrderBean.DataBeanX.DataBean dataBean = itemList.get(i);

            //图片
            String shopimg = dataBean.getShopimg();
            if (!TextUtils.isEmpty(shopimg)){
                Picasso.with(mActivity).load(AppConstants.IMG_BASE_URL+shopimg).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_icon);
            }else{
                Picasso.with(mActivity).load(R.mipmap.good_default).into(holder.iv_icon);
            }

            holder.tv_name.setText(dataBean.getWname());//玩具名
            if (!TextUtils.isEmpty(dataBean.getSuitage())) {
                holder.tv_age.setText("适合年龄：" + dataBean.getSuitage());//适合年龄
            }
            holder.tv_chandi.setText(dataBean.getBrandplace());//产地
            if (!TextUtils.isEmpty(dataBean.getJia())) {
                holder.tv_danjia.setText(dataBean.getJia() + " 元/天");//单价
            }
            if (!TextUtils.isEmpty(dataBean.getDpj())) {
                holder.tv_diaopaijia.setText("吊牌价：" + dataBean.getDpj());//吊牌价
            }
            holder.tv_count.setText("x" + dataBean.getShu());//数量
            return view;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_chandi;
            TextView tv_age;
            TextView tv_diaopaijia;
            TextView tv_danjia;
            TextView tv_count;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // LogUtil.e("回到我的订单页面===");
        //从订单详情页跳转回来，刷新页面
        //initData();
//        OrderActivity activity = (OrderActivity) getActivity();
//        activity.notifyOrder();

    }
}
