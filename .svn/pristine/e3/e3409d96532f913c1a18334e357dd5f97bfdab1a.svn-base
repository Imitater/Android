package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.VipPowerRvAdapter;
import com.ruiyihong.toyshop.bean.VipBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/7/26.
 */

public class VipDetailActivity extends BaseActivity {
    public final static int TYPE_VIP_PT = 0;
    public final static int TYPE_VIP_BJ = 1;
    public final static int TYPE_VIP_YZ = 2;
    public final static int TYPE_VIP_JZ = 3;
    public final static int TYPE_VIP_TOP = 4;
    private static final int MSG_VIP_TYPE = 10;
    @InjectView(R.id.tv_vip_detail)
    TextView mTvVipDetail;
    @InjectView(R.id.tv_vip_detail_uname)
    TextView mTvVipDetailUname;
    @InjectView(R.id.rv_vip_quanxian)
    RecyclerView mRvVipQuanxian;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VIP_TYPE:
                    vipData((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        int type = getIntent().getIntExtra("type", -1);
        HashMap<String, Object> map = new HashMap<>();
        String strType = "";
        switch (type) {
            case TYPE_VIP_PT:
                strType = "普通会员";
                break;
            case TYPE_VIP_BJ:
                strType = "铂金会员";
                break;
            case TYPE_VIP_YZ:
                strType = "银钻会员";
                break;
            case TYPE_VIP_JZ:
                strType = "金钻会员";
                break;
            case TYPE_VIP_TOP:
                strType = "TOP会员";
                break;

        }
        map.put("vctype", strType);
        netPost(AppConstants.VIP_DETAIL, map);

        mTvVipDetail.setText(strType + "卡详情");
        String user = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
        String uname = "";
        if (!TextUtils.isEmpty(user)) {
            try {
                JSONObject object = new JSONObject(user);
                uname = object.getString("uname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mTvVipDetailUname.setText(uname);
    }

    private void netPost(String url, Map<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(result)) {
                        Message msg = Message.obtain();
                        msg.what = MSG_VIP_TYPE;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void vipData(String obj) {
        VipBean vipBean = GsonUtil.parseJsonWithGson(obj, VipBean.class);
        if (vipBean != null && vipBean.getData() != null && vipBean.getData().size() > 0 && vipBean.getData().toString().length() > 2) {
            VipBean.DataBean bean = vipBean.getData().get(0);
            String vip_power = bean.getVcqy();
            String[] powers = vip_power.split(";");
            mRvVipQuanxian.setLayoutManager(new FullyGridLayoutManager(this,1));
            mRvVipQuanxian.setAdapter(new VipPowerRvAdapter(this,powers));
        }
    }
    @Override
    protected void initEvent() {
    }

    @Override
    protected void processClick(View v) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
