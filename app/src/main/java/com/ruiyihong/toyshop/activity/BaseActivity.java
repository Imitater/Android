package com.ruiyihong.toyshop.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.view.View;
import android.view.WindowManager;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.StatusBarUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by 81521 on 2017/7/5.
 * Activity基类
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //初始化ButterKinfe
        ButterKnife.inject(this);
        setStatusBar();
        initView();
        initData();
        initEvent();
        regComButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**获取布局id*/
    protected abstract int getLayoutId();

    /**初始化view*/
    protected abstract void initView();

    /**初始化数据*/
    protected abstract void initData();

    /**初始化事件*/
    protected abstract void initEvent();

    /**
     * 将所有页面都有的共性按钮的点击事件在这里注册
     */
    private void regComButton() {
        View view = findViewById(R.id.back);
        if (view!=null){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //在多个页面都存在的点击，统一在此处处理
            case R.id.back:
                finish();
                break;
            default:
                try {
                    processClick(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 在baseActivity中没有处理的点击事件，在此处处理
     */
    protected abstract void processClick(View v) throws IOException;

	protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        Class clazz = getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);

            extraFlagField.invoke(getWindow(),darkModeFlag,darkModeFlag);//状态栏透明且黑色字体

        }catch (Exception e){

        }

    }




}
