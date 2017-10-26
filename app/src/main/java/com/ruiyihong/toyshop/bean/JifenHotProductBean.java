package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/10.
 * 积分商城--热门兑换--图书玩具
 */

public class JifenHotProductBean {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 15
         * shopimg : 20170724\2347a250c257b8ad9d6636ddce3867a9.png
         * lbb : 0
         * longtime : 2天
         */

        private int id;
        private String shopimg;
        private int lbb;
        private String longtime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getShopimg() {
            return shopimg;
        }

        public void setShopimg(String shopimg) {
            this.shopimg = shopimg;
        }

        public int getLbb() {
            return lbb;
        }

        public void setLbb(int lbb) {
            this.lbb = lbb;
        }

        public String getLongtime() {
            return longtime;
        }

        public void setLongtime(String longtime) {
            this.longtime = longtime;
        }
    }
}
