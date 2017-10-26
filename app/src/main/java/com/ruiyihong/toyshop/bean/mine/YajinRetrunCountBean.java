package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/9/27.
 * 获取绑定的支付宝账号
 */

public class YajinRetrunCountBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 17
         * zfhao : 123456789
         * uid : 16
         * time : 1506497811
         */

        private int id;
        private String zfhao;
        private int uid;
        private int time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getZfhao() {
            return zfhao;
        }

        public void setZfhao(String zfhao) {
            this.zfhao = zfhao;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
