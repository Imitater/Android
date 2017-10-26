package com.ruiyihong.toyshop.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.yixia.camera.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/9/6.
 */

public class JoinActivity extends BaseActivity {
    private static final int MSG_JOIN_OUR = 0;
    @InjectView(R.id.rl_cooper_intent)
    RelativeLayout mRlCooperIntent;
    @InjectView(R.id.tv_cooper_intent)
    TextView mTvCooperIntent;
    @InjectView(R.id.et_join_name)
    EditText mEtJoinName;
    @InjectView(R.id.et_join_phone)
    EditText mEtJoinPhone;
    @InjectView(R.id.et_join_sheng)
    EditText mEtJoinSheng;
    @InjectView(R.id.et_join_shi)
    EditText mEtJoinShi;
    @InjectView(R.id.tv_join)
    TextView mTvJoin;
    private PopupWindow pop;
    private boolean flag_pop;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_JOIN_OUR:
                    int status = (int) msg.obj;
                    if (status == 1){
                        ToastHelper.getInstance().displayToastShort("信息提交成功");
                    }else if(status == 0){
                        ToastHelper.getInstance().displayToastShort("信息提交失败");
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        mRlCooperIntent.setOnClickListener(this);
        mTvJoin.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.rl_cooper_intent:
                if (flag_pop) {
                    pop.dismiss();
                    flag_pop = !flag_pop;
                } else {
                    showPopupWindow();
                }
                break;
            case R.id.tv_select1:
                flag_pop = false;
                mTvCooperIntent.setText("加盟");
                pop.dismiss();
                break;
            case R.id.tv_select2:
                flag_pop = false;
                mTvCooperIntent.setText("城市合伙人");
                pop.dismiss();
                break;
            case R.id.tv_select3:
                flag_pop = false;
                mTvCooperIntent.setText("其它");
                pop.dismiss();
                break;
            case R.id.tv_join:
                join_our();
                break;

        }
    }

    private void join_our() {
        String name = mEtJoinName.getText().toString().trim();
        String phone = mEtJoinPhone.getText().toString().trim();
        final String sheng = mEtJoinSheng.getText().toString().trim();
        String shi = mEtJoinShi.getText().toString().trim();
        String cooper = mTvCooperIntent.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(sheng) || TextUtils.isEmpty(shi) || TextUtils.isEmpty(cooper) ){
            ToastHelper.getInstance().displayToastShort("请完善信息");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("phone",phone);
        map.put("addrs",sheng);
        map.put("addrshi",shi);
        map.put("cooper",cooper);

        try {
            OkHttpUtil.postJson(AppConstants.HOME_ENTER_OUR, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    Log.e("radish","result--"+result);
                    if (!TextUtils.isEmpty(result)){
                        try {
                            JSONObject object = new JSONObject(result);
                            int status = object.getInt("status");
                            Message msg = Message.obtain();
                            msg.what = MSG_JOIN_OUR;
                            msg.obj = status;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPopupWindow() {
        flag_pop = true;
        int width = mRlCooperIntent.getLayoutParams().width;
        int height = mRlCooperIntent.getLayoutParams().height;
        View popView = View.inflate(this, R.layout.popup_cooper_intent, null);
        pop = new PopupWindow(popView, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.showAsDropDown(mRlCooperIntent, 0, 2);
        TextView tv_select1 = popView.findViewById(R.id.tv_select1);
        TextView tv_select2 = popView.findViewById(R.id.tv_select2);
        TextView tv_select3 = popView.findViewById(R.id.tv_select3);
        tv_select1.setOnClickListener(this);
        tv_select2.setOnClickListener(this);
        tv_select3.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
