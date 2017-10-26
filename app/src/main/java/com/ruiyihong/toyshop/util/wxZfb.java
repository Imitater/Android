package com.ruiyihong.toyshop.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Burt on 2017/7/6 0006.
 */

public class wxZfb {

    /**
     * 检查微信是否存在
     */
    public  boolean isWeixinAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                System.out.println(pinfo.get(i).packageName);
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * 检查支付宝是否存在
     */
    private boolean isZfbAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                System.out.println(pinfo.get(i).packageName);
                if (pn.equals("com.alipay.android.app")) {
                    return true;
                }
            }
        }
        return false;
    }
}
