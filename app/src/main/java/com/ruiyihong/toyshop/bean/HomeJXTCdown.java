/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by Burt on 2017/7/11 0011.
 * 首页精选套餐
 */

public class HomeJXTCdown {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 22
         * name : 智力迷宫 3D立体 迷宫球 208关
         * shopimg : 20171001\a138f9dd680bcc0d6dafbcaf07b8c3c1.png
         * shopprice : 2
         * dpj : 40
         * kcl : 3
         * type:0
         */

        private int id;
        private String name;
        private String shopimg;
        private int shopprice;
        private int dpj;
        private int kcl;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShopimg() {
            return shopimg;
        }

        public void setShopimg(String shopimg) {
            this.shopimg = shopimg;
        }

        public int getShopprice() {
            return shopprice;
        }

        public void setShopprice(int shopprice) {
            this.shopprice = shopprice;
        }

        public int getDpj() {
            return dpj;
        }

        public void setDpj(int dpj) {
            this.dpj = dpj;
        }

        public int getKcl() {
            return kcl;
        }

        public void setKcl(int kcl) {
            this.kcl = kcl;
        }
    }
}
