package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by hegeyang on 2017/8/13 0013 .
 */

public class VipMemberDetialBean {

	public List<DataBean> data;
	

	public  class DataBean {
		/**
		 * id : 4
		 * vquan : 一年内无限次场馆体验；单次借阅精品玩具1件/普通玩具2件；单次图书借阅8本；主题活动/课程享受优先报名权；每日线下馆内的绘本时间、玩具时间。
		 * vprice : 1280
		 * vperiod : 一年
		 */

		public int id;
		public String vquan;
		public int vprice;
		public String vperiod;
		public String vjieyu;
		
	}
}
