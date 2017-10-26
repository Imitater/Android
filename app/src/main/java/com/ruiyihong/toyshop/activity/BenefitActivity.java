package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.EventMoreBenefitBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.StringUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
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
 * Created by Administrator on 2017/10/21 0021.
 */

public class BenefitActivity extends BaseActivity {
    @InjectView(R.id.iv_benefit_pic)
    ImageView mIvBenefitPic;
    @InjectView(R.id.tv_benefit_name)
    TextView mTvBenefitName;
    @InjectView(R.id.tv_benefit_person_number)
    TextView mTvBenefitPersonNumber;
    @InjectView(R.id.tv_benefit_time)
    TextView mTvBenefitTime;
    @InjectView(R.id.tv_benefit_person)
    TextView mTvBenefitPerson;
    @InjectView(R.id.tv_benefit_address)
    TextView mTvBenefitAddress;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    private static final int MSG_BENEFIT = 1;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;
    private static final int NetWorkError = 7;
    @InjectView(R.id.tv_benefit_condition)
    TextView mTvBenefitCondition;
    @InjectView(R.id.tv_benefit_target)
    TextView mTvBenefitTarget;
    @InjectView(R.id.tv_submit)
    TextView mTvSubmit;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BENEFIT:
                    //解析更多公益活动列表
                    parseBenefitData((String) msg.obj);
                    break;
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;
                case CloseLoadingView:     //关闭Loading动画
                    if (loadingView != null)
                        loadingView.loadSuccess(false);
                    break;
                case PageLoading:       //页面加载中动画
                    if (loadingView != null)
                        loadingView.load();
                    break;

            }
        }
    };
    private EventMoreBenefitBean.DataBean dataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_benefit;
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
        int id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        }
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            postNet(AppConstants.EVENT_BENEFIT_DETAIL, map, MSG_BENEFIT);
        } else {
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }

    }

    private void postNet(String url, Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(result)) {
                        Log.e("radish", "response活动详情------------------" + result);
                        CloseLoadingView();
                        Message msg = Message.obtain();
                        msg.what = type;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(NetWorkError);
            e.printStackTrace();
        }
    }

    private void parseBenefitData(String data) {
        //todo 解析data
        EventMoreBenefitBean bean = GsonUtil.parseJsonWithGson(data, EventMoreBenefitBean.class);
        List<EventMoreBenefitBean.DataBean> dataBeanList = bean.getData();
        if (dataBeanList == null || dataBeanList.size() < 1) {
            return;
        }
        dataBean = dataBeanList.get(0);
        Picasso.with(this).load(AppConstants.IMG_BASE_URL + dataBean.getAcimg()).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(mIvBenefitPic);
        mTvBenefitName.setText(dataBean.getTitle() + "");
        mTvBenefitPersonNumber.setText(dataBean.getLimit() + "");
        mTvBenefitTime.setText(dataBean.getStarttime() + "");
        mTvBenefitPerson.setText(dataBean.getMain() + "");
        mTvBenefitAddress.setText(dataBean.getWhere() + "");
        String value = parseTvValue(dataBean.getCondition());
        mTvBenefitCondition.setText(value + "");
        value = parseTvValue(dataBean.getTarget());
        mTvBenefitTarget.setText(value + "");
    }

    private String parseTvValue(String value) {
        if (value != null) {
            String[] split = value.split("；");
            if (split != null && split.length > 1) {
                Log.e("radish", "parseBenefitData: split.size()-------" + split.length);
                value = "";
                for (String str : split) {
                    value += str + "\n\r";
                }
            }
            Log.e("radish", "parseBenefitData: value-----" + value);
        }
        return value;
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    @Override
    protected void initEvent() {
        mTvSubmit.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.tv_submit:

                String starttime = dataBean.getStarttime();
                // "yyyy-MM-dd hh:mm:ss"
                if (StringUtil.compareTime(starttime) != 1) {
                    ToastHelper.getInstance().displayToastShort("报名时间已截止");
                    return;
                }
                final String username = SPUtil.getString(this, AppConstants.SP_LOGIN, "");
                String uname = "";
                try {
                    JSONObject object = new JSONObject(username);
                    uname = object.getString("uname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(uname)) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                Intent intent = new Intent(this, EventEnterActivity.class);
                intent.putExtra("type", dataBean.getBmfs());
                intent.putExtra("id", dataBean.getId());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
