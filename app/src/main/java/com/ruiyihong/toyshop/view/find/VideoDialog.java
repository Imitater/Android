package com.ruiyihong.toyshop.view.find;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.findvideoplay.SurfaceVideoViewCreator;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.BaseActivity;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;

import java.io.IOException;

/**
 * Created by 81521 on 2017/8/10.
 * 发现页面视频播放页面
 */

public class VideoDialog extends BaseActivity {
    private SurfaceVideoViewCreator surfaceVideoViewCreator;
    private String video_path;
    private String thumbImage_path;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_find_video;
    }

    @Override
    protected void initView() {
        video_path = getIntent().getStringExtra("video_path");

        thumbImage_path = getIntent().getStringExtra("thumbImage_path");
        LogUtil.e("播放页面=视频缩略图========"+thumbImage_path);
        if (TextUtils.isEmpty(video_path)){
            return;
        }
        if (TextUtils.isEmpty(thumbImage_path)){
            thumbImage_path = AppConstants.IMG_BASE_URL+"20170708\\4aace76a87fdb1f45524d31e684c5153.png";
        }
        //获取权限
        ActivityCompat.requestPermissions(
                VideoDialog.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1
        );

        surfaceVideoViewCreator =
                new SurfaceVideoViewCreator(this,(LinearLayout)findViewById(R.id.video_dialog)) {
                    @Override
                    protected Activity getActivity() {
                        return VideoDialog.this;     /** 当前的 Activity */
                    }

                    @Override
                    protected boolean setAutoPlay() {
                        return true;                 /** true 适合用于，已进入就自动播放的情况 */
                    }

                    @Override
                    protected int getSurfaceWidth() {
                        return 0;                     /** Video 的显示区域宽度，0 就是适配手机宽度 */
                    }
                    @Override
                    protected int geturfaceHeight() {
                       // todo  需要根据缩略图的宽高比计算
                        return 350; /** Video 的显示区域高度，dp 为单位 */
                    }
                    @Override
                    protected void setThumbImage(ImageView thumbImageView) {
                        Glide.with(VideoDialog.this)
                                .load(thumbImage_path)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(new ColorDrawable(Color.parseColor("#898787")))
                                .dontAnimate()
                                .into(thumbImageView);
                    }
                    /** 这个是设置返回自己的缓存路径，
                     * 应对这种情况：
                     *     录制的时候是在另外的目录，播放的时候默认是在下载的目录，所以可以在这个方法处理返回缓存
                     * */
                    @Override
                    protected String getSecondVideoCachePath() {
                        return null;
                    }

                    @Override
                    protected String getVideoPath() {
                         return video_path;
                    }
                };
        surfaceVideoViewCreator.debugModel = false;
        surfaceVideoViewCreator.setUseCache(getIntent().getBooleanExtra("useCache",false));

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        surfaceVideoViewCreator.onKeyEvent(event); /** 声音的大小调节 */
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    break;
//                }
//        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
