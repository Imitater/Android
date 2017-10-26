package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/9/6.
 */

public class CollectBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * wid : 20
         * issc : 0
         */

        private int wid;
        private int issc;

        public int getWid() {
            return wid;
        }

        public void setWid(int wid) {
            this.wid = wid;
        }

        public int getIssc() {
            return issc;
        }

        public void setIssc(int issc) {
            this.issc = issc;
        }
    }
}
