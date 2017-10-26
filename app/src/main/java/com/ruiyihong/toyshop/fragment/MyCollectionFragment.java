package com.ruiyihong.toyshop.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.HotTalkingDetailActivity;
import com.ruiyihong.toyshop.activity.PublishActivity;
import com.ruiyihong.toyshop.activity.VideoActivity;
import com.ruiyihong.toyshop.adapter.FindPicGvAdapter;
import com.ruiyihong.toyshop.bean.mine.MyCollectionTopicBean;
import com.ruiyihong.toyshop.bean.mine.MyShoucangLesstonBean;
import com.ruiyihong.toyshop.bean.mine.MyShoucangTuijianBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.videoshootActivity.ShootMainActivity;
import com.ruiyihong.toyshop.view.CircleImageView;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.find.ImageDialog;
import com.ruiyihong.toyshop.view.find.VideoDialog;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridlayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/8/22.
 * 我的收藏
 */

public class MyCollectionFragment extends BaseFragment {

    private static final String BUFFER_MY_COLLECTION_TOPCI = "buffer_my_collection_topci";
    private static final String BUFFER_MY_COLLECTION_TUJIAN = "buffer_my_collection_tujian";
    private static final String BUFFER_MY_COLLECTION_LESSON = "buffer_my_collection_lesson";
    private static final int HOT_TALKING = 10;
    private static final int LESSON = 11;
    @InjectView(R.id.rv_mycollection)
    RecyclerView rvMycollection;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.tv_no_djq)
    TextView tvNoCollection;

    private static final int NetWorkError = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;

    public static final int TYPE_LESSON = 0;
    public static final int TYPE_TUIJIAN = 1;
    public static final int TYPE_HUATI = 2;


    private List<MyShoucangTuijianBean.DataBean> dataList_tuijian;
    private List<MyShoucangLesstonBean.DataBean> lessonList;
    private List<MyCollectionTopicBean.DataBean> topicList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            switch (msg.what) {
                case NetWorkError:    //显示网络错误页面
                    if (loadingView != null)
                        loadingView.loadError();
                    break;
                case CloseLoadingView: //关闭Loading动画
                    if (loadingView != null)
                        loadingView.loadSuccess(false);
                    break;
                case PageLoading:  //页面加载中动画
                    if (loadingView != null)
                        loadingView.load();
                    break;
                case TYPE_HUATI:
                    //缓存数据
                    SPUtil.setString(mActivity, BUFFER_MY_COLLECTION_TOPCI, result);
                    parseHuatiData(result);
                    break;
                case TYPE_TUIJIAN:
                    SPUtil.setString(mActivity, BUFFER_MY_COLLECTION_TUJIAN, result);
                    parseTujianData(result);
                    break;
                case TYPE_LESSON:
                    SPUtil.setString(mActivity, BUFFER_MY_COLLECTION_LESSON, result);
                    parseLesstonData(result);
                    break;
            }
        }
    };
    private AlertDialog chose_dialog;
    private TopicAdapter topicAdapter;
    private LesssonAdapter lessonAdapter;
    private ShoucangAdapter shoucangAdapter;

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_mycollection, null);
        return view;
    }

    @Override
    protected void initData() {
        int type = (int) getArguments().get("type");
        if (TYPE_HUATI == type) {
            //热门话题收藏
            initHuati();
        } else if (TYPE_TUIJIAN == type) {
            //热门推荐收藏
            initTuijian();
        } else if (TYPE_LESSON == type) {
            //课程收藏
            initLesson();
        }
    }

    private void initHuati() {
        //获取缓存数据
        String buffer = SPUtil.getString(mActivity, BUFFER_MY_COLLECTION_TOPCI, "");
        if (!TextUtils.isEmpty(buffer)) {
            parseHuatiData(buffer);
        }
        String url = AppConstants.SERVE_URL + "index/collect/htsc";
        try {
            getDataFromNet(url, TYPE_HUATI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTuijian() {
        //获取缓存数据
        String buffer = SPUtil.getString(mActivity, BUFFER_MY_COLLECTION_TUJIAN, "");
        if (!TextUtils.isEmpty(buffer)) {
            parseTujianData(buffer);
        }
        try {
            String url = AppConstants.SERVE_URL + "index/collect/rmsc";
            getDataFromNet(url, TYPE_TUIJIAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLesson() {
        //获取缓存数据
        String buffer = SPUtil.getString(mActivity, BUFFER_MY_COLLECTION_LESSON, "");
        if (!TextUtils.isEmpty(buffer)) {
            parseLesstonData(buffer);
        }
        try {
            String url = AppConstants.SERVE_URL + "index/collect/kcsc";
            getDataFromNet(url, TYPE_LESSON);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromNet(String url, final int type) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String[] uid = SPUtil.getUid(mActivity);
        if (uid != null) {
            map.put("uid", uid[0]);
        }
        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e(type + "===我的收藏======" + result);
                if (result != null) {
                    Message msg = Message.obtain();
                    msg.obj = result;
                    switch (type) {
                        case TYPE_HUATI:
                            msg.what = TYPE_HUATI;
                            break;
                        case TYPE_TUIJIAN:
                            msg.what = TYPE_TUIJIAN;
                            break;
                        case TYPE_LESSON:
                            msg.what = TYPE_LESSON;
                            break;
                    }

                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void parseHuatiData(String result) {
        CloseLoadingView();
        MyCollectionTopicBean myCollectionTopicBean = GsonUtil.parseJsonWithGson(result, MyCollectionTopicBean.class);
        if (myCollectionTopicBean != null) {
            topicList = myCollectionTopicBean.getData();
            if (topicList!=null && topicList.size()>0) {
                rvMycollection.setVisibility(View.VISIBLE);
                tvNoCollection.setVisibility(View.GONE);
                rvMycollection.setLayoutManager(new LinearLayoutManager(mActivity));
                topicAdapter = new TopicAdapter();
                rvMycollection.setAdapter(topicAdapter);
            }else{
                rvMycollection.setVisibility(View.GONE);
                tvNoCollection.setVisibility(View.VISIBLE);
            }
        }else{
            rvMycollection.setVisibility(View.GONE);
            tvNoCollection.setVisibility(View.VISIBLE);
        }
    }

    private void parseLesstonData(String result) {
        Log.e("radish", "parseLesstonData: " + result);
        CloseLoadingView();
        MyShoucangLesstonBean myShoucangLessonBean = GsonUtil.parseJsonWithGson(result, MyShoucangLesstonBean.class);
        if (myShoucangLessonBean != null) {
            lessonList = myShoucangLessonBean.getData();
            if (lessonList!=null && lessonList.size()>0) {
                rvMycollection.setVisibility(View.VISIBLE);
                tvNoCollection.setVisibility(View.GONE);

                rvMycollection.setLayoutManager(new GridLayoutManager(mActivity, 2));
                lessonAdapter = new LesssonAdapter();
                rvMycollection.setAdapter(lessonAdapter);
            }else{
                rvMycollection.setVisibility(View.GONE);
                tvNoCollection.setVisibility(View.VISIBLE);
            }
        }else{
            rvMycollection.setVisibility(View.GONE);
            tvNoCollection.setVisibility(View.VISIBLE);
        }
    }

    private void parseTujianData(String result) {
        CloseLoadingView();
        MyShoucangTuijianBean myShoucangTuijianBean = GsonUtil.parseJsonWithGson(result, MyShoucangTuijianBean.class);

        if (myShoucangTuijianBean != null) {
            dataList_tuijian = myShoucangTuijianBean.getData();
            if (dataList_tuijian!=null && dataList_tuijian.size()>0) {
                rvMycollection.setVisibility(View.VISIBLE);
                tvNoCollection.setVisibility(View.GONE);
                rvMycollection.setLayoutManager(new LinearLayoutManager(mActivity));
                shoucangAdapter = new ShoucangAdapter();
                rvMycollection.setAdapter(shoucangAdapter);
            }else{
                rvMycollection.setVisibility(View.GONE);
                tvNoCollection.setVisibility(View.VISIBLE);
            }
        }else{
            rvMycollection.setVisibility(View.GONE);
            tvNoCollection.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_my_shoucang_top, null);

            return new TopicViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, final int position) {
            holder.tv_title.setText(topicList.get(position).getTitle());
            //holder.tv_content.setText(topicList.get(position).getContent());
            Picasso.with(mActivity).load(AppConstants.FIND_HOT_TALKING_IAMGE_BASE + topicList.get(position).getImg()).placeholder(R.mipmap.good_default).into(holder.iv_image);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转详情页
                    Intent intent = new Intent(mActivity, HotTalkingDetailActivity.class);
                    intent.putExtra("id", topicList.get(position).getId());
                    startActivityForResult(intent,HOT_TALKING);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //取消收藏对话框
                    Log.e("radish", "onLongClick: "+topicList.get(position).getSign() );
                    showIconDialog(topicList.get(position).getId(),1,position);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return topicList.size();
        }

        class TopicViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_image;
            TextView tv_content;
            TextView tv_title;

            public TopicViewHolder(View itemView) {
                super(itemView);
                iv_image = itemView.findViewById(R.id.iv_shoucang_image);
                tv_content = itemView.findViewById(R.id.tv_shoucang_content);
                tv_title = itemView.findViewById(R.id.tv_shoucang_title);
            }
        }
    }
    private void showIconDialog(final int id , final int flag, final int position) {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.select_icon_dialog_collection, null);
        chose_dialog = new AlertDialog.
                Builder(mActivity).create();
        TextView tv_select_collection = v.findViewById(R.id.tv_select_collection);
        tv_select_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择图片
                //收藏
                try {
                    postCollect(id,flag,position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chose_dialog.dismiss();
            }
        });

        chose_dialog.setView(v);
        chose_dialog.show();
    }

    /**
     * 收藏
     */
    private void postCollect(int id, final int flag, final int position) throws IOException {
        String[] uid = SPUtil.getUid(mActivity);
        if (uid == null) {
            ToastHelper.getInstance().displayToastShort("请登录后操作");
            return;
        }
        final HashMap<String, String> map = new HashMap<>();
        map.put("wid", id + "");//说说id
        map.put("uid", uid[0]);//用户id
        map.put("sign", ""+flag);//标记 1 是热门话题收藏

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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //更新界面
                                    switch (flag){
                                        case 0:
                                            dataList_tuijian.remove(position);
                                            shoucangAdapter.notifyDataSetChanged();
                                            break;
                                        case 1:
                                            topicList.remove(position);
                                            topicAdapter.notifyDataSetChanged();
                                            break;
                                        case 2:
                                            lessonList.remove(position);
                                            lessonAdapter.notifyDataSetChanged();
                                            break;

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

    class LesssonAdapter extends RecyclerView.Adapter<LesssonAdapter.LessonViewHolder> {
        @Override
        public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_video, null);
            return new LessonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LessonViewHolder holder, final int position) {
            final MyShoucangLesstonBean.DataBean dataBean = lessonList.get(position);
            //缩略图
            String image_url = AppConstants.IMG_BASE_URL + dataBean.getBgimg();
            Picasso.with(mActivity).load(image_url).placeholder(R.mipmap.lunbo_default).error(R.mipmap.lunbo_default).into(holder.iv_itme);
            //底部文字说明
            holder.tv_bottom.setText(dataBean.getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.e("课程收藏 type=====" + dataBean.getKcclass());
                    //跳转到课程详情页面
                    Intent intent = new Intent(mActivity, VideoActivity.class);
                    intent.putExtra("id", dataBean.getWid());
                    Log.e("radish", "onClick: " + dataBean.getKcclass());
                    intent.putExtra("type", Integer.parseInt(dataBean.getKcclass()));
                    startActivityForResult(intent,LESSON);
                }
            });


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //取消收藏对话框
                    Log.e("radish", "onLongClick: "+dataBean.getSign() );
                    showIconDialog(dataBean.getWid(),2,position);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return lessonList.size();
        }

        class LessonViewHolder extends RecyclerView.ViewHolder {
            TextView tv_bottom;
            ImageView iv_itme;

            public LessonViewHolder(View itemView) {
                super(itemView);
                tv_bottom = itemView.findViewById(R.id.tv_class_bg_buttom);
                iv_itme = itemView.findViewById(R.id.iv_class_bg_item);

            }
        }
    }

    class ShoucangAdapter extends RecyclerView.Adapter<ShoucangAdapter.ShoucangViewholder> {
        @Override
        public ShoucangViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_my_shoucang, null);
            return new ShoucangViewholder(view);
        }

        @Override
        public void onBindViewHolder(ShoucangViewholder holder, final int position) {
            final MyShoucangTuijianBean.DataBean dataBean = dataList_tuijian.get(position);
            holder.tv_name.setText(dataBean.getYhniche());// 用户名
            //用户头像
            if (!TextUtils.isEmpty(dataBean.getYhimg())) {
                // LogUtil.e("用户头像"+AppConstants.FIND_IMAGE_BASE_URL+listBean.getYhimg());
                Picasso.with(mActivity).load(AppConstants.FIND_HOT_TALKING_IAMGE_BASE + dataBean.getYhimg()).placeholder(R.mipmap.personinfo_head_icon).error(R.mipmap.personinfo_head_icon).into(holder.tv_icon);
            }
            String content = dataBean.getContent();
            if (TextUtils.isEmpty(content)) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(content);
            }
            holder.tv_time.setText(dataBean.getTime());
            //是否官方
            if ("1".equals(dataBean.getIsadmin())) {
                holder.tv_isguanfang.setVisibility(View.VISIBLE);//官方
            } else {
                holder.tv_isguanfang.setVisibility(View.INVISIBLE);  //非官方
            }

            if (TextUtils.isEmpty(dataBean.getPic())) {
                //没有图片或视频
                holder.nineGridlayout.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
            } else {
                //有图片或视频
                String ispic = dataBean.getIspic();
                if ("0".equals(ispic)) {
                    holder.nineGridlayout.setVisibility(View.VISIBLE);
                    holder.rl_video.setVisibility(View.GONE);
                    //是图片
                    String pic = dataBean.getPic();
                    if (pic.endsWith(";")) {
                        pic = pic.substring(0, pic.length() - 1);
                    }
                    String[] split = pic.split(";");
                    List<String> imageList = Arrays.asList(split);
                    FindPicGvAdapter adapter = new FindPicGvAdapter(mActivity, imageList);
                    holder.nineGridlayout.setAdapter(adapter);
                    holder.nineGridlayout.setDefaultWidth(ScreenUtil.dp2px(mActivity, 300));
                    holder.nineGridlayout.setDefaultHeight(ScreenUtil.dp2px(mActivity, 200));
                    holder.nineGridlayout.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO: 2017/8/22  图片点击处理
                            showImage(position, dataBean);
                        }
                    });

                } else if ("1".equals(ispic)) {
                    //是视频
                    holder.nineGridlayout.setVisibility(View.GONE);
                    holder.rl_video.setVisibility(View.VISIBLE);
                    //获取视频缩略图
                    String picPath = dataBean.getPic();
                    if (picPath.endsWith(";")) {
                        picPath = picPath.substring(0, picPath.length() - 1);
                    }
                    String[] split = picPath.split(";");
                    final String s1 = split[0];
                    String s2 = "";
                    if (split.length > 1) {
                        s2 = split[1];
                    }
                    if (s1.endsWith(".PNG") || s1.endsWith(".png") || s1.endsWith(".JPG") || s1.endsWith(".jpg")) {
                        //s1是图片，s2是视频
                        Picasso.with(mActivity).load(AppConstants.FIND_IMAGE_BASE_URL + s1).fit().into(holder.vv_item);
                        // videoPath = s2;
                        final String finalS = s2;
                        holder.rl_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playVideo(finalS, s1);
                            }
                        });

                    } else {
                        //s2是图片，s1是视频
                        Picasso.with(mActivity).load(AppConstants.FIND_IMAGE_BASE_URL + s2).fit().into(holder.vv_item);
                        // videoPath = s1;
                        final String finalS1 = s2;
                        holder.rl_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playVideo(s1, finalS1);
                            }
                        });
                    }
                }
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //取消收藏对话框
                    Log.e("radish", "onLongClick: "+dataBean.getSign() );
                    showIconDialog(dataBean.getId(),0,position);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList_tuijian.size();
        }

        class ShoucangViewholder extends RecyclerView.ViewHolder {
            CircleImageView tv_icon;
            TextView tv_name;
            TextView tv_time;
            TextView tv_isguanfang;
            TextView tv_content;
            NineGridlayout nineGridlayout;
            ImageView vv_item;
            ImageView iv_play;
            RelativeLayout rl_video;

            public ShoucangViewholder(View itemView) {
                super(itemView);
                tv_icon = itemView.findViewById(R.id.tv_find_hottuijian_item_icon);
                tv_name = itemView.findViewById(R.id.tv_find_hottuijian_item_name);
                tv_time = itemView.findViewById(R.id.tv_find_hottuijian_item_time);
                tv_isguanfang = itemView.findViewById(R.id.find_item_isguanfang);
                tv_content = itemView.findViewById(R.id.tv_item_content);
                nineGridlayout = itemView.findViewById(R.id.gv_find_hottuijian_item);
                vv_item = itemView.findViewById(R.id.video_find_hottuijian_item);
                iv_play = itemView.findViewById(R.id.iv_find_play);
                rl_video = itemView.findViewById(R.id.rl_video);

            }
        }
    }

    /**
     * 放大显示图片
     */
    private void showImage(int position, MyShoucangTuijianBean.DataBean listBean) {
        final ImageDialog imageDialog = ImageDialog.getDialog(mActivity);
        String pic = listBean.getPic();
        if (pic.endsWith(";")) {
            pic = pic.substring(0, pic.length() - 1);
        }
        String[] split = pic.split(";");

        imageDialog.setImageData(split, position);

        imageDialog.setOnDialogClickListener(new ImageDialog.DialogItemClickListener() {
            @Override
            public void itemClick(int position) {
                imageDialog.dismiss();
            }
        });

        imageDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageDialog.show();


    }

    /**
     * 播放视频
     *
     * @param picPath
     * @param thumbImage_path
     */
    private void playVideo(String picPath, String thumbImage_path) {
        if (picPath.endsWith(";")) {
            picPath = picPath.substring(0, picPath.length() - 1);
        }
        Intent intent = new Intent(mActivity, VideoDialog.class);
        intent.putExtra("video_path", AppConstants.FIND_IMAGE_BASE_URL + picPath);
        intent.putExtra("thumbImage_path", AppConstants.FIND_IMAGE_BASE_URL + thumbImage_path);//缩略图路径
        intent.putExtra("useCache", true);
        startActivity(intent);
    }

    //关闭LoadingView
    private void CloseLoadingView() {
        handler.sendEmptyMessage(CloseLoadingView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HOT_TALKING){
            //热门话题
            initHuati();
        }
        if (requestCode == LESSON){
            //课程
            initLesson();
        }
    }
}
