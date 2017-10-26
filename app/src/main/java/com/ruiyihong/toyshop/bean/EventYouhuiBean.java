package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/7/28.
 */

public class EventYouhuiBean {

    /**
     * time : 2017-07-29
     * data : [{"id":9,"shopimg":"20170727\\51980354532ef74273cefb74fd844903.png","yhprice":"30"},
     * {"id":10,"shopimg":"20170727\\16f7776d0b88a030ea6c2978f2e2519f.png","yhprice":"2"},
     * {"id":13,"shopimg":"20170727\\cfab92e41b83946fe1979870fec73321.png","yhprice":"11"},
     * {"id":17,"shopimg":"20170724\\6a891eae3dc3f4f9a374137218b88dd8.png","yhprice":"34"},
     * {"id":23,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","yhprice":"77"},
     * {"id":27,"shopimg":"20170708\\0ec638978114445e3f2f499b2e61c46c.jpg","yhprice":"454"},
     * {"id":29,"shopimg":"20170727\\1aa517a609fbdd70dbf4662fcfa8f553.png","yhprice":"234"},
     * {"id":33,"shopimg":"20170727\\b8bd438ac639610a7c752c23ed503849.png","yhprice":"12"}]
     */

    private String time;
    private List<DataBean> data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
         * shopimg : 20170727\51980354532ef74273cefb74fd844903.png
         * yhprice : 30
         */

        private int id;
        private String shopimg;
        private String yhprice;
        private int isbw;
        private int dpj;
        private String name;
        private int kcl;
        private int isnew;

        public int getIsbw() {
            return isbw;
        }

        public void setIsbw(int isbw) {
            this.isbw = isbw;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getShopimg() {
            return shopimg;
        }

        public void setShopimg(String shopimg) {
            this.shopimg = shopimg;
        }

        public String getYhprice() {
            return yhprice;
        }

        public void setYhprice(String yhprice) {
            this.yhprice = yhprice;
        }

        public int getDpj() {
            return dpj;
        }

        public void setDpj(int dpj) {
            this.dpj = dpj;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getKcl() {
            return kcl;
        }

        public void setKcl(int kcl) {
            this.kcl = kcl;
        }

        public int getIsnew() {
            return isnew;
        }

        public void setIsnew(int isnew) {
            this.isnew = isnew;
        }
    }
}
