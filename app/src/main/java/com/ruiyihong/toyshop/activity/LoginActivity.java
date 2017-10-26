package com.ruiyihong.toyshop.activity;
/** *2017.
 * Huida.Burt
 * CopyRight
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.UserBean;
import com.ruiyihong.toyshop.mApplication;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.REGutil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.SendSmsTimerUtils;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.wxapi.WXEntryActivity;
import com.squareup.picasso.Picasso;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.iv_login_icon)
    ImageView ivLoginIcon;
    @InjectView(R.id.act_login_username)
    AutoCompleteTextView actLoginUsername;
    @InjectView(R.id.et_login_pass)
    EditText etLoginPass;
    @InjectView(R.id.bt_login)
    Button btLogin;
    @InjectView(R.id.tv_login_forget)
    TextView tvLoginForget;
    @InjectView(R.id.tv_login_regi)
    TextView tvLoginRegi;
    @InjectView(R.id.textView4)
    TextView textView4;
    @InjectView(R.id.textView5)
    TextView textView5;
    @InjectView(R.id.imageView4)
    ImageView imageView4;
    @InjectView(R.id.imageView5)
    ImageView imageView5;
    private String PhoneCode="";
    private Tencent mTencent;
    private SendSmsTimerUtils sendSmsTimerUtils;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private long sendTime;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ToastHelper.getInstance().displayToastShort("该用户已经注册，请直接登录");
                    dialog.dismiss();
                    sendSmsTimerUtils.onFinish();
                    break;
                case 1:
                    String phone = (String) msg.obj;
                    try {
                        regPhone(phone);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    ToastHelper.getInstance().displayToastShort("短信发送失败,请您稍后再发一条");
                    break;
            }
        }
    };
    private AlertDialog dialog;
    private String selePhone;
    private ProgressDialog progressDialog;

    //发送短信
    private void regPhone(String phone) throws IOException {
        final String sendMsg = AppConstants.SERVE_URL+"index/Xin/regMsg";
        final String number = phone;
        HashMap<String, String> params = new HashMap<>();
        params.put("mobilet",phone);
        final int numcode = (int) ((Math.random() * 9 + 1) * 10000);
        params.put("checkma",numcode+"");
        OkHttpUtil.postString(sendMsg, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if(result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int state = jsonObject.getInt("status");
                        if(state==-2){
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   ToastHelper.getInstance().displayToastShort("一天只能发送5条验证信息，请明天再试");
                                   return;
                               }
                           });
                        }else if(state==1){
                            sendTime = System.currentTimeMillis();
                            PhoneCode = number+"-"+numcode;
                        }else{
                          handler.sendEmptyMessage(2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showWaitDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(R.style.MaterialDialog);
        progressDialog.setMessage("正在提交");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        imageView5.setFocusable(true);
        imageView5.setFocusableInTouchMode(true);
        imageView5.requestFocus();
    }

    @Override
    protected void initData() {
        int type = getIntent().getIntExtra("type",0);
        if(type==WXEntryActivity.WX_TYPE){
            String path = getIntent().getStringExtra("path");
            String openId = getIntent().getStringExtra("openId");
            wxPost(path,openId);
        }
    }

    private void wxPost(String path, final String openid) {
        //网络请求，根据自己的请求方式
        try {
            OkHttpUtil.get(path, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if(result!=null){
                        LogUtil.e("getUserMesg_result:" + result);
                        JSONObject jsonObject = null;
                        try {
                            LogUtil.e("用户基本信息:");
                            jsonObject = new JSONObject(result);
                            String nickname = jsonObject.getString("nickname");
                            int sex = Integer.parseInt(jsonObject.get("sex").toString());
                            final String headimgurl = jsonObject.getString("headimgurl");

                            LogUtil.e("用户基本信息:");
                            LogUtil.e("nickname:" + nickname);
                            LogUtil.e("sex:" + sex);
                            LogUtil.e("headimgurl:" + headimgurl);
                            LogUtil.e("OPEN ID:  "+ openid);

                            String url=AppConstants.SERVE_URL+"index/San/wechat";
                            Map<String,String> para=new HashMap();
                            para.put("nc",nickname);
                            para.put("wx",openid);
                            para.put("yhimg",headimgurl);
                            OkHttpUtil.postString(url, para, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    ToastHelper.getInstance().displayToastShort("网络跑丢了，刷新一下网络试试");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result1 = OkHttpUtil.getResult(response);
                                    if(result1!=null){
                                        LogUtil.e("微信登陆结果"+result1);
                                        UserBean userBean = GsonUtil.parseJsonWithGson(result1, UserBean.class);
                                        userBean.yhimg = headimgurl;
                                        SPUtil.setString(LoginActivity.this,AppConstants.SP_LOGIN,GsonUtil.getGson().toJson(userBean));
                                        ShoppingCartBiz.ParityGood(getApplicationContext());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LoginActivity.this.finish();
                                            }
                                        });
                                    }
                                }
                            });

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

    @Override
    protected void initEvent() {
//        imageView5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        RegistUser();


        //actLoginUsername.
        //String[] arr = {"18813567709", "156353338864", "17633568899"};
        String zjzh = SPUtil.getString(this, AppConstants.SP_ZJZH, "");
        if(!TextUtils.isEmpty(zjzh)){
            String[] split = zjzh.split(",");
            //此处为从SP中读取到的曾经用户名
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_drop_down, split);
            actLoginUsername.setAdapter(arrayAdapter);
        }

        actLoginUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String name = actLoginUsername.getText().toString().trim();
                //md5util.getMD5()
                boolean result = REGutil.checkCellphone(name);
                //ToastHelper.getInstance()._toast("是否合法"+result);
                if (!result && name.length() == 11) {
                    ToastHelper.getInstance()._toast("请输入正确的手机号");
                    actLoginUsername.setText(null);
                }

            }
        });


    }

    @Override
    protected void processClick(View v) {

    }

    private void RegistUser() {

        tvLoginRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_regist_user, null);
                dialog = new AlertDialog.
                        Builder(LoginActivity.this,R.style.Dialog_Fullscreen).create();

                Window win = dialog.getWindow();

                WindowManager.LayoutParams lp = win.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.dimAmount = 0.2f;

                win.setAttributes(lp);
                //win.addContentView(v,lp);
                dialog.setView(v);
                dialog.show();
                final EditText regi_phone = v.findViewById(R.id.et_regi_phone);
                final EditText regi_pass  = v.findViewById(R.id.et_regi_pass);
                final EditText regi_repass  = v.findViewById(R.id.et_regi_repass);
                final EditText regi_yzm  =v.findViewById(R.id.et_regi_yzm);

                v.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //返回键
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                    }
                });
                TextView send_yzm=v.findViewById(R.id.send_yzm);
                sendSmsTimerUtils = new SendSmsTimerUtils(send_yzm, 60000, 1000);
                send_yzm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phone=regi_phone.getText().toString().trim();
                        if(TextUtils.isEmpty(phone)){
                            ToastHelper.getInstance().displayToastShort("请输入手机号");
                            return;
                        }else if(!TextUtils.isEmpty(phone)){
                            boolean b = REGutil.checkCellphone(phone);
                            if(!b){
                                ToastHelper.getInstance().displayToastShort("手机号不合法");
                                return;
                            }
                        }
                        try {
                            sendSms(phone);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendSmsTimerUtils.start();
                    }
                });

                Button regi=v.findViewById(R.id.bt_regit_regi);
                v.findViewById(R.id.now_login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                regi_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b){
                            String phone = regi_phone.getText().toString().trim();
                            if(!REGutil.checkCellphone(phone))
                                regi_phone.setError("手机号不合法");

                        }
                    }
                });


                regi_repass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        String pass=regi_pass.getText().toString().trim();
                        String repass=regi_repass.getText().toString().trim();
                        if(!b){
                            if(!pass.equals(repass)){
                                regi_repass.setError("两次输入的密码不一样");
                            }
                        }

                    }
                });

                regi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.e("单击注册");
                        String pass=regi_pass.getText().toString().trim();
                        final String phone = regi_phone.getText().toString().trim();
                        String yzm = regi_yzm.getText().toString().trim();
                        if(TextUtils.isEmpty(phone)){
                            ToastHelper.getInstance().displayToastShort("请输入手机号");
                            return;
                        }else if(!TextUtils.isEmpty(phone)){
                            boolean b = REGutil.checkCellphone(phone);
                            if(!b){
                                ToastHelper.getInstance().displayToastShort("手机号不合法");
                                return;
                            }
                        }
                        if(TextUtils.isEmpty(yzm)){
                            ToastHelper.getInstance().displayToastShort("验证码不能为空");
                            return;
                        }else if(!TextUtils.isEmpty(yzm)){
                            long nowTime = System.currentTimeMillis();
                            if(!PhoneCode.contains(phone)){
                                ToastHelper.getInstance().displayToastShort("手机号还没发过验证码");
                                return;
                            }
                            if(!PhoneCode.contains(yzm)){
                                ToastHelper.getInstance().displayToastShort("验证码不正确");
                                return;
                            }
                            if((nowTime-sendTime)/6000>20){
                                ToastHelper.getInstance().displayToastShort("验证码过期");
                                return;
                            }

                        }
                        if(TextUtils.isEmpty(pass)){
                            ToastHelper.getInstance().displayToastShort("请输入密码");
                            return;
                        }else if(!TextUtils.isEmpty(pass)){
                            String repass=regi_repass.getText().toString().trim();
                            if(!pass.equals(repass)){
                                regi_repass.setError("两次输入的密码不一样");
                                return;
                            }
                        }

                        final Map<String,String> para=new HashMap<String, String>();
                        para.put("username",phone);
                        para.put("password",pass);
                        showWaitDialog();
                        try {
                            OkHttpUtil.postString(AppConstants.REGI_URL, para, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            ToastHelper.getInstance().displayToastShort("注册失败，请检查网络");
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = OkHttpUtil.getResult(response);
                                    if(result!=null){
                                        try {
                                            Object status = new JSONObject(result).get("status");
                                            LogUtil.e("注册成功"+status.toString());

                                            OkHttpUtil.postString(AppConstants.LOGIN_URL, para, new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {

                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    String result = OkHttpUtil.getResult(response);
                                                    if(result!=null && result.length()>4){
                                                        JSONObject jsonObject = null;
                                                        try {
                                                            jsonObject = new JSONObject(result);
                                                            int status = jsonObject.getInt("status");
                                                            if(status!=1){
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        progressDialog.dismiss();
                                                                        ToastHelper.getInstance().displayToastShort("注册失败，请重新注册");
                                                                    }
                                                                });
                                                                return;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        SPUtil.setString(LoginActivity.this,AppConstants.SP_LOGIN,result);
                                                        String zjzh = SPUtil.getString(LoginActivity.this, AppConstants.SP_ZJZH, "");
                                                        if(zjzh.length()>120){
                                                            SPUtil.setString(LoginActivity.this,AppConstants.SP_ZJZH,"");
                                                        }
                                                        if(!zjzh.contains(phone)){
                                                            zjzh=phone+","+zjzh;
                                                            SPUtil.setString(LoginActivity.this,AppConstants.SP_ZJZH,zjzh);
                                                        }

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Intent intent = new Intent(LoginActivity.this, PersonInfoActivity.class);
                                                                intent.putExtra("type",PersonInfoActivity.REGIST_KEY);
                                                                startActivity(intent);
                                                                dialog.dismiss();
                                                                progressDialog.dismiss();
                                                                ToastHelper.getInstance().displayToastShort("注册成功");
                                                                LoginActivity.this.finish();
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void sendSms(final String phone) throws Exception {
        selePhone = phone;
        String regPhone = AppConstants.SERVE_URL+"index/Xin/regPhone";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", selePhone);
        OkHttpUtil.postString(regPhone, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.getInstance().displayToastShort("发送短信失败");
                        sendSmsTimerUtils.onFinish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("fasongjieguo"+result);
                if(result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        switch (status){
                            case 0:
                                handler.sendEmptyMessage(0);
                                break;
                            case 1:
                                Message obtain = Message.obtain();
                                obtain.what=1;
                                obtain.obj= selePhone;
                                handler.sendMessage(obtain);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @OnClick({R.id.iv_login_icon, R.id.act_login_username, R.id.et_login_pass, R.id.bt_login, R.id.tv_login_forget, R.id.tv_login_regi, R.id.textView4, R.id.imageView4, R.id.imageView5, R.id.textView5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_login_icon:
                //大图点击
                break;
            case R.id.act_login_username:
                //输入框点击
                break;
            case R.id.et_login_pass:
                //密码点击
                break;
            case R.id.bt_login:
                //登陆按钮点击
                try {
                    Login();
                } catch (Exception e) {
                    ToastHelper.getInstance()._toast("登录验证异常");
                }
                break;
            case R.id.tv_login_forget:
                //忘记密码
                Intent intent = new Intent(LoginActivity.this,FogetPasActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login_regi:
                //用户注册
                break;
            case R.id.textView4:
                //大图下面的字
                break;
            case R.id.imageView4:
                //qq
                qqLogin();
                break;
            case R.id.imageView5:
                //微信
                ConsoleWx();
                break;
            case R.id.textView5:
                //第三方登陆事件

                break;
        }
    }



    private void qqLogin() {
        mTencent = Tencent.createInstance(AppConstants.TENCENT_APP_ID, LoginActivity.this.getApplicationContext());
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this, "all", mIUiListener);
    }
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            //第一次 response里面是授权信息
            /**
             * {
             "ret": 0,
             "openid": "19E8D43EB75ED256CAC70C02953F188A",
             "access_token": "65A5A933F116085E051F39CAD65084EF",
             "pay_token": "BA387A1679483A4C8585BE268C7C4128",
             "expires_in": 7776000,
             "pf": "desktop_m_qq-10000144-android-2002-",
             "pfkey": "2c7171fb052154f89e9d439e17e18c11",
             "msg": "",
             "login_cost": 442,
             "query_authority_cost": 322,
             "authority_cost": 0
             }
             */

            ToastHelper.getInstance().displayToastShort("授权成功");
            //Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                //Log.e("huida",openID);
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        //在userInfo 里存储了用户的信息。
                        JSONObject obj = (JSONObject) response;
                        try {
                            final String nickname = obj.getString("nickname");
                            final String headurl = obj.getString("figureurl_qq_2");
                            Log.e("huida", "登录成功" + nickname+"===****==="+headurl);
                            //LogUtil.e(headurl);
                            qqPost(openID,nickname,headurl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        //Log.e(TAG, "登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        //Log.e(TAG, "登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            ToastHelper.getInstance().displayToastShort("授权失败");
        }

        @Override
        public void onCancel() {
            ToastHelper.getInstance().displayToastShort("授权取消");
        }

    }

    private void qqPost(String openID, String nickname, final String headurl) {
        String qqurl=AppConstants.SERVE_URL+"index/San/qq";
        Map<String,String> para=new HashMap();
        para.put("nc",nickname);
        para.put("qq",openID);
        para.put("Uid","0");
        para.put("yhimg",headurl);
        try {
            OkHttpUtil.postString(qqurl, para, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastHelper.getInstance().displayToastShort("网络跑丢了，刷新一下网络试试");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //String result1 = OkHttpUtil.getResult(response);
                    String result1 = response.body().string();
                    LogUtil.e("qq登陆结果"+result1);
                    if(result1!=null){
                        UserBean userBean = GsonUtil.parseJsonWithGson(result1, UserBean.class);
                        userBean.yhimg = headurl;
                        SPUtil.setString(LoginActivity.this,AppConstants.SP_LOGIN,GsonUtil.getGson().toJson(userBean));
                        ShoppingCartBiz.ParityGood(getApplicationContext());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoginActivity.this.finish();
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //登录处理方式
    private void Login() throws IOException {
      /*
        登录成功过后存储到 SP 中，进行加密方法
        上传过程中需要进行 MD532 位加密方法
       */
      final String username=actLoginUsername.getText().toString().trim();
      String password=etLoginPass.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            ToastHelper.getInstance().displayToastShort("请输入用户名");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastHelper.getInstance().displayToastShort("密码不能为空");
            return;
        }
        showWaitDialog();
        Map<String, String> para = new HashMap<String, String>();
        para.put("username", actLoginUsername.getText().toString().trim());
        para.put("password", etLoginPass.getText().toString().trim());
        OkHttpUtil.postString(AppConstants.LOGIN_URL, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ToastHelper.getInstance().displayToastShort("网络连接超时");
                        }
                    });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("登录成功==============="+result);
                if(result!=null && result.length()>4){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if(status!=1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    ToastHelper.getInstance().displayToastShort("账号或密码错误，请重新登录");
                                }
                            });
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SPUtil.setString(LoginActivity.this,AppConstants.SP_LOGIN,result);
                    String zjzh = SPUtil.getString(LoginActivity.this, AppConstants.SP_ZJZH, "");
                    if(zjzh.length()>120){
                        SPUtil.setString(LoginActivity.this,AppConstants.SP_ZJZH,"");
                    }
                    if(!zjzh.contains(username)){
                        zjzh=username+","+zjzh;
                        SPUtil.setString(LoginActivity.this,AppConstants.SP_ZJZH,zjzh);
                    }
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           progressDialog.dismiss();
                           ToastHelper.getInstance()._toast("登录成功");
                           LoginActivity.this.finish();
                       }
                   });
                }

            }
        });


    }

    private IWXAPI mIWXinApi;
    private void ConsoleWx() {
        mIWXinApi = WXAPIFactory.createWXAPI(LoginActivity.this, AppConstants.WEIXIN_APP_ID, false);
        if (mIWXinApi.isWXAppInstalled()) {
            mIWXinApi.registerApp(AppConstants.WEIXIN_APP_ID);
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_logining";
            mIWXinApi.sendReq(req);

            finish();
        } else {
            ToastHelper.getInstance()._toast("微信未安装");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
