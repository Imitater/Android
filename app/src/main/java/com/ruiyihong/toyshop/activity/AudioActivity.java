package com.ruiyihong.toyshop.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.AudioRvAdapter;
import com.ruiyihong.toyshop.bean.ClassBean;
import com.ruiyihong.toyshop.bean.ClassDetailBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.StatusBarUtil;
import com.ruiyihong.toyshop.util.StringUtil;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;
import com.ruiyihong.toyshop.view.MoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/7/18.
 */

public class AudioActivity extends BaseActivity {
    @InjectView(R.id.mtv_audio)
    MoreTextView mMtvAudio;
    @InjectView(R.id.rv_audio)
    RecyclerView mRvAudio;
    @InjectView(R.id.vitamio)
    VideoView mVitamio;
    @InjectView(R.id.ib_shared)
    ImageButton mIbShared;
    @InjectView(R.id.video_top)
    RelativeLayout mVideoTop;
    @InjectView(R.id.iv_play_bottom)
    ImageView mIvPlayBottom;
    @InjectView(R.id.mediacontroller_time_current)
    TextView mMediacontrollerTimeCurrent;
    @InjectView(R.id.mediacontroller_progress)
    SeekBar mMediacontrollerProgress;
    @InjectView(R.id.mediacontroller_time_total)
    TextView mMediacontrollerTimeTotal;
    @InjectView(R.id.video_bottom)
    LinearLayout mVideoBottom;
    @InjectView(R.id.pb_buffer)
    ProgressBar mPbBuffer;
    @InjectView(R.id.rl_video)
    RelativeLayout mRlVideo;
    @InjectView(R.id.iv_bottom_screen_default)
    ImageView mivBottomScreenDefault;
    @InjectView(R.id.tv_vide_title)
    TextView mTvVideTitle;
    @InjectView(R.id.iv_bg_vitamio)
    ImageView mIvBgVitamio;
    @InjectView(R.id.ib_shared_shu)
    ImageButton mIbSharedShu;
    private static final int MSG_HIDE_CONTROLLER = 1;
    private static final int MSG_UPDATE_POSITION = 2;
    private static final int MSG_DETAIL_INFO = 3;
    private static final int MSG_RECOMMEND_VIDEO = 4;
    private static final int MSG_AUDEIO_PAY = 5;
    private boolean isControllerShowing = false;
    private boolean isShared = false;
    private GestureDetector detector;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_POSITION:
                    //获取当前播放时长
                    updatePlayPosition();
                    break;
                case MSG_HIDE_CONTROLLER:
                    if (isControllerShowing) {
                        switchController();
                    }
                    break;
                case MSG_DETAIL_INFO:
                    deitalInfo((String) msg.obj);
                    break;
                case MSG_RECOMMEND_VIDEO:
                    initRvAudio((String) msg.obj);
                    break;
                case MSG_AUDEIO_PAY:
                    vitamioBoFang((String) msg.obj);
                    break;

            }
        }
    };
    private int id;

    private void vitamioBoFang(String body) {
        ClassDetailBean bean = GsonUtil.parseJsonWithGson(body, ClassDetailBean.class);
        if (bean != null && bean.getData() != null && bean.getData().size()>0) {
            //设置视频播放
            initVitamio(bean.getData().get(0).getMedia());
        }
    }

    private ClassDetailBean.DataBean bean;
    private String username;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio;
    }

    @Override
    protected void initView() {
    }

    private void deitalInfo(String body) {
        ClassDetailBean detailBean = GsonUtil.parseJsonWithGson(body, ClassDetailBean.class);
        if (detailBean != null && detailBean.getData() != null && detailBean.getData().size() > 0) {
            bean = detailBean.getData().get(0);
            if (bean != null) {
                //设置标题
                mTvVideTitle.setText(bean.getTitle());

                //设置查看更多
                mMtvAudio.setText(bean.getBrief());
                mMtvAudio.refreshText();

                switch (detailBean.getStatus()) {
                    case 0:
                        //请付费观看
                        dialogForPay();
                        break;
                    case 1:
                        //已付费视频
                        initVitamio(bean.getMedia());
                        break;
                    case 2:
                        //免费视频
                        initVitamio(bean.getMedia());
                        break;

                }


                //设置视频播放
                //initVitamio(bean.getMedia());

            }
        }
    }

    /**
     * 视频支付情况
     */
    private void dialogForPay() {
        final float money = bean.getPay();
        if (money > 0) {
            final Dialog dialogPay1 = new Dialog(this, R.style.Dialog_Fullscreen);
            dialogPay1.setCancelable(false);


            dialogPay1.getWindow().setGravity(Gravity.CENTER);
            dialogPay1.setContentView(R.layout.dialog_class_pay);
            TextView tv_money = dialogPay1.findViewById(R.id.tv_pay1_money);
            TextView tv_pay = dialogPay1.findViewById(R.id.tv_pay1);
            ImageView iv_cancel = dialogPay1.findViewById(R.id.imageView8);
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPay1.dismiss();
                    finish();
                }
            });
            tv_money.setText(money + "");

            tv_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPay1.dismiss();
                    final Dialog dialog_pay2 = new Dialog(AudioActivity.this, R.style.Dialog_Fullscreen);
                    dialog_pay2.setCancelable(false);
                    dialog_pay2.getWindow().setGravity(Gravity.CENTER);
                    dialog_pay2.setContentView(R.layout.dialog_class_pay2);
                    final ImageView iv_weixin = dialog_pay2.findViewById(R.id.iv_weixin);
                    final ImageView iv_zhifubao = dialog_pay2.findViewById(R.id.iv_zhifubao);
                    dialog_pay2.findViewById(R.id.iv_close_pay2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_pay2.dismiss();
                            finish();
                        }
                    });
                    iv_weixin.setSelected(true);
                    iv_zhifubao.setSelected(false);
                    iv_weixin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            iv_weixin.setSelected(true);
                            iv_zhifubao.setSelected(false);
                        }
                    });

                    iv_zhifubao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            iv_weixin.setSelected(false);
                            iv_zhifubao.setSelected(true);
                        }
                    });

                    dialog_pay2.findViewById(R.id.tv_pay2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (iv_weixin.isSelected()) {
                                //微信支付
                            } else if (iv_zhifubao.isSelected()) {
                                //支付宝支付
                            }

                            dialog_pay2.dismiss();

                            String payUrl = AppConstants.CLASS_PAY;
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("kid", id);
                            map.put("price", money);
                            map.put("uname", username);
                            netPost(payUrl, map, MSG_AUDEIO_PAY);
                        }
                    });
                    dialog_pay2.show();
                }
            });
            dialogPay1.show();
        }
    }

    /**
     * 视频设置
     *
     * @param path
     */
    private void initVitamio(String path) {

        mVitamio.setVideoPath(AppConstants.VIDEO_BASE_URL + path);
        mVitamio.setBufferSize(10240);
        mVitamio.requestFocus();
        mVitamio.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 1.8f);
        Picasso.with(this).load(bean.getBgimg()).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(mIvBgVitamio);

        //头尾布局默认不显示
        mVideoTop.setVisibility(View.GONE);
        mVideoBottom.setVisibility(View.GONE);
    }

    private void initRvAudio(String obj) {

        final List<ClassBean> list = (List<ClassBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ClassBean>>() {
        }.getType());
        ;
        mRvAudio.setLayoutManager(new FullyGridLayoutManager(this, 3));
        AudioRvAdapter rvAdapter = new AudioRvAdapter(this, list);
        mRvAudio.setAdapter(rvAdapter);


        rvAdapter.setOnItemClickListener(new AudioRvAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(AudioActivity.this, AudioActivity.class);
                //携带玩具id
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        netSetting();

    }
    private void init(){

        String sp_login = SPUtil.getString(this, AppConstants.SP_LOGIN, "");


        try {
            JSONObject object = new JSONObject(sp_login);
            username = object.getString("uname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //初始化信息
        initInfo();

        initRelativityAudio();
    }

    private void initRelativityAudio() {

        String recommendUrl = AppConstants.CLASS_RECOMMEND;
        HashMap<String, Object> map = new HashMap<>();
        map.put("kcclass", 0);
        netPost(recommendUrl, map, MSG_RECOMMEND_VIDEO);
    }

    private void initInfo() {

        Vitamio.isInitialized(this);
        mivBottomScreenDefault.setVisibility(View.GONE);
        mIbShared.setVisibility(View.GONE);
        id = getIntent().getIntExtra("id", -1);
        String detailUrl = AppConstants.CLASS_DETAIL;
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("uname", username);
        netPost(detailUrl, map, MSG_DETAIL_INFO);

    }
    private void netSetting() {
        if (NetWorkUtil.is3gConnected(this)){
            //流量
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("网络判断");
            builder.setMessage("目前是流量访问，是否继续？");
            builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    init();
                }
            });
            builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }else{
            init();
        }
    }

    private void netPost(final String url, HashMap<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.isSuccessful()) {
                        String body = response.body().string();
                        if (!TextUtils.isEmpty(body)) {
                            Message msg = Message.obtain();
                            msg.what = type;
                            msg.obj = body;
                            handler.sendMessage(msg);

                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initEvent() {
        mIvPlayBottom.setOnClickListener(this);
      //  mIbShared.setOnClickListener(this);
        mIbSharedShu.setOnClickListener(this);

        MyOnSeekBarChangeListener myOnSeekBarChangeListener = new MyOnSeekBarChangeListener();

        mMediacontrollerProgress.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        //手势识别事件监听
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //视频准备好监听
        mVitamio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediacontrollerTimeTotal.setText(StringUtil.formatDuration(mp.getDuration()));

                mMediacontrollerProgress.setMax((int) mp.getDuration());

                updatePlayPosition();
                mp.start();
            }
        });


        //视频播放错误监听
        mVitamio.setOnErrorListener(new MyOnErrorListener());

        //视频播放完成监听
        mVitamio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //规避系统解析的错误

                //视频完成后，移除自动更新进度
                handler.removeMessages(MSG_UPDATE_POSITION);

                mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(mp.getDuration()));

                mMediacontrollerProgress.setProgress((int) mp.getDuration());

                //切换播放暂停按钮
                mIvPlayBottom.setBackgroundResource(R.drawable.video_stop);

                //TODO 播放下一曲

            }
        });
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bottom:
                playOrPauseVideo();
                break;
            case R.id.ib_shared_shu:
                showDialogShared();
                break;
        }
    }

    private void showDialogShared() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shared);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
    }

    /**
     * 更新播放时长
     */
    private void updatePlayPosition() {
        //获取当前播放时长
        long currentPosition = mVitamio.getCurrentPosition();
        mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(currentPosition));
        mMediacontrollerProgress.setProgress((int) currentPosition);
        handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }

    /**
     * 播放视频
     */
    private void playOrPauseVideo() {
        if (mVitamio.isPlaying()) {
            //播放-->暂停
            mVitamio.pause();
            mIvPlayBottom.setBackgroundResource(R.drawable.video_stop);
            handler.removeMessages(MSG_UPDATE_POSITION);
        } else {
            //暂停-->播放
            mVitamio.start();
            mIvPlayBottom.setBackgroundResource(R.drawable.video_play);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
        }
    }

    /**
     * 切换控制面板状态方法
     */
    private void switchController() {
        if (isControllerShowing) {
            //显示---->隐藏
            mVideoTop.setVisibility(View.GONE);
            mVideoBottom.setVisibility(View.GONE);
            isControllerShowing = false;
        } else {
            //隐藏---->显示
            mVideoTop.setVisibility(View.VISIBLE);
            mVideoBottom.setVisibility(View.VISIBLE);
            isControllerShowing = true;
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 3000);
        }
    }

    /**
     * 触摸事件监听
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detector.onTouchEvent(event)) return true;
        // 处理手势结束

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势识别
     */
    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        /**
         * 确认是单击时触发，仅单击
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //切换控制面板的状态
            switchController();
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPauseVideo();
            return super.onDoubleTap(e);
        }
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度条变化时
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
              /*  case R.id.sb_volume:
                    *//**
                 * 1.音频流类型
                 * 2.音量
                 * 3.标志 0:不显示系统音量弹窗
                 *        1:显示系统音量弹窗
                 *//*
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                    break;*/
                case R.id.mediacontroller_progress:
                    if (!fromUser) {
                        return;
                    }
                    mVitamio.seekTo(seekBar.getProgress());
                    mMediacontrollerTimeCurrent.setText(StringUtil.formatDuration(progress));

                    mIvPlayBottom.setBackgroundResource(R.drawable.video_play);
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
                    break;
            }
        }

        /**
         * 当手指触摸seekbar时调用
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //不需要自动隐藏
            handler.removeMessages(MSG_HIDE_CONTROLLER);
        }

        /**
         * 当手指离开seekbar时调用
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //自动隐藏
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 3000);
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //给用户提示信息
            AlertDialog.Builder builder = new AlertDialog.Builder(AudioActivity.this);
            builder.setTitle("提示");
            builder.setMessage("当前音频不可播放");
            builder.setNegativeButton("退出播放", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
            return true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucent(this);
    }
}