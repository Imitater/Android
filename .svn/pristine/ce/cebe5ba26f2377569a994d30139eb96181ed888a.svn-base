package com.ruiyihong.toyshop.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.StringUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressUIListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/8/6.
 */

public class EventEnterActivity extends BaseActivity {
    public static final int TYPE_PUTONG = 0;
    public static final int TYPE_VIDEO = 1;
    @InjectView(R.id.et_child_name)
    EditText mEtChildName;
    @InjectView(R.id.et_child_age)
    EditText mEtChildAge;
    @InjectView(R.id.et_parent_name)
    EditText mEtParentName;
    @InjectView(R.id.et_phone)
    EditText mEtPhone;
    @InjectView(R.id.tv_submit)
    TextView mTvSubmit;
    private int mType = -1;
    private static final int MSG_ENTER_PT = 0;
    private static final int MSG_UPDATE_POSITION = 1;
    private static final int INTENT_EVENT_ENTER = 10;

    private VideoView vv_enter;
    private SeekBar sb_video;
    private TextView tv_total;
    private SeekBar video_progress;
    private TextView tv_current;
    private ImageView iv_play;
    private ImageView iv_video;
    private Thread thread;
    private TextView tv_up;
    private String videoPath;
    private boolean isUploadVideo = false;
    private int mId;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ENTER_PT:
                    Log.i("radish","msg.bj------------------"+(String)msg.obj );
                    enter_pt((String)msg.obj);
                    break;
                case MSG_UPDATE_POSITION:
                    //获取当前播放时长
                    updatePlayPosition();
                    break;

            }
        }
    };
    private String child_name;
    private String child_age;
    private String parent_name;
    private String phone;

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", -1);
        mId = intent.getIntExtra("id", -1);
        if (mType == TYPE_PUTONG) {
            return R.layout.dialog_enter;
        } else {
            return R.layout.dialog_enter_video;
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (mType == TYPE_VIDEO) {
            dialogEnterVideo();
        }
    }
    public void netPost(String url, Map<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //报名信息上传完成
                    isUploadVideo = false;
                    progressDialog.dismiss();
                    String result = OkHttpUtil.getResult(response);
                    Log.i("radish","response_baoming------------------ "+result );
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String list = object.getString("list");
                            Message msg = Message.obtain();
                            msg.what = MSG_ENTER_PT;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void dialogEnterVideo() {
        vv_enter = findViewById(R.id.vv_enter);
        tv_up = findViewById(R.id.tv_enter_video_up);
        sb_video = findViewById(R.id.sb_enter_video);
        iv_play = findViewById(R.id.iv_play_bottom);
        tv_current = findViewById(R.id.mediacontroller_time_current);
        video_progress = findViewById(R.id.mediacontroller_progress);
        tv_total = findViewById(R.id.mediacontroller_time_total);
        iv_video = findViewById(R.id.iv_video);


        //vv_enter.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 2.6f);
        //vv_enter.setBufferSize(10240);
        vv_enter.requestFocus();
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOrPauseVideo();
            }
        });

        tv_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventEnterActivity.this, EnterVideoActivity.class);
                startActivityForResult(intent,INTENT_EVENT_ENTER);
            }
        });

        video_progress.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //视频准备好监听
        vv_enter.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                tv_total.setText(StringUtil.formatDuration(mp.getDuration()));
                iv_play.setImageResource(R.mipmap.enter_video_play);
                video_progress.setMax((int) mp.getDuration());
                updatePlayPosition();
                mp.start();
            }
        });

        vv_enter.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
           //     iv_video.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void initEvent() {

        mTvSubmit.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.tv_submit:
                submit();
                break;
        }
    }

    private void submit() {
        if (mType == TYPE_VIDEO && TextUtils.isEmpty(videoPath)){
            ToastHelper.getInstance().displayToastShort("请选择视频");
            return;
        }
        child_name = mEtChildName.getText().toString().trim();
        child_age = mEtChildAge.getText().toString().trim();
        parent_name = mEtParentName.getText().toString().trim();
        phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(child_name) || TextUtils.isEmpty(child_age) || TextUtils.isEmpty(parent_name) || TextUtils.isEmpty(phone)){
            ToastHelper.getInstance().displayToastShort("请完善报名信息");
            return;
        }
        if (mType == TYPE_VIDEO) {
            //上传视频
            uploadVideo();
        }else{
            submitInfo("");
        }
        // 开始上传报名信息
        isUploadVideo = true;

        progressDialog = new ProgressDialog(EventEnterActivity.this);
        progressDialog.setProgressStyle(R.style.MaterialDialog);
        progressDialog.setMessage("正在上传");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void submitInfo( String videoNetPath) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("video", videoNetPath);

        final String username = SPUtil.getString(EventEnterActivity.this, AppConstants.SP_LOGIN, "");
        String uname="";
        try {
            JSONObject object = new JSONObject(username);
            uname = object.getString("uname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("id",mId);
        map.put("uname", uname);
        map.put("bname",child_name);
        map.put("bage",child_age);
        map.put("dname",parent_name);
        map.put("dphone",phone);
        map.put("isbm",mType);
        netPost(AppConstants.EVENT_ENTER_PT,map);
    }
    private void enter_pt(String body) {
        int status = -2;
        try {
            JSONObject object = new JSONObject(body);
            status = object.getInt("status");
            String msg = "";
            switch (status){
                case -2:
                    msg="访问网络不成功";
                    break;
                case -1:
                    msg="信息不完整请重新填写";
                    break;
                case 0:
                    msg="您已报名";
                    break;
                case 1:
                    msg="报名成功";
                    break;
            }
            ToastHelper.getInstance().displayToastShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    private void videoData() {
        if (vv_enter != null) {
            //视频设置
            iv_video.setVisibility(View.GONE);
            vv_enter.setVideoURI(Uri.parse(videoPath));
        }
        if (sb_video != null){
            //上传进度条
            sb_video.setMax(100);
        }

    }
    /**
     * 更新播放时长
     */
    private void updatePlayPosition() {
        //获取当前播放时长
        long currentPosition = vv_enter.getCurrentPosition();
        tv_current.setText(StringUtil.formatDuration(currentPosition));
        video_progress.setProgress((int) currentPosition);
        handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }

    @Override
    public void onBackPressed() {
        if (isUploadVideo) {
            Log.i("radish","dialog------------------" );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("正在报名，是否退出？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                    dialogInterface.dismiss();
                    isUploadVideo = false;
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }else{
            finish();
        }
    }

    private void uploadVideo() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadMethod(videoPath);
                } catch (Exception e) {
                    Log.e("huida", "上传出现了bug" + e.toString());
                }
            }
        });
        thread.start();
        thread.interrupt();
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.mediacontroller_progress:
                    if (!fromUser) {
                        return;
                    }
                    vv_enter.seekTo(seekBar.getProgress());
                    tv_current.setText(StringUtil.formatDuration(progress));

                    iv_play.setImageResource(R.mipmap.enter_video_play);
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
                    break;
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * 播放视频
     */
    private void playOrPauseVideo() {
        if (vv_enter == null){
            return;
        }
        if (vv_enter.isPlaying()) {
            //播放-->暂停
            vv_enter.pause();
            iv_play.setImageResource(R.mipmap.enter_video_stop);
            handler.removeMessages(MSG_UPDATE_POSITION);
        } else {
            //暂停-->播放
            vv_enter.start();
            iv_play.setImageResource(R.mipmap.enter_video_play);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
        }
    }
    //上传文件方法
    private void UploadMethod(String path) throws IOException {
        File apkFile = new File(path);
        String url = AppConstants.EVENT_VIDEO_UPLOAD;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("video", apkFile.getName(), RequestBody.create(null, apkFile));
        MultipartBody build = bodyBuilder.build();

        RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                Log.e("huida", "onUIProgressStart:" + totalBytes);
                tv_up.setText("正在上传");
            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
               /* Log.e("huida", "=============start===============");
                Log.e("huida", "numBytes:" + numBytes);
                Log.e("huida", "totalBytes:" + totalBytes);
                Log.e("huida", "percent:" + percent);
                Log.e("huida", "speed:" + speed);
                Log.e("huida", "============= end ===============");*/
                sb_video.setProgress((int) (100 * percent));

            }

            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                Log.e("huida", "onUIProgressFinish:");
                tv_up.setText("上传成功");

            }
        });
        builder.post(requestBody);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("huida", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (!TextUtils.isEmpty(result)){
                    try {
                        JSONObject object = new JSONObject(result);
                        String videoNetPath = object.getString("video");
                        submitInfo(videoNetPath);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vv_enter != null && vv_enter.isPlaying()) {
            playOrPauseVideo();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_EVENT_ENTER && resultCode == EnterVideoActivity.INTENT_ENTER_EVENT){
            videoPath =  data.getStringExtra(EnterVideoActivity.VIDEO_DATA);
            if (videoPath != null) {
                android.util.Log.i("radish", "videoPath------------------" + videoPath);

              //  File file = new File(videoPath);

                //这里进行替换uri的获得方式
//                imageUri = FileProvider.getUriForFile(this,"com.ruiyihong.toyshop", file);

                videoData();
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
