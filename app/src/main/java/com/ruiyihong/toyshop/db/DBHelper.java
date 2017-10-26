package com.ruiyihong.toyshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ruiyihong.toyshop.util.LogUtil;

/**
 * 数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_Name="shopping";
    public static final String Table_Car="car";
    public static final String Table_Order="order";


    public DBHelper(Context context){
        super(context,DB_Name,null,1);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        购物车表结构
        1    2     3    4     5      6     7     8     9
        {ID Type Count Name   Age   Img   Price  Dpj  Num}
        id type 库存量 名字 适合年龄 图片 售价 吊牌价 数量
         */
        //toyage,toynum,toydpj,toyprice,toytype,toyname,toyimg,toyid,toycount
        String Create_Table_Car="create table "+Table_Car+
                "(toyid text" + ",toytype text" + ",toycount text" + ",toyname text" + ",toyage text" +
                ",toyimg text" + ",toyprice text" + ",toydpj text" + ",toynum text)";

        /*订单表结构
        { ID     订单编号  网络获取到的商品Json串  }*/
        String Create_Table_Order="create table "+Table_Order+"(order text,json text)";
        // db.execSQL(Create_Table_Order);
        db.execSQL(Create_Table_Car);
        LogUtil.e("数据库创建-->插入默认数据");

    }

    /*
    * 为数据库更新版本使用
    * 数据库版本的约定
    * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
