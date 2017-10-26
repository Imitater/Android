package com.ruiyihong.toyshop.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fire.photoselector.activity.PhotoSelectorActivity;
import com.fire.photoselector.models.PhotoSelectorSetting;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.PublishAdapter;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressUIListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.ruiyihong.toyshop.videoshootActivity.ShootMainActivity;
import com.victor.loading.rotate.RotateLoading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/8/5 0005 .
 */

public class PublishActivity extends BaseActivity {
    private static final int SELECT_VIDEO = 200;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 300;
    private static final int SHORT_VIDEO = 0;
    private static final String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong"
            + File.separator + "icon";
    private static final int MSG_PHOTO = 0;

    @InjectView(R.id.tv_add_title)
    TextView tvAddTitle;
    @InjectView(R.id.ib_publish_back)
    ImageButton ibPublishBack;
    @InjectView(R.id.tv_publish_save)
    TextView tvPublishSave;
    @InjectView(R.id.et_publish_content)
    EditText etPublishContent;
    @InjectView(R.id.gv_public_addicon)
    GridView gvPublicAddicon;
    private static final int REQUEST_SELECT_PHOTO = 100;
    @InjectView(R.id.sv_publish)
    SurfaceView svPublish;
    private ArrayList<String> result = new ArrayList<>();
    private PublishAdapter publishAdapter;
    private android.app.AlertDialog chose_dialog;
    private MediaPlayer mp;
    private String mVideoPath = "";
    private int currentUploadType = 0;
    private static final int UPLOAD_TYPE_VIDEO = 276;//上传视频
    private static final int UPLOAD_TYPE_PHOTO = 277;//上传图片

    private static final String[] PERMISSIONS_CONTACT = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String mThumbnail = null;

    private android.app.AlertDialog dialog;
    private android.app.AlertDialog upload_dialog;
    private Uri imageUri;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    private int getDataSize() {
        return result == null ? 0 : result.size();
    }

    @Override
    protected void initView() {
        publishAdapter = new PublishAdapter(this, result);
        gvPublicAddicon.setAdapter(publishAdapter);
        //publishAdapter.setList(result);
        gvPublicAddicon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == getDataSize()) {//点击“+”号位置添加图片
                    showIconDialog();

                } else {//点击图片删除
                    result.remove(i);
                    publishAdapter.setList(result);
                }
            }
        });
    }

    private void showIconDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.select_icon_dialog_find, null);
        chose_dialog = new android.app.AlertDialog.
                Builder(this).create();
        RelativeLayout rl_select_photo = v.findViewById(R.id.rl_select_photo);
        RelativeLayout rl_select_video = v.findViewById(R.id.rl_select_video);
        RelativeLayout rl_shot = v.findViewById(R.id.rl_select_shot);
        RelativeLayout rl_shot_photo = v.findViewById(R.id.rl_select_shot_photo);
        rl_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择图片
                selectPhotos(6, 3);
                chose_dialog.dismiss();
            }
        });
        rl_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择视频
                selectVideo();
                chose_dialog.dismiss();
            }
        });
        rl_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄视频
                Intent intent = new Intent(PublishActivity.this, ShootMainActivity.class);
                startActivityForResult(intent, 0);
                chose_dialog.dismiss();
            }
        });
        rl_shot_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄相片
                choseImageFromCameraCapture();
                chose_dialog.dismiss();
            }
        });

        chose_dialog.setView(v);
        chose_dialog.show();
    }

    private String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong" + File.separator + "icon";
    //private static final String IMAGE_FILE_NAME = System.currentTimeMillis() + ".png";
    private static final int CODE_CAMERA_REQUEST = 0xa1;

    private void choseImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File image_file = new File(path, getImageName());
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(this, "com.ruiyihong.toyshop", image_file);
            }else {
                imageUri = Uri.fromFile(image_file);
            }
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);

        }
    }

    private String getImageName(){
        return System.currentTimeMillis()+".png";
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    break;
                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initEvent() {
        ibPublishBack.setOnClickListener(this);
        tvPublishSave.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.tv_publish_save:
                //上传视频或图片
                if (result.size() == 0 && svPublish.getVisibility() != View.VISIBLE) {
                    //只有文字
                    submitShuoshuo("", "");
                    return;
                }
                upLoad();
                break;
            case R.id.ib_publish_back:
                //返回
                showExitDialog();
                break;
        }
    }

    /**
     * 上传发布视频或图片
     */
    private void upLoad() {

        if (currentUploadType == UPLOAD_TYPE_VIDEO) {
            //上传视频
            if (!TextUtils.isEmpty(mVideoPath) && mThumbnail != null) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            upLoadVideo(mVideoPath, mThumbnail);
                        } catch (Exception e) {
                            Log.e("huida", "上传出现了bug" + e.toString());
                        }
                    }
                });
                thread.start();
                thread.interrupt();
            }
        } else if (currentUploadType == UPLOAD_TYPE_PHOTO) {
            //上传图片 todo
            if (result != null && result.size() > 0) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            upLoadPics();
                        } catch (Exception e) {
                            Log.e("huida", "上传出现了bug" + e.toString());
                        }
                    }
                });
                thread.start();
                thread.interrupt();
            }
        }
    }

    private void upLoadPics() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder dialog_builder = new android.app.AlertDialog.Builder(PublishActivity.this, R.style.RandomDialog);
                View dialogview = View.inflate(PublishActivity.this, R.layout.zhifubao_loading, null);
                TextView message = dialogview.findViewById(R.id.law_shuati_tvdowload);
                message.setText("努力上传中");
                final RotateLoading zhifubao_loading = dialogview.findViewById(R.id.zhifubao_loading);
                zhifubao_loading.start();
                dialog_builder.setView(dialogview);
                upload_dialog = dialog_builder.create();
                upload_dialog.setCancelable(false);
                upload_dialog.show();
            }
        });
        ArrayList<File> fileList = new ArrayList<>();
        try {
            //压缩图片
            for (int i = 0; i < result.size(); i++) {
                //LogUtil.e("图片大小为==========压缩前"+new File(result.get(i)).length());
                //压缩后图片保存的路径

                //原图片文件
                File tempFile = new File(result.get(i));
                String picFilePath = "";
                if (tempFile.length() / 1024 > 1024) {//图片大于1M 压缩
                    //压缩图片
                    picFilePath = filePath + File.separator + System.currentTimeMillis() + ".png";
                    try {
                        compressAndGenImage(result.get(i), picFilePath, 1024, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    picFilePath = result.get(i);
                }
                File file = new File(picFilePath);//上传的图片文件
                if (!file.getParentFile().exists()) {
                    file.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileList.add(file);
            }
            //上传图片
            String url = AppConstants.PUBLISH_UPLOAD_VIDEO_OR_PHOTO;

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(url);

            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);

            for (int i = 0; i < fileList.size(); i++) {

                File file = fileList.get(i);
                bodyBuilder.addFormDataPart("pic[]", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
            MultipartBody build = bodyBuilder.build();
            RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressStart(long totalBytes) {
                    super.onUIProgressStart(totalBytes);

                }

                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    //upload_dialog.setProgress((int) (100 * percent));
                }

                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();

                }
            });
            builder.post(requestBody);

            Call call = okHttpClient.newCall(builder.build());

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("huida", "=============上传失败=============="+e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("==上传图片===" + result);
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String msg = object.getString("msg");
                            if (msg != null && msg.contains("成功")) {
                                String videoNetPath = object.getString("data");
                                //视频发布上传成功，发布说说
                                submitShuoshuo(videoNetPath, "0");
                            }
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

    private void upLoadVideo(String filePath, String mThumbnail) {
        File videoFile = new File(filePath);

        if (!videoFile.exists()) {
            return;
        }
        File thumbFile = new File(mThumbnail);
        if (!thumbFile.exists()) {
            return;
        }
        String url = AppConstants.PUBLISH_UPLOAD_VIDEO_OR_PHOTO;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);

        bodyBuilder.addFormDataPart("pic[]", videoFile.getName(), RequestBody.create(null, videoFile));
        bodyBuilder.addFormDataPart("pic[]", thumbFile.getName(), RequestBody.create(MediaType.parse("image/png"), thumbFile));

        MultipartBody build = bodyBuilder.build();

        RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                //等待动画
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PublishActivity.this, R.style.RandomDialog);
                View dialogview = View.inflate(PublishActivity.this, R.layout.zhifubao_loading, null);
                TextView message = dialogview.findViewById(R.id.law_shuati_tvdowload);
                message.setText("努力上传中");
                final RotateLoading zhifubao_loading = dialogview.findViewById(R.id.zhifubao_loading);
                zhifubao_loading.start();
                builder.setView(dialogview);
                upload_dialog = builder.create();
                upload_dialog.setCancelable(false);
                upload_dialog.show();

            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {

               // upload_dialog.setProgress((int) (100 * percent));
            }

            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();

            }
        });
        builder.post(requestBody);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("huida", "=============上传失败==============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("==上传视频===" + result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        String msg = object.getString("msg");
                        if (msg != null && msg.contains("成功")) {
                            String videoNetPath = object.getString("data");
                            //视频发布上传成功，发布说说
                            submitShuoshuo(videoNetPath, "1");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 发布说说
     *
     * @param paths
     */
    private void submitShuoshuo(String paths, String type) throws IOException {

        String content = etPublishContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            //没有发表文字内容
            if (TextUtils.equals(type,"0") ){
                //有图片
                content = "分享图片";
            }else if (TextUtils.equals(type,"1")){
                //有视频
                content = "分享视频";
            }else {
                //没有图片或视频
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.getInstance().displayToastLong("您没有发布任何内容!");
                    }
                });
                return;
            }
        }
        final String uid = SPUtil.getUid(this)[0];

        String url = AppConstants.PUBLISH_UPLOAD_SHUOSHUO;
        HashMap<String, String> map = new HashMap<>();
        map.put("content", content);
        map.put("uid", uid);
        map.put("pic", paths);
        map.put("ispic", type);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("发布说说=========" + result);
                if (result != null) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        String msg = object.getString("msg");
                        if (msg != null && msg.contains("成功")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (upload_dialog != null) {
                                        upload_dialog.dismiss();
                                    }
                                    finish();
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void selectVideo() {
        Intent intent = new Intent(PublishActivity.this, EnterVideoActivity.class);
        intent.putExtra(EnterVideoActivity.VIDEO_DURATION, 10 * 1000L);

        startActivityForResult(intent, SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_VIDEO:
                currentUploadType = UPLOAD_TYPE_VIDEO;
                if (resultCode == EnterVideoActivity.INTENT_ENTER_EVENT) {
                    String videoPath = data.getStringExtra(EnterVideoActivity.VIDEO_DATA);
                    //final Bitmap videoThumbnail = new MyThumbnailUtils().createVideoThumbnail(videoPath, ScreenUtil.getScreenWidth(PublishActivity.this), (int) DensityUtil.dp2px(200));
                    Bitmap videoThumbnail = getVideoThumbnail(videoPath);
                    String ThumbnailPath = saveVideoThumb(videoThumbnail);
                    LogUtil.e(videoPath + "==缩略图==" + videoThumbnail);
                    if (videoPath != null && new File(ThumbnailPath).exists()) {
                        videoData(videoPath, ThumbnailPath);
                    }
                }
                break;
            case REQUEST_SELECT_PHOTO:
                currentUploadType = UPLOAD_TYPE_PHOTO;
                if (resultCode == RESULT_OK) {
                    svPublish.setVisibility(View.GONE);
                    gvPublicAddicon.setVisibility(View.VISIBLE);
                    // result为照片绝对路径集合,isSelectedFullImage标识是否选择原图
                    result = data.getStringArrayListExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST);
                    publishAdapter.setList(result);
                }
                break;
            case SHORT_VIDEO:
                currentUploadType = UPLOAD_TYPE_VIDEO;
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    Bitmap videoThumbnail = getVideoThumbnail(path);
                    String Thumbnail = saveVideoThumb(videoThumbnail);
                    if (path != null) {
                        videoData(path, Thumbnail);
                    }
                }
                break;case
            CODE_CAMERA_REQUEST:
                // 用户没有进行有效的设置操作，返回
                if (resultCode == RESULT_CANCELED) {
                    //Toast.makeText(Updata_meActivity.this, "取消了", Toast.LENGTH_LONG).show();
                    return;
                }
                if (hasSdcard()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String path = imageUri.getPath();
                            File file = new File(path);
                            LogUtil.e("图片是否存在==="+file.exists());
                            LogUtil.e("path=========="+ path);
                            currentUploadType = UPLOAD_TYPE_PHOTO;
                            svPublish.setVisibility(View.GONE);
                            gvPublicAddicon.setVisibility(View.VISIBLE);
                            //result.add(tempFile.getPath());
                            result.add(path);
                            publishAdapter.setList(result);
                        }
                    });
                } else {
                    ToastHelper.getInstance().displayToastShort("没有SDCard");
                }
                break;
        }
    }
    /**
     * 获取视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    /**
     * 保存方法
     */
    private String saveVideoThumb(Bitmap thumb) {
        String thumbPath = filePath + System.currentTimeMillis() + ".jpg";
        File img = new File(thumbPath);
        if (!img.exists()) {
            try {
                img.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(thumbPath);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return thumbPath;
    }

    private void videoData(String videoPath, String thumbnail) {
        this.mVideoPath = videoPath;
        this.mThumbnail = thumbnail;
        //获取视频，显示
        svPublish.setVisibility(View.VISIBLE);
        gvPublicAddicon.setVisibility(View.GONE);

        svPublish.getHolder().setKeepScreenOn(true);
        svPublish.getHolder().addCallback(new SurfaceViewLis());
        video_play(0, videoPath);

    }

    /**
     * 开始播放 回显视频
     *
     * @param msec 播放初始位置
     */
    protected void video_play(final int msec, final String videoPath) {

        LogUtil.e("视频路径=================" + videoPath);

        // 获取视频文件地址
        try {
            mp = new MediaPlayer();
            //设置音频流类型
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.e("准备好了，播放");
                    mp.start();
                    // 按照初始位置播放
                    mp.seekTo(msec);

                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    mp.reset();
                    video_play(0, videoPath);
                }
            });

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    mp.reset();
                    video_play(0, videoPath);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mp.reset();
            try {
                //设置视屏文件图像的显示参数
                mp.setDisplay(holder);

                mp.setDataSource(mVideoPath);
                mp.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mp != null) {
                mp.stop();
                //释放资源
                mp.release();
            }
        }

    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否取消发布？");
        builder.setNegativeButton("确定取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton("继续保持", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 选择图片页面
     *
     * @param sum
     * @param columnCount
     */
    private void selectPhotos(int sum, int columnCount) {
        PhotoSelectorSetting.MAX_PHOTO_SUM = sum;
        PhotoSelectorSetting.COLUMN_COUNT = columnCount;
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST, result);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param maxSize     target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    public void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * Get bitmap from specified image path
     *
     * @param imgPath
     * @return
     */
    public Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

}
