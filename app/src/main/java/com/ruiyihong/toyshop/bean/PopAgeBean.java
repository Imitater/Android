package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 81521 on 2017/7/10.
 */

public class PopAgeBean {


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
         * age : 0-3个月
         */

        private int id;
        private String age;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
