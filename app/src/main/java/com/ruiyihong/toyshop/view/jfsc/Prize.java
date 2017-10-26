package com.ruiyihong.toyshop.view.jfsc;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by 81521 on 2017/8/3.
 */

class Prize {
    private int id;  //奖品id
    private String name;  //奖品名称
    private Bitmap icon;   //奖品
    private Rect rect;   //奖品摆放的位置
    private int bgColor;   //方块背景颜色
    private String info; //文本信息
    private String iconUrls;//奖品的图片的url地址
    private int weight;//中奖的几率（权重控制）

    public int getJiang_dengji() {
        return jiang_dengji;
    }

    public void setJiang_dengji(int jiang_dengji) {
        this.jiang_dengji = jiang_dengji;
    }

    private int jiang_dengji;//几等奖

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }




    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
    private OnClickListener listener;
    public interface OnClickListener{
        void onClick();
    }

    public boolean isClick(Point point){
        //如果点击的位置是点击按钮覆盖的范围，则返回true，否则返回false
        if (rect!=null) {
            return rect.contains(point.x, point.y);
        }else {
            return false;
        }
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }


    public void click(){
        if(listener!=null){
            listener.onClick();
        }
    }
    public Rect getRect() {
        return rect;
    }
    public void setRect(Rect rect) {
        this.rect = rect;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Bitmap getIcon() {
        return icon;
    }
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setInfo(String info){
        this.info = info;

    }

    public String getInfo() {
        return info;
    }
}
