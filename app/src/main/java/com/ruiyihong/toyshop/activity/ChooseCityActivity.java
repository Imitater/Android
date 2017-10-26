package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.adapter.AllCityAdapter;
import com.ruiyihong.toyshop.adapter.HotCityAdapter;
import com.ruiyihong.toyshop.bean.CharacterParser;
import com.ruiyihong.toyshop.bean.PinyinComparator;
import com.ruiyihong.toyshop.bean.SortModel;
import com.ruiyihong.toyshop.db.DBManager;
import com.ruiyihong.toyshop.fragment.HomeFragment;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.MyGridView;
import com.ruiyihong.toyshop.view.MyListView;
import com.ruiyihong.toyshop.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;

public class ChooseCityActivity extends BaseActivity {

    private static final int SQ_DATA = 0;
    @InjectView(R.id.tv_localcity)
    TextView mTvLocalcity;
    @InjectView(R.id.gv_hotcity)
    MyGridView mGvHotcity;
    @InjectView(R.id.sidrbar)
    SideBar mSidrbar;
    @InjectView(R.id.lv_city)
    MyListView mLvCity;
    @InjectView(R.id.et_home_search)
    EditText mEtSearch;


    private CharacterParser characterParser;
    // private List<SortModel> SourceDateList;

    private PinyinComparator pinyinComparator;
    private AllCityAdapter mAdapter;
    private List<SortModel> sortModels;
    private DBManager dbManager;
    //热门城市
    private String[] hotcitys = {"北京", "上海", "重庆", "天津", "合肥", "厦门", "广州", "深圳",
            "武汉", "南京", "苏州", "太原", "济南", "成都", "西安", "杭州"};



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case SQ_DATA://数据库查询的城市数据
                    mAdapter = new AllCityAdapter(ChooseCityActivity.this, sortModels);
                    mLvCity.setAdapter(mAdapter);
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_city;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //定位城市
        String local_city = getIntent().getStringExtra("local_city");
        mTvLocalcity.setText(local_city);

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        mGvHotcity.setAdapter(new HotCityAdapter(hotcitys, this));
        //从本地数据库获取城市信息
        dbManager = new DBManager(this);

        //开启子线程 查询数据库
        new Thread() {
            @Override
            public void run() {
                ArrayList<String> cityList = dbManager.getCityData(getPackageName());
                sortModels = filledData(cityList);

                // 根据a-z进行排序源数据
                pinyinComparator = new PinyinComparator();
                Collections.sort(sortModels, pinyinComparator);

                //查询完成，主线程更新ui
                Message message = Message.obtain();
                message.obj = sortModels;
                message.what = SQ_DATA;
                handler.sendMessage(message);

            }
        }.start();

    }

    @Override
    protected void initEvent() {
        //点击定位城市
        mTvLocalcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到点击的城市名称，并结束当前页面，回到首页
                Intent intent =getIntent();
                //这里使用bundle绷带来传输数据
                Bundle bundle =new Bundle();
                //传输的内容仍然是键值对的形式
                bundle.putString("city",getIntent().getStringExtra("local_city"));//回发的消息,hello world from secondActivity!
                intent.putExtras(bundle);
                setResult(HomeFragment.CHOOSE_CITY_RESULT,intent);
                finish();
            }
        });
        //设置右侧触摸监听
        mSidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    mLvCity.setSelection(position);
                }

            }
        });

        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //获取到点击的城市名称，并结束当前页面，回到首页
                Intent intent =getIntent();
                //这里使用bundle绷带来传输数据
                Bundle bundle =new Bundle();
                //传输的内容仍然是键值对的形式
                bundle.putString("city",((SortModel) mAdapter.getItem(position)).getName());//回发的消息,hello world from secondActivity!
                intent.putExtras(bundle);
                setResult(HomeFragment.CHOOSE_CITY_RESULT,intent);
                finish();
            }
        });
        //根据输入框输入值的改变来过滤搜索
        mEtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //设置热门城市的条目点击事件
        mGvHotcity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent =getIntent();
                //这里使用bundle绷带来传输数据
                Bundle bundle =new Bundle();
                //传输的内容仍然是键值对的形式
                bundle.putString("city",hotcitys[i]);//回发的消息,hello world from secondActivity!
                intent.putExtras(bundle);
                setResult(HomeFragment.CHOOSE_CITY_RESULT,intent);
                finish();
            }
        });
    }

    @Override
    protected void processClick(View v) {

    }

    /**
     * 将城市信息转成listview需要的数据格式
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(ArrayList<String> date) {
        List<SortModel> mSortList = new ArrayList<>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i));
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = sortModels;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : sortModels) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mAdapter.updateListView(filterDateList);
    }

}
