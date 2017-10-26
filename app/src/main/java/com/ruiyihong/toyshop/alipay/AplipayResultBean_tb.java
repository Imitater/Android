package com.ruiyihong.toyshop.alipay;

import java.util.List;

/**
 * Created by 81521 on 2017/10/11.
 */

public class AplipayResultBean_tb {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1034
         * did : f566ee1234853eb6
         * tai : 1
         * jia : 1
         * total : 10
         */

        private int id;
        private String did;
        private int tai;
        private int jia;
        private int total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public int getTai() {
            return tai;
        }

        public void setTai(int tai) {
            this.tai = tai;
        }

        public int getJia() {
            return jia;
        }

        public void setJia(int jia) {
            this.jia = jia;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
