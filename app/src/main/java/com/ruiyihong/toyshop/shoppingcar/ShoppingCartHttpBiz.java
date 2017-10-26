/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.shoppingcar;


/*
* 从网络读取的json串会缓存到本地进行备份
* 购物车读取网络数据类
* 购物车本地数据缓存类
* */

import android.content.Context;
import com.ruiyihong.toyshop.bean.ShoppingCarBean;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShoppingCartHttpBiz {
    //购物车缓存关键字
    public static final String BUFFER_SHOPPING_CART = "buffer_shopping_cart";

    private static String ErrorInfo="网络购物车返回失败";
    private static String OKInfo="网络购物车返回正常";
    private static OnResultCallbackListener listener;

    public interface OnResultCallbackListener{
        void OnResultCallback(String result);
    }
    public static void setOnResultCallBackListener(OnResultCallbackListener listeners){
        listener=listeners;
    }

    public static void Base(String url,Map<String,Object> para,Callback callback){
        try {
            OkHttpUtil.postJson(url,para,callback);
        } catch (IOException e) {
            LogUtil.e("购物车网络异常"+e.toString());
        }
    }

    //添加购物车
    public static void addGood(int wid,String uid,int num) {
        String url= AppConstants.AddShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("wid",wid);
        para.put("uid",uid);
        para.put("shu",num);

        Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("添加"+ErrorInfo);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result!=null&&result.length()>2){
                    LogUtil.e("添加"+OKInfo+result);
                }
            }
        });
    }


    //查询购物车所有信息
    public static void getAll(String uid) {
        String url=AppConstants.QueryShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("uid",uid);
        Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("查询"+ErrorInfo);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result!=null&&result.length()>2){
                    LogUtil.e("查询"+OKInfo+result);
                    if(listener!=null){
                        listener.OnResultCallback(result);
                    }
                }
            }
        });
    }


    // 删除某个商品,即删除其  ID + TYPE
    public static void delGood(String wid,String uid) {
        String url=AppConstants.DelShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("wid",wid);
        para.put("uid",uid);

        Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("删除"+ErrorInfo);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result!=null&&result.length()>2){
                    LogUtil.e("删除购物车===="+OKInfo+result);
                }
            }
        });
    }


    //更新购物车的单个商品数量
    public static void pdateGoodsNumber(int wid,String uid,int num) {
        String url=AppConstants.UpdateShoppingCar;
        Map<String,Object> para=new HashMap<>();
        para.put("wid",wid);
        para.put("uid",uid);
        para.put("shu",num);

        Base(url, para, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("更新"+ErrorInfo);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                if (result!=null&&result.length()>2){
                    LogUtil.e("更新"+OKInfo+result);
                }
            }
        });
    }



    /**
     * 按id查找购物车商品数量
     * @param context
     * @param list
     * @return
     */
    public static int findAllCount(Context context, List<ShppingCarHttpBean.WjlistBean> list){
        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            ShppingCarHttpBean.WjlistBean shoppingCarBean = list.get(i);
            num += shoppingCarBean.getWshu();
        }
        return num;
    }

    /**
     * 按id查找购物车商品数量
     * @param context
     * @param list
     * @param id
     * @return
     */
    public static int findCountById(Context context, List<ShppingCarHttpBean.WjlistBean> list, int id){
        int toyNum = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ShppingCarHttpBean.WjlistBean shoppingCarBean = list.get(i);
                if (id == shoppingCarBean.getId()) {
                    //找到此产品
                    toyNum = shoppingCarBean.getWshu();
                    return toyNum;
                }
            }
        }
        return 0;
    }
}
