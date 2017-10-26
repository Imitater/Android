package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/7/28.
 */

public class EventBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 7
         * acimg : 20170726\efb009ef9cf8723eb004c0070db66897.png
         * title : 孩子户外活动宣传环保绘画活动
         * bmfs : 1
         * starttime : 1501207518
         */

        private int id;
        private String acimg;
        private String title;
        private int bmfs;
        private String starttime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAcimg() {
            return acimg;
        }

        public void setAcimg(String acimg) {
            this.acimg = acimg;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getBmfs() {
            return bmfs;
        }

        public void setBmfs(int bmfs) {
            this.bmfs = bmfs;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }
    }

   /* *//**
     * id : 4
     * acimg : 20170726\079957008e0709a5f8224035ebbcbebb.png
     * title : 孩子公益活动宣传环保绘画活动
     *//*

    private int id;
    private String acimg;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcimg() {
        return acimg;
    }

    public void setAcimg(String acimg) {
        this.acimg = acimg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }*/
}
