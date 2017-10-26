package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.find.HotTalkDetailBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.ProgressWebView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HotTalkingDetailActivity extends BaseActivity {
    private static final int PARSE_DATA = 0;
    @InjectView(R.id.iv_hot_talk_detail)
    ImageView ivHotTalkDetail;
    @InjectView(R.id.tv_hottalk_detail)
    TextView tvHottalkDetail;
    @InjectView(R.id.tv_hottalk_detail_title)
    TextView tvHottalkDetailTitle;
    @InjectView(R.id.ll_detail)
    LinearLayout llDetail;
    @InjectView(R.id.wv_hottalk_detail)
    ProgressWebView wvHottalkDetail;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.tv_collect)
    TextView tvCollect;

    private static final int NetWorkError = 4;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PARSE_DATA:
                    parseData((String) msg.obj);
                    break;
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;


            }
        }
    };
    private int id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hot_talking_detail;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        }

        id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            try {
                getDataFromNet();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromNet() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String[] uids = SPUtil.getUid(this);
        String uid = "";
        if (uids!=null){
            uid = uids[0];
        }
        map.put("id", id + "");
        map.put("uid",uid);
        OkHttpUtil.postString(AppConstants.FIND_HOTTALKING_DETAIL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("热门话题详情页====" + result);
                if (result != null) {
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = PARSE_DATA;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void parseData(String json) {
        HotTalkDetailBean hotTalkDetailBean = GsonUtil.parseJsonWithGson(json, HotTalkDetailBean.class);
        //回显是否收藏
        List<HotTalkDetailBean.ScBean> sc = hotTalkDetailBean.getSc();
        for (int i = 0; i < sc.size(); i++) {
            HotTalkDetailBean.ScBean scBean = sc.get(i);
            String sign = scBean.getSign();
            if ("1".equals(sign)){
                //是热门话题的收藏
                String wid = scBean.getWid();
                if (wid.equals(id+"")){
                    //是自己收藏的
                    tvCollect.setSelected(true);
                    break;
                }
            }

        }
        List<HotTalkDetailBean.DataBean> dataList = hotTalkDetailBean.getData();
        if (dataList != null && dataList.size() > 0) {
            HotTalkDetailBean.DataBean dataBean = dataList.get(0);
            String title = dataBean.getTitle();
            String img = dataBean.getImg();
            String content = dataBean.getContent();
            //图片
            Picasso.with(this).load(AppConstants.FIND_HOT_TALKING_IAMGE_BASE + img).placeholder(R.mipmap.lunbo_default).into(ivHotTalkDetail);
            //图片上的标题
            tvHottalkDetail.setText(title);
            //文本标题
            tvHottalkDetailTitle.setText(title);
            //正文内容
            wvHottalkDetail.getSettings().setJavaScriptEnabled(true);

            wvHottalkDetail.loadData(getHtmlData(content), "text/html; charset=UTF-8", null);
        }
    }

    @Override
    protected void initEvent() {
        loadingView.setOnRefreshPagerListener(new CommonLoadingView.OnRefreshPageListener() {
            @Override
            public void Refresh() {
                initData();
            }
        });
        llDetail.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.ll_detail:
                //收藏
                try {
                    postCollect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 收藏
     */
    private void postCollect() throws IOException {
        String[] uid = SPUtil.getUid(this);
        if (uid == null) {
            ToastHelper.getInstance().displayToastShort("请登录后操作");
            return;
        }
        final HashMap<String, String> map = new HashMap<>();
        map.put("wid", id + "");//说说id
        map.put("uid", uid[0]);//用户id
        map.put("sign", "1");//标记 1 是热门话题收藏

        OkHttpUtil.postString(AppConstants.COLLECT_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("热门话题收藏===" + result);
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        final String msg = object.getString("msg");
                        if (!TextUtils.isEmpty(msg) && msg.contains("成功")) {
                            HotTalkingDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (msg.contains("取消")) {
                                        //取消点赞
                                        tvCollect.setSelected(false);

                                    } else {
                                        //点赞
                                        tvCollect.setSelected(true);
                                    }
                                }
                            });
                        }
                        if (!TextUtils.isEmpty(msg) && msg.contains("失败")) {
                            ToastHelper.getInstance().displayToastShort("操作失败，请稍后再试");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
