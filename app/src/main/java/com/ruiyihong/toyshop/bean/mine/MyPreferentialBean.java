package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/24.
 * 我的可用优惠券
 */

public class MyPreferentialBean {


    private List<DataBean> data;
    private List<Data1Bean> data1;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<Data1Bean> getData1() {
        return data1;
    }

    public void setData1(List<Data1Bean> data1) {
        this.data1 = data1;
    }

    public static class DataBean {
        /**
         * id : 3
         * dhtime : 2017-09-04 14:39:57
         * dmian : 5
         * duse : 99
         * dluo : 50
         * dimg : 20170921\76b2d0d4513372df4d7bfc986127bc80.png
         * dhcs : 22
         * dqtime : 1970-01-01 08:00:03
         * olddjq : 20170921\f060afaa75e03dd80ff87745eb682a05.png
         * time : 1505993909
         */

        private int id;
        private String dhtime;
        private int dmian;
        private int duse;
        private int dluo;
        private String dimg;
        private int dhcs;
        private String dqtime;
        private String olddjq;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDhtime() {
            return dhtime;
        }

        public void setDhtime(String dhtime) {
            this.dhtime = dhtime;
        }

        public int getDmian() {
            return dmian;
        }

        public void setDmian(int dmian) {
            this.dmian = dmian;
        }

        public int getDuse() {
            return duse;
        }

        public void setDuse(int duse) {
            this.duse = duse;
        }

        public int getDluo() {
            return dluo;
        }

        public void setDluo(int dluo) {
            this.dluo = dluo;
        }

        public String getDimg() {
            return dimg;
        }

        public void setDimg(String dimg) {
            this.dimg = dimg;
        }

        public int getDhcs() {
            return dhcs;
        }

        public void setDhcs(int dhcs) {
            this.dhcs = dhcs;
        }

        public String getDqtime() {
            return dqtime;
        }

        public void setDqtime(String dqtime) {
            this.dqtime = dqtime;
        }

        public String getOlddjq() {
            return olddjq;
        }

        public void setOlddjq(String olddjq) {
            this.olddjq = olddjq;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class Data1Bean {
        /**
         * id : 6
         * dhtime : 2017-09-18 15:16:33
         * pnum : 5
         * pic : 20170921\114c5f63f4ef4248cab972aa5672063f.png
         * ischou : 1
         * dqtime : 1970-01-01 08:00:03
         * jiang : 2
         * oldhb : 20170921\818647df48a286e45bc21bb2a4a78fca.png
         * wdhb : 20170921\dea3bc23918c408914a311cde879f6d4.png
         * time : 1505994291
         */

        private int id;
        private String dhtime;
        private int pnum;
        private String pic;
        private int ischou;
        private String dqtime;
        private int jiang;
        private String oldhb;
        private String wdhb;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDhtime() {
            return dhtime;
        }

        public void setDhtime(String dhtime) {
            this.dhtime = dhtime;
        }

        public int getPnum() {
            return pnum;
        }

        public void setPnum(int pnum) {
            this.pnum = pnum;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getIschou() {
            return ischou;
        }

        public void setIschou(int ischou) {
            this.ischou = ischou;
        }

        public String getDqtime() {
            return dqtime;
        }

        public void setDqtime(String dqtime) {
            this.dqtime = dqtime;
        }

        public int getJiang() {
            return jiang;
        }

        public void setJiang(int jiang) {
            this.jiang = jiang;
        }

        public String getOldhb() {
            return oldhb;
        }

        public void setOldhb(String oldhb) {
            this.oldhb = oldhb;
        }

        public String getWdhb() {
            return wdhb;
        }

        public void setWdhb(String wdhb) {
            this.wdhb = wdhb;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
