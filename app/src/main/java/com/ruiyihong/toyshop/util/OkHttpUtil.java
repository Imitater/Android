package com.ruiyihong.toyshop.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ruiyihong.toyshop.view.MyImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Date: 2016-03-07
 * Time: 21:45
 */
public class OkHttpUtil {

    private static OkHttpClient mClient;

    static {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
    }

    /**
     * 执行Get请求
     *
     * @param url
     * @param callback
     */
    public static void get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 执行post
     *
     * @param url
     * @param params
     * @param callback
     * @throws IOException
     */
    public static void postJson(String url, Map<String, Object> params, Callback callback) throws IOException {
        FormBody formBody = getBuilder(params, new FormBody.Builder()).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    public static void postString(String url, Map<String, String> params, Callback callback) throws IOException {
        FormBody formBody = getBuilderString(params, new FormBody.Builder()).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 提交png
     */
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static void postPNG(String url, Map<String, Object> params, File file, String cookie ,Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).
                post(getBuilder(params, new FormBody.Builder()).build()).
                post(RequestBody.create(MEDIA_TYPE_PNG, file)).
                addHeader("cookie", cookie).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 上传文件(包括视频音频)
     * application/octet-stream  二进制流
     * text/x-markdown; charset=utf-8
     */
    public static void upLoadFile(String url, File file,String fileName,Callback callback) throws IOException {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , file);
       /* RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file" , fileName , fileBody)
                .build();*/
        Request request = new Request.Builder().url(url).post(fileBody).build();
        mClient.newCall(request).enqueue(callback);
    }
    /**
     * 装载formBody
     *
     * @param params
     * @param builder
     * @return
     */
    private static FormBody.Builder getBuilder(Map<String, Object> params, FormBody.Builder builder) {
        Gson gson = new Gson();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof String) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            } else {
                builder.add(entry.getKey(), gson.toJson(entry.getValue()));
            }
        }
        return builder;
    }


    /**
     * 装载formBody
     *
     * @param params
     * @param builder
     * @return
     */
    private static FormBody.Builder getBuilderString(Map<String, String> params, FormBody.Builder builder) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public static void download(String url, Callback Callback) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(Callback);
    }

    /**
     * 加载网络图片
     * @param url
     * @param view
     * @throws IOException
     */
    public static void getPic(String url, final View view, final Activity activity) throws IOException {
        get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null && response.code()==200){
                    InputStream is = response.body().byteStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (view instanceof ImageView && bitmap!=null) {
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap(bitmap);
                            }else if (view instanceof MyImageView){
                                MyImageView iv = (MyImageView) view;
                                iv.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            }
        });
    }

    public static String getResult(Response response) throws IOException {

        if (response!=null && !"null".equals(response) && response.code() == 200)
            return response.body().string();
        return null;
    }
}

