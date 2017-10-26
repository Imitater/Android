package com.ruiyihong.toyshop.view.find;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.squareup.picasso.Picasso;

/**
 *  Created by 81521 on 2017/8/6.
 *  自定义dialog  发现页面：全屏图片展示
 */

public class ImageDialog extends Dialog {
    private String[] imageDatas;//图片数据
    private ViewPager vp;
    private DialogItemClickListener mListener;
    private LinearLayout ll_point;
    private static ImageDialog imageDialog;
    private int prePoint;


    public ImageDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public ImageDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected ImageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.find_image_dialog_layout);
        vp = findViewById(R.id.vp_image);
        ll_point = findViewById(R.id.ll_image_point);

    }
    public static ImageDialog getDialog(Context context){
        if (imageDialog==null){
            imageDialog = new ImageDialog(context,R.style.Dialog_Find_Image);
        }
        return imageDialog;
    };

    /**
     * 设置图片数据
     * @param imageDatas
     */
    public void setImageData(String[] imageDatas, int position){
        this.imageDatas = imageDatas;


        initData(position);

    }

    private void initData(int selectedPosition) {
        //初始化数据
        ll_point.removeAllViews();
        for (int i = 0; i < imageDatas.length; i++) {
            ImageView iv_point = new ImageView(getContext());
            iv_point.setBackgroundResource(R.drawable.nomarl_point);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( DensityUtil.dp2px(6), DensityUtil.dp2px(6));
            if (i!=0){
                params.leftMargin = DensityUtil.dp2px(7);
            }

            iv_point.setLayoutParams(params);
            ll_point.addView(iv_point);

        }
        vp.setAdapter(new ImageAdapter());
        vp.setCurrentItem(selectedPosition);
        ll_point.getChildAt(vp.getCurrentItem()).setBackgroundResource(R.drawable.blue_point);


        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View point = ll_point.getChildAt(position);
                for (int i = 0; i < ll_point.getChildCount(); i++) {
                    ll_point.getChildAt(i).setBackgroundResource(R.drawable.nomarl_point);
                }
                point.setBackgroundResource(R.drawable.blue_point);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageDatas.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            PinchImageView iv = new PinchImageView(getContext());
            //iv.setBackgroundResource(imageDatas[position]);
            Picasso.with(getContext()).load(AppConstants.FIND_IMAGE_BASE_URL+imageDatas[position]).placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(iv);
//
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!=null){
                        mListener.itemClick(position);
                    }
                }
            });

            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface DialogItemClickListener{
        void itemClick(int position);
    }

    /**
     * 图片点击事件
     * @param listener
     */
    public void setOnDialogClickListener(DialogItemClickListener listener){
        this.mListener = listener;
    }

}
