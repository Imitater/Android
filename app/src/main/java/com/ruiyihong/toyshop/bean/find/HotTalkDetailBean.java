package com.ruiyihong.toyshop.bean.find;

import java.util.List;

/**
 * Created by 81521 on 2017/8/22.
 */

public class HotTalkDetailBean {


    private List<DataBean> data;
    private List<ScBean> sc;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<ScBean> getSc() {
        return sc;
    }

    public void setSc(List<ScBean> sc) {
        this.sc = sc;
    }

    public static class DataBean {
        /**
         * id : 5
         * img : 20170813\579593d15c17539251feaa808ed1ffe6.png
         * title : 尿不湿怎么选
         * content :
         * time : 1501922483
         */

        private int id;
        private String img;
        private String title;
        private String content;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class ScBean {
        /**
         * id : 35
         * uid : 26
         * wid : 5
         * sign : 1
         * time : 1503473002
         */

        private int id;
        private String uid;
        private String wid;
        private String sign;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getWid() {
            return wid;
        }

        public void setWid(String wid) {
            this.wid = wid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
