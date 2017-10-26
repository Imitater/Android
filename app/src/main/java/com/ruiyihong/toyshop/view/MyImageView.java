package com.ruiyihong.toyshop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 81521 on 2017/7/6.
 * 自定义 可以自适应图片大小的ImageView
 * 注：该自定义控件只适用于父布局是填充屏幕的大图片
 *     布局中的小图片不适用
 */

public class MyImageView extends View {
    Bitmap bitmap ;
    private int mScreenWidth;
    private int mScreenHeight;
    private int layout_width;
    private int layout_height;

    public MyImageView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        String namespce="http://schemas.android.com/apk/res-auto";
        String android_namespce="http://schemas.android.com/apk/res/android";

        int ir = attrs.getAttributeResourceValue(namespce, "ir", -1);
        setImageResouce(ir);

        layout_width = attrs.getAttributeIntValue(android_namespce, "layout_width", -3);
        layout_height = attrs.getAttributeIntValue(android_namespce, "layout_height", -3);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float bmW = bitmap.getWidth();
        float bmH = bitmap.getHeight();
        float scale = 1;
        float w = bmW;
        float h = bmH;

        if (bmH>mScreenHeight && bmW>mScreenWidth){
           //图片的宽和高同时大于屏幕
            w = mScreenWidth;
            h = mScreenHeight;

        }else if(bmW>mScreenWidth){
            //图片大于屏幕的宽
            scale = mScreenWidth/bmW; //缩放比
            w = mScreenWidth;
            h = (bmH*scale);

        }else if (bmH>mScreenHeight){
            //图片大于屏幕的宽
            scale = mScreenHeight/bmH; //缩放比
            h= mScreenHeight;
            w =  (bmW*scale);
        }else {
            //图片的宽高都小于屏幕，按宽度放大
            scale = mScreenWidth/bmW;
            w = mScreenWidth;
            h= bmH*scale;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap
                .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

        setMeasuredDimension((int)w,(int)h);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        //背景
        canvas.drawBitmap(bitmap,0,0,null);

    }

    public void setImageResouce(int bg){

        bitmap = BitmapFactory.decodeResource(getResources(), bg);

    }

    public void setImageBitmap(Bitmap bitmap){

        this.bitmap = bitmap;

    }
}
