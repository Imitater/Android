package com.ruiyihong.toyshop.bean;

import java.util.List;

/**
 * Created by 81521 on 2017/7/7.
 * 首页轮播图 bean类
 */

public class HomeLunboBean {


    /**
     * data : [{"id":2,"adimg":"20170707\\fa7ecfd5ca68fec3d52f906eba736d38.png","url":"http://www.baidu.com","pos":"1","time":"1499337087"},{"id":3,"adimg":"20170707\\7b5a22c8234a3a9ac89658b09326dd3d.png","url":"www.baidu.com","pos":"2","time":"1499391172"},{"id":4,"adimg":"20170707\\87b2f19b8b916f8c945ffcea876c6cd3.png","url":"www.baidu.com","pos":"0","time":"1499391208"}]
     * msg : 成功
     */

    private String msg;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2
         * adimg : 20170707\fa7ecfd5ca68fec3d52f906eba736d38.png
         * url : http://www.baidu.com
         * pos : 1
         * time : 1499337087
         */

        private int id;
        private String adimg;
        private String url;
        private String pos;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAdimg() {
            return adimg;
        }

        public void setAdimg(String adimg) {
            this.adimg = adimg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
