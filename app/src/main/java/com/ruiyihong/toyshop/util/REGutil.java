/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Burt on 2017/7/11 0011.
 */

public class REGutil {

        //手机号的正则表达式
    public static boolean checkCellphone(String mobiles) {
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
