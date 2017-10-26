package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/22.
 * 我的收藏---热门推荐
 */

public class MyShoucangTuijianBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 39
         * uid : 23
         * wid : 39
         * sign : 0
         * time : 2017-09-04 11:03:54
         * content :
         * pic : 20170904\e1086ea5bc99f2ba5483cc022a875147.jpg;
         * ispic : 0
         * isadmin : 0
         * isshow : 0
         * dzl : 1
         * plnum : 5
         * simg :
         * yhniche : 1871356
         * yhimg : 20170727\59e9afaf492999adda540aca3863612a.png
         */

        private int id;
        private int uid;
        private int wid;
        private int sign;
        private String time;
        private String content;
        private String pic;
        private String ispic;
        private int isadmin;
        private int isshow;
        private int dzl;
        private int plnum;
        private String simg;
        private String yhniche;
        private String yhimg;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getWid() {
            return wid;
        }

        public void setWid(int wid) {
            this.wid = wid;
        }

        public int getSign() {
            return sign;
        }

        public void setSign(int sign) {
            this.sign = sign;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getIspic() {
            return ispic;
        }

        public void setIspic(String ispic) {
            this.ispic = ispic;
        }

        public int getIsadmin() {
            return isadmin;
        }

        public void setIsadmin(int isadmin) {
            this.isadmin = isadmin;
        }

        public int getIsshow() {
            return isshow;
        }

        public void setIsshow(int isshow) {
            this.isshow = isshow;
        }

        public int getDzl() {
            return dzl;
        }

        public void setDzl(int dzl) {
            this.dzl = dzl;
        }

        public int getPlnum() {
            return plnum;
        }

        public void setPlnum(int plnum) {
            this.plnum = plnum;
        }

        public String getSimg() {
            return simg;
        }

        public void setSimg(String simg) {
            this.simg = simg;
        }

        public String getYhniche() {
            return yhniche;
        }

        public void setYhniche(String yhniche) {
            this.yhniche = yhniche;
        }

        public String getYhimg() {
            return yhimg;
        }

        public void setYhimg(String yhimg) {
            this.yhimg = yhimg;
        }
    }
}
