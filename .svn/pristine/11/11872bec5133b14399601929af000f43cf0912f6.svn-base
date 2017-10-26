package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.LogUtil;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 81521 on 2017/8/17.
 */

public class LunboDetailActivity extends BaseActivity {
    @InjectView(R.id.webview)
    WebView mWebview;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lunbo_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String url = getIntent().getStringExtra("url");
        LogUtil.e("URL==="+url);
        if (!TextUtils.isEmpty(url)) {
            mWebview.loadUrl("http://"+url);
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
