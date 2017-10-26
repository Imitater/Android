package com.ruiyihong.toyshop.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fire.photoselector.activity.PhotoSelectorActivity;
import com.fire.photoselector.models.PhotoSelectorSetting;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.PinglunGvAdapter;
import com.ruiyihong.toyshop.bean.OrderBean;
import com.ruiyihong.toyshop.bean.mine.OrderDataBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.FullyLinearLayoutManager;
import com.ruiyihong.toyshop.view.RatingBar;
import com.ruiyihong.toyshop.view.uploadfile.ProgressHelper;
import com.ruiyihong.toyshop.view.uploadfile.ProgressUIListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * Created by hegeyang on 2017/7/27 0027 .
 */

public class PinglunActivity extends BaseActivity {

    private static final int MSG_ORDER_DATA = 251;
    private static final int MSG_PINGJIA_RESULT = 252;
    @InjectView(R.id.back_pinglun)
    ImageButton backPinglun;
    @InjectView(R.id.tv_pinglun_title)
    TextView tvPinglunTitle;
    @InjectView(R.id.tv_pinglun_submit)
    TextView tvPinglunSubmit;
    @InjectView(R.id.rv_order_pinglun)
    RecyclerView rvOrderPinglun;
    @InjectView(R.id.rt_pinglun_miaoshu1)
    RatingBar rtPinglunMiaoshu1;
    @InjectView(R.id.tv_pinglun_miaoshu1_text)
    TextView tvPinglunMiaoshu1Text;
    @InjectView(R.id.rt_pinglun_miaoshu2)
    RatingBar rtPinglunMiaoshu2;
    @InjectView(R.id.tv_pinglun_miaoshu2_text)
    TextView tvPinglunMiaoshu2Text;
    @InjectView(R.id.rt_pinglun_miaoshu3)
    RatingBar rtPinglunMiaoshu3;
    @InjectView(R.id.tv_pinglun_miaoshu3_text)
    TextView tvPinglunMiaoshu3Text;

    //private List<String> list = new ArrayList<String>();
    private String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong" + File.separator + "icon";
    private static final int CODE_CAMERA_REQUEST = 0xa1;

    private int choseImageFromCameraCapture_pisiton = -1;
    private static final int REQUEST_SELECT_PHOTO = 100;
    private int pingjia = 1;
    private String did;
    private AlertDialog chose_dialog;
    private int upload_count = 0;
    private android.app.AlertDialog upload_dialog;
    private Uri imageUri;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_DATA:
                    //解析订单
                    parseOrder((String) msg.obj);
                    break;
                case MSG_PINGJIA_RESULT:
                    int status = msg.arg1;
                    if (status == 0) {
                        ToastHelper.getInstance().displayToastShort("评价失败，请稍后再试");
                    }
                    if (status == 1) {
                        wCount++;
                        LogUtil.e("评价成功的商品数量=" + wCount);
                    }
                    if (mapPinjia != null && wCount == mapPinjia.size()) {
                        ToastHelper.getInstance().displayToastShort("评价成功！");
                        finish();
                    }
                    break;
            }
        }
    };
    private List<OrderDataBean.DataBean> dataList;
    private HashMap<Integer, GoodPingjiaBean> mapPinjia;
    private int wCount = 0;

    private void parseOrder(String result) {
        LogUtil.e("订单详情========" + result);
        OrderDataBean orderDataBean = GsonUtil.parseJsonWithGson(result, OrderDataBean.class);
        if (orderDataBean != null) {
            dataList = orderDataBean.getData();
            mapPinjia = new HashMap<>(); //用来存放每个商品评价信息的集合
            GoodPingjiaBean goodBean = null;
            if (dataList != null && dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    goodBean = new GoodPingjiaBean();
                    goodBean.iconList = new ArrayList<>();
                    goodBean.pinglunGvAdapter = new PinglunGvAdapter(PinglunActivity.this, goodBean.iconList);
                    mapPinjia.put(i, goodBean);
                }
                rvOrderPinglun.setLayoutManager(new FullyLinearLayoutManager(PinglunActivity.this));

                PinglunAdapter adapter = new PinglunAdapter();
                rvOrderPinglun.setAdapter(adapter);
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.acitivity_pinglun;
    }

    @Override
    protected void initView() {
        //订单号
        did = getIntent().getStringExtra("did");

        //根据订单号查询订单
        try {
            getOrder();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getOrder() throws IOException {
        String url = AppConstants.MY_ORDER_SEL;
        HashMap<String, String> map = new HashMap<>();
        String[] uids = SPUtil.getUid(this);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }
        map.put("uid", uid);
        map.put("xuhao", did);
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_ORDER_DATA;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void showIconDialog(final ArrayList<String> result, final int position) {
        View v = LayoutInflater.from(this).inflate(R.layout.select_icon_dialog_find, null);
        chose_dialog = new AlertDialog.
                Builder(this).create();
        RelativeLayout rl_select_photo = v.findViewById(R.id.rl_select_photo);
        RelativeLayout rl_select_video = v.findViewById(R.id.rl_select_video);
        RelativeLayout rl_shot = v.findViewById(R.id.rl_select_shot);

        TextView tv_pic_or_video = v.findViewById(R.id.tv_video_or_pic);
        TextView tv_pic = v.findViewById(R.id.tv_pic);

        tv_pic_or_video.setVisibility(View.GONE);
        tv_pic.setVisibility(View.GONE);
        rl_select_video.setVisibility(View.GONE);

        rl_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择图片
                selectPhotos(6, 3, result, position);
                chose_dialog.dismiss();
            }
        });
        rl_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄
                choseImageFromCameraCapture(position);
                chose_dialog.dismiss();
            }
        });
        chose_dialog.setView(v);
        chose_dialog.show();

    }

    private void choseImageFromCameraCapture(int posititon) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File image_file = new File(path, getImageName());
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(this, "com.ruiyihong.toyshop.privider", image_file);
            } else {
                imageUri = Uri.fromFile(image_file);
            }
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
            choseImageFromCameraCapture_pisiton = posititon;

        }


    }

    private String getImageName() {
        return System.currentTimeMillis() + ".png";
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

    private int getDataSize(ArrayList<String> result) {
        return result == null ? 0 : result.size();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        backPinglun.setOnClickListener(this);

        tvPinglunSubmit.setOnClickListener(this);

        rtPinglunMiaoshu1.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                switch ((int) ratingCount) {
                    case 1:
                        tvPinglunMiaoshu1Text.setText("非常差");
                        break;
                    case 2:
                        tvPinglunMiaoshu1Text.setText("差");
                        break;
                    case 3:
                        tvPinglunMiaoshu1Text.setText("一般");
                        break;
                    case 4:
                        tvPinglunMiaoshu1Text.setText("好");
                        break;
                    case 5:
                        tvPinglunMiaoshu1Text.setText("非常好");
                        break;
                }
            }
        });
        rtPinglunMiaoshu2.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                switch ((int) ratingCount) {
                    case 1:
                        tvPinglunMiaoshu2Text.setText("非常差");
                        break;
                    case 2:
                        tvPinglunMiaoshu2Text.setText("差");
                        break;
                    case 3:
                        tvPinglunMiaoshu2Text.setText("一般");
                        break;
                    case 4:
                        tvPinglunMiaoshu2Text.setText("好");
                        break;
                    case 5:
                        tvPinglunMiaoshu2Text.setText("非常好");
                        break;
                }
            }
        });
        rtPinglunMiaoshu3.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                switch ((int) ratingCount) {
                    case 1:
                        tvPinglunMiaoshu3Text.setText("非常差");
                        break;
                    case 2:
                        tvPinglunMiaoshu3Text.setText("差");
                        break;
                    case 3:
                        tvPinglunMiaoshu3Text.setText("一般");
                        break;
                    case 4:
                        tvPinglunMiaoshu3Text.setText("好");
                        break;
                    case 5:
                        tvPinglunMiaoshu3Text.setText("非常好");
                        break;
                }
            }
        });

    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.back_pinglun:
                showDialog();
                break;
            case R.id.tv_pinglun_submit:
                submit();
                break;
        }
    }

    private void submit() {
        LogUtil.e("save======评价商品0000");
        upload_count = 0;
        upload();
    }

    private void upload() {
        LogUtil.e("save======评价商品01");
        if (mapPinjia == null) {
            return;
        }

        //检查是否填写评论内容
        for (int i = 0; i < mapPinjia.size(); i++) {
            GoodPingjiaBean goodPingjiaBean = mapPinjia.get(i);
            if (TextUtils.isEmpty(goodPingjiaBean.content)) {
                ToastHelper.getInstance().displayToastShort("请填写评价内容！");
                return;
            }
        }
        //上传图片
        for (int i = 0; i < mapPinjia.size(); i++) {
            final int position = i;
            GoodPingjiaBean goodPingjiaBean = mapPinjia.get(i);
            final ArrayList<String> result = goodPingjiaBean.iconList;
            if (result != null && result.size() > 0) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            upLoadPics(result, position);
                        } catch (Exception e) {

                        }
                    }
                });
                thread.start();
                thread.interrupt();
            } else {
                //图片为空，只上传文本
                try {
                    submitOther("", position);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static final String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong"
            + File.separator + "icon";

    private void upLoadPics(ArrayList<String> result, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder dialog_builder = new android.app.AlertDialog.Builder(PinglunActivity.this, R.style.RandomDialog);
                View dialogview = View.inflate(PinglunActivity.this, R.layout.zhifubao_loading, null);
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
//            for (int i = 0; i < result.size(); i++) {
//                File file = new File(result.get(i));//上传的图片文件
//                if (!file.getParentFile().exists()) {
//                    file.mkdirs();
//                }
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                fileList.add(file);
//            }
            //上传图片
            final String url = AppConstants.MY_UPLOAD_PJ_PIC;

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(url);

            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);

            for (int i = 0; i < fileList.size(); i++) {
                LogUtil.e("要上传的图片========" + result.get(i));
                File file = fileList.get(i);
                bodyBuilder.addFormDataPart("img[]", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
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
//                    if (progressDialog!=null) {
//                        progressDialog.setProgress((int) (100 * percent));
//                        LogUtil.e("progerssDialog===going");
//                    }
                }

                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();
                    LogUtil.e("progerssDialog===finish");
                }
            });
            builder.post(requestBody);

            Call call = okHttpClient.newCall(builder.build());

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("huida", "=============上传失败==============" + e.getMessage());
                    e.printStackTrace();
                    upload_count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (upload_count == mapPinjia.size()) {
                                if (upload_dialog != null) {
                                    upload_dialog.dismiss();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String result = OkHttpUtil.getResult(response);

                    LogUtil.e(position + "===上传图片==评论=====" + result);
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String path = object.getString("data");
                            if (path != null && path.length() > 0) {
                                //上传图片完成，提交其他
                                submitOther(path, position);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    upload_count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (upload_count == mapPinjia.size()) {
                                if (upload_dialog != null) {
                                    upload_dialog.dismiss();
                                }
                            }
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交评价
     *
     * @param path
     * @param position
     */
    private void submitOther(String path, int position) throws IOException {
        String url = AppConstants.SERVE_URL + "index/Ping/addpl";

        String[] uids = SPUtil.getUid(PinglunActivity.this);
        String uid = "";
        if (uids != null) {
            uid = uids[0];
        }

        String wuliu = tvPinglunMiaoshu1Text.getText().toString().trim();
        String fuwu = tvPinglunMiaoshu2Text.getText().toString().trim();
        String miaoshu = tvPinglunMiaoshu3Text.getText().toString().trim();

        if (TextUtils.isEmpty(wuliu)) {
            wuliu = "非常差";
        }
        if (TextUtils.isEmpty(fuwu)) {
            fuwu = "非常差";
        }
        if (TextUtils.isEmpty(miaoshu)) {
            miaoshu = "非常差";
        }
        GoodPingjiaBean pingjiaBean = mapPinjia.get(position);
        boolean isNiming = pingjiaBean.isNiming;
        String isni = isNiming ? "0" : "1";
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("wid", pingjiaBean.id);
        map.put("pltext", pingjiaBean.content); //评论内容
        map.put("plimg", path); //图片
        map.put("plei", pingjiaBean.pLei);//评论类别（1差评，2中评，3差评）
        map.put("niming", isni);//todo 是否匿名
        map.put("pwuliu", getStartCount(wuliu));//物流评价（0一星，1，2，3，4）
        map.put("pfuwu", getStartCount(fuwu));//服务评价（0，1，2，3，4）
        map.put("pmiaoshu", getStartCount(miaoshu));//描述评价（0，1，2，3，4）
        map.put("did", did);//订单号


        LogUtil.e("uid==" + uid + "\n是否匿名===" + isni + "\n商品id====" + pingjiaBean.id + "\n评价内容====" + pingjiaBean.content + "\n图片路径====" + path + "\n评价===" + pingjiaBean.pLei + "\n物流====" + getStartCount(wuliu) +
                "\n服务==" + getStartCount(fuwu) + "\n描述===" + getStartCount(miaoshu));


        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("评价提交结果=============" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Message obtain = Message.obtain();
                    obtain.what = MSG_PINGJIA_RESULT;
                    obtain.arg1 = status;
                    handler.sendMessage(obtain);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * 获取好评星星数
     *
     * @param str
     * @return
     */
    private String getStartCount(String str) {
        String result = "";
        switch (str) {
            case "非常差":
                result = 0 + "";
                break;
            case "差":
                result = 1 + "";
                break;
            case "一般":
                result = 2 + "";
                break;
            case "好":
                result = 3 + "";
                break;
            case "非常好":
                result = 4 + "";
                break;
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认取消发布吗？");
        builder.setNegativeButton("确定取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton("继续发布", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectPhotos(int sum, int columnCount, ArrayList<String> result, int position) {
        PhotoSelectorSetting.MAX_PHOTO_SUM = sum;
        PhotoSelectorSetting.COLUMN_COUNT = columnCount;
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST, result);
        intent.putExtra(PhotoSelectorSetting.ITEM_POSITION, position);//需要携带item的位置
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra(PhotoSelectorSetting.ITEM_POSITION, -2);
                    LogUtil.e("========position======" + position);
                    // result为照片绝对路径集合,isSelectedFullImage标识是否选择原图
                    ArrayList<String> result = data.getStringArrayListExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST);
                    GoodPingjiaBean goodPingjiaBean = mapPinjia.get(position);
                    //goodPingjiaBean.iconList.addAll(result);
                    goodPingjiaBean.iconList = result;

                    boolean isSelectedFullImage = data.getBooleanExtra(PhotoSelectorSetting.SELECTED_FULL_IMAGE, false);
                    //压缩图片
                    for (int i = 0; i < goodPingjiaBean.iconList.size(); i++) {
                        //LogUtil.e("图片大小为==========压缩前"+new File(result.get(i)).length());
                        //压缩后图片保存的路径
                        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "ruiyihong"
                                + File.separator + "icon";
                        //原图片文件
                        File tempFile = new File(goodPingjiaBean.iconList.get(i));
                        String picFilePath = "";
                        if (tempFile.length() / 1024 > 500) {//图片大于300kb 压缩
                            //压缩图片
                            picFilePath = filePath + File.separator + System.currentTimeMillis() + ".png";
                            try {
                                compressAndGenImage(goodPingjiaBean.iconList.get(i), picFilePath, 500, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            picFilePath = goodPingjiaBean.iconList.get(i);
                        }
                        goodPingjiaBean.iconList.set(i, picFilePath);

                    }
                    goodPingjiaBean.pinglunGvAdapter.setList(goodPingjiaBean.iconList);

                }
                break;
            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    String path = imageUri.getPath();
                    File file = new File(path);
                    //cropRawPhoto(Uri.fromFile(tempFile));
                    GoodPingjiaBean goodPingjiaBean = mapPinjia.get(choseImageFromCameraCapture_pisiton);
                    goodPingjiaBean.iconList.add(file.getPath());
                    goodPingjiaBean.pinglunGvAdapter.setList(goodPingjiaBean.iconList);

                } else {
                    ToastHelper.getInstance().displayToastShort("没有SDCard");
                }

                break;

        }
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

    class PinglunAdapter extends RecyclerView.Adapter<PinglunAdapter.PinglunViewHolder> {
        @Override
        public PinglunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PinglunActivity.this).inflate(R.layout.item_my_order_pingjia, parent, false);
            return new PinglunViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PinglunViewHolder holder, final int position) {

            //商品图片
            OrderDataBean.DataBean dataBean = dataList.get(position);
            Picasso.with(PinglunActivity.this).load(AppConstants.IMG_BASE_URL + dataBean.getShopimg()).placeholder(R.mipmap.good_default).into(holder.iv_icom);

            holder.cb_niming.setChecked(true); //匿名
            holder.rg_pinglun.check(R.id.rb_pinglun_haoping); //默认好评

            //评论图片九宫格初始化
            final GoodPingjiaBean goodPingjiaBean = mapPinjia.get(position);
            holder.gv_icon.setAdapter(goodPingjiaBean.pinglunGvAdapter);
            goodPingjiaBean.pinglunGvAdapter.setList(goodPingjiaBean.iconList);

            //商品id
            goodPingjiaBean.id = dataBean.getId() + "";

            //选择图片点击监听
            holder.gv_icon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == getDataSize(goodPingjiaBean.iconList)) {//点击“+”号位置添加图片
                        showIconDialog(goodPingjiaBean.iconList, position);
                    } else {//点击图片删除
                        goodPingjiaBean.iconList.remove(i);
                        goodPingjiaBean.pinglunGvAdapter.setList(goodPingjiaBean.iconList);
                    }
                }
            });
            //评论（好评，中评，差评）选中监听
            holder.rg_pinglun.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    switch (i) {
                        case R.id.rb_pinglun_haoping:
                            goodPingjiaBean.pLei = 1 + "";
                            break;
                        case R.id.rb_pinglun_zhongping:
                            goodPingjiaBean.pLei = 2 + "";
                            break;
                        case R.id.rb_pinglun_chaping:
                            goodPingjiaBean.pLei = 3 + "";
                            break;

                    }
                }
            });
            //是否匿名监听
            holder.cb_niming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    goodPingjiaBean.isNiming = b;
                }
            });


            holder.et_content.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    goodPingjiaBean.content = holder.et_content.getText().toString().trim();
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class PinglunViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_icom;
            RadioGroup rg_pinglun;
            RadioButton rb_haoping;
            RadioButton rb_zhongping;
            RadioButton rb_chaping;
            EditText et_content;
            GridView gv_icon;
            CheckBox cb_niming;

            public PinglunViewHolder(View itemView) {
                super(itemView);
                iv_icom = itemView.findViewById(R.id.iv_pinglun_icon);
                rg_pinglun = itemView.findViewById(R.id.rg_pinglun);
                rb_haoping = itemView.findViewById(R.id.rb_pinglun_haoping);
                rb_zhongping = itemView.findViewById(R.id.rb_pinglun_zhongping);
                rb_chaping = itemView.findViewById(R.id.rb_pinglun_chaping);
                et_content = itemView.findViewById(R.id.et_pinpun_content);
                gv_icon = itemView.findViewById(R.id.gv_pinglun_icon_changchuan);
                cb_niming = itemView.findViewById(R.id.cb_pinglun_niming);
            }
        }
    }

    /**
     * 单个商品评价
     */
    class GoodPingjiaBean {
        String id; //商品id
        ArrayList<String> iconList; //图片集合
        String content; //评论内容
        String pLei = 1 + "";//评论类别  （1差评，2中评，3差评）
        boolean isNiming = true; //是否匿名

        PinglunGvAdapter pinglunGvAdapter;//九宫格适配器


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
