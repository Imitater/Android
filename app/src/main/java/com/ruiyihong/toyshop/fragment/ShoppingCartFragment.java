package com.ruiyihong.toyshop.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.DetailActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.activity.SettleActivity;
import com.ruiyihong.toyshop.activity.ToyShopActivity;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.DecimalUtil;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.Refresh_Listener;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.swipemenu.AmountView;
import com.ruiyihong.toyshop.view.swipemenu.BaseSwipListAdapter;
import com.ruiyihong.toyshop.view.swipemenu.SwipeMenu;
import com.ruiyihong.toyshop.view.swipemenu.SwipeMenuCreator;
import com.ruiyihong.toyshop.view.swipemenu.SwipeMenuItem;
import com.ruiyihong.toyshop.view.swipemenu.SwipeMenuListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.squareup.picasso.Picasso;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 */
@SuppressLint("ValidFragment")
public class ShoppingCartFragment extends BaseFragment {


    private static final int MSG_SHOPPING_FINDALL = 0;
    private static final int MSG_JIESUAN = 1;
    private boolean flag = false;

    private CheckBox ivSelectAll;
    private TextView btnSettle;
    private TextView btnDEl;
    private TextView tvCountMoney;
    private TextView tvTitle;
    private CheckBox tvEdit;
    private LinearLayout rlShoppingCartEmpty;
    private RelativeLayout rlBottomBar;

    private CarAdapter mAdapter;
    private SwipeMenuListView mListView;
    private View view;

    //选中条目
    private ArrayList<ShppingCarHttpBean.WjlistBean> mCarListSelected = new ArrayList<>();
    private ImageButton ib_back;
    public static final String selectedList = "CarSelected";
    private SmartRefreshLayout smartRefreshLayout;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SHOPPING_FINDALL://查询所有购物车信息
                    shopAllList = (List<ShppingCarHttpBean.WjlistBean>) msg.obj;

                    initData();
                    break;
                case MSG_JIESUAN:
                    Intent intent = new Intent(mActivity, SettleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(selectedList, (Serializable) mCarListSelected);
                    intent.putExtra(selectedList, bundle);
                    mActivity.startActivity(intent);
                    //修改处 todo
                    mCarListSelected.clear();
                    ivSelectAll.setChecked(false);
                    reflushTitle();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private String[] uid;

    public ShoppingCartFragment() {

    }

    public ShoppingCartFragment(boolean flag) {
        this.flag = flag;
    }

    @Override
    protected View initView() {
        view = View.inflate(mActivity, R.layout.fragment_shoppincart, null);
        initContent();
        uid = SPUtil.getUid(mActivity);
        String buffer = SPUtil.getString(mActivity, ShoppingCartHttpBiz.BUFFER_SHOPPING_CART, "");
        if (!TextUtils.isEmpty(buffer) && this.uid != null){
            ShppingCarHttpBean bean = GsonUtil.parseJsonWithGson(buffer, ShppingCarHttpBean.class);
            shopAllList = bean.getWjlist();
            initData();
        }
        return view;
    }


    private void initContent() {
        ivSelectAll = view.findViewById(R.id.ckSelectAll);
        btnSettle = view.findViewById(R.id.btnSettle);
        btnDEl = view.findViewById(R.id.btnDEl);
        tvCountMoney = view.findViewById(R.id.tvCountMoney);
        tvTitle = view.findViewById(R.id.tv_car_title);
        tvEdit = view.findViewById(R.id.tv_car_edit);
        rlShoppingCartEmpty = view.findViewById(R.id.rlShoppingCartEmpty);
        rlBottomBar = view.findViewById(R.id.rlBottomBar);
        mListView = view.findViewById(R.id.ShppingCar_listView);
        ib_back = view.findViewById(R.id.ib_back);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        smartRefreshLayout.setEnableLoadmore(false);

        smartRefreshLayout.setOnMultiPurposeListener(new Refresh_Listener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                super.onRefresh(refreshlayout);
           //   查询购物车信息
                findAllGood();
            }
        });


        if (flag) {
            ib_back.setVisibility(View.VISIBLE);
        } else {
            ib_back.setVisibility(View.GONE);
        }
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        rlShoppingCartEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ToyShopActivity.class);
                intent.putExtra("type", ToyShopActivity.DATA_TYPE_TOY);
                startActivity(intent);
            }
        });
        Drawable drawable = ivSelectAll.getCompoundDrawables()[0];
        int i = ScreenUtil.dp2px(mActivity, 20);
        drawable.setBounds(0, 0, i, i);
        ivSelectAll.setCompoundDrawables(drawable, null, null, null);
        initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        uid = SPUtil.getUid(mActivity);
        Log.e("radish","选中商品个数------------------"+mCarListSelected.size() );
        if (uid != null)
            findAllGood();
    }

    @Override
    public void initData() {
        if (shopAllList != null) {
            int count = shopAllList.size();
            tvTitle.setText("购物车(" + (count) + ")");
        }
        if (ivSelectAll!=null && btnSettle!=null && tvCountMoney!=null) {
            ivSelectAll.setChecked(false);
            btnSettle.setText("结算(" + 0 + ")");
            tvCountMoney.setText("合计：￥" + 0 + "/天");
        }
      //  mCarListSelected = new ArrayList<>();
        if (shopAllList != null && mListView != null){
            mListView.setVisibility(View.VISIBLE);
            rlShoppingCartEmpty.setVisibility(View.GONE);
            mAdapter = new CarAdapter();
            mListView.setAdapter(mAdapter);
        }else{
            mListView.setVisibility(View.GONE);
            rlShoppingCartEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initEvent() {


        btnDEl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除已经选定的数据
                uid = SPUtil.getUid(mActivity);
                if (uid == null) {
                    ToastHelper.getInstance()._toast("请您登陆后操作");
                    return;
                }
                for (ShppingCarHttpBean.WjlistBean bean : mCarListSelected) {
                    ShoppingCartHttpBiz.delGood(bean.getId()+"", uid[0]);
                    if (shopAllList.contains(bean)) {
                        shopAllList.remove(bean);
                    }
                }
                mCarListSelected.clear();
                tvEdit.setChecked(false);
                ivSelectAll.setChecked(false);
                EditOrDel(false);
                reflushTitle();
            }
        });
        // 结算
        btnSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SPUtil.getUid(mActivity) == null) {
                    ToastHelper.getInstance()._toast("请您登录后使用");
                    return;
                }
                //结算已经选定的数据
                if (mCarListSelected!=null && mCarListSelected.size() > 0) {
                    handler.sendEmptyMessage(MSG_JIESUAN);
                } else {
                    ToastHelper.getInstance()._toast("您还木有选择商品");
                }
            }
        });


        //编辑按钮切换
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditOrDel(tvEdit.isChecked());
            }
        });


        //全选按钮切换
        ivSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivSelectAll.isChecked()) {
                    //全选
                    selectAll(true);
                    if (mCarListSelected == null){
                        mCarListSelected = new ArrayList<ShppingCarHttpBean.WjlistBean>();
                    }
                    mCarListSelected.clear();
                    mCarListSelected.addAll(shopAllList);
                    reflushTitle();
                    mAdapter.notifyDataSetChanged();
                } else {
                    //全不选
                    selectAll(false);
                    if (mCarListSelected == null){
                        mCarListSelected = new ArrayList<ShppingCarHttpBean.WjlistBean>();
                    }
                    mCarListSelected.clear();
                    reflushTitle();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void EditOrDel(boolean check) {
        if (check) {
            btnDEl.setVisibility(View.VISIBLE);
            btnSettle.setVisibility(View.GONE);
            tvEdit.setText("取消");
        } else {
            btnSettle.setVisibility(View.VISIBLE);
            btnDEl.setVisibility(View.GONE);
            tvEdit.setText("编辑");
        }
    }


    @Override
    public void onClick(View view) {

    }

    // 选择全部，点下全部按钮，改变所有商品选中状态
    public void selectAll(boolean isSelectAll) {
        for (int i = 0; i < shopAllList.size(); i++) {
            shopAllList.get(i).setCheck(isSelectAll);
            boolean check = shopAllList.get(i).isCheck();
        }

    }


    public void initAdapter() {
        //初始化条目的左划删除按钮
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitleSize(ScreenUtil.dp2sp(mActivity, 16));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                // deleteItem.setIcon(R.mipmap.ic_delete);
                //图片大小看怎么放到文件夹里了
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // step 1. create a MenuCreator
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        //break;
                    case 1:
                        //删除对应数据库内容
                        ShppingCarHttpBean.WjlistBean car = shopAllList.get(position);
                        shopAllList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        reflushTitle();
                        uid = SPUtil.getUid(mActivity);
                        if (uid != null) {
                            ShoppingCartHttpBiz.delGood(car.getId()+"", uid[0]);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void JumpToDetailActivity(ShppingCarHttpBean.WjlistBean item) {
        Intent intent = new Intent(mActivity, DetailActivity.class);
        intent.putExtra("id", item.getId());
        if (item.getIsbw() == 0){
            //图书
            intent.putExtra("type", DetailActivity.BOOK_TYPE);
        }else if(item.getIsbw() == 1){
            //玩具
            intent.putExtra("type", DetailActivity.TOY_TYPE);
        }
        startActivity(intent);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    
    @Override
    public void onPause() {
        super.onPause();
        /*Log.i("radish","onPause------------------" );
        if (mCarListSelected != null){
            mCarListSelected = null;
        }*/
    }


    //刷新购物车状态
    private void reflushTitle() {
        if (tvTitle != null) {
            int count = shopAllList.size();
            tvTitle.setText("购物车(" + (count) + ")");
            btnSettle.setText("结算(" + mCarListSelected.size() + ")");
            //计算所选商品的价格
            String selectedPrice = getSelectedPrice(mCarListSelected);
            tvCountMoney.setText("合计：￥" + selectedPrice + "/天");
            //合计：￥0/天
            mAdapter.notifyDataSetChanged();
            //数据库总是会有一条空记录，所以总数下标-1就是数据总数
            if (count < 1) {
                mListView.setVisibility(View.GONE);
                rlShoppingCartEmpty.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.VISIBLE);
                rlShoppingCartEmpty.setVisibility(View.GONE);
            }
        }
    }
    //获得所有选择的价钱
    public static String getSelectedPrice(List<ShppingCarHttpBean.WjlistBean> list){
        String AllPrice="0";
        for (ShppingCarHttpBean.WjlistBean carBean : list) {
            int num = carBean.getWshu();
            int shopprice = carBean.getShopprice();
            String toyPrice = DecimalUtil.multiplyWithScale(num+"", shopprice+"", 2);
            AllPrice=DecimalUtil.add(toyPrice,AllPrice);
        }
        return AllPrice;
    }

    /**
     * 查找所有购物车产品
     */
    public void findAllGood(){
        uid = SPUtil.getUid(mActivity);
        if(this.uid ==null){
            mActivity.startActivity(new Intent(mActivity,LoginActivity.class));
            return;
        }
        ShoppingCartHttpBiz.setOnResultCallBackListener(new ShoppingCartHttpBiz.OnResultCallbackListener() {
            @Override
            public void OnResultCallback(final String result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("购物车接口回调结果： "+result);
                        ShppingCarHttpBean bean = GsonUtil.parseJsonWithGson(result, ShppingCarHttpBean.class);
                        int status = bean.getStatus();
                        List<ShppingCarHttpBean.WjlistBean> list = null;
                        if(status==1){
                            //1 购物车有数据
                            list = bean.getWjlist();
                            //本地保存缓存
                            SPUtil.setString(mActivity,ShoppingCartHttpBiz.BUFFER_SHOPPING_CART,result);
                        }else{
                            //0 购物车无数据
                        }
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOPPING_FINDALL;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });
        ShoppingCartHttpBiz.getAll(this.uid[0]);
    }

    /**
     * 修改购物车产品数量
     * @param id
     * @param type
     * @param num
     */
    public void updateGoodsNumber(int id, int type,int num) {
        String[] arr = SPUtil.getUid(mActivity);
        if (arr==null){
            mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
            return;
        }
        ShoppingCartHttpBiz.pdateGoodsNumber(id,arr[0],num);

    }

    /**
     * 购物车adapter
     */
    class CarAdapter extends BaseSwipListAdapter {

        @Override
        public int getCount() {
            if (shopAllList != null) {
                return shopAllList.size();
            }else{
                return 0;
            }
        }

        @Override
        public ShppingCarHttpBean.WjlistBean getItem(int position) {
            if (shopAllList != null) {
                return shopAllList.get(position);
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(),
                        R.layout.item_list_shopping_car, null);
                new ViewHolder(convertView);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final ShppingCarHttpBean.WjlistBean item = getItem(position);
            holder.tv_name.setText(item.getName());

            //商品是否被选中
            holder.iv_CheckGood.setChecked(item.isCheck());
            // 加载商品图片
            Picasso.with(mActivity)
                    .load(AppConstants.IMG_BASE_URL + item.getShopimg())
                    .placeholder(R.mipmap.good_default).error(R.mipmap.good_default).fit()
                    .into(holder.iv_icon);

            if (!TextUtils.isEmpty(item.getSuitage())) {
                holder.tv_info.setText("适合年龄 " + item.getSuitage());
            } else {
                holder.tv_info.setText("适合所有儿童");
            }
            holder.tv_price.setText(item.getShopprice()+"");
            holder.tv_old_price.setText("￥"+item.getDpj()+"/天");
            holder.tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            //
            holder.mAmountView.setGoods_storage(item.getKcl());
            holder.mAmountView.setAmout(item.getWshu());

            holder.iv_CheckGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.iv_CheckGood.isChecked()) {
                        //选中商品 todo
                        if (mCarListSelected != null) {
                            mCarListSelected.add(item);
                            item.setCheck(true);
                            if (mCarListSelected.size() == shopAllList.size()) {
                                ivSelectAll.setChecked(true);
                            }
                        }
                    } else {
                        //去掉商品
                        // mCarListSelected.remove()
                        if (mCarListSelected != null) {
                            ivSelectAll.setChecked(false);
                            item.setCheck(false);
                            if (item.getId() > 0) {
                                int id = item.getId();
                                for (int i = 0; i < mCarListSelected.size(); i++) {
                                    ShppingCarHttpBean.WjlistBean car = mCarListSelected.get(i);
                                    if (car.getId() == id && car.getIsbw() == item.getIsbw()) {
                                        mCarListSelected.remove(i);
                                    }
                                }
                            }
                        }
                    }
                    reflushTitle();
                }
            });


            holder.mAmountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
                @Override
                public void onAmountChange(View view, int amount) {
                    // 修改商品数量
                    int type = item.getIsbw();
                    int id = item.getId();
                    if (item.getKcl() <= amount) {
                        ToastHelper.getInstance()._toast("库存不足");
                    } else {
                        updateGoodsNumber(id,type,amount);
                        item.setWshu(amount);
                        notifyDataSetChanged();
                        reflushTitle();
                    }
                }
            });

            holder.item_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JumpToDetailActivity(item);
                }
            });


            return convertView;
        }


        class ViewHolder {
            CheckBox iv_CheckGood;
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_info;
            TextView tv_price;
            TextView tv_old_price;


            RelativeLayout item_container;

            AmountView mAmountView;

            public ViewHolder(View view) {
                iv_CheckGood = view.findViewById(R.id.ck_CheckGood);
                tv_name = view.findViewById(R.id.good_name);
                tv_info = view.findViewById(R.id.good_info);
                iv_icon = view.findViewById(R.id.iv_good_icon);
                tv_price = view.findViewById(R.id.tv_good_price);
                tv_old_price = view.findViewById(R.id.tv_good_old_price);
                mAmountView = view.findViewById(R.id.amount_view);
                item_container = view.findViewById(R.id.car_item_container);

                mAmountView.setGoods_storage(10);

                view.setTag(this);
            }
        }

        @Override
        public boolean getSwipEnableByPosition(int position) {

            return true;
        }
    }
}
