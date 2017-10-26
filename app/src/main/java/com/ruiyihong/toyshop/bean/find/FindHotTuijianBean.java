package com.ruiyihong.toyshop.bean.find;

import java.util.List;

/**
 * Created by 81521 on 2017/8/6.
 * 发现页面热门推荐
 */

public class FindHotTuijianBean {


    private List<ListBean> list;
    private List<List1Bean> list1;
    private List<List2Bean> list2;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<List1Bean> getList1() {
        return list1;
    }

    public void setList1(List<List1Bean> list1) {
        this.list1 = list1;
    }

    public List<List2Bean> getList2() {
        return list2;
    }

    public void setList2(List<List2Bean> list2) {
        this.list2 = list2;
    }

    public static class ListBean {
        /**
         * id : 33
         * uid : 16
         * content : 郭明明
         * pic : 20170816\8445e7d52c259ba7fa80efed25e1d502.png;
         * ispic : 0
         * isadmin : 0
         * isshow : 0
         * dzl : 2
         * plnum : 1
         * simg :
         * time : 2017-08-16 15:13:12
         * yhniche : xxhhh
         * yhimg : 20170815\66930080cdb98cfc3b736ca8cdfe3644.png
         */

        private int id;
        private int uid;
        private String content;
        private String pic;
        private int ispic;
        private int isadmin;
        private int isshow;
        private int dzl;
        private int plnum;
        private String simg;
        private String time;
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

        public int getIspic() {
            return ispic;
        }

        public void setIspic(int ispic) {
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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

    public static class List1Bean {
        /**
         * id : 453
         * tid : 30
         * uid : 26
         * time : 1502966204
         */

        private int id;
        private int tid;
        private int uid;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class List2Bean {
        /**
         * wid : 24
         * sign : 0
         */

        private int wid;
        private int sign;

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
    }
}
