package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/10.
 * 积分商城--热门兑换--代金券
 */

public class JifenHotDjqBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 5
         * dmian : 10
         * duse : 99
         * dluo : 100
         * dimg : 20170815\5315af35a2efc36443e7f5d55a0f3eb4.png
         */

        private int id;
        private int dmian;
        private int duse;
        private int dluo;
        private String dimg;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDmian() {
            return dmian;
        }

        public void setDmian(int dmian) {
            this.dmian = dmian;
        }

        public int getDuse() {
            return duse;
        }

        public void setDuse(int duse) {
            this.duse = duse;
        }

        public int getDluo() {
            return dluo;
        }

        public void setDluo(int dluo) {
            this.dluo = dluo;
        }

        public String getDimg() {
            return dimg;
        }

        public void setDimg(String dimg) {
            this.dimg = dimg;
        }
    }
}
