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
 * Created by Burt on 2017/8/15 0015.
 */

public class ZuQiBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * vkind : 次卡
         * vzdzq : 365
         * uid : 23
         * uclass : 银钻
         */

        private int id;
        private String vkind;
        private String vzdzq;
        private String uid;
        private String uclass;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVkind() {
            return vkind;
        }

        public void setVkind(String vkind) {
            this.vkind = vkind;
        }

        public String getVzdzq() {
            return vzdzq;
        }

        public void setVzdzq(String vzdzq) {
            this.vzdzq = vzdzq;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUclass() {
            return uclass;
        }

        public void setUclass(String uclass) {
            this.uclass = uclass;
        }
    }
}
