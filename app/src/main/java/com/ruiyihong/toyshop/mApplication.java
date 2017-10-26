package com.ruiyihong.toyshop;

import android.app.Application;
import android.os.Environment;

import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.CrashHandler;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yixia.camera.VCamera;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Burt on 2017/7/6 0006.
 */

public class mApplication extends Application {

    public static IWXAPI wxapi;
    public static String VIDEO_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong"
            + File.separator + "video";
    @Override
    public void onCreate() {
        super.onCreate();
        ToastHelper.getInstance().init(getApplicationContext());
        File file = new File(VIDEO_PATH);
        if(!file.exists()) file.mkdirs();
        //设置视频缓存路径
        VCamera.setVideoCachePath(VIDEO_PATH);

        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);

        // 初始化拍摄SDK，必须
        VCamera.initialize(getApplicationContext());
	    JPushInterface.init(getApplicationContext());
	    JPushInterface.setDebugMode(true);
/*
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());*/
    }
}
