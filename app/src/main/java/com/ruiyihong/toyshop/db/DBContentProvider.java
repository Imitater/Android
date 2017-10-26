/*
 * 2017.
 * Huida.Burt
 * CopyRight
 */

package com.ruiyihong.toyshop.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.ruiyihong.toyshop.util.LogUtil;

/**
 * Created by Burt on 2017/7/8 0008.
 */

public class DBContentProvider extends ContentProvider {
    public static final String HostNmae="com.ruiyihong.toyshop.shoppingcar";
    public static final String Table_Car = "car";
    public static final String Table_Order = "order";

    // ctrl + shift + X(变大写)   变小写  + y
    private static final int QueryCar = 0;
    private static final int InsertCar = 1;
    private static final int UpdateCar = 2;
    private static final int DelCar = 3;

    private static final int QueryOrder = 4;
    private static final int InsertOrder = 5;
    private static final int UpdateOrder = 6;
    private static final int DelOrder = 7;

    //1 想使用内容提供者 必须定义 匹配规则   code:定义的匹配规则 如果 匹配不上  有一个返回码  -1
    static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DBHelper helper;
    static {
        // 2. 添加匹配规则
        /*
         * authority   主机名  通过主机名来访问我暴露的数据 cn.huida.burt.testdb
         * path   你也可以随意 写 cn.huida.burt.testdb/querycar
         * code 匹配码
         */

        matcher.addURI(HostNmae, "query" + Table_Car, QueryCar);
        matcher.addURI(HostNmae, "insert" + Table_Car, InsertCar);
        matcher.addURI(HostNmae, "update" + Table_Car, UpdateCar);
        matcher.addURI(HostNmae, "delete" + Table_Car, DelCar);

        matcher.addURI(HostNmae, "query" + Table_Order, QueryOrder);
        matcher.addURI(HostNmae, "insert" + Table_Order, InsertOrder);
        matcher.addURI(HostNmae, "update" + Table_Order, UpdateOrder);
        matcher.addURI(HostNmae, "delete" + Table_Order, DelOrder);
    }
    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = matcher.match(uri);
        Cursor cursor=null;
        switch (match){
            case QueryCar:
              //  LogUtil.e("查询购物车");
                if(selection==null){
                    cursor= helper.getReadableDatabase().query(DBHelper.Table_Car, projection, selection, selectionArgs, null, null, null);
                }else{
                    String sql= "select * from car where toyid = ? and toytype = ?";
                    cursor= helper.getReadableDatabase().rawQuery(sql,selectionArgs);
                }

                break;
            case QueryOrder:
                LogUtil.e("查询历史订单");
                cursor= helper.getReadableDatabase().query(DBHelper.Table_Order, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
       return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = matcher.match(uri);
        switch (match){
            case InsertCar:
              //long insert = helper.getWritableDatabase().insert(DBHelper.Table_Car, null, values);
                SQLiteDatabase db = helper.getWritableDatabase();
                int count = db.query(DBHelper.Table_Car, null, null, null, null, null, null).getCount();
                if(count==0){
                    ContentValues v = new ContentValues();
                    v.put("toyid", "null");
                    v.put("toytype","null");
                    v.put("toycount","null");
                    v.put("toyname","null");
                    v.put("toyage","null");
                    v.put("toyimg","null");
                    v.put("toyprice","null");
                    v.put("toydpj","null");
                    v.put("toynum","null");
                    db.insert(DBHelper.Table_Car, null, v);
                    LogUtil.e("插入购物车数据库默认数据");
                }
                long insert = db.insert(DBHelper.Table_Car, null, values);
                LogUtil.e("插入购物车数据库"+insert);

                break;
            case InsertOrder:
                LogUtil.e("插入订单数据库"+match);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = matcher.match(uri);
        int index=0;
        switch (match){
            case DelCar:
                if(selectionArgs==null){
                    helper.getWritableDatabase().execSQL("delete from car");
                }else{
                    helper.getWritableDatabase().execSQL("delete from car where toyid ="+selectionArgs[0]);
                }
                LogUtil.e("删除购物车本地");
                break;
            case DelOrder:
                LogUtil.e("删除订单本地"+selection);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return index;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = matcher.match(uri);
        int index=0;
        switch (match){
            case UpdateCar:
                helper.getWritableDatabase().execSQL("update car set toynum= "+selection+" where toyid ="+selectionArgs[0]);
                LogUtil.e("更新购物车数据库id "+selectionArgs[0]+" num "+selection);
                break;
            case UpdateOrder:
                LogUtil.e("删除订单数据库"+match);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        //内容观察者
        return index;
    }
}
