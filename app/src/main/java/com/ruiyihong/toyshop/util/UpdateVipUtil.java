package com.ruiyihong.toyshop.util;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/10/11.
 * 累计积分
 */

public class UpdateVipUtil {
    /**
     * 累计积分
     * @param price
     */
    public static  void upVip(int price, Context context) throws IOException {
        String url = AppConstants.SERVE_URL+"index/vip/vipUp";
        HashMap<String, String> map = new HashMap<>();
        String[] uid = SPUtil.getUid(context);
        if (uid!=null) {
            map.put("uid",uid[0]);
            map.put("amount", price+"");
            OkHttpUtil.postString(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.e("累计积分结果=="+OkHttpUtil.getResult(response));
                }
            });
        }
    }
}
