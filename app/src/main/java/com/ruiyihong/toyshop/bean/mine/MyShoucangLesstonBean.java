package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/22.
 * 我的收藏---热门推荐
 */

public class MyShoucangLesstonBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 18
         * uid : 26
         * wid : 18
         * sign : 2
         * time :
         * kcclass : 0
         * bgimg : 20170728\40412a2d23031a5e28485785f90a334f.jpg
         * media : 20170728\d917d38976fb41df27bd992c0510eea3.mp3
         * tip : 热门
         * title : 这是音乐
         * pay : 0
         * ispay : 0
         * brief : 这是一个很好听的音频
         * cretime : 1501230430
         * fname : 滑梯/秋千
         * liulan : 4
         * zhangjie : 第一章
         * love : 0
         * share : 0
         * collect : 0
         */

        private int id;
        private int uid;
        private int wid;
        private int sign;
        private String time;
        private String kcclass;
        private String bgimg;
        private String media;
        private String tip;
        private String title;
        private int pay;
        private int ispay;
        private String brief;
        private String cretime;
        private String fname;
        private int liulan;
        private String zhangjie;
        private int love;
        private int share;
        private int collect;

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

        public String getKcclass() {
            return kcclass;
        }

        public void setKcclass(String kcclass) {
            this.kcclass = kcclass;
        }

        public String getBgimg() {
            return bgimg;
        }

        public void setBgimg(String bgimg) {
            this.bgimg = bgimg;
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPay() {
            return pay;
        }

        public void setPay(int pay) {
            this.pay = pay;
        }

        public int getIspay() {
            return ispay;
        }

        public void setIspay(int ispay) {
            this.ispay = ispay;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getCretime() {
            return cretime;
        }

        public void setCretime(String cretime) {
            this.cretime = cretime;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public int getLiulan() {
            return liulan;
        }

        public void setLiulan(int liulan) {
            this.liulan = liulan;
        }

        public String getZhangjie() {
            return zhangjie;
        }

        public void setZhangjie(String zhangjie) {
            this.zhangjie = zhangjie;
        }

        public int getLove() {
            return love;
        }

        public void setLove(int love) {
            this.love = love;
        }

        public int getShare() {
            return share;
        }

        public void setShare(int share) {
            this.share = share;
        }

        public int getCollect() {
            return collect;
        }

        public void setCollect(int collect) {
            this.collect = collect;
        }
    }
}
