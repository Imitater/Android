package com.ruiyihong.toyshop.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.ShareUtils;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 李晓曼 on 2017/8/8.
 */

public class AboutOurActivity extends BaseActivity {
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.wv)
    WebView mWv;
    @InjectView(R.id.ib_shared)
    ImageButton mIbShared;
    private String web_url;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_about_out;
    }

    @Override
    protected void initView() {
        web_url = "http://appadmin.y91edu.com/aboutus/aboutus.html";
    }

    @Override
    protected void initData() {
        mWv.loadUrl(web_url);

        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings settings = mWv.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void initEvent() {
        mIbShared.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.ib_shared:
                showDialogShared();
                break;
        }
    }

    private void showDialogShared() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shared);
        dialog.findViewById(R.id.tv_share_moments).setOnClickListener(new AboutOurActivity.ShareOnClickListener());//朋友圈
        dialog.findViewById(R.id.tv_share_wechat).setOnClickListener(new AboutOurActivity.ShareOnClickListener());//微信
        dialog.findViewById(R.id.tv_share_qq).setOnClickListener(new AboutOurActivity.ShareOnClickListener());//qq
        dialog.findViewById(R.id.tv_share_qq_kj).setOnClickListener(new AboutOurActivity.ShareOnClickListener());//qq空间
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
    }
    // TODO: 2017/8/29 分享
    class ShareOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            ShareUtils shareUtils = new ShareUtils();
            switch (view.getId()){
                case R.id.tv_share_moments:
                    ShareUtils.shareUrl(AboutOurActivity.this,web_url,"于玖壹 关于我们", R.mipmap.icon_mapmarker,null,1);
                    break;
                case R.id.tv_share_wechat:
                    //分享至微信好友
                    ShareUtils.shareUrl(AboutOurActivity.this,web_url,"于玖壹 关于我们",R.mipmap.icon_mapmarker,null,0);
                    break;
                case R.id.tv_share_qq:
                    //分享至qq好友
                    //分享至朋友圈
                    shareUtils.shareToQq(AboutOurActivity.this,"于玖壹 关于我们", "", web_url,"", new MyUiListener());
                    break;
                case R.id.tv_share_qq_kj:
                    //分享至qq空间
                    LogUtil.e("关于我们，分享到qq空间");
                    shareUtils.shareToQzone(AboutOurActivity.this, "于玖壹 关于我们", "关于我们", web_url, web_url, new MyUiListener());
                    break;
            }
        }
    }
    class MyUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastHelper.getInstance().displayToastShort("分享成功");
        }

        @Override
        public void onError(UiError uiError) {
            LogUtil.e("分享失败==="+uiError.errorMessage );
            ToastHelper.getInstance().displayToastShort("分享失败");
        }

        @Override
        public void onCancel() {
            ToastHelper.getInstance().displayToastShort("分享取消");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
       // ButterKnife.bind(this);
    }
}
