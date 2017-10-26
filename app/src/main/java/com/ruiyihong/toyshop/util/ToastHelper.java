package com.ruiyihong.toyshop.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiyihong.toyshop.R;

public class ToastHelper {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static ToastHelper toast;
    private static Toast toast1 = null;
    private Context context;
    private static Object synObj = new Object();
    public static ToastHelper getInstance() {
        if (toast == null) {
            toast = new ToastHelper();
        }
        return toast;
    }

    public void init(Context context) {
        this.context = context;
    }

    public Toast _toast(String str) {
        return displayToastShort(str);
    }

    /**
     * 显示Toast，duration为short
     *
     * @param
     *            {@link Context} 当前窗体的上下文
     * @param str
     *            {@link String} 消息主体
     *
     */
    public Toast displayToastShort(String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        View v = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView tv_cc = v.findViewById(R.id.tv_toast);
        tv_cc.setText(str);
        toast.setView(v);
        toast.show();
        return toast;
    }

    /**
     * 显示Toast，duration为long
     *
     * @param
     *            {@link Context} 当前窗体的上下文
     * @param str
     *            {@link String} 消息主体
     *
     */
    public Toast displayToastLong(String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        View v = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView tv_cc = v.findViewById(R.id.tv_toast);
        tv_cc.setText(str);
        toast.setView(v);
        toast.show();
        return toast;
    }
    /**
     * 快速关闭toast
     * 不停的疯狂的点击某个按钮，触发了toast以后，toast内容会一直排着队的显示出来，不能很快的消失。这样可能会影响用户的使用。
     *
     * @param
     *            {@link Context} 当前窗体的上下文
     * @param str
     *            {@link String} 消息主体
     *
     * */

    public  void displayToastWithQuickClose(
            final String str) {
        new Thread(new Runnable() {
         
            public void run() {
                handler.post(new Runnable() {
                   
                    public void run() {
                        synchronized (synObj) {
                            if (toast1 != null) {
                                toast1.setText(str);
                                toast1.setDuration(Toast.LENGTH_SHORT);
                            } else {
                                toast1 = Toast.makeText(context, str,
                                        Toast.LENGTH_SHORT);
                            }
                            toast1.show();
                        }
                    }
                });
            }
        }).start();
    }
}
