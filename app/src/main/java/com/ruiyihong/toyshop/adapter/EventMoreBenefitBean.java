package com.ruiyihong.toyshop.adapter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/10/24 0024.
 */

public class EventMoreBenefitBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 21
         * title : 测试
         * acimg : 20171023\f770f616bfcd83c5ab59e53b209344b3.jpg
         * class : 0
         * where : 秦皇岛
         * starttime : 2017-10-09 00:00:00
         * bmfs : 0
         * number : 0
         * limit : 301
         * main : 河北t队1
         * condition : 1.有家长陪同.2-12岁儿童都可参加；2.在店内办理会员的3
         * target : 1.能从多个物体进行10以内的按数取物；2.比较数字的大小.感知数与量的对应关系。；3.体验数学活动的乐趣，乐意与同伴合作。
         * time : 1508720821
         * endtime : 2017-10-28 00:00:00
         */

        private int id;
        private String title;
        private String acimg;
        @SerializedName("class")
        private String classX;
        private String where;
        private String starttime;
        private int bmfs;
        private int number;
        private int limit;
        private String main;
        private String condition;
        private String target;
        private String time;
        private String endtime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAcimg() {
            return acimg;
        }

        public void setAcimg(String acimg) {
            this.acimg = acimg;
        }

        public String getClassX() {
            return classX;
        }

        public void setClassX(String classX) {
            this.classX = classX;
        }

        public String getWhere() {
            return where;
        }

        public void setWhere(String where) {
            this.where = where;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public int getBmfs() {
            return bmfs;
        }

        public void setBmfs(int bmfs) {
            this.bmfs = bmfs;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }
    }
}
