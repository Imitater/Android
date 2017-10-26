package com.ruiyihong.toyshop.activity;

import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.util.OkHttpUtil;

import java.io.File;
import java.io.IOException;

import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VitamioActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vitamio;
    }

    @Override
    protected void initView() {
        if (Vitamio.isInitialized(this)) {
            VideoView videoView = (VideoView) findViewById(R.id.vitamio);
            videoView.setVideoURI(Uri.parse("http://112.253.22.157/17/z/z/y/u/zzyuasjwufnqerzvyxgkuigrkcatxr/hc.yinyuetai.com/D046015255134077DDB3ACA0D7E68D45.flv"));
            MediaController controller = new MediaController(this);
            videoView.setMediaController(controller);
            videoView.start();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                //上传视频
                uploadVideo();
                break;
        }
    }

    private void uploadVideo() {
        //获取文件
        String path = Environment.getExternalStorageDirectory().getPath() + "/video/oppo.mp4";


        String url = "http://10.0.2.2:8080/toy/video";
        File file = new File(path);
        if(!file.exists()){

        }
        try {
            OkHttpUtil.upLoadFile(url, file,"oppo.mp4", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
    
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){

                    }else{

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
