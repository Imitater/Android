/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.db;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.ruiyihong.toyshop.util.LogUtil;

/**
 * Created by Burt on 2017/7/9 0009.
 */

public class DBContentObserver extends ContentObserver {
    private Handler mHandler;
    public DBContentObserver(Handler handler) {
        super(handler);
        this.mHandler=handler;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        LogUtil.e("购物车发生变化");
        mHandler.sendEmptyMessage(0);
    }
}
