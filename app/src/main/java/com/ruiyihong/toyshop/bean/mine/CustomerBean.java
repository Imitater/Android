package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/9/15.
 * 联系客服
 */

public class CustomerBean {

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
         * kefu :
         * phone : 111
         * addr : 111
         * bgimg : 20171017\7395e00d0613247b99adb88a1ff7dd28.png
         * time : 1508153288
         */

        private int id;
        private String kefu;
        private String phone;
        private String addr;
        private String bgimg;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getKefu() {
            return kefu;
        }

        public void setKefu(String kefu) {
            this.kefu = kefu;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getBgimg() {
            return bgimg;
        }

        public void setBgimg(String bgimg) {
            this.bgimg = bgimg;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
