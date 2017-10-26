package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 81521 on 2017/9/13.
 */

public class JianpinBean {


    /**
     * data : [{"id":9,"pnum":1,"pic":"20170912\\28cd6f27594d22f58e6bde5e754f181f.png","ischou":1,"dqtime":"3","jiang":1,"time":"1505181713"},{"id":8,"pnum":1,"pic":"20170912\\63bcdbcde4eb41451a9fc70dfdb5adf3.png","ischou":1,"dqtime":"3","jiang":1,"time":"1505181694"},{"id":7,"pnum":2,"pic":"20170912\\874db918441f309f5906067b25bb4b6d.png","ischou":1,"dqtime":"3","jiang":1,"time":"1505181682"},{"id":6,"pnum":4,"pic":"20170912\\aeffce7210d153a78fcca3ee24227b90.png","ischou":1,"dqtime":"3","jiang":2,"time":"1505181670"},{"id":5,"pnum":2,"pic":"20170912\\a08d7339b855c04e9d94c84886a32a2e.png","ischou":1,"dqtime":"3","jiang":2,"time":"1505181659"},{"id":4,"pnum":1,"pic":"20170912\\6977b9f1120f3a563549880cd69bbd9d.png","ischou":1,"dqtime":"3","jiang":3,"time":"1505181627"},{"id":3,"pnum":2,"pic":"20170912\\1fa53880deb141b32eab0bbcc0bf9457.png","ischou":1,"dqtime":"3","jiang":2,"time":"1505181651"},{"id":2,"pnum":2,"pic":"20170912\\f0b0a42d02e705c2963c62723497a996.png","ischou":1,"dqtime":"3","jiang":3,"time":"1505181637"}]
     * xhjifen : 5
     */

    private int xhjifen;
    private List<DataBean> data;

    public int getXhjifen() {
        return xhjifen;
    }

    public void setXhjifen(int xhjifen) {
        this.xhjifen = xhjifen;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 9
         * pnum : 1
         * pic : 20170912\28cd6f27594d22f58e6bde5e754f181f.png
         * ischou : 1
         * dqtime : 3
         * jiang : 1
         * time : 1505181713
         */

        private int id;
        private int pnum;
        private String pic;
        private int ischou;
        private String dqtime;
        private int jiang;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
