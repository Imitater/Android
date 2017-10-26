package com.ruiyihong.toyshop.bean.mine;

import java.util.List;

/**
 * Created by 81521 on 2017/8/31.
 * 我的订单
 */

public class MyOrderBean {


    private List<DataBeanX> data;

    public List<DataBeanX> getData() {
        return data;
    }

    public void setData(List<DataBeanX> data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * id : 5c08243cdd18647e
         * data : [{"id":823,"uid":16,"wid":8,"shu":1,"jia":1000,"tai":1,"wname":"1222221sd","brandplace":"33","suitage":"3-6个月","dpj":6660,"shopimg":"20170708\\4aace76a87fdb1f45524d31e684c5153.png","ptime":null,"uaddr":"西环南路与文昌路交叉口西南方向燕大星苑红树湾册子","unames":"哈哈 先生","uphone":"17190175362","spnum":2,"expense":10,"total":2010},{"id":822,"uid":16,"wid":12,"shu":1,"jia":1000,"tai":0,"wname":"小小玩具-3","brandplace":"杭州","suitage":"1岁-1岁半","dpj":127889,"shopimg":"20170724\\303d864db89dd8d0edcf9bae965dd1c6.png","ptime":null,"uaddr":"西环南路与文昌路交叉口西南方向燕大星苑红树湾册子","unames":"哈哈 先生","uphone":"17190175362","spnum":2,"expense":10,"total":2010}]
         */

        private String id;
        private List<DataBean> data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 823
             * uid : 16
             * wid : 8
             * shu : 1
             * jia : 1000
             * tai : 1
             * wname : 1222221sd
             * brandplace : 33
             * suitage : 3-6个月
             * dpj : 6660
             * shopimg : 20170708\4aace76a87fdb1f45524d31e684c5153.png
             * ptime : null
             * uaddr : 西环南路与文昌路交叉口西南方向燕大星苑红树湾册子
             * unames : 哈哈 先生
             * uphone : 17190175362
             * spnum : 2
             * expense : 10
             * total : 2010
             */

            private int id;
            private int uid;
            private int wid;
            private int shu;
            private String jia;
            private int tai;
            private String wname;
            private String brandplace;
            private String suitage;
            private String dpj;
            private String shopimg;
            private Object ptime;
            private String uaddr;
            private String unames;
            private String uphone;
            private int spnum;
            private int expense;
            private int total;

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

            public int getShu() {
                return shu;
            }

            public void setShu(int shu) {
                this.shu = shu;
            }

            public String getJia() {
                return jia;
            }

            public void setJia(String jia) {
                this.jia = jia;
            }

            public int getTai() {
                return tai;
            }

            public void setTai(int tai) {
                this.tai = tai;
            }

            public String getWname() {
                return wname;
            }

            public void setWname(String wname) {
                this.wname = wname;
            }

            public String getBrandplace() {
                return brandplace;
            }

            public void setBrandplace(String brandplace) {
                this.brandplace = brandplace;
            }

            public String getSuitage() {
                return suitage;
            }

            public void setSuitage(String suitage) {
                this.suitage = suitage;
            }

            public String getDpj() {
                return dpj;
            }

            public void setDpj(String dpj) {
                this.dpj = dpj;
            }

            public String getShopimg() {
                return shopimg;
            }

            public void setShopimg(String shopimg) {
                this.shopimg = shopimg;
            }

            public Object getPtime() {
                return ptime;
            }

            public void setPtime(Object ptime) {
                this.ptime = ptime;
            }

            public String getUaddr() {
                return uaddr;
            }

            public void setUaddr(String uaddr) {
                this.uaddr = uaddr;
            }

            public String getUnames() {
                return unames;
            }

            public void setUnames(String unames) {
                this.unames = unames;
            }

            public String getUphone() {
                return uphone;
            }

            public void setUphone(String uphone) {
                this.uphone = uphone;
            }

            public int getSpnum() {
                return spnum;
            }

            public void setSpnum(int spnum) {
                this.spnum = spnum;
            }

            public int getExpense() {
                return expense;
            }

            public void setExpense(int expense) {
                this.expense = expense;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }
        }
    }
}
