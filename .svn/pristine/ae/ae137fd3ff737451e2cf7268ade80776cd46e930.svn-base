package com.ruiyihong.toyshop.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ruiyihong.toyshop.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by 81521 on 2017/7/3.
 */

public class DBManager {
    private String DB_NAME = "ChinaCity.db";
    private String TABLE_NAME = "china_city_code";
    private Context mContext;

    public DBManager(Context mContext) {
        this.mContext = mContext;
    }
    //把assets目录下的db文件复制到dbpath下
    public SQLiteDatabase initDBManager(String packName)  {
        String dbPath = "/data/data/" + packName
                + "/databases" ;
        File file = new File(dbPath);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(file,DB_NAME);

        LogUtil.e("file存在==="+file.exists());
        if (!file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                InputStream in = mContext.getAssets().open("ChinaCity.db");
                byte[] buffer = new byte[1024];
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1)
                    out.write(buffer, 0, readBytes);
                in.close();
                out.close();
                LogUtil.e("写入私有目录成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return SQLiteDatabase.openOrCreateDatabase(file.getPath(), null);

    }
    public ArrayList<String> getCityData(String packName){
        SQLiteDatabase db = initDBManager(packName);
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> cityList = new ArrayList<>();
        while (cursor.moveToNext()){
            String city = cursor.getString(cursor.getColumnIndex("city"));
            if (!cityList.contains(city)){
                cityList.add(city);
            }
        }
      //  LogUtil.e("size::"+cityList.size());
        return cityList;
    }



}
