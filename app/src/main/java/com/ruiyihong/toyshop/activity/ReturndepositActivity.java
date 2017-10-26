package com.ruiyihong.toyshop.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.mine.YajinRetrunCountBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

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

public class ReturndepositActivity extends BaseActivity {


    private static final int MSG_POST_SUCESS = 0;
    private static final int MSG_GET_HAO = 1;
    private static final int REQUEST_RESULT = 2;
    @InjectView(R.id.settle_title)
    RelativeLayout settleTitle;
    @InjectView(R.id.tv_yajin_money)
    TextView tvYajinMoney;
    @InjectView(R.id.tv_guize)
    TextView tvGuize;
    @InjectView(R.id.ll_bangding)
    LinearLayout llBangding;
    @InjectView(R.id.tv_account)
    TextView tvAccount;
    @InjectView(R.id.tv_qubangding)
    TextView tvQubangding;
    @InjectView(R.id.tv_tui)
    TextView tvTui;
    private AlertDialog insureDialog;
    private String zfb_account = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.e("绑定结果==========" + msg.what);
            switch (msg.what) {
                case MSG_POST_SUCESS:
                    int status = msg.arg1;
                    String return_zfHao = (String) msg.obj;
                    if (status == 1) {
                        //提交成功
                        ToastHelper.getInstance().displayToastLong("绑定成功！");
                        //销毁对话框
                        if (dialog_bd != null) {
                            dialog_bd.dismiss();
                        }
                        //显示支付宝账号
                        if (zfb_account.equals(return_zfHao)) {
                            String start = zfb_account.substring(0, 4);
                            String end = zfb_account.substring(zfb_account.length() - 4);
                            tvAccount.setText("支付宝账号：" + start + "***" + end);
                            tvQubangding.setText("编辑 >");
                        }
                    } else {
                        //提交失败
                        ToastHelper.getInstance().displayToastLong("系统繁忙，请稍后再试！");
                    }
                    break;
                case MSG_GET_HAO:
                    parseZhanghao((String) msg.obj);
                    break;
                case REQUEST_RESULT:
                    ToastHelper.getInstance().displayToastShort((String)msg.obj);
                    break;
            }
        }
    };

    private void parseZhanghao(String result) {
        YajinRetrunCountBean bean = GsonUtil.parseJsonWithGson(result, YajinRetrunCountBean.class);
        if (bean != null) {
            List<YajinRetrunCountBean.DataBean> dataList = bean.getData();
            if (dataList != null && dataList.size() > 0) {
                String zfhao = dataList.get(0).getZfhao();
                if (!TextUtils.isEmpty(zfhao)) {
                    String start = zfhao.substring(0, 4);
                    String end = zfhao.substring(zfhao.length() - 4);
                    tvAccount.setText("支付宝账号：" + start + "***" + end);
                    tvQubangding.setText("编辑 >");
                } else {
                    tvAccount.setText("未绑定账号");
                    tvQubangding.setText("去绑定  >");
                }

            }
        }


    }

    private Dialog dialog_bd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_returndeposit;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String yajin = intent.getStringExtra("yajin");
        if (!TextUtils.isEmpty(yajin)) {
            tvYajinMoney.setText("已交押金：" + yajin);
        }

        //获取支付宝账号
        try {
            getBangdingInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getBangdingInfo() throws IOException {
        String url = AppConstants.SERVE_URL + "index/vipclass/selzhifu";
        String[] uids = SPUtil.getUid(this);
        String uid = "";
        if (uids != null) {

            uid = uids[0];
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_GET_HAO;
                    msg.obj = result;
                    handler.sendMessage(msg);

                }
            }
        });
    }

    @Override
    protected void initEvent() {
        llBangding.setOnClickListener(this);
        tvTui.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.ll_bangding:
                showBangdingDialog();
                break;
            case R.id.tv_tui:
                //提交申请退押金
                postRequest();
                break;
        }
    }

    private void postRequest() throws IOException {
        String url = AppConstants.SERVE_URL+"index/vipclass/apply";
        String[] uid = SPUtil.getUid(this);
        if (uid==null){
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid",uid[0]);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("申请退押金结果"+result);
                if (result!=null){
                    Message msg = Message.obtain();
                    msg.what = REQUEST_RESULT;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status==1){
                            //申请退款成功
                            msg.obj = "退押金申请已提交";
                        }else {
                            //申请退款失败
                            msg.obj = "你已提交过退押金申请，无需重复提交！";
                        }
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void showBangdingDialog() {
        dialog_bd = new Dialog(this, R.style.Dialog_Fullscreen);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bangding_zfb, null);
        final EditText et_zfb = view.findViewById(R.id.et_input_zfb);
        Button btn_insure = view.findViewById(R.id.btn_insure);
        btn_insure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取支付宝号
                String zfb = et_zfb.getText().toString().trim();
                showInsureDialog(zfb);

            }
        });
        dialog_bd.setContentView(view);
        dialog_bd.show();
    }

    private void showInsureDialog(final String zfb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你输入的支付宝账号为:\n" + zfb + "\n为了保证退款成功，请仔细核对后再确认！！！");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postToNet(zfb);
                if (insureDialog != null) {
                    insureDialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        insureDialog = builder.create();
        insureDialog.show();
    }

    private void postToNet(final String zfb) {
        String url = AppConstants.SERVE_URL + "index/vipclass/remoney";
        String[] uids = SPUtil.getUid(this);
        String uid = "";
        if (uid != null) {
            uid = uids[0];
        }

        HashMap<String, String> map = new HashMap<>();
        LogUtil.e("uid===" + uid + ",zfb===" + zfb);
        map.put("uid", uid);
        map.put("remoney", zfb);

        try {
            OkHttpUtil.postString(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    //String result = response.body().string();
                    LogUtil.e(response.code() + "===退押金===" + result);
                    if (result != null) {
                        zfb_account = zfb;

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int status = jsonObject.getInt("status");
                            String zfhao = jsonObject.getString("zfhao");
                            Message message = new Message();
                            message.obj = zfhao;
                            message.arg1 = status;
                            message.what = MSG_POST_SUCESS;
                            handler.sendMessage(message);

                        } catch (JSONException e) {
                            LogUtil.e("绑定账号异常了=====");
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }
}
