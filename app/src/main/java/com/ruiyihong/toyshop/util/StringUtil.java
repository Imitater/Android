package com.ruiyihong.toyshop.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 李晓曼 on 2017/5/14.
 *
 */

public class StringUtil {
    private static final long HOUR = 1000*60*60;
    private static final long MINUTE = 1000*60;
    private static final long SECONED = 1000;

    /**
     * 格式化时长的方法
     * 00:00
     * 00:00:00
     */
    public static String formatDuration(long duration){
        long hour = duration / HOUR;
        long minute = duration % HOUR / MINUTE;
        long seconed = duration % MINUTE/SECONED;

        if (hour == 0){
            //00:00
            return String.format("%02d:%02d",minute,seconed);
        }else{
            //00:00:00
            return String.format("%02d:%02d:%02d",hour,minute,seconed);
        }

    }

    /**
     * 获取格式化后系统时间
     * @return 00:00:00
     */
    public static String getSystemTime(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = simpleDateFormat.format(date);
        return time;
    }


    /**
     * 格式化音频名称
     * name.map--->name
     */
    public static String formatAudioName(String name){
        //就.切割时，要注意.是表示任意字符
        int index = name.lastIndexOf(".");
        String substring = name.substring(0, index);//包含头，不包含尾
        return substring;
    }

    /**
     * 比较时间大小
     * @param str
     * @return
     */
    public static int compareTime(String str){
        Date date1 = new Date();
        Date date2 = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date2 = format.parse(str);
            long time1 = date1.getTime();
            long time2 = date2.getTime();
            long test = time2 - time1;
            if (test > 0){
                //str 日期在后
                return 1;
            }else{
                return  0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 2017-07-29 00:00:00转为date类型，再与当前时间做差值
     * @param str
     * @return
     */
    public static long[] formatDaojishi(String str){
        Date date1 = new Date();
        Date date2 = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long[] times = null;
        try {
            date2 = format.parse(str);
            long time1 = date1.getTime();
            long time2 = date2.getTime();
            long test = time2 - time1;
            System.out.println("time:"+test);
            if (test <= 0){
                return null;
            }
            long time = test/1000;//秒为单位
            long ss = time%60;//秒
            time = time/60;
            long mm = time%60;
            time = time/60;
            long hh = time%24;
            long dd = time/24;
            times = new long[]{dd,hh,mm,ss};
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }
}
