package com.ruiyihong.toyshop.bean;

/**
 * Created by Administrator on 2017/9/27 0027.
 */

public class AlipaySuccessBean {

    /**
     * data : {"id":7,"ding":"7b16c4f73aeb1013","tai":0,"price":10}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 7
         * ding : 7b16c4f73aeb1013
         * tai : 0
         * price : 10
         */

        private int id;
        private String ding;
        private int tai;
        private int price;
        private int total;//订单总价

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDing() {
            return ding;
        }

        public void setDing(String ding) {
            this.ding = ding;
        }

        public int getTai() {
            return tai;
        }

        public void setTai(int tai) {
            this.tai = tai;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }
}
