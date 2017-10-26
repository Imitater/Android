package com.ruiyihong.toyshop.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
/**
 * Created by hegeyang on 2017/3/25 0025 .
 */

public class SPUtil {
	private static final String SPNAME ="ruiyihong" ;
	private static SharedPreferences sp;
	public static void putBoolean(String key, boolean value, Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key,value).commit();
	}

	public static boolean getBoolean(String key,Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		boolean b = sp.getBoolean(key, false);
		return b;
	}


	//写入数据
	public static void setString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	// 读取数据
	public static String getString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		return sp.getString(key, value);
	}


	public static String[] getUid(Context context){
		String str = SPUtil.getString(context, AppConstants.SP_LOGIN, "");
		if(TextUtils.isEmpty(str)){
			return null;
		}
		String[] s=new String[3];
		try {
			JSONObject jsonObject = new JSONObject(str);
			String uid = jsonObject.getString("uid");
			String uname=jsonObject.getString("uname");
			String nickname=jsonObject.getString("yhniche");
			s[0]=uid;
			s[1]=uname;
			s[2]=nickname;
		} catch (JSONException e) {
			LogUtil.e("登陆信息解析错误" + e.toString());
			return null;
		}
		return s;
	}
	public static void deleteSp(Context context){
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().clear().commit();
	}
}
