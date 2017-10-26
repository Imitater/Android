package com.ruiyihong.toyshop.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.CollectionActivity;
import com.ruiyihong.toyshop.activity.ContactCustomerActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.activity.MineAddressActivity;
import com.ruiyihong.toyshop.activity.MinePreferentialActivity;
import com.ruiyihong.toyshop.activity.OrderActivity;
import com.ruiyihong.toyshop.activity.PersonInfoActivity;
import com.ruiyihong.toyshop.activity.ReturndepositActivity;
import com.ruiyihong.toyshop.activity.SetingActivity;
import com.ruiyihong.toyshop.activity.VipMemberBuyActivity;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.bean.VipDataBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的
 */

public class MineFragment extends BaseFragment {
    @InjectView(R.id.ib_message_center)
    ImageButton ibMessageCenter;
    @InjectView(R.id.iv_head_icon)
    CircleImageView ivHeadIcon;
    @InjectView(R.id.iv_vip_icon)
    CircleImageView ivVipIcon;
    @InjectView(R.id.tv_mine_nickname)
    TextView tvMineNickname;
    @InjectView(R.id.tv_mine_info)
    TextView tvMineInfo;
    @InjectView(R.id.ib_mine_right)
    ImageButton ibMineRight;
    @InjectView(R.id.rl_mine_personal)
    RelativeLayout rlMinePersonal;
    @InjectView(R.id.iv_mine_youhuiquan)
    ImageView ivMineYouhuiquan;
    @InjectView(R.id.iv_youhui_right)
    ImageView ivYouhuiRight;
    @InjectView(R.id.rl_mine_youhuiquan)
    RelativeLayout rlMineYouhuiquan;
    @InjectView(R.id.iv_mine_address)
    ImageView ivMineAddress;
    @InjectView(R.id.iv_address_right)
    ImageView ivAddressRight;
    @InjectView(R.id.rl_mine_address)
    RelativeLayout rlMineAddress;
    @InjectView(R.id.iv_mine_customer)
    ImageView ivMineCustomer;
    @InjectView(R.id.iv_customer_right)
    ImageView ivCustomerRight;
    @InjectView(R.id.rl_mine_customer)
    RelativeLayout rlMineCustomer;
    @InjectView(R.id.iv_mine_question)
    ImageView ivMineQuestion;
    @InjectView(R.id.iv_question_right)
    ImageView ivQuestionRight;
    @InjectView(R.id.rl_mine_question)
    RelativeLayout rlMineQuestion;
    @InjectView(R.id.iv_mine_share)
    ImageView ivMineShare;
    @InjectView(R.id.iv_share_right)
    ImageView ivShareRight;
    @InjectView(R.id.rl_mine_share)
    RelativeLayout rlMineShare;
    @InjectView(R.id.iv_mine_set)
    ImageView ivMineSet;
    @InjectView(R.id.iv_set_right)
    ImageView ivSetRight;
    @InjectView(R.id.rl_mine_set)
    RelativeLayout rlMineSet;
    @InjectView(R.id.bt_mine_login)
    Button bt_mine_login;
    @InjectView(R.id.rl_mine_vip)
    RelativeLayout rlMineOrder;
    @InjectView(R.id.tv_viptime_mine)
    TextView tvViptimeMine;
    @InjectView(R.id.tv_viptype_mine)
    TextView tvViptypeMine;
    @InjectView(R.id.iv_mine_vip)
    ImageView ivMineVip;
    @InjectView(R.id.iv_vip_right)
    ImageView ivVipRight;
    @InjectView(R.id.iv_mine_collect)
    ImageView ivMineCollect;
    @InjectView(R.id.iv_collect_right)
    ImageView ivCollectRight;
    @InjectView(R.id.rl_mine_collect)
    RelativeLayout rlMineCollect;
    @InjectView(R.id.ll_viptype_other)
    LinearLayout llViptypeOther;
    @InjectView(R.id.tv_vipmoney_mine)
    TextView tvVipmoneyMine;
    @InjectView(R.id.ll_viptype_cika)
    LinearLayout llViptypeCika;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        parseVipData((String) msg.obj);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private VipDataBean vipDataBean;

    private void parseVipData(String vipData) {
        Gson gson = new Gson();
        //LogUtil.e("vipData==="+vipData);
        vipDataBean = gson.fromJson(vipData, VipDataBean.class);
        final String vkind = vipDataBean.vkind;
        if (TextUtils.isEmpty(vkind)) {
            llViptypeCika.setVisibility(View.VISIBLE);
            llViptypeOther.setVisibility(View.INVISIBLE);
            tvVipmoneyMine.setText("0元");
        } else if (vkind.contains("次卡")) {
            llViptypeCika.setVisibility(View.VISIBLE);
            llViptypeOther.setVisibility(View.INVISIBLE);
            tvVipmoneyMine.setText(vipDataBean.urest + "元");
        } else {
            llViptypeCika.setVisibility(View.INVISIBLE);
            llViptypeOther.setVisibility(View.VISIBLE);
            tvViptimeMine.setText(vipDataBean.dayrest + "天");
        }
        llViptypeCika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String[] uid = SPUtil.getUid(mActivity);
                if (uid == null) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    if (TextUtils.isEmpty(vkind)) {
                        intent = new Intent(mActivity, VipMemberBuyActivity.class);
                    } else {
                        intent = new Intent(mActivity, ReturndepositActivity.class);
                        intent.putExtra("yajin", vipDataBean.urest);
                    }
                }
                startActivity(intent);
            }
        });
    }

    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_mine, null);
        return view;
    }


    protected void initData() {
        ButterKnife.inject(mActivity);
        getData();
    }

    private void getData() {
        String login = SPUtil.getString(mActivity, AppConstants.SP_LOGIN, "");
        if (TextUtils.isEmpty(login)) {
            bt_mine_login.setVisibility(View.VISIBLE);
            rlMinePersonal.setVisibility(View.INVISIBLE);
            llViptypeCika.setVisibility(View.INVISIBLE);
            llViptypeOther.setVisibility(View.INVISIBLE);
        } else {
            bt_mine_login.setVisibility(View.INVISIBLE);
            rlMinePersonal.setVisibility(View.VISIBLE);
            try {
                parseData(login);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getVipType();
        }

    }

    private void getVipType() {
        String url = AppConstants.SERVE_URL + "index/vipclass/membinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", SPUtil.getUid(mActivity)[0]);
        try {
            OkHttpUtil.postString(url, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    Log.e("radish", "onResponse: result====" + result);
                    if (result != null) {
                        Message obtain = Message.obtain();
                        obtain.what = 0;
                        obtain.obj = result;
                        handler.sendMessage(obtain);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseData(String login) {
        LogUtil.e("longin===============" + login);
        Gson gson = new Gson();
        UserBean userBean = gson.fromJson(login, UserBean.class);
        String headUrl = "";
        if (userBean.yhimg != null && userBean.yhimg.contains("http")) {
            headUrl = userBean.yhimg;
        } else {
            headUrl = AppConstants.IMG_BASE_URL + userBean.yhimg;
        }
        Picasso.with(mActivity).load(headUrl).placeholder(R.mipmap.personinfo_head_icon).error(R.mipmap.personinfo_head_icon).fit().into(ivHeadIcon);
        tvMineNickname.setText(userBean.yhniche);
        if (!TextUtils.isEmpty(userBean.births)) {
            String info = "";
            switch (userBean.bsex) {
                case 0:
                    info += "男宝宝";
                    break;
                case 1:
                    info += "女宝宝";
                    break;
            }
            tvMineInfo.setText(info + " " + userBean.births);
        } else {
            tvMineInfo.setText("请完善宝宝信息");
        }

        String uclass = "";
        if (userBean.uclass != null) {
            uclass = userBean.uclass;
        }
        int uclassIcon = 0;
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
        if (uclassIcon != 0) {
            ivVipIcon.setImageResource(uclassIcon);
        }

    }


    @Override
    protected void initEvent() {
        rlMineYouhuiquan.setOnClickListener(this);
        rlMineAddress.setOnClickListener(this);
        rlMineSet.setOnClickListener(this);
        bt_mine_login.setOnClickListener(this);
        rlMinePersonal.setOnClickListener(this);
        rlMineOrder.setOnClickListener(this);
        rlMineCollect.setOnClickListener(this);
        rlMineCustomer.setOnClickListener(this);
        llViptypeCika.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        String string = SPUtil.getString(mActivity, AppConstants.SP_LOGIN, "");
        switch (view.getId()) {
            case R.id.rl_mine_youhuiquan://我的优惠券
                if (TextUtils.isEmpty(string)) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    intent = new Intent(mActivity, MinePreferentialActivity.class);
                }
                break;
            case R.id.rl_mine_address://常用地址
                if (TextUtils.isEmpty(string)) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    intent = new Intent(mActivity, MineAddressActivity.class);
                    intent.putExtra("type", MineAddressActivity.MINE_ADDRESS);
                }
                break;
            case R.id.rl_mine_set://设置
                intent = new Intent(mActivity, SetingActivity.class);
                break;
            case R.id.bt_mine_login://登录
                intent = new Intent(mActivity, LoginActivity.class);
                break;
            case R.id.rl_mine_personal://个人信息
                intent = new Intent(mActivity, PersonInfoActivity.class);
                intent.putExtra("type", PersonInfoActivity.lOGIN_KEY);
                break;
            case R.id.rl_mine_vip: //我的订单
                if (TextUtils.isEmpty(string)) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    intent = new Intent(mActivity, OrderActivity.class);
                }
                break;
            case R.id.rl_mine_collect: //我的收藏
                if (TextUtils.isEmpty(string)) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    intent = new Intent(mActivity, CollectionActivity.class);
                }
                break;
            case R.id.rl_mine_customer: //客服
                if (TextUtils.isEmpty(string)) {
                    intent = new Intent(mActivity, LoginActivity.class);
                } else {
                    intent = new Intent(mActivity, ContactCustomerActivity.class);
                }
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        //注册广播接收者
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.ruiyihong.toyshop.updataVip");
//		MyReceiver myReceiver = new MyReceiver();
//		mActivity.registerReceiver(myReceiver,filter);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新vip卡信息
        }
    }
}
