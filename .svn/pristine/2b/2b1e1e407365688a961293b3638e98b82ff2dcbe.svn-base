package com.ruiyihong.toyshop.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruiyihong.toyshop.bean.HomeLunboBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李晓曼 on 2017/7/7.
 */

public class GsonUtil {
    private static Gson gson ;
    static{
        if (gson==null){
            gson = new Gson();
        }
    }
    //获取gson对象
    public static Gson getGson(){
        return gson;
    }
    //将Json数据解析成相应的映射对象
     public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {

         T result = gson.fromJson(jsonData, type);
         return result;
     }
    /**
     * 把json字符串变成集合
     * @param json
     * @param type  new TypeToken<List<yourbean>>(){}.getType()
     * @return
     */
    public static List<?> parseJsonToList(String json, Type type) {

        List<?> list = gson.fromJson(json, type);
        return list;
    }
}
