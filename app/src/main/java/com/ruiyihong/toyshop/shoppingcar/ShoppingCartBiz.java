package com.ruiyihong.toyshop.shoppingcar;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ruiyihong.toyshop.bean.ShoppingCarBean;
import com.ruiyihong.toyshop.bean.ShppingCarHttpBean;
import com.ruiyihong.toyshop.db.DBContentProvider;
import com.ruiyihong.toyshop.db.DBHelper;
import com.ruiyihong.toyshop.db.ProviderContent;
import com.ruiyihong.toyshop.util.DecimalUtil;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;


public class ShoppingCartBiz {
    //购物车缓存关键字
    public static final String BUFFER_SHOPPING_CART = "buffer_shopping_cart";
    /*
            读取数据库中所有数据
            读取网络数据
            两个数据进行比对
            数据整理算法
            如果两个数据相同，保留本地的数据
            如果云库有数据那么就将云库的数据进行保存到本地
            如果本地有数据，云库没有，那么就将本地的数据保存到云库中

            对两个集合去  retainall
        */
    public static ParityDataListener listener;
    public static void setParityDataListener(ParityDataListener listeners){
        listener=listeners;
    }
    public interface ParityDataListener{
        void Done();
    }

    public static void ParityGood(final Context context){
        final String[] uid = SPUtil.getUid(context);
        if(uid==null){
            //ToastHelper.getInstance()._toast("请您登陆后使用");
            return;
        }
        ShoppingCartHttpBiz.setOnResultCallBackListener(new ShoppingCartHttpBiz.OnResultCallbackListener() {
            @Override
            public void OnResultCallback(final String result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("购物车接口回调结果： "+result);
                        //本地保存缓存
                        SPUtil.setString(context,BUFFER_SHOPPING_CART,result);
                        ShppingCarHttpBean bean = GsonUtil.parseJsonWithGson(result, ShppingCarHttpBean.class);
                        int status = bean.getStatus();
                        if(status==1){
                            //1 购物车有数据
                            List<ShppingCarHttpBean.WjlistBean> list = bean.getWjlist();
                         //   UnionGood(list,context,uid[0]);
                        }else{
                            //0 购物车无数据
                          //  UnionGood(null,context, uid[0]);
                        }
                    }
                }).start();

            }
        });
        ShoppingCartHttpBiz.getAll(uid[0]);
    }

    /*
        将本地和网络购物车取并集，多终端端数据同步
        加速算法
        1.本地为空，直接插入云端数据
        2.云端为空，直接上传本地数据
        LogUtil.e("");
    */
    private static void UnionGood(List<ShppingCarHttpBean.WjlistBean> list, Context context, String uid){
        List<ShoppingCarBean> all = getAll(context);
        int count = getCount(context)-1;

       // int count = all.size() - 1;
        if(list==null){  //**************云没有
            if(count>0){
                //本地有：插入云
//                LogUtil.e("云没有-->本地有：插入云");
//                for (ShoppingCarBean bean:all) {
//                    ShoppingCartHttpBiz.addGood(bean.getId(),uid,bean.getNum());
//                }
            }else{
                //两个都没有
            }
        }else{          //**************云有
            List<ShoppingCarBean> union = castToLocation(list);
            if(count>0){
                //两个都有
                syncCarItem(all,union,uid,context);
                LogUtil.e("count数据"+count);
            }else{
                int i = getAll(context).size();
                if (i>0){
                    return;
                }
                //本地没有：插入本地
                LogUtil.e("云有-->本地没有：插入本地");
                for (ShoppingCarBean bean:union) {
                    ShoppingCartBiz.addGood(context,bean.getId(),bean.getType(),bean.getCount(),bean.getName(),bean.getSuitage(),bean.getShopimg(),bean.getShopprice(),bean.getDpj(),bean.getNum());
                }
            }
        }
    }
    //两个都有数据的情况下处理方法
    private static void syncCarItem(List<ShoppingCarBean> location, List<ShoppingCarBean> yun, String uid, Context context) {
        for (ShoppingCarBean locationbean:location) {
            String locationID = locationbean.getId();
            boolean locationflag=true;
            for (ShoppingCarBean yunbean:yun) {
                if(TextUtils.equals(locationID,yunbean.getId())){
                    locationflag=false;
                }
            }
            if(locationflag){   //本地有的云没有，上传到云
                LogUtil.e("//本地有的云没有，上传到云 Locationid: "+locationID);
                ShoppingCartHttpBiz.addGood(Integer.parseInt(locationbean.getId()),uid,Integer.parseInt(locationbean.getNum()));
            }
        }

        for (ShoppingCarBean yunbean:yun) {
            String yunID = yunbean.getId();
            boolean yunflag=true;
            for (ShoppingCarBean locationbean:location) {
                if(TextUtils.equals(yunID,locationbean.getId())){
                    yunflag=false;
                }
            }
            if(yunflag){  //云里有的本地没有，添加到本地
                LogUtil.e("云里有的本地没有，添加到本地 Locationid: "+yunID);
                ShoppingCartBiz.addGood(context,yunbean.getId(),yunbean.getType(),yunbean.getCount(),yunbean.getName(),yunbean.getSuitage(),yunbean.getShopimg(),yunbean.getShopprice(),yunbean.getDpj(),yunbean.getNum());
            }
        }
    }
        //云端数据转换成本地库数据
    private static List<ShoppingCarBean> castToLocation(List<ShppingCarHttpBean.WjlistBean> list) {
        List<ShoppingCarBean> union=new ArrayList<>();
        for (ShppingCarHttpBean.WjlistBean bean:list) {
            ShoppingCarBean beanLocation = new ShoppingCarBean();
            beanLocation.setCheck(false);
            beanLocation.setId(bean.getId()+"");
            beanLocation.setType(bean.getIsbw()+"");
            beanLocation.setCount(bean.getKcl()+"");
            beanLocation.setName(bean.getName());
            beanLocation.setSuitage(bean.getSuitage());
            beanLocation.setShopimg(bean.getShopimg());
            beanLocation.setShopprice(bean.getShopprice()+"");
            beanLocation.setDpj(bean.getDpj()+"");
            beanLocation.setNum(bean.getWshu()+"");
            union.add(beanLocation);
        }
        return union;
    }

    public static int getGoodNumber(List<ShppingCarHttpBean.WjlistBean> list){
        int number=0;
        for (ShppingCarHttpBean.WjlistBean bean:list) {
            number = number+bean.getWshu();
        }
        return number;
    }


    //添加购物车
    public static void addGood(Context context,String id,String type,String count,String name,String age,String img,String price,String dpj,String num){
        String[] arr = SPUtil.getUid(context);
        if (arr==null){
            ToastHelper.getInstance()._toast("请您登录后使用");
            return;
        }
        ShoppingCartHttpBiz.addGood(Integer.parseInt(id),arr[0],Integer.parseInt(num));
        boolean flag=true;
        List<ShoppingCarBean> all = getAll(context);
        for (ShoppingCarBean car: all) {
            if(TextUtils.equals(id,car.getId())){
                updateGoodsNumber(context,id,type,DecimalUtil.add(car.getNum(),num));
                flag=false;
            }
        }
        if(flag){  //  toyage,toynum,toydpj,toyprice,toytype,toyname,toyimg,toyid,toycount
            ContentValues values = new ContentValues();
            values.put("toyid", id);
            values.put("toytype",type);
            values.put("toycount",count);
            values.put("toyname",name);
            values.put("toyage",age);
            values.put("toyimg",img);
            values.put("toyprice",price);
            values.put("toydpj",dpj);
            values.put("toynum",num);
            String uri= ProviderContent.UriStringPaser("insert", DBContentProvider.Table_Car);
            Uri uril = Uri.parse(uri);
            context.getContentResolver().insert(uril, values);
        }
    }

    //这个方法测试有问题
    public static ShoppingCarBean getGood(Context context,String id,String type){
        String uri = ProviderContent.UriStringPaser("query", DBHelper.Table_Car);
        Uri uri1 = Uri.parse(uri);
        Cursor cursor = context.getContentResolver().query(uri1, null, "1", new String[]{id,type}, null);
        int count = cursor.getCount();
        ShoppingCarBean bean=null;
        if(count>0){
            bean = new ShoppingCarBean();
        bean.setId(cursor.getString(cursor
                .getColumnIndex("toyid")));
        bean.setType(cursor.getString(cursor
                .getColumnIndex("toytype")));

          LogUtil.e( cursor.getColumnName(0));
        }
        return bean;
    }

    public static int getCount(Context context){
        String uri = ProviderContent.UriStringPaser("query", DBHelper.Table_Car);
        Uri uri1 = Uri.parse(uri);
        Cursor cursor = context.getContentResolver().query(uri1, null, null, null, null);
        int s=cursor.getCount();
        if(s>1){
            s=s-1;
        }else{
            s=0;
        }
        return s;
    }

    public static List<ShoppingCarBean> getAll(Context context){
        ArrayList<ShoppingCarBean> carBeanArrayList = new ArrayList<>();

        if(context!=null){
            String uri = ProviderContent.UriStringPaser("query", DBHelper.Table_Car);
            Uri uri1 = Uri.parse(uri);
            Cursor cursor = context.getContentResolver().query(uri1, null, null, null, null);
            int s=cursor.getCount();
            if(cursor!=null&&cursor.moveToFirst()){
                while (cursor.moveToNext()) {
                    ShoppingCarBean carBean = new ShoppingCarBean();
                    carBean.setCheck(false);
                    /**************************************/
                    carBean.setId(cursor.getString(cursor
                            .getColumnIndex("toyid")));
                    carBean.setType(cursor.getString(cursor
                            .getColumnIndex("toytype")));
                    carBean.setCount(cursor.getString(cursor
                            .getColumnIndex("toycount")));
                    carBean.setName(cursor.getString(cursor
                            .getColumnIndex("toyname")));
                    carBean.setSuitage(cursor.getString(cursor
                            .getColumnIndex("toyage")));
                    carBean.setShopimg(cursor.getString(cursor
                            .getColumnIndex("toyimg")));
                    carBean.setShopprice(cursor.getString(cursor
                            .getColumnIndex("toyprice")));
                    carBean.setDpj(cursor.getString(cursor
                            .getColumnIndex("toydpj")));
                    carBean.setNum(cursor.getString(cursor
                            .getColumnIndex("toynum")));
                    /**************************************/
                    carBeanArrayList.add(carBean);
                }
            }
        }
        return carBeanArrayList;
    }



    //购物车所有商品的价钱
    public static String getAllPrice(Context context){
        String AllPrice="";
        List<ShoppingCarBean> list = getAll(context);
        for (ShoppingCarBean carBean : list) {
            String num = carBean.getNum();
            String shopprice = carBean.getShopprice();
            String toyPrice = DecimalUtil.multiplyWithScale(num, shopprice, 2);
            AllPrice=DecimalUtil.add(toyPrice,AllPrice);
        }
        return AllPrice;
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

    // 删除某个商品,即删除其  ID + TYPE
    public static void delGood(Context context,String id,String type) {
        String[] arr = SPUtil.getUid(context);
        if (arr==null){
            ToastHelper.getInstance()._toast("请您登录后使用");
            return;
        }
        String uri = ProviderContent.UriStringPaser("delete", DBHelper.Table_Car);
        Uri uri1 = Uri.parse(uri);
        context.getContentResolver().delete(uri1,"删除",new String[]{id,type});
    }

    //删除全部商品
    public static void delAllGoods(Context context) {
       /* String[] arr = SPUtil.getUid(context);
        if (arr==null){
            ToastHelper.getInstance()._toast("请您登录后使用");
            return;
        }*/
        String uri = ProviderContent.UriStringPaser("delete", DBHelper.Table_Car);
        Uri uri1 = Uri.parse(uri);
        context.getContentResolver().delete(uri1,"shan除全部",null);
    }

    //增减数量，操作通用，数据不通用
    //更新购物车的单个商品数量--更新数据库
    public static void updateGoodsNumber(Context context,String id, String type,String num) {
        String[] arr = SPUtil.getUid(context);
        if (arr==null){
            ToastHelper.getInstance()._toast("请您登录后使用");
            return;
        }
        ShoppingCartHttpBiz.pdateGoodsNumber(Integer.parseInt(id),arr[0],Integer.parseInt(num));
        String uri = ProviderContent.UriStringPaser("update", DBHelper.Table_Car);
        Uri uri1 = Uri.parse(uri);
        context.getContentResolver().update(uri1,null,num,new String[]{id,type});
    }

    public static long findCountAll(Context context){
        int count = 0;
        List<ShoppingCarBean> all = ShoppingCartBiz.getAll(context);
        for (int i = 0; i < all.size(); i++) {
            ShoppingCarBean shoppingCarBean = all.get(i);
            count += Integer.parseInt(shoppingCarBean.getNum());
        }
        return count;
    }

    public static int findCountById(Context context,int id){

        int toyNum = 0;

        List<ShoppingCarBean> all = ShoppingCartBiz.getAll(context);
        for (int i = 0; i < all.size(); i++) {
            ShoppingCarBean shoppingCarBean = all.get(i);
            if ((id+"").equals(shoppingCarBean.getId())){
                //找到此产品
                toyNum = Integer.parseInt(shoppingCarBean.getNum());
                return toyNum;
            }
        }
        return 0;
    }
}