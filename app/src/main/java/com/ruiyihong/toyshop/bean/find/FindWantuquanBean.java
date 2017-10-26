package com.ruiyihong.toyshop.bean.find;

import java.util.List;

/**
 * Created by 81521 on 2017/8/7.
 * 发现--玩图圈 bean
 */

public class FindWantuquanBean {

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
         * pname : 奥迪
         */

        private int id;
        private String pname;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }
    }
}
