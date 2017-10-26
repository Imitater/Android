package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/10.
 */

public class JifenDuihuanBean {

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
         * shoptext : 图书图书，，，
         * shopprice : 120
         * longtime : 1天
         * lbb : 1000
         * ischange : 1
         * kcl : 3454
         */

        private int id;
        private String shoptext;
        private int shopprice;
        private String longtime;
        private int lbb;
        private int ischange;
        private int kcl;
        private String shopimg;

        public String getShopimg() {
            return shopimg;
        }

        public void setShopimg(String shopimg) {
            this.shopimg = shopimg;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getShoptext() {
            return shoptext;
        }

        public void setShoptext(String shoptext) {
            this.shoptext = shoptext;
        }

        public int getShopprice() {
            return shopprice;
        }

        public void setShopprice(int shopprice) {
            this.shopprice = shopprice;
        }

        public String getLongtime() {
            return longtime;
        }

        public void setLongtime(String longtime) {
            this.longtime = longtime;
        }

        public int getLbb() {
            return lbb;
        }

        public void setLbb(int lbb) {
            this.lbb = lbb;
        }

        public int getIschange() {
            return ischange;
        }

        public void setIschange(int ischange) {
            this.ischange = ischange;
        }

        public int getKcl() {
            return kcl;
        }

        public void setKcl(int kcl) {
            this.kcl = kcl;
        }
    }
}
