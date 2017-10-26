package com.ruiyihong.toyshop.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ShareUtils;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressHelper;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yixia.camera.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.ruiyihong.toyshop.util.ShareUtils.bmpToByteArray;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static final int WX_TYPE = 111;
    private IWXAPI mIwxapi;
    public final static String WX_CODE = "pass_wx_code";
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    //AppSecret: ef2342875abee6fe43d3c51466949504
    private static final String APP_SECRET="ef2342875abee6fe43d3c51466949504";
    private String openid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIwxapi = WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_APP_ID, false);
        mIwxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIwxapi.handleIntent(getIntent(), this);
        mFinish();
    }

    private void mFinish() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.e("错误码 : " + resp.errCode + "");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    ToastHelper.getInstance()._toast("分享失败");
                    LogUtil.e("分享失败");
                } else{
                    ToastHelper.getInstance()._toast("登录失败");
                    LogUtil.e("登录失败");
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        LogUtil.e("code= " + code);
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                       // OkHttpUtil.get();
                        try {
                            getAccess_token(code);
                        } catch (IOException e) {
                           LogUtil.e("Accesstoken:  "+e.toString());
                        }
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        ToastHelper.getInstance()._toast("微信分享成功");
                        finish();
                        break;
                }
                break;
        }
        mFinish();
    }

    /**
     * 获取openid accessToken值用于后期操作
     * @param code 请求码
     */
    private void getAccess_token(final String code) throws IOException {
        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + AppConstants.WEIXIN_APP_ID
                + "&secret="
                + APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        LogUtil.e("getAccess_token：" + path);
        //网络请求，根据自己的请求方式
        OkHttpUtil.get(path, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if(result!=null){
                    LogUtil.e("getAccess_token_result:" + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        openid = jsonObject.getString("openid").toString().trim();
                        String access_token = jsonObject.getString("access_token").toString().trim();
                        LogUtil.e("ACCESS YOKEN:  "+access_token);
                        getUserMesg(access_token, openid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 获取微信的个人信息
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) throws IOException {
        String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        LogUtil.e("getUserMesg：" + path);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type",WX_TYPE);
        intent.putExtra("path",path);
        intent.putExtra("openId",openid);
        WXEntryActivity.this.startActivity(intent);
       /* //网络请求，根据自己的请求方式
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
                        jsonObject = new JSONObject(result);
                        String nickname = jsonObject.getString("nickname");
                        int sex = Integer.parseInt(jsonObject.get("sex").toString());
                        String headimgurl = jsonObject.getString("headimgurl");

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
                                LogUtil.e("微信登陆失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result1 = OkHttpUtil.getResult(response);
                                if(result1!=null){
                                   LogUtil.e("微信登陆结果"+result1);
                                    //Todo 第三方数据登录解析有问题，
                                    SPUtil.setString(WXEntryActivity.this,AppConstants.SP_LOGIN,result1);
                                    ShoppingCartBiz.ParityGood(getApplicationContext());

                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });*/
    }
}
