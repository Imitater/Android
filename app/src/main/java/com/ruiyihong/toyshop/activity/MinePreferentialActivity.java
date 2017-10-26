package com.ruiyihong.toyshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.mine.MyPreferentialBean;
import com.ruiyihong.toyshop.bean.mine.MyPreferentialOverLineBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/7/14.
 * 我的优惠券
 */

public class MinePreferentialActivity extends BaseActivity {
    private static final String HB_IMAGE_BASE = "http://appadmin.y91edu.com/hbimage/";//红包图片路径

    private static final int MSG_PARSE_DJQ = 0;
    private static final int MSG_PARSE_LS_DJQ = 1;
    private static final String TYPE_KY = "type_ky";
    private static final String TYPE_LS = "type_ls";
    @InjectView(R.id.rb_youhui_used)
    RadioButton mRbYouhuiUsed;
    @InjectView(R.id.rb_youhui_history)
    RadioButton mRbYouhuiHistory;
    @InjectView(R.id.iv_youhui_line_used)
    ImageView mIvYouhuiLine_used;
    @InjectView(R.id.iv_youhui_line_history)
    ImageView mIvYouhuiLine_history;
    @InjectView(R.id.common_LoadingView)
    CommonLoadingView loadingView;
    @InjectView(R.id.rg_youhui)
    RadioGroup mRgYouhui;
    @InjectView(R.id.vp_youhui)
    ViewPager mVpYouhui;

    private static final int NetWorkError = 4;
    private static final int CloseLoadingView = 5;
    private static final int PageLoading = 6;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
                case MSG_PARSE_DJQ://我的可用优惠券
                    parseDjq((String) msg.obj);
                    break;
                case MSG_PARSE_LS_DJQ://历史代金券
                    parseOverLineDjq((String) msg.obj);
                    break;

            }
        }
    };


    private ArrayList<RelativeLayout> viewList;
    private List<MyPreferentialBean.DataBean> djqList;
    private List<MyPreferentialOverLineBean.DjqBean> ls_djqList;
    private ArrayList<Object> dataList_ky;
    private ArrayList<Object> dataList_ls;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_preferential;
    }

    @Override
    protected void initView() {
        mIvYouhuiLine_used.setFocusableInTouchMode(true);
        mIvYouhuiLine_used.setFocusable(true);
        mIvYouhuiLine_used.requestFocus();
    }

    @Override
    protected void initData() {
        if (!NetWorkUtil.isNetWorkAvailable(this)) {
            handler.sendEmptyMessage(NetWorkError);
            return;
        } else {
            handler.sendEmptyMessage(PageLoading);
        }

        //初始化状态
        mRgYouhui.check(R.id.rb_youhui_used);
        mIvYouhuiLine_used.setVisibility(View.VISIBLE);
        mIvYouhuiLine_history.setVisibility(View.INVISIBLE);

        initViewPager();
    }

    private void initViewPager() {
        RelativeLayout view = (RelativeLayout) View.inflate(this, R.layout.no_djq_item, null);
        RelativeLayout view1 = (RelativeLayout) View.inflate(this, R.layout.no_djq_item, null);
        viewList = new ArrayList<>();
        viewList.add(view);
        viewList.add(view1);

        mVpYouhui.setAdapter(new PreferentialAdapter());
    }

    private void initMyDqj() {
        try {
            getDataFromNet(AppConstants.MY_DJQ, TYPE_KY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOverLineDjq() {
        try {
            getDataFromNet(AppConstants.MY_OVERLINE_DJQ, TYPE_LS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromNet(String url, final String type) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
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
                if (result != null) {
                    Message msg = Message.obtain();
                    if (type.equals(TYPE_KY)) {
                        msg.what = MSG_PARSE_DJQ;

                    } else if (type.equals(TYPE_LS)) {
                        msg.what = MSG_PARSE_LS_DJQ;
                    }
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void parseDjq(String result) {
        dataList_ky = new ArrayList<>();
        LogUtil.e("我的代金券====" + result);
        MyPreferentialBean myPreferentialBean = GsonUtil.parseJsonWithGson(result, MyPreferentialBean.class);
        if (myPreferentialBean != null) {
            djqList = myPreferentialBean.getData();//代金券
            List<MyPreferentialBean.Data1Bean> hbList = myPreferentialBean.getData1();//红包
            dataList_ky.addAll(djqList);
            dataList_ky.addAll(hbList);
            RelativeLayout relativeLayout = viewList.get(0);
            RecyclerView rv_djq_ky = relativeLayout.findViewById(R.id.rv_djq);
            TextView tv_no_djq = relativeLayout.findViewById(R.id.tv_no_djq);
            if (djqList!=null  && hbList!=null &&dataList_ky.size()>0) {
                //有代金券
                rv_djq_ky.setVisibility(View.VISIBLE);
                tv_no_djq.setVisibility(View.GONE);
                rv_djq_ky.setLayoutManager(new LinearLayoutManager(this));
                DjqAdapter adapter = new DjqAdapter(TYPE_KY);
                rv_djq_ky.setAdapter(adapter);
            }else{
                rv_djq_ky.setVisibility(View.GONE);
                tv_no_djq.setVisibility(View.VISIBLE);
            }
        }

    }

    private void parseOverLineDjq(String result) {
        LogUtil.e("我的历史代金券====" + result);
        dataList_ls = new ArrayList<>();
        MyPreferentialOverLineBean myPreferentialOverLineBean = GsonUtil.parseJsonWithGson(result, MyPreferentialOverLineBean.class);
        if (myPreferentialOverLineBean != null) {
            ls_djqList = myPreferentialOverLineBean.getDjq();
            List<MyPreferentialOverLineBean.HbBean> hb_list_ls = myPreferentialOverLineBean.getHb();
            dataList_ls.addAll(ls_djqList);
            dataList_ls.addAll(hb_list_ls);
            //LogUtil.e("历史代金券size==="+dataList_ls.size()+"，红包=="+hb_list_ls.size()+",代金券=="+ls_djqList.size());

            RelativeLayout relativeLayout = viewList.get(1);
            RecyclerView rv_djq_ls = relativeLayout.findViewById(R.id.rv_djq);
            TextView tv_no_djq = relativeLayout.findViewById(R.id.tv_no_djq);
            if (ls_djqList!=null && hb_list_ls!=null && dataList_ls.size()>0) {
                rv_djq_ls.setVisibility(View.VISIBLE);
                tv_no_djq.setVisibility(View.GONE);
                rv_djq_ls.setLayoutManager(new LinearLayoutManager(this));
                DjqAdapter adapter = new DjqAdapter(TYPE_LS);
                rv_djq_ls.setAdapter(adapter);
                LogUtil.e("itemCount=========="+adapter.getItemCount());
            }else {
                rv_djq_ls.setVisibility(View.GONE);
                tv_no_djq.setVisibility(View.VISIBLE);
            }
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

        mRgYouhui.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rb_youhui_used://可用优惠劵
                        mVpYouhui.setCurrentItem(0);
                        break;
                    case R.id.rb_youhui_history://历史优惠劵
                        mVpYouhui.setCurrentItem(1);
                        break;
                }
            }
        });

        mVpYouhui.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mIvYouhuiLine_used.setVisibility(View.VISIBLE);
                    mIvYouhuiLine_history.setVisibility(View.INVISIBLE);
                    mRgYouhui.check(R.id.rb_youhui_used);
                } else if (position == 1) {
                    mIvYouhuiLine_used.setVisibility(View.INVISIBLE);
                    mIvYouhuiLine_history.setVisibility(View.VISIBLE);
                    mRgYouhui.check(R.id.rb_youhui_history);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void processClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    class PreferentialAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            if (position == 0) {
                //可用优惠券
                initMyDqj();
            } else {
                //历史优惠券
                initOverLineDjq();
            }
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class DjqAdapter extends RecyclerView.Adapter<DjqAdapter.DjqViewHolder> {
        private final String type;

        public DjqAdapter(String type) {
            this.type = type;
        }

        @Override
        public DjqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MinePreferentialActivity.this).inflate(R.layout.item_mine_djq_layout, parent,false);
            return new DjqViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DjqViewHolder holder, int position) {
            if (TYPE_KY.equals(type)) {
                Object o = dataList_ky.get(position);
                if (o instanceof MyPreferentialBean.Data1Bean ){
                    //红包
                    MyPreferentialBean.Data1Bean dataBean = (MyPreferentialBean.Data1Bean) o;
                    String dimg = dataBean.getWdhb();
                    Picasso.with(MinePreferentialActivity.this).load(HB_IMAGE_BASE + dimg).placeholder(R.mipmap.lunbo_default).into(holder.iv_djq);
                    holder.tv_deadline.setText("到期日期："+dataBean.getDhtime());

                }else if (o instanceof  MyPreferentialBean.DataBean){
                    //代金券
                    MyPreferentialBean.DataBean dataBean = (MyPreferentialBean.DataBean) o;
                    String dimg = dataBean.getDimg();
                    Picasso.with(MinePreferentialActivity.this).load(AppConstants.IMG_BASE_URL + dimg).placeholder(R.mipmap.lunbo_default).into(holder.iv_djq);
                    holder.tv_deadline.setText("到期日期："+dataBean.getDhtime());
                }
            }
            if (TYPE_LS.equals(type)) {
                Object o = dataList_ls.get(position);
                if (o instanceof MyPreferentialOverLineBean.DjqBean) {
                    //代金券
                    MyPreferentialOverLineBean.DjqBean djqBean = (MyPreferentialOverLineBean.DjqBean) o;
                    String dimg = djqBean.getOlddjq();
                    Picasso.with(MinePreferentialActivity.this).load(AppConstants.IMG_BASE_URL + dimg).placeholder(R.mipmap.lunbo_default).into(holder.iv_djq);
                    holder.tv_deadline.setText("到期日期：" + djqBean.getDhtime());

                }else if (o instanceof MyPreferentialOverLineBean.HbBean){
                    //红包
                    MyPreferentialOverLineBean.HbBean hbBean = (MyPreferentialOverLineBean.HbBean) o;
                    String dhtime = hbBean.getDhtime();//到期日期
                    String oldhb_pic = hbBean.getOldhb();
                    Picasso.with(MinePreferentialActivity.this).load(HB_IMAGE_BASE + oldhb_pic).placeholder(R.mipmap.lunbo_default).into(holder.iv_djq);
                    holder.tv_deadline.setText("到期日期：" + dhtime);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (TYPE_KY.equals(type)) {
                return dataList_ky.size();
            } else if (TYPE_LS.equals(type)) {
                return dataList_ls.size();
            }
            return 0;
        }

        class DjqViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_djq;
            TextView tv_deadline;
            public DjqViewHolder(View itemView) {
                super(itemView);
                iv_djq = itemView.findViewById(R.id.iv_djq);
                tv_deadline = itemView.findViewById(R.id.tv_deadline);
            }
        }
    }
}
