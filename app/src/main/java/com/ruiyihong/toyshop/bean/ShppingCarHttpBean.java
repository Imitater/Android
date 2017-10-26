/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Burt on 2017/8/10 0010.
 */

public class ShppingCarHttpBean implements Serializable{


    /**
     * status : 1
     * wjlist : [{"id":9,"isbw":0,"name":"有个性的羊","shopimg":"20170814\\c0320348c56bd9f576dd0857e4276d6e.png","suitage":"3-6个月","shopprice":4512,"dpj":120111,"kcl":67,"wshu":1,"gid":347},{"id":10,"isbw":0,"name":"小小玩具-1","shopimg":"20170809\\f3e5121d320d097ca087fb89814fd4c0.png","suitage":"2岁-3岁","shopprice":4512,"dpj":120111,"kcl":89,"wshu":3,"gid":346},{"id":11,"isbw":1,"name":"小小玩具-2","shopimg":"20170724\\c41f49b0ce618ac801f2ae8c69b4c862.jpg","suitage":"6-12个月","shopprice":4512,"dpj":9878,"kcl":23,"wshu":6,"gid":338}]
     * total : 10
     */

    private int status;
    private int total;
    private List<WjlistBean> wjlist;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<WjlistBean> getWjlist() {
        return wjlist;
    }

    public void setWjlist(List<WjlistBean> wjlist) {
        this.wjlist = wjlist;
    }

    public static class WjlistBean implements Serializable{
        /**
         * id : 9
         * isbw : 0
         * name : 有个性的羊
         * shopimg : 20170814\c0320348c56bd9f576dd0857e4276d6e.png
         * suitage : 3-6个月
         * shopprice : 4512
         * dpj : 120111
         * kcl : 67
         * wshu : 1
         * gid : 347
         */

        private int id;
        private int isbw;
        private String name;
        private String shopimg;
        private String suitage;
        private int shopprice;
        private int dpj;
        private int kcl;
        private int wshu;
        private int gid;
        private boolean isCheck;

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIsbw() {
            return isbw;
        }

        public void setIsbw(int isbw) {
            this.isbw = isbw;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShopimg() {
            return shopimg;
        }

        public void setShopimg(String shopimg) {
            this.shopimg = shopimg;
        }

        public String getSuitage() {
            return suitage;
        }

        public void setSuitage(String suitage) {
            this.suitage = suitage;
        }

        public int getShopprice() {
            return shopprice;
        }

        public void setShopprice(int shopprice) {
            this.shopprice = shopprice;
        }

        public int getDpj() {
            return dpj;
        }

        public void setDpj(int dpj) {
            this.dpj = dpj;
        }

        public int getKcl() {
            return kcl;
        }

        public void setKcl(int kcl) {
            this.kcl = kcl;
        }

        public int getWshu() {
            return wshu;
        }

        public void setWshu(int wshu) {
            this.wshu = wshu;
        }

        public int getGid() {
            return gid;
        }

        public void setGid(int gid) {
            this.gid = gid;
        }
    }
}
