/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.DetailActivity;
import com.ruiyihong.toyshop.activity.DetailSuitActivity;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.activity.MoreToyActivity;
import com.ruiyihong.toyshop.activity.MoreTuijianActivity;
import com.ruiyihong.toyshop.activity.ShoppingCarActivity;
import com.ruiyihong.toyshop.adapter.Search_wj_resultAdapter;
import com.ruiyihong.toyshop.adapter.ToyShopAdapter;
import com.ruiyihong.toyshop.bean.SearchResult;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.shoppingcar.ShoppingCartHttpBiz;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.NetWorkUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.ruiyihong.toyshop.view.FullyGridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Burt on 2017/7/22 0022.
 */

public class SearchToyFragment extends BaseFragment {
    private List<SearchResult.WjlistBean> wjList;
    private Search_wj_resultAdapter mAdapter;
    private ImageView iv_shop_cart;
    private TextView tv_pop_shopping_number;
    private RecyclerView rv_result;
    private RelativeLayout rl_parent;

    private static final int MSG_SHOPPING_FINDALL = 11;
    private static final int MSG_SHOPPING_ADD = 12;
    private static final int INTENT_REQUEST_LOGIN = 13;
    private int count = -1;
    private String[] uid;
    private List<ShppingCarHttpBean.WjlistBean> shopAllList;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SHOPPING_FINDALL:
                shopAllList = (List<ShppingCarHttpBean.WjlistBean>) msg.obj;
                shppingCartSetting();
                break;
                case MSG_SHOPPING_ADD://向购物车添加商品
                    String obj = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(obj);
                        if (object.getInt("status") == 0){
                            ToastHelper.getInstance().displayToastShort("添加购物车失败");
                        }else{
                            ToastHelper.getInstance().displayToastShort("添加购物车成功");
                            addShoppingSetting();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;


            }
        }
    };

    private void shppingCartSetting() {
        if (shopAllList == null || shopAllList.size() == 0){
            count = 0;
            if (tv_pop_shopping_number != null) {
                tv_pop_shopping_number.setVisibility(View.INVISIBLE);
            }
        }else{
            count = ShoppingCartHttpBiz.findAllCount(mActivity,shopAllList);
            if (tv_pop_shopping_number != null) {
                tv_pop_shopping_number.setVisibility(View.VISIBLE);
                tv_pop_shopping_number.setText(count + "");
            }
        }
    }



    private void addShoppingSetting() {
        count++;
        Log.i("radish", "addcount------------------" + count);
        tv_pop_shopping_number.setVisibility(View.VISIBLE);
        tv_pop_shopping_number.setText(count + "");
    }

    public SearchToyFragment(List<SearchResult.WjlistBean> wjlist) {
        this.wjList=wjlist;
        Log.i("radish","wjlist------------------"+wjList );
    }

    @Override
    protected View initView() {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.search_fragment_layout, null);
        rv_result = v.findViewById(R.id.rv_search_result);
        iv_shop_cart = v.findViewById(R.id.iv_shop_cart);
        rl_parent = v.findViewById(R.id.rl_parent);
        tv_pop_shopping_number = v.findViewById(R.id.tv_pop_shopping_number);
        rv_result.setLayoutManager(new FullyGridLayoutManager(mActivity, 2));
        mAdapter = new Search_wj_resultAdapter(mActivity, wjList);
        rv_result.setAdapter(mAdapter);
        rv_result.setItemAnimator(new DefaultItemAnimator());
        if(wjList.size()==0){
            ToastHelper.getInstance()._toast("搜索结果为空");
        }
        return v;
    }

    @Override
    protected void initData() {
        //回显购物车的数量
        if (count > 0) {
            tv_pop_shopping_number.setVisibility(View.VISIBLE);
            tv_pop_shopping_number.setText(count + "");
        } else {
            tv_pop_shopping_number.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        findAllGood();
        if (count > 0) {
            tv_pop_shopping_number.setVisibility(View.VISIBLE);
            tv_pop_shopping_number.setText(count + "");
        } else {
            tv_pop_shopping_number.setVisibility(View.GONE);
        }

    }
    /**
     * 查找所有购物车产品
     */
    public void findAllGood(){
        uid = SPUtil.getUid(mActivity);
        if(uid ==null){
            // startActivity(new Intent(this,LoginActivity.class));
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
        ShoppingCartHttpBiz.getAll(uid[0]);
    }



    @Override
    protected void initEvent() {
        //条目点击事件
        mAdapter.setOnItemClickListener(new ToyShopAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SearchResult.WjlistBean bean = wjList.get(position);
                Intent intent = new Intent(mActivity, DetailActivity.class);
                if (bean.getIsbw() == 0){
                    //图书
                    intent.putExtra("type",DetailActivity.BOOK_TYPE);
                }else {
                    //玩具
                    intent.putExtra("type",DetailActivity.TOY_TYPE);
                }
                intent.putExtra("id",bean.getId());
                startActivity(intent);

            }
        });
        //购物车点击事件
        mAdapter.setOnShoppingcartClickListener(new ToyShopAdapter.onShoppingcartClickListener() {
            @Override
            public void onShoppingcartClick(View v, int position) {
                uid = SPUtil.getUid(mActivity);
                if (uid == null) {
                    //未登录
                    startActivityForResult(new Intent(mActivity,LoginActivity.class),INTENT_REQUEST_LOGIN);
                } else {
                    if (count < 0) {
                        addData();
                    }
                    SearchResult.WjlistBean bean = wjList.get(position);
                    int kcl = bean.getKcl();
                    Log.e("radish", "onShoppingcartClick: kcl:"+kcl);
                    int toyCount = ShoppingCartHttpBiz.findCountById(mActivity,shopAllList, bean.getId());
                    if (kcl > toyCount) {
                        Log.e("radish", "addShoppingCar: 库存量充足" );
                        //将商品添加到购物车动画
                        addToCartWithAnimation(v);
                        //将商品添加到购物车
                        addGood(bean.getId(),uid[0],1);
                    } else {
                        Log.e("radish", "addShoppingCar: 库存量不足" );
                        ToastHelper.getInstance().displayToastShort("库存量不足");
                    }
                }
            }
        });

        iv_shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ShoppingCarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addData(){
        if (NetWorkUtil.isNetWorkAvailable(mActivity)) {
            if (count<0){
                findAllGood();
            }
        }else {
            ToastHelper.getInstance().displayToastShort("网络异常");
        }

    }
    //添加购物车
    public void addGood(int wid,String uid,int num) {
        String url= AppConstants.AddShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("wid",wid);
        para.put("uid",uid);
        para.put("shu",num);

        ShoppingCartHttpBiz.Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                Log.e("radish", "onResponse: "+result );
                if (result!=null&&result.length()>2){
                    Message msg = Message.obtain();
                    msg.what = MSG_SHOPPING_ADD;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        });
    }
    @Override
    public void onClick(View view) {

    }
    private float mParentY = -1;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];

    private void addToCartWithAnimation(View v) {

        if (mParentY == -1) {
            //rv在屏幕上的位置
            int[] location1 = new int[2];
            rl_parent.getLocationOnScreen(location1);
            mParentY = location1[1];
        }

        final ImageView goods = new ImageView(mActivity);
        goods.setImageResource(R.mipmap.head_icon_jifen);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
        rl_parent.addView(goods, params);

        //动画（位移动画，从点击的位置，移动到浮动按钮的位置）
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        float startX = location[0]; //按钮在屏幕中的位置 x
        float startY = location[1] - mParentY; //按钮在屏幕中的位置-顶部的高度 y


        float toX = iv_shop_cart.getX();  //目的x
        float toY = iv_shop_cart.getY(); //目的y
        LogUtil.e("tox==" + toX);
        LogUtil.e("toy==" + toY);

        float diffX = Math.abs(startX - toX);
        float diffY = Math.abs(startY - toY);

        float diff = diffX * diffX + diffY * diffY;
        diff = (float) Math.sqrt(diff);

        //四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration((long) (diff * 1));
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });
        //   五、 开始执行动画
        valueAnimator.start();

        //   六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // todo 购物车的数量加1,初始的数据要从购物车中获取
                rl_parent.removeView(goods);
/*
                String number = tv_pop_shopping_number.getText().toString();
                int i = Integer.parseInt(number);

                i++;
                if (i == 1) {
                    tv_pop_shopping_number.setVisibility(View.VISIBLE);
                }
                tv_pop_shopping_number.setText(i + "");*/

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_LOGIN) {
            addData();
        }
    }

}
