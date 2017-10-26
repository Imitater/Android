package com.ruiyihong.toyshop.util;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by 81521 on 2017/9/21.
 * 获取随机订单号
 */

public class GetOrderNumUtil {
    public static String getOrderNum(){
        long l = System.currentTimeMillis();
        String result = "";
        try {
            String md5 = md5util.getMD5(l + "");
            Random random = new Random();
            int i = random.nextInt(100);
            result = md5 +i;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


}
