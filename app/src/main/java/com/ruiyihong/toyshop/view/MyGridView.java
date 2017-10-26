package com.ruiyihong.toyshop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by 81521 on 2017/7/3.
 */

public class MyGridView extends GridView {
    private int flag = 0;
    private double StartX;
    private double StartY;

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return false;
//    }
@Override
public boolean onInterceptTouchEvent(MotionEvent ev) {
    //总是调用listview的touch事件处理
    onTouchEvent(ev);
    if(ev.getAction()==MotionEvent.ACTION_DOWN){
        StartX = ev.getX();
        StartY = ev.getY();
        return false;
    }
    if(ev.getAction()==MotionEvent.ACTION_MOVE){
        double ScollX = ev.getX()-StartX;
        double ScollY = ev.getY()-StartY;
        //判断是横滑还是竖滑，竖滑的话拦截move事件和up事件（不拦截会由于listview和scrollview同时执行滑动卡顿）
        if(Math.abs(ScollX)<Math.abs(ScollY)){
            flag = 1;
            return true;
        }
        return false;
    }
    if(ev.getAction()==MotionEvent.ACTION_UP){
        if(flag==1){

            return true;
        }
        return false;
    }
    return super.onInterceptTouchEvent(ev);
}
}
