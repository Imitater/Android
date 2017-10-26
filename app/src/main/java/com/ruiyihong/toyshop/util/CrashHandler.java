/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.util;

/**
 * Created by Burt on 2017/7/28 0028.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Description: 全局异常捕获日志打印
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/octet-stream");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {

        System.out.println("uncaughtException");
        String logPath = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong";
        }

        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            final String path = logPath + File.separator
                    + "errorlog.log";
            FileWriter fw = new FileWriter(path, true);
            fw.write(new Date() + "\n");
            // 错误信息
            // 这里还可以加上当前的系统版本，机型型号 等等信息
            StackTraceElement[] stackTrace = arg1.getStackTrace();
            fw.write(arg1.getMessage() + "\n");
            for (int i = 0; i < stackTrace.length; i++) {
                fw.write("file:" + stackTrace[i].getFileName() + " class:"
                        + stackTrace[i].getClassName() + " method:"
                        + stackTrace[i].getMethodName() + " line:"
                        + stackTrace[i].getLineNumber() + "\n");
            }
            fw.write("\n");
            fw.close();
            // 上传错误信息到服务器
            new Thread(){
                @Override
                public void run() {
                    uploadToServer(path);
                }
            }.start();
        } catch (IOException e) {
            Log.e("crash handler", "load file failed...", e.getCause());
        }

        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());



        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
               //清空数据
                SPUtil.deleteSp(mContext);
                Looper.loop();
            }
        }.start();

    }

    private void uploadToServer(String path) {
        LogUtil.e("开始上传");
        String requestUrl = AppConstants.SERVE_URL+"Version/log";
        File file = new File(path);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(requestUrl);
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("log", file.getName(), RequestBody.create(MEDIA_TYPE_MARKDOWN, file));
        RequestBody requestBody = bodyBuilder.build();
        builder.post(requestBody);
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.e("日志上传"+response.body().string());
            }
        });
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }


        return true;
    }
}


