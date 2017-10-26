package com.ruiyihong.toyshop.adapter;

/**
 * Created by 李晓曼 on 2017/8/10.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.AudioActivity;
import com.ruiyihong.toyshop.activity.DetailActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.activity.ToyShopActivity;
import com.ruiyihong.toyshop.activity.VideoActivity;
import com.ruiyihong.toyshop.bean.ClassBean;

import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.NetUtli;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/7/17 0017 .
 * 这个类我改过了
 */

public class LessionPagerTest {
    private static final int MSG_AUDIO = 0;
    private static final int MSG_VIDEO = 1;
    private static final int MSG_REFRESH = 2;
    private static final int MSG_LOAD_MORE = 3;
    private static final int MSG_FAILURE = 4;
    private static final String BUFFER_CLASS_MAIN_VIDEO = "buffer_class_main_video";
    private static final String BUFFER_CLASS_MAIN_AUDIO = "buffer_class_main_audio";
    private static final String BUFFER_CLASS_MAIN_TOY = "buffer_class_main_toy";
    private static final String BUFFER_CLASS_MAIN_BCAC = "buffer_class_main_bcac";
    private Activity activity;
    private int position;
    public View rootView;
    private FrameLayout fl_lession_pager;
    private GridView gv;
    private AudioAdapter audioAdapter;
    private VideoAdapter videoAdapter;

    private List<ClassBean> audioList;
    private List<ClassBean> bcacList;
    private List<ClassBean> toyList;
    private List<ClassBean> videoList;
    private List<ClassBean> list;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            String obj= (String)msg.obj;
            switch (msg.what){
                case MSG_AUDIO:
                    audioList = (List<ClassBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = audioList;
                    getAudio();
                    break;
                case MSG_VIDEO:
                    videoList = (List<ClassBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = videoList;
                    getVideo();
                    break;
                case MSG_REFRESH:
                    refreshMore((String)msg.obj);
                    break;
                case MSG_LOAD_MORE:
                    loadMore((String)msg.obj);
                    break;
                case MSG_FAILURE:
                    //访问网络失败
                    ToastHelper.getInstance().displayToastShort("访问网络失败");
                    int type = (int) msg.obj;
                    if (type == MSG_LOAD_MORE || type == MSG_REFRESH){
                        mSmartRefreshLayout.finishRefresh(0);
                    }
                    break;
            }
        }
    };
    private SmartRefreshLayout mSmartRefreshLayout;

    public LessionPagerTest(Activity activity,int position) {
        this.activity = activity;
        this.position = position;
        rootView = initView();
    }
    private int page = 1;
    private View initView() {
        View inflate = View.inflate(activity, R.layout.lession_pager, null);
        fl_lession_pager = inflate.findViewById(R.id.fl_lession_pager);
        mSmartRefreshLayout = inflate.findViewById(R.id.smartRefreshLayout);
        mSmartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (NetWorkUtil.isNetWorkAvailable(activity)){
                    //上拉加载
                    Map<String,Object> map = new HashMap();
                    map.put("kcclass",position);
                    map.put("page",page++);
                    netPost(AppConstants.CLASS_LOAD_MORE,map,MSG_LOAD_MORE);

                }else{
                    mSmartRefreshLayout.finishLoadmore(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (NetWorkUtil.isNetWorkAvailable(activity)){
                    //下拉刷新
                    Map<String,Object> map = new HashMap();
                    map.put("kcclass",position);
                    netPost(AppConstants.CLASS_MAIN,map,MSG_REFRESH);

                }else{
                    mSmartRefreshLayout.finishRefresh(0);
                    ToastHelper.getInstance().displayToastShort("请检查网络");
                }
            }
        });
        return  inflate;
    }
    public void initData(){
        page = 1;
        gv = new GridView(activity);
        gv.setNumColumns(2);
        initBuffer();
        if (NetUtli.isNetworkAvailable(activity)){
            //网络可用
            String class_url = AppConstants.CLASS_MAIN;
            Map<String, Object> map = new HashMap<>();
            map.put("kcclass",position);
            netPost(class_url,map,position);
        }else{
            //网络不可用
            ToastHelper.getInstance().displayToastShort("请检查网络");
        }
    }

    private void initBuffer() {
        String buffer = "";
        switch (position){
            case 0:
                buffer = SPUtil.getString(activity, BUFFER_CLASS_MAIN_AUDIO, "");
                if (!TextUtils.isEmpty(buffer)) {
                    audioList = (List<ClassBean>) GsonUtil.parseJsonToList(buffer, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = audioList;
                    getAudio();
                }
                break;
            case 1:
                buffer = SPUtil.getString(activity, BUFFER_CLASS_MAIN_BCAC, "");
                if (!TextUtils.isEmpty(buffer)) {
                    bcacList = (List<ClassBean>) GsonUtil.parseJsonToList(buffer, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = bcacList;

                    // TODO BCAC
                    //		getBcac();
                }
                break;
            case 2:
                buffer = SPUtil.getString(activity, BUFFER_CLASS_MAIN_TOY, "");
                if (!TextUtils.isEmpty(buffer)) {
                    toyList = (List<ClassBean>) GsonUtil.parseJsonToList(buffer, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = toyList;
                    // TODO TOY
                    //		getToy();
                }
                break;
            case 3:
                buffer = SPUtil.getString(activity, BUFFER_CLASS_MAIN_VIDEO, "");
                if (!TextUtils.isEmpty(buffer)) {
                    videoList = (List<ClassBean>) GsonUtil.parseJsonToList(buffer, new TypeToken<List<ClassBean>>() {
                    }.getType());
                    list = videoList;
                    getVideo();
                }
                break;
        }
    }


    private void refreshMore(String obj) {
        List<ClassBean> refreshList = (List<ClassBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ClassBean>>() {
        }.getType());
        if(refreshList!=null && refreshList.size()>0) {
            if (position == 0 && audioList != null && audioList.size()>0) {
                if (refreshList.size() == audioList.size()){
                    ToastHelper.getInstance().displayToastShort("暂无更多数据");
                }else{
                    Log.i("radish","list.size------------------"+list.size() );
                    int refreshCount = refreshList.size() - audioList.size();
                    Log.i("radish","refreshCount------------------"+refreshCount );
                    audioList = refreshList;
                    SPUtil.setString(activity,BUFFER_CLASS_MAIN_AUDIO,obj);
                    for (int i = 0; i < refreshCount; i++) {
                        list.add(i,audioList.get(i));
                    }
                    Log.i("radish","list.size------------------"+list.size() );
                    audioAdapter.notifyDataSetChanged();
                }
            } else if(position == 1 && videoList != null && videoList.size()>0){
                if (refreshList.size() == videoList.size()){
                    ToastHelper.getInstance().displayToastShort("暂无更多数据");
                }else{
                    Log.i("radish","list.size------------------"+list.size() );
                    int refreshCount = refreshList.size() - videoList.size();
                    Log.i("radish","refreshCount------------------"+refreshCount );
                    videoList = refreshList;
                    SPUtil.setString(activity,BUFFER_CLASS_MAIN_VIDEO,obj);
                    for (int i = 0; i < refreshCount; i++) {
                        list.add(i,videoList.get(i));
                    }
                    Log.i("radish","list.size------------------"+list.size() );
                    videoAdapter.notifyDataSetChanged();
                }
            }
        }
        mSmartRefreshLayout.finishRefresh(0);
    }

    private void loadMore(String obj) {
        List<ClassBean> loadMoreList = (List<ClassBean>) GsonUtil.parseJsonToList(obj, new TypeToken<List<ClassBean>>() {
        }.getType());
        if (loadMoreList!=null && loadMoreList.size()>0) {
            if (position == 0) {
                list.addAll(loadMoreList);
                audioAdapter.notifyDataSetChanged();
            } else {
                list.addAll(loadMoreList);
                videoAdapter.notifyDataSetChanged();
            }
        }else{
            ToastHelper.getInstance().displayToastShort("暂无更多数据");
        }
        mSmartRefreshLayout.finishLoadmore(0);
    }

    private void netPost(String class_url, Map<String, Object> map, final int type) {
        try {
            OkHttpUtil.postJson(class_url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg = Message.obtain();
                    msg.what = MSG_FAILURE;
                    msg.obj = type;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.isSuccessful()) {
                        String string = response.body().string();
                        //缓存数据
                        if(!TextUtils.isEmpty(string)){
                            Log.i("radish","string------------------"+string );
                            Message msg = Message.obtain();
                            switch(type){
                                case MSG_AUDIO:
                                    SPUtil.setString(activity,BUFFER_CLASS_MAIN_AUDIO,string);
                                    break;
                                case MSG_VIDEO:
                                    SPUtil.setString(activity,BUFFER_CLASS_MAIN_VIDEO,string);
                                    break;
                            }
                            msg.what = type;
                            msg.obj = string;

                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getVideo() {
        if(videoAdapter==null){
            videoAdapter = new VideoAdapter();
        }
        gv.setAdapter(videoAdapter);
        fl_lession_pager.removeAllViews();
        fl_lession_pager.addView(gv);

        jumpActivity(VideoActivity.class,1);
    }

    //17190175362
    private void jumpActivity(final Class activityClass, final int type) {
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = SPUtil.getString(activity, AppConstants.SP_LOGIN, "");
                Intent intent=null;
                if (TextUtils.isEmpty(username)){
                    intent= new Intent(activity, LoginActivity.class);
                }else{
                    intent = new Intent(activity, activityClass);
                    //携带数据
                    if (type == 0){
                        //audio
                        intent.putExtra("id",audioList.get(i).getId());
                    }else{
                        //video
                        intent.putExtra("id",videoList.get(i).getId());
                    }
                }
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });

    }

    private void getAudio() {
        if(audioAdapter==null){
            audioAdapter = new AudioAdapter();
        }

        gv.setAdapter(audioAdapter);
        fl_lession_pager.removeAllViews();
        fl_lession_pager.addView(gv);
        jumpActivity(AudioActivity.class,0);
    }

/*	public void getBcac() {
		if(bcacAdapter==null){
			bcacAdapter = new BcacAdapter();
		}

		gv.setAdapter(audioAdapter);
		fl_lession_pager.removeAllViews();
		fl_lession_pager.addView(gv);
		jumpActivity(BcacActivity.class,1);

	}*/

    class AudioAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(view ==null){
                holder = new ViewHolder();
                view = LayoutInflater.from(activity).inflate(R.layout.item_audio, null);
                holder.iv_bg = view.findViewById(R.id.iv_class_bg_item);
                holder.tv_bottom = view.findViewById(R.id.tv_class_bg_buttom);
                holder.tv_xinpin = view.findViewById(R.id.tv_class_bg_head_blue);
                holder.tv_hot = view.findViewById(R.id.tv_class_bg_head_red);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            ClassBean classBean =  list.get(i);
            Picasso.with(activity).load(AppConstants.IMG_BASE_URL+classBean.getBgimg()).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_bg);
            holder.tv_bottom.setText(classBean.getTitle());
            if (!TextUtils.isEmpty(classBean.getTip())){
                if ("新品".equals(classBean.getTip())){
                    holder.tv_xinpin.setVisibility(View.VISIBLE);
                    holder.tv_hot.setVisibility(View.GONE);
                }else if("热门".equals(classBean.getTip())){
                    holder.tv_hot.setVisibility(View.VISIBLE);
                    holder.tv_xinpin.setVisibility(View.GONE);
                }
            }
            return view;
        }
    }
    class VideoAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(view ==null){
                holder = new ViewHolder();
                view = LayoutInflater.from(activity).inflate(R.layout.item_video, null);
                holder.iv_bg = view.findViewById(R.id.iv_class_bg_item);
                holder.tv_bottom = view.findViewById(R.id.tv_class_bg_buttom);
                holder.tv_xinpin = view.findViewById(R.id.tv_class_bg_head_blue);
                holder.tv_hot = view.findViewById(R.id.tv_class_bg_head_red);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            ClassBean classBean =  list.get(i);
            Picasso.with(activity).load(AppConstants.IMG_BASE_URL+classBean.getBgimg()).fit().placeholder(R.mipmap.good_default).error(R.mipmap.good_default).into(holder.iv_bg);
            holder.tv_bottom.setText(classBean.getTitle());
            if (!TextUtils.isEmpty(classBean.getTip())){
                if ("新品".equals(classBean.getTip())){
                    holder.tv_xinpin.setVisibility(View.VISIBLE);
                    holder.tv_hot.setVisibility(View.GONE);
                }else if("热门".equals(classBean.getTip())){
                    holder.tv_xinpin.setVisibility(View.GONE);
                    holder.tv_hot.setVisibility(View.VISIBLE);
                }
            }
            return view;
        }
    }
    class ViewHolder{
        ImageView iv_bg;
        TextView tv_bottom;
        TextView tv_xinpin;
        TextView tv_hot;
    }
}
