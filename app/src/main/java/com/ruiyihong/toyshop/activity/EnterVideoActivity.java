package com.ruiyihong.toyshop.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.VideoEditAdapter;
import com.ruiyihong.toyshop.adapter.VideoListRvAdapter;
import com.ruiyihong.toyshop.bean.EnterVideoBean;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 本实例，我们将通过检索SDCard上的Video信息
 * 在MediaStore中，MediaStore.Video.Media中就有Video相关信息，
 * 同时MediaStore.Video.Thumbnails中含有各个video对应的缩略图信息
 *
 * @author Administrator
 */
public class EnterVideoActivity extends BaseActivity {

    public static final String VIDEO_DATA = "video_data";
    public static final String VIDEO_DURATION = "video_duration";
    public static final int INTENT_ENTER_EVENT = 11;
    private static final int INTENT_ENTER_EDIT = 12;
    private static final int INTENT_DATA_NULL = 15;
    @InjectView(R.id.rv_frag_video)
    RecyclerView mRvFragVideo;
    private Cursor cursor;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initUI();
        }
    };
    private ArrayList<EnterVideoBean> videoList;
    private long maxDution = 60 * 1000L * 3;//最长时长

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enter_video;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //获取传递的时长
        maxDution = getIntent().getLongExtra(VIDEO_DURATION, 60 * 1000L * 3);
        new Thread() {
            @Override
            public void run() {
                String[] thumbColumns = new String[]{
                        MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.VIDEO_ID
                };

                String[] mediaColumns = new String[]{
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.MIME_TYPE
                };
                //首先检索SDcard上所有的video
                cursor = EnterVideoActivity.this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);

                videoList = new ArrayList<EnterVideoBean>();

                if (cursor.moveToFirst()) {
                    do {
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        if(size > 0 && duration > 1000 && ".mp4".equals(path.substring(path.length() - 4))){
                            //1秒以上的视频
                            EnterVideoBean info = new EnterVideoBean();
                            info.setSize(size);
                            info.setDuration(duration);
                            info.setFilePath(path);
                            info.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                            info.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                            //获取当前Video对应的Id，然后根据该ID获取其Thumb
                            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                            Cursor videoThumbnailCursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);

                            String thumbUri = "";
                            if (videoThumbnailCursor.moveToFirst()) {
                                thumbUri = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            }

                            ContentResolver crThumb = getContentResolver();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 1;
                            info.setThumbPath(thumbUri);
                            //然后将其加入到videoList
                            videoList.add(info);
                        }
                    } while (cursor.moveToNext());
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private void initUI() {


        if (videoList != null) {
            mRvFragVideo.setLayoutManager(new FullyGridLayoutManager(this,3));
            VideoListRvAdapter rvAdapter = new VideoListRvAdapter(this, videoList);
            mRvFragVideo.setAdapter(rvAdapter);
            rvAdapter.setOnItemClickListener(new VideoListRvAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(EnterVideoActivity.this, VideoEditActivity.class);
                    intent.putExtra(VideoEditActivity.VIDEO_PATH,videoList.get(position).getFilePath());
                    intent.putExtra(VideoEditActivity.INTENT_MAX_DURATION,maxDution);
                    startActivityForResult(intent,INTENT_ENTER_EDIT);
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_ENTER_EDIT && resultCode == VideoEditActivity.INTENT_EDIT_ENTER){
            String backPath = data.getStringExtra(VideoEditActivity.VIDEO_BACK_PATH);
            if (!TextUtils.isEmpty(backPath)){
                Intent intent = getIntent();
                intent.putExtra(VIDEO_DATA, backPath);
               // intent.putExtra(Video_THUMB,);
                setResult(INTENT_ENTER_EVENT, intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }


    @Override
    public void onBackPressed() {
        setResult(INTENT_DATA_NULL);
        finish();
    }

}